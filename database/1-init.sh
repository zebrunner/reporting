#!/bin/bash

if [ $( psql -v ON_ERROR_STOP=1 --username $POSTGRES_USER -tAc "SELECT 1 FROM pg_namespace WHERE nspname = 'zafira'" ) == '1' ]; then
echo "Schema already exists"
exit 1
fi

psql -v ON_ERROR_STOP=1 --username $POSTGRES_USER -f /docker-entrypoint-initdb.d/db-pg.sql postgres
psql -v ON_ERROR_STOP=1 --username $POSTGRES_USER -f /docker-entrypoint-initdb.d/db-data.sql postgres
