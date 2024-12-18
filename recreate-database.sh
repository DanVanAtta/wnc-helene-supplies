#!/bin/bash

# Recreates database
# If first arg is 'prod', then recreates the non-test database

set -eux
database="wnc_helene_test"
if [ "${1-}" == "prod" ]; then
  database=wnc_helene
fi

# postgres user password
export PGPASSWORD=blackbox

set -eux

echo "drop database $database" | psql -U postgres
echo "create database $database" | psql -U postgres
echo "alter database $database owner to wnc_helene" | psql -U postgres

./schema/run-flyway.sh
