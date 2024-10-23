#!/bin/bash -e

VERSION=$(mvn help:evaluate -Dexpression=keycloak.version -q -DforceStdout)

rm -rf test-server/keycloak
rm -rf test-server/cert

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

mkdir cert
cd cert

openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -sha256 -days 3650 -nodes -subj "/CN=localhost"

keytool -import -alias your-alias -keystore truststore.jks -file cert.pem -storepass mypassword -noprompt
keytool -import -alias your-alias -keystore truststore.pfx -file cert.pem -storepass mypassword -noprompt

cd ../

export DEBUG=true

keycloak/bin/kc.sh start \
--hostname-strict=false \
--http-enabled=true \
--import-realm \
--cache=local \
--bootstrap-admin-username admin --bootstrap-admin-password admin \
--bootstrap-admin-client-secret=mysecret \
--features=admin-fine-grained-authz,token-exchange \
--log=file \
--https-certificate-file=cert/cert.pem --https-certificate-key-file=cert/key.pem
