# Getting started

#### Prerequesties
The easiest way to deploy Zafira it to use [Docker](https://docs.docker.com/). You will find all the details for installation of [Docker Engine](https://docs.docker.com/install/) and [Docker Compose](https://docs.docker.com/compose/install/) in oficial documentation. Docker allows to install Zafira on Linux, Mac or Windows. Make sure that you have allocated at least 2 CPUs and 4Gb or RAM for Docker operations.

<p align="center">
  <img width="650px" height="550px" src="../img/docker.png">
</p>

Before running Zafira, verify that following ports are not binded:

* 8080  (Tomcat)
* 5433  (PostgresDB)
* 15672 (RabbitMQ)
* 15674 (RabbitMQ)
* 5672  (RabbitMQ)
* 5601  (ELK)
* 9200  (ELK)
* 5044  (ELK)
 
#### Startup in Docker
* Clone Zafira repo:
```
$ git clone git@github.com:qaprosoft/zafira.git
```

* If you are planning to access Zafira remotely you will need to modify **docker-compose.yml** specifying appropriate IP address of your host:
```
environment:
  - ZAFIRA_URL=http://localhost:8080
```

* Start Zafira:
```
$ docker-compose up -d
```

* Verify deployment by running:
```
$ docker ps

CONTAINER ID        IMAGE                               COMMAND    
c0e8e371de0f        qaprosoft/zafira:latest             "/bin/sh -c /entrypo…"
a207d5718996        sebp/elk:630                        "/usr/local/bin/star…" 
5ccaadeeafe7        qaprosoft/rabbitmq:latest           "docker-entrypoint.s…"
922b75b2849a        selenium/standalone-chrome:latest   "/opt/bin/entry_poin…"
e8a2d32590ed        qaprosoft/postgres:9.6              "docker-entrypoint.s…"
```

* Open Zafira in your browser:
```
http://localhost:8080/zafira
```

* Use default credentials to login:
```
qpsdemo/qpsdemo
```

<p align="center">
  <img src="../img/login.png">
</p>
