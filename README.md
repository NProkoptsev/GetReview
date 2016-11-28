# GetReviews

## GetReviews prototype usage

User: login - user, password - user

Admin: login - admin, password - admin

To fill DB with grabbed data - see Grabber part below

## GetReviews Setup

* Setup VirtualBox
* Setup vagrant (https://www.vagrantup.com/)
* clone GetReviews
* run "vagrant up" in GetReviews folder
* vagrant ssh

### setup postgresql

* sudo apt-get update 
* sudo apt-get -y install postgresql postgresql-contrib
* sudo vim /etc/postgresql/9.5/main/pg_hba.conf
  * set all from "md5", "peer" to "trust"
  * add "host    all             all             all                     trust"
* sudo vim /etc/postgresql/9.5/main/postgresql.conf
  * listen_addresses = '*'
* sudo -u postgres createuser GetReviews
* sudo /etc/init.d/postgresql restart

* Add postgres to PgAdmin on your host
* In PgAdmin create database GetReviews with owner GetReviews

### start GetReviews

* cd /vagrant
* ./gradlew npmInstall
* npm install --no-bin-links (for Windows)
* ./gradlew
* open http://localhost:8080/ in browser on your host

### possible solution for gradle errors
* rm /home/vagrant/.gradle/caches -r -f

## Grabber

Import preserialized pack of objects (run localhost:8080/RESOURCE, where RESOURCE - below): 

/grabber/local - only objects, which were found in both shops (**use this by default**)

/grabber/local?import_all=true - all the objects, grabbed from Ozon and Yandex

Grab objects from Ozon (online):

/grabber/ozon - default categories will be grabbed online

/grabber/ozon?cat=CAT_ID - grab the items from the specified category. catid (int) can be copied from any category page from ozon.ru

/grabber/ozon?cat=CAT_ID&depth=X - if category is specified, depth means amount of pages from this category to be grabbed (there are usually 20 items per page)

/grabber/ozon?start=ITEMID&finifh=ITEMID - should be specified both, the range of items' ids to be parsed, itemid should be 9-digit number and start from 135

?ignoreHistory=true - the history will be ignored, i.e. even broken links will be revisited (it can be used to faster parse short range of items)

Grab objects from Yandex (online):

/grabber/yandex - default categories will be parsed

/grabber/yandex/cat_name=RUS_NAME - parse items from the specified category's name

depth=X - X is amount of pages within the category to be parsed (max = 50 due to api limitations), there are usually 30 items per page

# Development docs

Before you can build this project, you must install and configure the following dependencies on your machine:
1. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools (like
[Bower][] and [BrowserSync][]). You will only need to run this command when dependencies change in package.json.

    npm install

We use [Gulp][] as our build system. Install the Gulp command-line tool globally with:

    npm install -g gulp-cli

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

    ./gradlew
    gulp

Bower is used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in `bower.json`. You can also run `bower update` and `bower install` to manage dependencies.
Add the `-h` flag on any command to see how you can use it. For example, `bower update -h`.

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

## Building for production

To optimize the GetReviews application for production, run:

    ./gradlew -Pprod clean bootRepackage

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

    java -jar build/libs/*.war

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

Refer to [Using JHipster in production][] for more details.

## Testing

To launch your application's tests, run:

    ./gradlew test

### Client tests

Unit tests are run by [Karma][] and written with [Jasmine][]. They're located in `src/test/javascript/` and can be run with:

    gulp test


### Other tests

Performance tests are run by [Gatling][] and written in Scala. They're located in `src/test/gatling` and can be run with:

    ./gradlew gatlingRun

For more information, refer to the [Running tests page][].

## Using Docker to simplify development (optional)

You can use Docker to improve your JHipster development experience. A number of docker-compose configuration are available in the `src/main/docker` folder to launch required third party services.
For example, to start a postgresql database in a docker container, run:

    docker-compose -f src/main/docker/postgresql.yml up -d

To stop it and remove the container, run:

    docker-compose -f src/main/docker/postgresql.yml down

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

    ./gradlew bootRepackage -Pprod buildDocker

Then run:

    docker-compose -f src/main/docker/app.yml up -d

For more information refer to [Using Docker and Docker-Compose][], this page also contains information on the docker-compose sub-generator (`yo jhipster:docker-compose`), which is able to generate docker configurations for one or several JHipster applications.

## Continuous Integration (optional)

To set up a CI environment, consult the [Setting up Continuous Integration][] page.

[JHipster Homepage and latest documentation]: https://jhipster.github.io
[JHipster 3.9.1 archive]: https://jhipster.github.io/documentation-archive/v3.9.1

[Using JHipster in development]: https://jhipster.github.io/documentation-archive/v3.9.1/development/
[Using Docker and Docker-Compose]: https://jhipster.github.io/documentation-archive/v3.9.1/docker-compose
[Using JHipster in production]: https://jhipster.github.io/documentation-archive/v3.9.1/production/
[Running tests page]: https://jhipster.github.io/documentation-archive/v3.9.1/running-tests/
[Setting up Continuous Integration]: https://jhipster.github.io/documentation-archive/v3.9.1/setting-up-ci/

[Gatling]: http://gatling.io/
[Node.js]: https://nodejs.org/
[Bower]: http://bower.io/
[Gulp]: http://gulpjs.com/
[BrowserSync]: http://www.browsersync.io/
[Karma]: http://karma-runner.github.io/
[Jasmine]: http://jasmine.github.io/2.0/introduction.html
[Protractor]: https://angular.github.io/protractor/
