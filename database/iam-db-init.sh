#!/bin/bash
psql -v ON_ERROR_STOP=1 --username $POSTGRES_USER -f /docker-entrypoint-initdb.d/iam/db-iam-database.sql
