# GitHub App Caster

Publish GitHub repository releases as Sparkle compatible appcast.xml

## Install

### 1) Deploy this project to Heroku

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

### 2) Enter the requested values

- `GITHUB_API_LOGIN`: your GitHub login (username)
- `GITHUB_API_TOKEN`: a generated GitHub [personal access tokens](https://help.github.com/articles/creating-an-access-token-for-command-line-use/) with the public_repo scope

### 3) Hit the `Deploy for Free` button

Then wait for Heroku to create, configure and deploy your app.

### 4) Visit your app

The `appcast.xml` for one of your project hosted on GitHub at `your_username/your_project` will be found at:

https:// **generated-name-123456**.herokuapp.com/**your_project**/appcast.xml

Where `generated-name-123456` is the name of your newly deployed app, and `your_project` is the name of any repository that belongs to the user identified earlier into the `GITHUB_API_LOGIN` variable.
