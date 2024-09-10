#!/bin/bash -e

if (curl -s --retry 1 --retry-delay 1 --retry-connrefused http://localhost:8080); then
    echo "Started"
else
    echo "Not started"
    cat test-server/keycloak-*/data/log/keycloak.log
fi