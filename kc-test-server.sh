#!/bin/bash

VERSION=999.0.0-SNAPSHOT

rm -rf target/keycloak-$VERSION

unzip ~/dev/dl/keycloak-$VERSION.zip -d target/
mkdir -p target/keycloak-$VERSION/data/import

cp src/test/resources/testrealm.json target/keycloak-$VERSION/data/import/

export KEYCLOAK_ADMIN=admin
export KEYCLOAK_ADMIN_PASSWORD=admin

target/keycloak-$VERSION/bin/kc.sh start-dev --import-realm
