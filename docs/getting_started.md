# Getting started

#### Prerequesties
The easiest way to deploy Zafira it to use [Docker](https://docs.docker.com/). You will find all the details for installation of [Docker Engine](https://docs.docker.com/install/) and [Docker Compose](https://docs.docker.com/compose/install/) in oficial documentation. Docker allows to install Zafira on Linux, Mac or Windows. Make sure that you have allocated at least 2 CPUs and 4Gb or RAM for Docker operations. Please verify that following ports are not binded:
* 5433  (PostgresDB)
* 15672 (RabbitMQ)
* 15674 (RabbitMQ)
* 5672  (RabbitMQ)
* 5601  (ELK)
* 9200  (ELK)
* 5044  (ELK)
* 8080  (Tomcat)
 
#### Zafira startup using Docker
<p align="center">
  <img width="650px" height="550px" src="./img/docker.png">
</p>
