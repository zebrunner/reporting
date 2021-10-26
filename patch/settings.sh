#!/bin/bash

set_reporting_settings() {

  if [[ $ZBR_INSTALLER -eq 0 ]]; then
    echo "Zebrunner Reporting Settings"
    local is_confirmed=0
    if [[ -z $ZBR_HOSTNAME ]]; then
      ZBR_HOSTNAME=`curl -s ifconfig.me`
    fi

    while [[ $is_confirmed -eq 0 ]]; do
      read -r -p "Protocol [$ZBR_PROTOCOL]: " local_protocol
      if [[ ! -z $local_protocol ]]; then
        ZBR_PROTOCOL=$local_protocol
      fi

      read -r -p "Fully qualified domain name (ip) [$ZBR_HOSTNAME]: " local_hostname
      if [[ ! -z $local_hostname ]]; then
        ZBR_HOSTNAME=$local_hostname
      fi

      read -r -p "Port [$ZBR_REPORTING_PORT]: " local_port
      if [[ ! -z $local_port ]]; then
        ZBR_REPORTING_PORT=$local_port
      fi

      confirm "Zebrunner Reporting URL: $ZBR_PROTOCOL://$ZBR_HOSTNAME:$ZBR_REPORTING_PORT" "Continue?" "y"
      is_confirmed=$?
    done

    export ZBR_PROTOCOL=$ZBR_PROTOCOL
    export ZBR_HOSTNAME=$ZBR_HOSTNAME
    export ZBR_REPORTING_PORT=$ZBR_REPORTING_PORT
  fi

  # Collect reporting settings
  ## Crypto token and salt
  if [[ -z $ZBR_TOKEN_SIGNING_SECRET ]]; then
    # generate random value as it is first setup
    ZBR_TOKEN_SIGNING_SECRET=$(random_string)
  fi
  if [[ -z $ZBR_CRYPTO_SALT ]]; then
    # generate random value as it is first setup
    ZBR_CRYPTO_SALT=$(random_string)
  fi
  export ZBR_TOKEN_SIGNING_SECRET=$ZBR_TOKEN_SIGNING_SECRET
  export ZBR_CRYPTO_SALT=$ZBR_CRYPTO_SALT

  ## iam-service posgtres
  if [[ -z $ZBR_IAM_POSTGRES_PASSWORD ]]; then
    # generate random value as it is first setup
    ZBR_IAM_POSTGRES_PASSWORD=$(random_string)
  fi
  export ZBR_IAM_POSTGRES_PASSWORD=$ZBR_IAM_POSTGRES_PASSWORD

  ## reporting posgtres instance
  if [[ -z $ZBR_POSTGRES_PASSWORD ]]; then
    # generate random value as it is first setup
    ZBR_POSTGRES_PASSWORD=$(random_string)
  fi
  export ZBR_POSTGRES_PASSWORD=$ZBR_POSTGRES_PASSWORD


  echo
  confirm "Use AWS S3 bucket for storing test artifacts (logs, video, screenshots etc)? Embedded Minio Storage can be configured if you don't have Amazon account." "Use?" "$ZBR_AWS_S3_ENABLED"
  if [[ $? -eq 1 ]]; then
    ZBR_AWS_S3_ENABLED=1
    ZBR_MINIO_ENABLED=0
    set_aws_storage_settings
  else
    ZBR_MINIO_ENABLED=1
  fi

  ## email-service (smtp)
  echo
  confirm "Use SMTP for emailing test results?" "Use?" "$ZBR_SMTP_ENABLED"
  if [[ $? -eq 0 ]]; then
    ZBR_SMTP_ENABLED=0
  else
    ZBR_SMTP_ENABLED=1
    local is_confirmed=0
    while [[ $is_confirmed -eq 0 ]]; do
      read -r -p "Host [$ZBR_SMTP_HOST]: " local_smtp_host
      if [[ ! -z $local_smtp_host ]]; then
        ZBR_SMTP_HOST=$local_smtp_host
      fi

      read -r -p "Port [$ZBR_SMTP_PORT]: " local_smtp_port
      if [[ ! -z $local_smtp_port ]]; then
        ZBR_SMTP_PORT=$local_smtp_port
      fi

      read -r -p "Sender email [$ZBR_SMTP_EMAIL]: " local_smtp_email
      if [[ ! -z $local_smtp_email ]]; then
        ZBR_SMTP_EMAIL=$local_smtp_email
      fi

      read -r -p "User [$ZBR_SMTP_USER]: " local_smtp_user
      if [[ ! -z $local_smtp_user ]]; then
        ZBR_SMTP_USER=$local_smtp_user
      fi

      read -r -p "Password [$ZBR_SMTP_PASSWORD]: " local_smtp_password
      if [[ ! -z $local_smtp_password ]]; then
        ZBR_SMTP_PASSWORD=$local_smtp_password
      fi

      echo
      echo "SMTP Integration"
      echo "host=$ZBR_SMTP_HOST:$ZBR_SMTP_PORT"
      echo "email=$ZBR_SMTP_EMAIL"
      echo "user=$ZBR_SMTP_USER"
      echo "password=$ZBR_SMTP_PASSWORD"
      confirm "" "Continue?" "y"
      is_confirmed=$?
    done
  fi

  export ZBR_SMTP_HOST=$ZBR_SMTP_HOST
  export ZBR_SMTP_PORT=$ZBR_SMTP_PORT
  export ZBR_SMTP_EMAIL=$ZBR_SMTP_EMAIL
  export ZBR_SMTP_USER=$ZBR_SMTP_USER
  export ZBR_SMTP_PASSWORD=$ZBR_SMTP_PASSWORD


  ## reporting rabbitmq
  if [[ -z $ZBR_RABBITMQ_PASSWORD ]]; then
    # generate random value as it is first setup
    ZBR_RABBITMQ_PASSWORD=$(random_string)
  fi
  export ZBR_RABBITMQ_USER=$ZBR_RABBITMQ_USER
  export ZBR_RABBITMQ_PASSWORD=$ZBR_RABBITMQ_PASSWORD

  ## reporting redis
  if [[ -z $ZBR_REDIS_PASSWORD ]]; then
    # generate random value as it is first setup
    ZBR_REDIS_PASSWORD=$(random_string)
  fi
  export ZBR_REDIS_PASSWORD=$ZBR_REDIS_PASSWORD

}

