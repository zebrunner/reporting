#!/bin/bash

  setup() {
    # PREREQUISITES: valid values inside ZBR_PROTOCOL, ZBR_HOSTNAME and ZBR_PORT env vars!
    local url="$ZBR_PROTOCOL://$ZBR_HOSTNAME:$ZBR_PORT"

    cp ${BASEDIR}/configuration/_common/hosts.env.original ${BASEDIR}/configuration/_common/hosts.env
    sed -i "s#http://localhost:8081#${url}#g" ${BASEDIR}/configuration/_common/hosts.env

    cp ${BASEDIR}/configuration/reporting-service/variables.env.original ${BASEDIR}/configuration/reporting-service/variables.env
    sed -i "s#http://localhost:8081#${url}#g" ${BASEDIR}/configuration/reporting-service/variables.env

    cp ${BASEDIR}/configuration/reporting-ui/variables.env.original ${BASEDIR}/configuration/reporting-ui/variables.env
    sed -i "s#http://localhost:8081#${url}#g" ${BASEDIR}/configuration/reporting-ui/variables.env

    #TODO: parametrize postgres credentials later
    #configuration/postgres/variables.env.original
    #configuration/iam-db/variables.env
    #configuration/rabbitmq/variables

    echo "setup finished"
  }

  shutdown() {
    if [[ -f ${BASEDIR}/.disabled ]]; then
      exit 0
    fi

    docker-compose --env-file ${BASEDIR}/.env -f ${BASEDIR}/docker-compose.yml down -v

    rm ${BASEDIR}/configuration/_common/hosts.env
    rm ${BASEDIR}/configuration/reporting-service/variables.env
    rm ${BASEDIR}/configuration/reporting-ui/variables.env
    rm ${BASEDIR}/configuration/postgres/variables.env
    rm ${BASEDIR}/configuration/iam-db/variables.env
    rm ${BASEDIR}/configuration/rabbitmq/variables.env

    minio-storage/zebrunner.sh shutdown
  }

  start() {
    if [[ -f ${BASEDIR}/.disabled ]]; then
      exit 0
    fi

    # create infra network only if not exist
    docker network inspect infra >/dev/null 2>&1 || docker network create infra

    if [[ ! -f ${BASEDIR}/configuration/_common/hosts.env ]]; then
      cp ${BASEDIR}/configuration/_common/hosts.env.original ${BASEDIR}/configuration/_common/hosts.env
    fi

    if [[ ! -f ${BASEDIR}/configuration/reporting-service/variables.env ]]; then
      cp ${BASEDIR}/configuration/reporting-service/variables.env.original ${BASEDIR}/configuration/reporting-service/variables.env
    fi

    if [[ ! -f ${BASEDIR}/configuration/reporting-ui/variables.env ]]; then
      cp ${BASEDIR}/configuration/reporting-ui/variables.env.original ${BASEDIR}/configuration/reporting-ui/variables.env
    fi

    if [[ ! -f ${BASEDIR}/configuration/postgres/variables.env ]]; then
      cp ${BASEDIR}/configuration/postgres/variables.env.original ${BASEDIR}/configuration/postgres/variables.env
    fi

    if [[ ! -f ${BASEDIR}/configuration/iam-db/variables.env ]]; then
      cp ${BASEDIR}/configuration/iam-db/variables.env.original ${BASEDIR}/configuration/iam-db/variables.env
    fi

    if [[ ! -f ${BASEDIR}/configuration/rabbitmq/variables.env ]]; then
      cp ${BASEDIR}/configuration/rabbitmq/variables.env.original ${BASEDIR}/configuration/rabbitmq/variables.env
    fi


    minio-storage/zebrunner.sh start
    docker-compose --env-file ${BASEDIR}/.env -f ${BASEDIR}/docker-compose.yml up -d
  }

  stop() {
    if [[ -f ${BASEDIR}/.disabled ]]; then
      exit 0
    fi

    minio-storage/zebrunner.sh stop
    docker-compose --env-file ${BASEDIR}/.env -f ${BASEDIR}/docker-compose.yml stop
  }

  down() {
    if [[ -f ${BASEDIR}/.disabled ]]; then
      exit 0
    fi

    minio-storage/zebrunner.sh down
    docker-compose --env-file ${BASEDIR}/.env -f ${BASEDIR}/docker-compose.yml down
  }

  backup() {
    if [[ -f ${BASEDIR}/.disabled ]]; then
      exit 0
    fi

    minio-storage/zebrunner.sh backup

    cp ${BASEDIR}/configuration/_common/hosts.env ${BASEDIR}/configuration/_common/hosts.env.bak
    cp ${BASEDIR}/configuration/reporting-service/variables.env ${BASEDIR}/configuration/reporting-service/variables.env.bak
    cp ${BASEDIR}/configuration/reporting-ui/variables.env ${BASEDIR}/configuration/reporting-ui/variables.env.bak
    cp ${BASEDIR}/configuration/postgres/variables.env ${BASEDIR}/configuration/postgres/variables.env.bak
    cp ${BASEDIR}/configuration/iam-db/variables.env ${BASEDIR}/configuration/iam-db/variables.env.bak
    cp ${BASEDIR}/configuration/rabbitmq/variables.env ${BASEDIR}/configuration/rabbitmq/variables.env.bak

    echo "TODO: implement backup for postgres DB content"
  }

  restore() {
    if [[ -f ${BASEDIR}/.disabled ]]; then
      exit 0
    fi

    minio-storage/zebrunner.sh restore

    cp ${BASEDIR}/configuration/_common/hosts.env.bak ${BASEDIR}/configuration/_common/hosts.env
    cp ${BASEDIR}/configuration/reporting-service/variables.env.bak ${BASEDIR}/configuration/reporting-service/variables.env
    cp ${BASEDIR}/configuration/reporting-ui/variables.env.bak ${BASEDIR}/configuration/reporting-ui/variables.env
    cp ${BASEDIR}/configuration/postgres/variables.env.bak ${BASEDIR}/configuration/postgres/variables.env
    cp ${BASEDIR}/configuration/iam-db/variables.env.bak ${BASEDIR}/configuration/iam-db/variables.env
    cp ${BASEDIR}/configuration/rabbitmq/variables.env.bak ${BASEDIR}/configuration/rabbitmq/variables.env

    echo "TODO: implement restore for postgres DB content"
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

