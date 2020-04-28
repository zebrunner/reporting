#!/bin/bash

BASEDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${BASEDIR}

case "$1" in
    start)
        echo "Starting Zebrunner..."
        docker-compose pull && docker-compose up -d
        ;;
    stop)
        echo "Stopping Zebrunner..."
        docker-compose stop
        ;;
    shutdown)
        echo "Shutting down Zebrunner and performing cleanup..."
        docker-compose down -v
        ;;
    *)
        echo "Usage: ./zebrunner-server start|stop|shutdown"
        exit 1
        ;;
esac
