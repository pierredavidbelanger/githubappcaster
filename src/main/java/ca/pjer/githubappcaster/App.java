package ca.pjer.githubappcaster;

import com.github.rjeschke.txtmark.Processor;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Path("/")
public class App {

    private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

    @XmlRootElement
    public static class Rss {

        @XmlAttribute
        public String version;

        @XmlElement(name = "channel")
        public List<Channel> channels;

    }

    public static class Channel {

        public String title;
        public String link;
        public String description;

        @XmlElement(namespace = "http://purl.org/dc/elements/1.1/")
        public String language;
        @XmlElement(namespace = "http://purl.org/dc/elements/1.1/")
        public String creator;
        @XmlElement(namespace = "http://purl.org/dc/elements/1.1/")
        public String date;

        @XmlElement(name = "item")
        public List<Item> items;

    }

    public static class Item {

        public String title;
        public String description;
        public String pubDate;

        @XmlElement(name = "enclosure")
        public List<Enclosure> enclosures;

    }

    public static class Enclosure {

        @XmlAttribute
        public String url;
        @XmlAttribute
        public String type;
        @XmlAttribute(namespace = "http://www.andymatuschak.org/xml-namespaces/sparkle")
        public String version;

    }

    private class RssCacheLoader extends CacheLoader<String, Rss> {

        private final GitHub gitHub;

        public RssCacheLoader(GitHub gitHub) {
            this.gitHub = gitHub;
        }

        @Override
        public Rss load(String repo) throws Exception {

            GHRepository repository = gitHub.getRepository(System.getenv("GITHUB_API_LOGIN") + "/" + repo);

            DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Rss rss = new Rss();
            rss.version = "2.0";
            rss.channels = new ArrayList<Channel>();

            Channel channel = new Channel();
            channel.title = repository.getName() + " - changelog";
            channel.link = new URI(uriInfo.getRequestUri().getScheme(), uriInfo.getRequestUri().getHost(), uriInfo.getRequestUri().getPath(), null).toString();
            channel.description = "Most recent changes with links to updates.";
            channel.language = "en";
            channel.creator = "githubappcaster";
            channel.date = dateFormat.format(new Date());
            channel.items = new ArrayList<Item>();
            rss.channels.add(channel);

            for (GHRelease release : repository.listReleases()) {

                Item item = new Item();
                item.title = release.getName();
                item.description = Processor.process(release.getBody());
                item.pubDate = dateFormat.format(release.getPublished_at());
                item.enclosures = new ArrayList<Enclosure>();
                channel.items.add(item);

                for (GHAsset asset : release.getAssets()) {

                    Enclosure enclosure = new Enclosure();
                    enclosure.url = asset.getBrowserDownloadUrl();
                    enclosure.type = "application/octet-stream";
                    enclosure.version = release.getTagName();
                    item.enclosures.add(enclosure);

                }
            }

            return rss;
        }
    }

    @Context
    private UriInfo uriInfo;

    private final LoadingCache<String, Rss> cache;

    public App() {
        try {
            GitHub gitHub = GitHub.connect(System.getenv("GITHUB_API_LOGIN"), System.getenv("GITHUB_API_TOKEN"));
            cache = CacheBuilder.from(System.getenv("CACHE_SPEC")).build(new RssCacheLoader(gitHub));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("/{repo}/appcast.xml")
    @Produces(MediaType.APPLICATION_XML)
    public Rss appcast(@PathParam("repo") String repo) throws Exception {
        return cache.get(repo);
    }

    public static void main(String[] args) {
        URI uri = UriBuilder.fromUri("http://0.0.0.0/").port(Integer.parseInt(System.getenv("PORT"))).build();
        ResourceConfig resourceConfig = new ResourceConfig().register(new App());
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                server.shutdown();
            }
        }));
    }
}
