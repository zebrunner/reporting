<p style="padding: 10px;" align="left">
  <img src="./docs/img/zebrunner_logo.png">
</p>
Zebrunner is a test automation management tool that accumulates and repseresents test results. It is designed to increase the transparency of automation, providing detailed reports with test logs, screenshots and video recordings of test sessions. Detailed reporting functionality reduces maintenance work for automation teams, allowing to identify application bugs and test implementation problems.

<p align="center">
  <img src="./docs/img/zebrunner_intro.png">
</p>

**Zebrunner Insights** - premium reporting features for your tests. See test results in real time - Zebrunner records interactive video sessions, publishes logs and screenshots. Track your pass rate, infrastructure usage, and automation team performance in one application.

**Zebrunner Engine** - cloud-based Selenium hub lets you run up to 1000 web, mobile, and API tests and reduce execution time and costs - you pay only for the period you test. We support popular platforms like Google Chrome, Firefox, Opera, Microsoft Edge, Internet Explorer, Android, iOS, and provide stability and quality of your execution.

**Zebrunner Guard** - your data and users will be safe with our enterprise-grade security and centralized administration. Due to the flexible permission management, you can limit access to certain information and keep the workflow steady.

## Core features
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

## Repositories structure

Zebrunner **server side** consists of the following modules:
- [`reporting-service`](https://github.com/zebrunner/reporting-service) application backend
- [`reporting-ui`](https://github.com/zebrunner/reporting-ui) application frontend


Zebrunner **client side** repositories:
- [`java-agent-core`](https://github.com/zebrunner/java-agent-core) agent library core (Java)
- [`java-agent-testng`](https://github.com/zebrunner/java-agent-testng) official TestNG agent
- [`java-agent-junit`](https://github.com/zebrunner/java-agent-junit) official JUnit agent
- [`java-agent-junit5`](https://github.com/zebrunner/java-agent-junit5) official JUnit 5 agent

## Installation steps
1. Install [Docker Engine](https://docs.docker.com/engine/installation) and [Docker Compose](https://docs.docker.com/compose/install)
2. Configure **vm.max_map_count** kernel setting using official [ES guide](https://www.elastic.co/guide/en/elasticsearch/reference/6.1/docker.html#docker-cli-run-prod-mode)
3. Clone this repo and navigate to the root folder
  ```
  git clone git@github.com:zebrunner/zebrunner.git && cd zebrunner
  ```
4. Run the application
  ```
  ./zebrunner-server.sh start
  ```
5. Login to the application with default credentials **qpsdemo**/**qpsdemo**:
  ```
  $ http://localhost/app
  ```

## Community and support
* [Telegram channel](https://t.me/zebrunner)
* [User manual](https://zebrunner.github.io/documentation)
* [On-premise Zebrunner deployment with QPS-Infra](https://www.qps-infra.io)
* [Zebrunner in cloud](https://zebrunner.com)

## License
Code - [Apache Software License v2.0](http://www.apache.org/licenses/LICENSE-2.0)