set_aws_storage_settings() {
  ## AWS S3 storage
  local is_confirmed=0
  #TODO: provide a link to documentation howto create valid S3 bucket
  echo
  echo "AWS S3 storage"
  while [[ $is_confirmed -eq 0 ]]; do
    read -r -p "Region [$ZBR_STORAGE_REGION]: " local_region
    if [[ ! -z $local_region ]]; then
      ZBR_STORAGE_REGION=$local_region
    fi

    ZBR_STORAGE_ENDPOINT_PROTOCOL="https"
    ZBR_STORAGE_ENDPOINT_HOST="s3.${ZBR_STORAGE_REGION}.amazonaws.com:443"

    read -r -p "Bucket [$ZBR_STORAGE_BUCKET]: " local_bucket
    if [[ ! -z $local_bucket ]]; then
      ZBR_STORAGE_BUCKET=$local_bucket
    fi

    read -r -p "Access key [$ZBR_STORAGE_ACCESS_KEY]: " local_access_key
    if [[ ! -z $local_access_key ]]; then
      ZBR_STORAGE_ACCESS_KEY=$local_access_key
    fi

    read -r -p "Secret key [$ZBR_STORAGE_SECRET_KEY]: " local_secret_key
    if [[ ! -z $local_secret_key ]]; then
      ZBR_STORAGE_SECRET_KEY=$local_secret_key
    fi

    if [[ $ZBR_REPORTING_ENABLED -eq 0 ]]; then
      export ZBR_MINIO_ENABLED=0
      read -r -p "[Optional] Tenant [$ZBR_STORAGE_TENANT]: " local_value
      if [[ ! -z $local_value ]]; then
        ZBR_STORAGE_TENANT=$local_value
      fi
    else
      read -r -p "UserAgent key [$ZBR_STORAGE_AGENT_KEY]: " local_agent_key
      if [[ ! -z $local_agent_key ]]; then
        ZBR_STORAGE_AGENT_KEY=$local_agent_key
      fi
    fi

    echo "Region: $ZBR_STORAGE_REGION"
    echo "Endpoint: $ZBR_STORAGE_ENDPOINT_PROTOCOL://$ZBR_STORAGE_ENDPOINT_HOST"
    echo "Bucket: $ZBR_STORAGE_BUCKET"
    echo "Access key: $ZBR_STORAGE_ACCESS_KEY"
    echo "Secret key: $ZBR_STORAGE_SECRET_KEY"
    echo "Agent key: $ZBR_STORAGE_AGENT_KEY"
    echo "Tenant: $ZBR_STORAGE_TENANT"
    confirm "" "Continue?" "y"
    is_confirmed=$?
  done

  export ZBR_STORAGE_REGION=$ZBR_STORAGE_REGION
  export ZBR_STORAGE_ENDPOINT_PROTOCOL=$ZBR_STORAGE_ENDPOINT_PROTOCOL
  export ZBR_STORAGE_ENDPOINT_HOST=$ZBR_STORAGE_ENDPOINT_HOST
  export ZBR_STORAGE_BUCKET=$ZBR_STORAGE_BUCKET
  export ZBR_STORAGE_ACCESS_KEY=$ZBR_STORAGE_ACCESS_KEY
  export ZBR_STORAGE_SECRET_KEY=$ZBR_STORAGE_SECRET_KEY
  export ZBR_STORAGE_AGENT_KEY=$ZBR_STORAGE_AGENT_KEY
}

