# GitHub App Caster

Publish GitHub repository releases as Sparkle compatible appcast.xml

This project is a [Heroku](https://heroku.com) ready JAVA JAX-RS server side application that accept requests for `/your_project/appcast.xml`, fetch (using the [GitHub API](https://developer.github.com/v3/)) releases infos from `github.com/your_username/your_project`, then format and return then into a [Sparkle compatible RSS feed](https://github.com/yahoo/Sparkle/blob/master/Sample%20Appcast.xml).

**WARNING:** Please understand the implications of issuing a GitHub personal access token. Ensure that the token you use is only used for this tool, and that you allow only the `public_repo` scope, or else, your private repositories releases may leak to the public!

## Deploy the server side application

Deploy the app, either easily with the `Deploy to Heroku` button, or flexibly with a `git clone` of this project.

### Quick deploy with the Heroku button

This method allows one to quikly try the project.

1) Click the `Deploy to Heroku` button just here:

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

2) Fill the mandatory env variable

- `GITHUB_API_LOGIN`: your GitHub login (username)
- `GITHUB_API_TOKEN`: a generated GitHub [personal access tokens](https://help.github.com/articles/creating-an-access-token-for-command-line-use/) with the `public_repo` scope

3) Hit the `Deploy for Free` button

Then wait for Heroku to create, configure and deploy your app.

### Deploy a clone of this repository

This method is more flexible.

1) Clone this repository

```
$ git clone https://github.com/pierredavidbelanger/githubappcaster
$ cd githubappcaster
```

2) Create an Heroku app from the clone

```
$ heroku create
```

3) Configure the app

```
$ heroku config:set GITHUB_API_LOGIN="your_username"
$ heroku config:set GITHUB_API_TOKEN="your_token"
$ heroku config:set CACHE_SPEC="maximumSize=10,expireAfterWrite=5m"
```

4) Push the app

```
$ git push heroku master
```

## Use the server side application

### Visit the app and see if everything is ok

The `appcast.xml` for one of your project hosted on GitHub at `your_username/your_project` will be found at:

https:// **generated-name-123456**.herokuapp.com/**your_project**/appcast.xml

Where `generated-name-123456` is the name of your newly deployed app, and `your_project` is the name of any repository that belongs to the user identified earlier into the `GITHUB_API_LOGIN` variable.

### Now point your `SUFeedURL` to the above URL

And enjoy automatic updates linked to your repository releases!
