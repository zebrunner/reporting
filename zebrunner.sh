#!/bin/bash

  setup() {
    if [[ $ZBR_INSTALLER -eq 1 ]]; then
      # Zebrunner CE installer
      url="$ZBR_PROTOCOL://$ZBR_HOSTNAME:$ZBR_PORT"
      ZBR_REPORTING_PORT=8081
    else
      # load default interactive installer settings
      source backup/settings.env.original

      # load ./backup/settings.env if exist to declare ZBR* vars from previous run!
      if [[ -f backup/settings.env ]]; then
        source backup/settings.env
      fi

      set_reporting_settings
      url="$ZBR_PROTOCOL://$ZBR_HOSTNAME:$ZBR_REPORTING_PORT"
    fi

    cp .env.original .env
    replace .env "REPORTING_PORT=8081" "REPORTING_PORT=$ZBR_REPORTING_PORT"

    cp configuration/_common/hosts.env.original configuration/_common/hosts.env
    replace configuration/_common/hosts.env "http://localhost:8081" "${url}"

    cp configuration/reporting-service/variables.env.original configuration/reporting-service/variables.env
    replace configuration/reporting-service/variables.env "http://localhost:8081" "${url}"

    cp configuration/reporting-ui/variables.env.original configuration/reporting-ui/variables.env
    replace configuration/reporting-ui/variables.env "http://localhost:8081" "${url}"

    cp configuration/_common/secrets.env.original configuration/_common/secrets.env
    replace configuration/_common/secrets.env "TOKEN_SIGNING_SECRET=AUwMLdWFBtUHVgvjFfMmAEadXqZ6HA4dKCiCmjgCXxaZ4ZO8od" "TOKEN_SIGNING_SECRET=${ZBR_TOKEN_SIGNING_SECRET}"
    replace configuration/_common/secrets.env "CRYPTO_SALT=TDkxalR4T3EySGI0T0YyMitScmkxWDlsUXlPV2R4OEZ1b2kyL1VJeFVHST0=" "CRYPTO_SALT=${ZBR_CRYPTO_SALT}"

    cp configuration/_common/s3.env.original configuration/_common/s3.env
    if [[ $ZBR_MINIO_ENABLED -eq 0 ]]; then
      # use case with AWS S3
      replace configuration/_common/s3.env "S3_REGION=us-east-1" "S3_REGION=${ZBR_STORAGE_REGION}"
      replace configuration/_common/s3.env "S3_ENDPOINT=http://minio:9000" "S3_ENDPOINT=${ZBR_STORAGE_ENDPOINT_PROTOCOL}://${ZBR_STORAGE_ENDPOINT_HOST}"
      replace configuration/_common/s3.env "S3_BUCKET=zebrunner" "S3_BUCKET=${ZBR_STORAGE_BUCKET}"
      replace configuration/_common/s3.env "S3_ACCESS_KEY_ID=zebrunner" "S3_ACCESS_KEY_ID=${ZBR_STORAGE_ACCESS_KEY}"
      replace configuration/_common/s3.env "S3_SECRET=J33dNyeTDj" "S3_SECRET=${ZBR_STORAGE_SECRET_KEY}"
    fi

    cp configuration/zebrunner-proxy/nginx.conf.original configuration/zebrunner-proxy/nginx.conf

    if [[ "$ZBR_PROTOCOL" == "https" ]] && [[ $ZBR_INSTALLER -ne 1 ]]; then
      # configure ssl only for independent setup!
      replace configuration/zebrunner-proxy/nginx.conf "listen 80" "listen 80 ssl"

      # uncomment default ssl settings
      replace configuration/zebrunner-proxy/nginx.conf "#        ssl_" "        ssl_"

      if [[ ! -f configuration/zebrunner-proxy/ssl/ssl.crt ]]; then
        echo "using self-signed certificate..."
        cp configuration/zebrunner-proxy/ssl/ssl.crt.original configuration/zebrunner-proxy/ssl/ssl.crt
      fi
      if [[ ! -f configuration/zebrunner-proxy/ssl/ssl.key ]]; then
        echo "using self-signed key..."
        cp configuration/zebrunner-proxy/ssl/ssl.key.original configuration/zebrunner-proxy/ssl/ssl.key
      fi

    fi

    if [[ $ZBR_MINIO_ENABLED -eq 0 ]]; then
      # use case with AWS S3
      replace configuration/zebrunner-proxy/nginx.conf "custom_secret_value" "${ZBR_STORAGE_AGENT_KEY}"
      replace configuration/zebrunner-proxy/nginx.conf "/zebrunner/" "/${ZBR_STORAGE_BUCKET}/"
      replace configuration/zebrunner-proxy/nginx.conf "http://minio:9000" "${ZBR_STORAGE_ENDPOINT_PROTOCOL}://${ZBR_STORAGE_ENDPOINT_HOST}"
    fi

    cp configuration/iam-service/variables.env.original configuration/iam-service/variables.env
    replace configuration/iam-service/variables.env "DATABASE_PASSWORD=iam-changeit" "DATABASE_PASSWORD=${ZBR_IAM_POSTGRES_PASSWORD}"

    cp configuration/iam-db/variables.env.original configuration/iam-db/variables.env
    replace configuration/iam-db/variables.env "POSTGRES_PASSWORD=iam-changeit" "POSTGRES_PASSWORD=${ZBR_IAM_POSTGRES_PASSWORD}"

    cp configuration/postgres/variables.env.original configuration/postgres/variables.env
    replace configuration/postgres/variables.env "POSTGRES_PASSWORD=db-changeit" "POSTGRES_PASSWORD=${ZBR_POSTGRES_PASSWORD}"
    replace configuration/reporting-service/variables.env "DATABASE_PASSWORD=db-changeit" "DATABASE_PASSWORD=${ZBR_POSTGRES_PASSWORD}"

    cp configuration/mail-service/variables.env.original configuration/mail-service/variables.env
    replace configuration/mail-service/variables.env "MAILING_HOST=smtp.gmail.com" "MAILING_HOST=${ZBR_SMTP_HOST}"
    replace configuration/mail-service/variables.env "MAILING_PORT=587" "MAILING_PORT=${ZBR_SMTP_PORT}"
    replace configuration/mail-service/variables.env "MAILING_SENDER_EMAIL=changeit" "MAILING_SENDER_EMAIL=${ZBR_SMTP_EMAIL}"
    replace configuration/mail-service/variables.env "MAILING_SENDER_NAME=changeit" "MAILING_SENDER_NAME=${ZBR_SMTP_USER}"
    replace configuration/mail-service/variables.env "MAILING_USERNAME=changeit" "MAILING_USERNAME=${ZBR_SMTP_USER}"
    replace configuration/mail-service/variables.env "MAILING_PASSWORD=changeit" "MAILING_PASSWORD=${ZBR_SMTP_PASSWORD}"

    cp configuration/rabbitmq/variables.env.original configuration/rabbitmq/variables.env
    replace configuration/rabbitmq/variables.env "RABBITMQ_DEFAULT_USER=rabbitmq-user" "RABBITMQ_DEFAULT_USER=${ZBR_RABBITMQ_USER}"
    replace configuration/rabbitmq/variables.env "RABBITMQ_DEFAULT_PASS=rabbitmq-password" "RABBITMQ_DEFAULT_PASS=${ZBR_RABBITMQ_PASSWORD}"
    cp configuration/logstash/logstash.conf.original configuration/logstash/logstash.conf
    replace configuration/logstash/logstash.conf "rabbitmq-user" "${ZBR_RABBITMQ_USER}"
    replace configuration/logstash/logstash.conf "rabbitmq-password" "${ZBR_RABBITMQ_PASSWORD}"
    cp configuration/_common/rabbitmq.env.original configuration/_common/rabbitmq.env
    replace configuration/_common/rabbitmq.env "rabbitmq-user" "${ZBR_RABBITMQ_USER}"
    replace configuration/_common/rabbitmq.env "rabbitmq-password" "${ZBR_RABBITMQ_PASSWORD}"
    cp configuration/rabbitmq/001-general-definition.json.original configuration/rabbitmq/definitions/001-general-definition.json
    replace configuration/rabbitmq/definitions/001-general-definition.json "rabbitmq-user" "${ZBR_RABBITMQ_USER}"
    replace configuration/rabbitmq/definitions/001-general-definition.json "rabbitmq-password" "${ZBR_RABBITMQ_PASSWORD}"

    cp configuration/redis/redis.conf.original configuration/redis/redis.conf
    replace configuration/redis/redis.conf "requirepass MdXVvJgDdz9Hnau7" "requirepass ${ZBR_REDIS_PASSWORD}"
    replace configuration/reporting-service/variables.env "REDIS_PASSWORD=MdXVvJgDdz9Hnau7" "REDIS_PASSWORD=${ZBR_REDIS_PASSWORD}"

    minio-storage/zebrunner.sh setup

    # export all ZBR* variables to save user input
    export_settings

    if [[ "$ZBR_PROTOCOL" == "https" ]] && [[ $ZBR_INSTALLER -ne 1 ]]; then
      #warn about ssl only in case of independent setup
      echo_warning "Replace self-signed ssl.crt and ssl.key in ./configuration/zebrunner-proxy/ssl/ onto valid ones!"
    fi

  }

  shutdown() {
    if [[ -f .disabled ]]; then
      rm -f .disabled
      exit 0 #no need to proceed as nothing was configured
    fi

    if [[ ! -f .env ]]; then
      echo_warning "Unable to erase as nothing is configured!"
      exit 0 #no need to proceed as nothing was configured
    fi


    if [[ -z ${SHUTDOWN_CONFIRMED} ]] || [[ ${SHUTDOWN_CONFIRMED} -ne 1 ]]; then
      # ask about confirmation if it is not confirmed in scope of CE
      echo_warning "Shutdown will erase all settings and data for \"${BASEDIR}\"!"
      confirm "" "      Do you want to continue?" "n"
      if [[ $? -eq 0 ]]; then
        exit
      fi
    fi


    docker compose down -v

    rm -f .env
    rm -f backup/settings.env
    rm -f configuration/_common/hosts.env
    rm -f configuration/_common/secrets.env
    rm -f configuration/_common/s3.env
    rm -f configuration/zebrunner-proxy/nginx.conf
    rm -f configuration/iam-service/variables.env
    rm -f configuration/iam-db/variables.env
    rm -f configuration/postgres/variables.env
    rm -f configuration/mail-service/variables.env
    rm -f configuration/rabbitmq/variables.env
    rm -f configuration/logstash/logstash.conf
    rm -f configuration/_common/rabbitmq.env
    rm -f configuration/rabbitmq/definitions/001-general-definition.json
    rm -f configuration/redis/redis.conf
    rm -f configuration/reporting-service/variables.env
    rm -f configuration/reporting-ui/variables.env

    rm -f configuration/zebrunner-proxy/ssl/ssl.crt
    rm -f configuration/zebrunner-proxy/ssl/ssl.key

    minio-storage/zebrunner.sh shutdown
  }

  start() {
    if [[ -f .disabled ]]; then
      exit 0
    fi

    if [ ! -f .env ]; then
      # need proceed with setup steps in advance!
      setup
      exit -1
    fi

    # create infra network only if not exist
    docker network inspect infra >/dev/null 2>&1 || docker network create infra

    if [[ ! -f configuration/_common/hosts.env ]]; then
      cp configuration/_common/hosts.env.original configuration/_common/hosts.env
    fi

    if [[ ! -f configuration/_common/secrets.env ]]; then
      cp configuration/_common/secrets.env.original configuration/_common/secrets.env
    fi

    if [[ ! -f configuration/_common/s3.env ]]; then
      cp configuration/_common/s3.env.original configuration/_common/s3.env
    fi

    if [[ ! -f configuration/zebrunner-proxy/nginx.conf ]]; then
      cp configuration/zebrunner-proxy/nginx.conf.original configuration/zebrunner-proxy/nginx.conf
    fi

    if [[ ! -f configuration/iam-service/variables.env ]]; then
      cp configuration/iam-service/variables.env.original configuration/iam-service/variables.env
    fi

    if [[ ! -f configuration/iam-db/variables.env ]]; then
      cp configuration/iam-db/variables.env.original configuration/iam-db/variables.env
    fi

    if [[ ! -f configuration/postgres/variables.env ]]; then
      cp configuration/postgres/variables.env.original configuration/postgres/variables.env
    fi

    if [[ ! -f configuration/mail-service/variables.env ]]; then
      cp configuration/mail-service/variables.env.original configuration/mail-service/variables.env
    fi

    if [[ ! -f configuration/rabbitmq/variables.env ]]; then
      cp configuration/rabbitmq/variables.env.original configuration/rabbitmq/variables.env
    fi

    if [[ ! -f configuration/logstash/logstash.conf ]]; then
      cp configuration/logstash/logstash.conf.original configuration/logstash/logstash.conf
    fi

    if [[ ! -f configuration/_common/rabbitmq.env ]]; then
      cp configuration/_common/rabbitmq.env.original configuration/_common/rabbitmq.env
    fi

    if [[ ! -f configuration/rabbitmq/definitions/001-general-definition.json ]]; then
      cp configuration/rabbitmq/001-general-definition.json.original configuration/rabbitmq/definitions/001-general-definition.json
    fi

    if [[ ! -f configuration/redis/redis.conf ]]; then
      cp configuration/redis/redis.conf.original configuration/redis/redis.conf
    fi

    if [[ ! -f configuration/reporting-service/variables.env ]]; then
      cp configuration/reporting-service/variables.env.original configuration/reporting-service/variables.env
    fi

    if [[ ! -f configuration/reporting-ui/variables.env ]]; then
      cp configuration/reporting-ui/variables.env.original configuration/reporting-ui/variables.env
    fi

    minio-storage/zebrunner.sh start
    docker compose up -d
  }

  stop() {
    if [[ -f .disabled ]]; then
      exit 0
    fi

    minio-storage/zebrunner.sh stop
    docker compose stop
  }

  down() {
    if [[ -f .disabled ]]; then
      exit 0
    fi

    minio-storage/zebrunner.sh down
    docker compose down
  }

  backup() {
    if [[ -f .disabled ]]; then
      exit 0
    fi

    minio-storage/zebrunner.sh backup

    cp .env .env.bak
    cp backup/settings.env backup/settings.env.bak

    cp configuration/_common/hosts.env configuration/_common/hosts.env.bak
    cp configuration/_common/secrets.env configuration/_common/secrets.env.bak
    cp configuration/_common/s3.env configuration/_common/s3.env.bak
    cp configuration/zebrunner-proxy/nginx.conf configuration/zebrunner-proxy/nginx.conf.bak
    cp configuration/iam-service/variables.env configuration/iam-service/variables.env.bak
    cp configuration/iam-db/variables.env configuration/iam-db/variables.env.bak
    cp configuration/postgres/variables.env configuration/postgres/variables.env.bak
    cp configuration/mail-service/variables.env configuration/mail-service/variables.env.bak
    cp configuration/rabbitmq/variables.env configuration/rabbitmq/variables.env.bak
    cp configuration/logstash/logstash.conf configuration/logstash/logstash.conf.bak
    cp configuration/_common/rabbitmq.env configuration/_common/rabbitmq.env.bak
    cp configuration/rabbitmq/definitions/001-general-definition.json configuration/rabbitmq/001-general-definition.json.bak
    cp configuration/redis/redis.conf configuration/redis/redis.conf.bak
    cp configuration/reporting-service/variables.env configuration/reporting-service/variables.env.bak
    cp configuration/reporting-ui/variables.env configuration/reporting-ui/variables.env.bak

    docker run --rm --volumes-from postgres -v "$(pwd)"/backup:/var/backup "ubuntu" tar -czvf /var/backup/postgres.tar.gz /var/lib/postgresql/data
    docker run --rm --volumes-from iam-db -v "$(pwd)"/backup:/var/backup "ubuntu" tar -czvf /var/backup/iam-db.tar.gz /var/lib/postgresql/data
    docker run --rm --volumes-from elasticsearch -v "$(pwd)"/backup:/var/backup "ubuntu" tar -czvf /var/backup/elasticsearch.tar.gz /usr/share/elasticsearch/data
    docker run --rm --volumes-from reporting-service -v "$(pwd)"/backup:/var/backup "ubuntu" tar -czvf /var/backup/reporting-service.tar.gz /opt/assets
  }

  restore() {
    if [[ -f .disabled ]]; then
      exit 0
    fi

    stop
    minio-storage/zebrunner.sh restore

    cp .env.bak .env
    cp backup/settings.env.bak backup/settings.env
    cp configuration/_common/hosts.env.bak configuration/_common/hosts.env
    cp configuration/_common/secrets.env.bak configuration/_common/secrets.env
    cp configuration/_common/s3.env.bak configuration/_common/s3.env
    cp configuration/zebrunner-proxy/nginx.conf.bak configuration/zebrunner-proxy/nginx.conf
    cp configuration/iam-service/variables.env.bak configuration/iam-service/variables.env
    cp configuration/iam-db/variables.env.bak configuration/iam-db/variables.env
    cp configuration/postgres/variables.env.bak configuration/postgres/variables.env
    cp configuration/mail-service/variables.env.bak configuration/mail-service/variables.env
    cp configuration/rabbitmq/variables.env.bak configuration/rabbitmq/variables.env
    cp configuration/logstash/logstash.conf.bak configuration/logstash/logstash.conf
    cp configuration/_common/rabbitmq.env.bak configuration/_common/rabbitmq.env
    cp configuration/rabbitmq/001-general-definition.json.bak configuration/rabbitmq/definitions/001-general-definition.json
    cp configuration/redis/redis.conf.bak configuration/redis/redis.conf
    cp configuration/reporting-service/variables.env.bak configuration/reporting-service/variables.env
    cp configuration/reporting-ui/variables.env.bak configuration/reporting-ui/variables.env

    docker run --rm --volumes-from postgres -v "$(pwd)"/backup:/var/backup "ubuntu" bash -c "cd / && tar -xzvf /var/backup/postgres.tar.gz"
    docker run --rm --volumes-from iam-db -v "$(pwd)"/backup:/var/backup "ubuntu" bash -c "cd / && tar -xzvf /var/backup/iam-db.tar.gz"
    docker run --rm --volumes-from elasticsearch -v "$(pwd)"/backup:/var/backup "ubuntu" bash -c "cd / && tar -xzvf /var/backup/elasticsearch.tar.gz"
    docker run --rm --volumes-from reporting-service -v "$(pwd)"/backup:/var/backup "ubuntu" bash -c "cd / && tar -xzvf /var/backup/reporting-service.tar.gz"
    down
  }

  version() {
    if [[ -f .disabled ]]; then
      exit 0
    fi

    # shellcheck disable=SC1091
    source .env
    echo "reporting: ${TAG_REPORTING_SERVICE}"
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
      	  restore        Restore container
      	  version        Version of container"
      echo_telegram
      exit 0
  }

  replace() {
    #TODO: https://github.com/zebrunner/zebrunner/issues/328 organize debug logging for setup/replace
    file=$1
    #echo "file: $file"
    content=$(<"$file") # read the file's content into
    #echo "content: $content"

    old=$2
    #echo "old: $old"

    new=$3
    #echo "new: $new"
    content=${content//"$old"/$new}

    #echo "content: $content"
    printf '%s' "$content" >"$file"    # write new content to disk
  }


BASEDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${BASEDIR}" || exit

# shellcheck disable=SC1091
source patch/utility.sh
source patch/settings.sh

case "$1" in
    setup)
        setup
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
    version)
        version
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

