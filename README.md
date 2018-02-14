## Zafira QA Automation Reporting

Web application that collects test results from QA automation.

## Installation steps

#### Simple set with Docker

1. Install [Docker](https://docs.docker.com/engine/installation/) ([Engine](https://docs.docker.com/engine/installation/), [Compose](https://docs.docker.com/compose/install/))
2. Download [Example of compose descriptor](https://raw.githubusercontent.com/qaprosoft/zafira/master/docker-compose.yml) to any folder

  ```Shell
  $ curl https://raw.githubusercontent.com/qaprosoft/zafira/master/docker-compose.yml -o docker-compose.yml
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
  $ http://localgost:8080/zafira
  ```
5. Use next login/pass for access: `qpsdemo/qpsdemo`.
