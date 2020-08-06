#!/bin/bash

  start() {
    # create infra network only if not exist
    docker network inspect infra >/dev/null 2>&1 || docker network create infra

    docker-compose --env-file ${BASEDIR}/.env -f ${BASEDIR}/docker-compose.yml up -d
  }

  stop() {
    docker-compose --env-file ${BASEDIR}/.env -f ${BASEDIR}/docker-compose.yml stop
  }

  down() {
    docker-compose --env-file ${BASEDIR}/.env -f ${BASEDIR}/docker-compose.yml down
  }

  shutdown() {
    docker-compose --env-file ${BASEDIR}/.env -f ${BASEDIR}/docker-compose.yml down -v
    echo "TODO: think about backup generation during shutdown."
  }

  backup() {
    echo "TODO: implement logic"
  }

  restore() {
    echo "TODO: implement logic"
  }


  set_host() {
    echo "Specify fully qualified domain name or ip address"
    HOST_NAME=""
    local IS_CONFIRMED=0
    while [[ -z $HOST_NAME || $HOST_NAME == "localhost" || $HOST_NAME == "127.0.0.1" || $IS_CONFIRMED -eq 0 ]]; do
      read -p "HOST_NAME: " HOST_NAME
      if [[ -z $HOST_NAME || $HOST_NAME == "localhost" || $HOST_NAME == "127.0.0.1" ]]; then
        echo "Unable to proceed with HOST_NAME=\"${HOST_NAME}\"!"
      else
        confirm "Continue?"
        IS_CONFIRMED=$?
      fi
    done

  }

  confirm() {
    while true; do
      read -p "$1 [y/n]" yn
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
      	  restore        Restore container
      For more help join telegram channel https://t.me/qps_infra"
      exit 0
  }


BASEDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd ${BASEDIR}

case "$1" in
    setup)
        docker network inspect infra >/dev/null 2>&1 || docker network create infra

        set_host
	echo

#        echo WARNING! Increase vm.max_map_count=262144 appending it to /etc/sysctl.conf on Linux Ubuntu
#        echo your current value is `sysctl vm.max_map_count`

#        echo Setup finished successfully using $HOST_NAME hostname.
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

