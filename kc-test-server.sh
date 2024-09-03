#!/bin/bash

VERSION=25.0.4

rm -rf test-server/
mkdir test-server

unzip ~/dev/dl/keycloak-$VERSION.zip -d test-server/
mkdir -p test-server/keycloak-$VERSION/data/import

cp src/test/resources/testrealm.json test-server/keycloak-$VERSION/data/import/

export DEBUG=true
export KEYCLOAK_ADMIN=admin
export KEYCLOAK_ADMIN_PASSWORD=admin

test-server/keycloak-$VERSION/bin/kc.sh start-dev --import-realm --cache=local --features=admin-fine-grained-authz,token-exchange
