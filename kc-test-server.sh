#!/bin/bash

rm -rf target/keycloak-23.0.7

unzip ~/dev/dl/keycloak-23.0.7.zip -d target/
mkdir -p target/keycloak-23.0.7/data/import

cp src/test/resources/testrealm.json target/keycloak-23.0.7/data/import/

export KEYCLOAK_ADMIN=admin
export KEYCLOAK_ADMIN_PASSWORD=admin

target/keycloak-23.0.7/bin/kc.sh start-dev --import-realm
