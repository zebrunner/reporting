![Alt text](./docs/img/zafira_logo.png "Zafira Logo")
==================

Zafira is Java-based web application that accumulates test automation results and increases the transparency of QA automation process. [D3JS](https://d3js.org)-based widgets with high flexibility of configuration allows to represent test results in different ways according to your companies processes.

![Integration](docs/img/integration.png)

## Features

#### Configurable dashboards based on [D3JS](https://d3js.org/)
![Alt text](docs/img/feature_dashboards.png "Dashboards")

#### Test runs management
![Alt text](docs/img/feature_testruns.png "Test runs management")

#### Real-time test results
![Alt text](docs/img/feature_testrun_results.png "Test results")

#### Comparison of test runs
![Alt text](docs/img/feature_testruns_comparison.png "Test runs comparison")

#### Live streaming and video recording of test sessions
![Alt text](docs/img/feature_live_streaming.png "Live streaming")

#### Test issues tracking
![Alt text](docs/img/feature_test_issues.png "Test issues tracking")

#### Integrations with multiple services
![Alt text](docs/img/feature_integrations.png "Integrations")

## Installation steps

#### Simple set with Docker

1. Install [Docker](https://docs.docker.com/engine/installation/) ([Engine](https://docs.docker.com/engine/installation/), [Compose](https://docs.docker.com/compose/install/))
2. Clone current repo and navigate to the repo root on your local machine
  ```
3. Deploy Zafira using `docker-compose` within the same folder

  ```Shell
  $ docker-compose up
  ```
To start Zafira in daemon mode, add '-d' argument:
  ```Shell
  $ docker-compose up -d
  ```  
4. Open in your browser IP address of deployed enviroment at port `8080`

  ```
  $ http://localhost:8080/zafira
  ```
5. Use next login/pass for access: `qpsdemo/qpsdemo`.

## Integration

Regardless of Zafira client language you have to generate **zafira_access_token** first, to do that navigate to **Username** > **My profile** in top menu and generate new access token.

![Alt text](docs/img/generate-token.png "Generate token")

#### Languages supported
* [Java - TestNG guide](https://github.com/qaprosoft/zafira-testng)
* [Ruby - Cucumber](https://github.com/qaprosoft/zafira-ruby#cucumber-usage)
* [Ruby - RSpec](https://github.com/qaprosoft/zafira-ruby#rspec-usage)
* [C# - NUnit](https://github.com/qaprosoft/zafira-nunit)


## License
Code - [Apache Software License v2.0](http://www.apache.org/licenses/LICENSE-2.0)

Documentation and Site - [Creative Commons Attribution 4.0 International License](http://creativecommons.org/licenses/by/4.0/deed.en_US)
