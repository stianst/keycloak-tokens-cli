#!/bin/bash -e

VERSION=25.0.5

rm -rf test-server/keycloak
mkdir -p test-server

cd test-server
if [ ! -f keycloak-999.0.0-SNAPSHOT.zip ]; then
    echo "Downloading https://github.com/keycloak/keycloak/releases/download/nightly/keycloak-999.0.0-SNAPSHOT.zip"
    curl -O -L https://github.com/keycloak/keycloak/releases/download/nightly/keycloak-999.0.0-SNAPSHOT.zip
fi
unzip keycloak-999.0.0-SNAPSHOT.zip

mv keycloak-999.0.0-SNAPSHOT keycloak

mkdir -p keycloak/data/import
cp ../src/test/resources/testrealm.json keycloak/data/import/

export DEBUG=true
export KEYCLOAK_ADMIN=admin
export KEYCLOAK_ADMIN_PASSWORD=admin

keycloak/bin/kc.sh start-dev --import-realm --cache=local --bootstrap-admin-client-secret=mysecret --features=admin-fine-grained-authz,token-exchange --log=file
