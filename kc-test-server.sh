#!/bin/bash -e

VERSION=25.0.4

rm -rf test-server/keycloak-$VERSION
mkdir -p test-server

cd test-server
if [ ! -f keycloak-$VERSION.zip ]; then
    echo "Downloading https://github.com/keycloak/keycloak/releases/download/$VERSION/keycloak-$VERSION.zip"
    curl -O -L https://github.com/keycloak/keycloak/releases/download/$VERSION/keycloak-$VERSION.zip
fi
unzip keycloak-$VERSION.zip

mkdir -p keycloak-$VERSION/data/import
cp ../src/test/resources/testrealm.json keycloak-$VERSION/data/import/

export DEBUG=true
export KEYCLOAK_ADMIN=admin
export KEYCLOAK_ADMIN_PASSWORD=admin

keycloak-$VERSION/bin/kc.sh start-dev --import-realm --cache=local --features=admin-fine-grained-authz,token-exchange --log=file
