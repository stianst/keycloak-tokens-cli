#!/bin/bash -e

VERSION=25.0.5

rm -rf test-server/keycloak
mkdir -p test-server

cd test-server
if [ ! -f keycloak-$VERSION.zip ]; then
    echo "Downloading https://github.com/keycloak/keycloak/releases/download/$VERSION/keycloak-$VERSION.zip"
    curl -O -L https://github.com/keycloak/keycloak/releases/download/$VERSION/keycloak-$VERSION.zip
fi
unzip keycloak-$VERSION.zip

mv keycloak-$VERSION keycloak

mkdir -p keycloak/data/import
cp ../src/test/resources/testrealm.json keycloak/data/import/

export DEBUG=true
export KEYCLOAK_ADMIN=admin
export KEYCLOAK_ADMIN_PASSWORD=admin

keycloak/bin/kc.sh start-dev --import-realm --cache=local --features=admin-fine-grained-authz,token-exchange --log=file
