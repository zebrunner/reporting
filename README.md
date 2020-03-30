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
4. Deploy application using `docker-compose`
  ```
  $ docker-compose pull && docker-compose up -d
  ```
5. Open application in browser:
  ```
  $ http://<HOST_IP>/app
  ```
6. Login to the application with default credentials
  ```
  qpsdemo/qpsdemo
  ```

## Community and support
* [Telegram channel](https://t.me/zebrunner)
* [User manual](https://zebrunner.github.io/documentation)
* [On-premise Zebrunner deployment with QPS-Infra](https://www.qps-infra.io)
* [Zebrunner in cloud](https://zebrunner.com)

## License
Code - [Apache Software License v2.0](http://www.apache.org/licenses/LICENSE-2.0)
