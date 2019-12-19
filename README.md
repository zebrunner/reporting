![Alt text](./docs/img/zafira.png "Zafira Logo")
==================

Zafira is a central automation reporting system that is built on the top of Java Spring Framework. It dramatically increases the transparency of test automation results and provides better understanding of product quality. Qaprosoft team is developing Zafira based on more than 10-years expertise in quality assurance and we strongly believe that your QA/Dev engineers and managers will love it!

<p align="center">
  <img width="600px" height="600px" src="./docs/img/space.png">
</p>

Zafira was initially designed to track automation progress of the tests written using [Carina automation framework](https://github.com/qaprosoft/carina/) but you are capable to integrate Zafira client into your own automation tool. We are actively developing clients for different languages and frameworks but still looking for active community to provide better support.

#### Primary features
* Real-time test results tracking via websockets
* VNC streaming and video recording of test sessions
* Flexibly configured widgets and dashboards based on [ECharts](https://www.echartsjs.com/en/index.html)
* User management with authorization policies
* Integration with TestRail and JIRA
* Integration with Slack
* Ability to compose automation reports and send via email

<table>
  </tr>
    <td>
      <b>Configurable dashboards</b>
      <img src="./docs/img/feature_dashboards.png">
    </td>
    <td>
      <b>Integrations with multiple services</b>
      <img src="./docs/img/feature_integrations.png">
    </td>
  </tr>
  </tr>
    <td>
      <b>Real-time test results</b>
      <img src="./docs/img/feature_testrun_results.png">
    </td>
    <td>
      <b>Live streaming and video recording</b>
      <img src="./docs/img/feature_live_streaming.png">
    </td>
  </tr>
  </tr>
    <td>
      <b>Comparison of test runs</b>
      <img src="./docs/img/feature_testruns_comparison.png">
    </td>
    <td>
      <b>Test issues tracking</b>
      <img src="./docs/img/feature_test_issues.png">
    </td>
  </tr>
</table>

## Installation steps

Disclaimer: installation steps described below are not suitable for production-grade deployment and meant for development purposes primarily. If you are looking for production-ready solution to deploy in your own private cloud please see [QPS Infra](https://github.com/qaprosoft/qps-infra). Zafira is also offered as a managed Cloud service - see [zebrunner.com](https://zebrunner.com). For more info and help do not hesitate to contact us via support channels listed at the bottom of this document.

---
The only dependency Zafira requires in order to spin it up (deploy to localhost) is Docker Compose installed. More information on Docker Compose installation can be found [here](https://docs.docker.com/compose/install/). Once you'll have Docker Compose installed you'll have two options, so let's take a closer look at both of them.
### 1. Spinning Zafira using Qaprosoft images
## Linux or MaxOS
In order to install Zafira (deploy to localhost) you'll need to complete following steps:
1. Clone current repo and navigate to the repo root on your local machine
2. To start Zafira execute:
    ```
    $ ./start.sh
    ```
    That's about it! Docker Compose will automatically pull all Zafira images from Docker Registry and spin those up. You can check list of running images by executing `docker ps` command.
    Images and their versions are declared in deployment descriptor called `docker-compose.yml` residing in git repository root directory. Please, note that descriptor does not necessarily contains all latest versions of images (however we usually update it in no time after newer versions are released), but you can be sure that the ones declared there are cross-compatible.
## Windows
1. [Docker for Desktop](https://hub.docker.com/?overlay=onboarding) shoud be installed
2. Make sure you have allocated at least 2CPU and 4GB of RAM for Docker (via Docker settings)
<p align="center">
  <img width="600px" src="./docs/img/docker-resources.png">
</p>
3. Make sure that you enabled drive sharing for Docker
<p align="center">
  <img width="600px" src="./docs/img/docker-drive.png">
</p>
4. Create volumes:

```
docker volume create --name=pgdata

docker volume create --name=esdata
```

5. Start services:
```
docker-compose -f docker-compose-win.yml up -d
```
    
### 2. Building Zafira image(s) from sources
Alternatively, if you'd like to play around with Zafira codebase and/or contribute to our project you might need to spin up images built from source code vs ones pulled from Docker Registry. In order to do so:
1. Once you'll update the code make sure to re-package the `.jar` file by executing following command from `sources` directory:
    ```
    ./gradlew clean build
    ```
    Bundled archive can be found inside `/sources/zafira-ws/build/libs` directory.
2. Go to `docker-compose.yml` and make sure that instead of image reference target service you want to build from sources contains a `build` instruction:
    ```yml
    zafira:
      container_name: zfr_zafira_back_end
      # image: qaprosoft/zafira:4.1.69
      build: .
    ```
    It will tell Docker Compose to look for Dokerfile in current directory and delegate image contruction process to Docker vs pulling the image from Registry.
3. Run the following command to start Zafira:
    ```
    docker-compose up -d --build
    ```
    `--build` instruction will tell Docker Compose to force rebuild container rather than use the one residing on your filesystem (if this is not your first Zafira run).
    That's it, go ahead and give applicaion a try!
### Signing in to the application
1. Open in your browser IP address of deployed enviroment and navigate to http://localhost/app to sign in to application (Zafira app is binded to default port 80)
2. Use folowing credentials to log in: username: `qpsdemo`, password: `qpsdemo`.

### Application configuration and sensitive parameters
As you might have notice already, Zafira comes with a pre-defined configuration so you have all the features enabled out-of-the-box. However, if you'd like to use Zafira in production you'll definetely need to tweak a few things.
Complete list of Zafira runtime configuration parameters can be seen in `zafira-properties.env` file. Parameter names are self-explanatory, so it shouldn't be a problem to figure out what's what: file contains both application-specific, and settings that are common for many applications out there (such as datasource, message broker, cache configuration, etc).
However there are a few things to pay attention to:
1. **Application URL and REST API url**
    `ZAFIRA_WEB_HOST=` should point to host machine, where front-end application is deployed
    `ZAFIRA_API_HOST=` should point to host machine, where back-end (or ELB sitting in front of it) application is deployed
2. **Secrets: crypto salt and auth token**
    `AUTH_TOKEN_SECRET=` value is a signature verification key that is used to validate any discovered JWS digital signatures and thus should not be set to default one
    `CRYPTO_SALT=` value is by application for encryption of sensitive settings (such as passwords, integration settings, etc) and thus should not be set to default one

Please note, that we do not provide on-premise production deployment guide for Zafira. However, if that is something you might be interested in, go ahead and check out our own [QPS-Infra](https://www.qps-infra.io) at https://www.qps-infra.io.
Wanna jump straight to testing with Zafira skipping all of the deployment and maintenance hassle? Check out [Zebrunner](https://zebrunner.com), Cloud-native version of Zafira at https://zebrunner.com.

## Integration

Regardless of Zafira client language you have to generate **zafira_access_token** first, to do that navigate to **Username** > **My profile** in top menu and generate new access token.

![Alt text](docs/img/access_token.png "Access token")

#### Languages and frameworks supported
* [Java - TestNG guide](https://github.com/qaprosoft/zafira-testng)
* [Ruby - Cucumber](https://github.com/qaprosoft/zafira-ruby#cucumber-usage)
* [Ruby - RSpec](https://github.com/qaprosoft/zafira-ruby#rspec-usage)
* [C# - NUnit](https://github.com/qaprosoft/zafira-nunit)

## Documentation support
* [User manual](http://qaprosoft.github.io/zafira)
* [Demo project](https://github.com/qaprosoft/carina-demo)
* [Telegram channel](https://t.me/qps_zafira)
* [On-premise Zafira deployment with QPS-Infra](https://www.qps-infra.io)
* [Zebrunner: Cloud-native Zafira](https://zebrunner.com)

## License
Code - [Apache Software License v2.0](http://www.apache.org/licenses/LICENSE-2.0)

Documentation and Site - [Creative Commons Attribution 4.0 International License](http://creativecommons.org/licenses/by/4.0/deed.en_US)
