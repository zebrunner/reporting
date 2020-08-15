#!/bin/bash

  setup() {
    # PREREQUISITES: valid values inside ZBR_PROTOCOL, ZBR_HOSTNAME and ZBR_PORT env vars!
    local url="$ZBR_PROTOCOL://$ZBR_HOSTNAME:$ZBR_PORT"

    cp configuration/_common/hosts.env.original configuration/_common/hosts.env
    sed -i "s#http://localhost:8081#${url}#g" configuration/_common/hosts.env

    cp configuration/reporting-service/variables.env.original configuration/reporting-service/variables.env
    sed -i "s#http://localhost:8081#${url}#g" configuration/reporting-service/variables.env

    cp configuration/reporting-ui/variables.env.original configuration/reporting-ui/variables.env
    sed -i "s#http://localhost:8081#${url}#g" configuration/reporting-ui/variables.env

    #TODO: parametrize postgres credentials later
    #configuration/postgres/variables.env
    #configuration/iam-db/variables.env
    #configuration/rabbitmq/variables

    echo "setup finished"
  }

  shutdown() {
    if [[ -f .disabled ]]; then
      exit 0
    fi

    docker-compose --env-file .env -f docker-compose.yml down -v

    rm configuration/_common/hosts.env
    rm configuration/reporting-service/variables.env
    rm configuration/reporting-ui/variables.env
    rm configuration/postgres/variables.env
    rm configuration/iam-db/variables.env
    rm configuration/rabbitmq/variables.env

    minio-storage/zebrunner.sh shutdown
  }

  start() {
    if [[ -f .disabled ]]; then
      exit 0
    fi

    # create infra network only if not exist
    docker network inspect infra >/dev/null 2>&1 || docker network create infra

    if [[ ! -f configuration/_common/hosts.env ]]; then
      cp configuration/_common/hosts.env.original configuration/_common/hosts.env
    fi

    if [[ ! -f configuration/reporting-service/variables.env ]]; then
      cp configuration/reporting-service/variables.env.original configuration/reporting-service/variables.env
    fi

    if [[ ! -f configuration/reporting-ui/variables.env ]]; then
      cp configuration/reporting-ui/variables.env.original configuration/reporting-ui/variables.env
    fi

    if [[ ! -f configuration/postgres/variables.env ]]; then
      cp configuration/postgres/variables.env.original configuration/postgres/variables.env
    fi

    if [[ ! -f configuration/iam-db/variables.env ]]; then
      cp configuration/iam-db/variables.env.original configuration/iam-db/variables.env
    fi

    if [[ ! -f configuration/rabbitmq/variables.env ]]; then
      cp configuration/rabbitmq/variables.env.original configuration/rabbitmq/variables.env
    fi


    minio-storage/zebrunner.sh start
    docker-compose --env-file .env -f docker-compose.yml up -d
  }

  stop() {
    if [[ -f .disabled ]]; then
      exit 0
    fi

    minio-storage/zebrunner.sh stop
    docker-compose --env-file .env -f docker-compose.yml stop
  }

  down() {
    if [[ -f .disabled ]]; then
      exit 0
    fi

    minio-storage/zebrunner.sh down
    docker-compose --env-file .env -f docker-compose.yml down
  }

  backup() {
    if [[ -f .disabled ]]; then
      exit 0
    fi

    minio-storage/zebrunner.sh backup

    cp configuration/_common/hosts.env configuration/_common/hosts.env.bak
    cp configuration/reporting-service/variables.env configuration/reporting-service/variables.env.bak
    cp configuration/reporting-ui/variables.env configuration/reporting-ui/variables.env.bak
    cp configuration/postgres/variables.env configuration/postgres/variables.env.bak
    cp configuration/iam-db/variables.env configuration/iam-db/variables.env.bak
    cp configuration/rabbitmq/variables.env configuration/rabbitmq/variables.env.bak

    source .env
    docker run --rm --volumes-from postgres -v $(pwd)/backup:/var/lib/postgresql/backup "qaprosoft/postgres:${TAG_POSTGRES}" tar -czvf /var/lib/postgresql/backup/postgres.tar.gz /var/lib/postgresql/data
    docker run --rm --volumes-from iam-db -v $(pwd)/backup:/var/lib/postgresql/backup "qaprosoft/postgres:${TAG_POSTGRES}" tar -czvf /var/lib/postgresql/backup/iam-db.tar.gz /var/lib/postgresql/data
    docker run --rm --volumes-from elasticsearch -v $(pwd)/backup:/usr/share/elasticsearch/backup "docker.elastic.co/elasticsearch/elasticsearch:${TAG_ELASTICSEARCH}" tar -czvf /usr/share/elasticsearch/backup/elasticsearch.tar.gz /usr/share/elasticsearch/data
    docker run --rm --volumes-from reporting-service -v $(pwd)/backup:/opt/backup "ubuntu" tar -czvf /opt/backup/reporting-service.tar.gz /opt/assets
    docker run --rm --volumes-from db-migration-tool -v $(pwd)/backup:/var/backup "zebrunner/data-migration-tool" tar -czvf /var/backup/db-migration-tool.tar.gz /var/migration-state
  }

  restore() {
    if [[ -f .disabled ]]; then
      exit 0
    fi

    stop
    minio-storage/zebrunner.sh restore

    cp configuration/_common/hosts.env.bak configuration/_common/hosts.env
    cp configuration/reporting-service/variables.env.bak configuration/reporting-service/variables.env
    cp configuration/reporting-ui/variables.env.bak configuration/reporting-ui/variables.env
    cp configuration/postgres/variables.env.bak configuration/postgres/variables.env
    cp configuration/iam-db/variables.env.bak configuration/iam-db/variables.env
    cp configuration/rabbitmq/variables.env.bak configuration/rabbitmq/variables.env

    source .env
    docker run --rm --volumes-from postgres -v $(pwd)/backup:/var/lib/postgresql/backup "qaprosoft/postgres:${TAG_POSTGRES}" bash -c "cd / && tar -xzvf /var/lib/postgresql/backup/postgres.tar.gz"
    docker run --rm --volumes-from iam-db -v $(pwd)/backup:/var/lib/postgresql/backup "qaprosoft/postgres:${TAG_POSTGRES}" bash -c "cd / && tar -xzvf /var/lib/postgresql/backup/iam-db.tar.gz"
    docker run --rm --volumes-from elasticsearch -v $(pwd)/backup:/usr/share/elasticsearch/backup "docker.elastic.co/elasticsearch/elasticsearch:${TAG_ELASTICSEARCH}" bash -c "cd / && tar -xzvf /usr/share/elasticsearch/backup/elasticsearch.tar.gz"
    docker run --rm --volumes-from reporting-service -v $(pwd)/backup:/opt/backup "ubuntu" bash -c "cd / && tar -xzvf /opt/backup/reporting-service.tar.gz"
    docker run --rm --volumes-from db-migration-tool -v $(pwd)/backup:/var/backup "zebrunner/data-migration-tool" bash -c "cd / && tar -xzvf /var/backup/db-migration-tool.tar.gz"

    down
  }

  echo_warning() {
    echo "
      WARNING! $1"

  }
  echo_telegram() {
    echo "
      For more help join telegram channel: https://t.me/zebrunner
      "
  }

  echo_help() {
    echo "
      Usage: ./zebrunner.sh [option]
      Flags:
          --help | -h    Print help
      Arguments:
      	  start          Start container
      	  stop           Stop and keep container
      	  restart        Restart container
      	  down           Stop and remove container
      	  shutdown       Stop and remove container, clear volumes
      	  backup         Backup container
      	  restore        Restore container"
      echo_telegram
      exit 0
  }

  # That's a full copy of set_global_settings method from qps-infra/zebrunner.sh. Make sure to sync code in case of any change in all places
  set_global_settings() {
    # Setup global settings: protocol, hostname and port. 

    local is_confirmed=0
    ZBR_PROTOCOL=http
    ZBR_HOSTNAME=$HOSTNAME
    ZBR_PORT=80

    while [[ $is_confirmed -eq 0 ]]; do
      read -p "PROTOCOL [$ZBR_PROTOCOL]: " local_protocol
      if [[ ! -z $local_protocol ]]; then
        ZBR_PROTOCOL=$local_protocol
      fi

      read -p "FQDN HOSTNAME [$ZBR_HOSTNAME]: " local_hostname
      if [[ ! -z $local_hostname ]]; then
        ZBR_HOSTNAME=$local_hostname
      fi

      read -p "PORT [$ZBR_PORT]: " local_port
      if [[ ! -z $local_port ]]; then
        ZBR_PORT=$local_port
      fi

      confirm "URL: $ZBR_PROTOCOL://$ZBR_HOSTNAME:$ZBR_PORT" "Continue?"
      is_confirmed=$?
    done

    export ZBR_PROTOCOL=$ZBR_PROTOCOL
    export ZBR_HOSTNAME=$ZBR_HOSTNAME
    export ZBR_PORT=$ZBR_PORT

  }

  confirm() {
    while true; do
      echo "$1"
      read -p "$2 [y/n]" yn
      case $yn in
      [y]*)
        return 1
        ;;
      [n]*)
        return 0
        ;;
      *)
        echo
        echo "Please answer y (yes) or n (no)."
        echo
        ;;
      esac
    done
  }


BASEDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd ${BASEDIR}

case "$1" in
    setup)
        if [[ ! -z $ZBR_PROTOCOL || ! -z $ZBR_HOSTNAME || ! -z $ZBR_PORT ]]; then
          setup
        else
          echo_warning "Setup procedure is supported only as part of Zebrunner Server (Community Edition)!"
          echo_telegram
        fi

#        echo WARNING! Increase vm.max_map_count=262144 appending it to /etc/sysctl.conf on Linux Ubuntu
#        echo your current value is `sysctl vm.max_map_count`

        ;;
    start)
	start
        ;;
    stop)
        stop
        ;;
    restart)
        down
        start
        ;;
    down)
        down
        ;;
    shutdown)
        shutdown
        ;;
    backup)
        backup
        ;;
    restore)
        restore
        ;;
    --help | -h)
        echo_help
        ;;
    *)
        echo "Invalid option detected: $1"
        echo_help
        exit 1
        ;;
esac

