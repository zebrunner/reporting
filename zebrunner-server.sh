#!/bin/bash

BASEDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd ${BASEDIR}

case "$1" in
    start)
        echo "Starting Zebrunner..."
        docker network inspect infra >/dev/null 2>&1 || docker network create infra
        docker-compose pull && docker-compose up -d
        for i in {0..8}; do
        echo "Starting Zebrunner..."
        sleep 20s
        done
        docker restart logstash >/dev/null 2>&1
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
