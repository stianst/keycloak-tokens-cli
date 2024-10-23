#!/bin/bash -e

if (curl -s --retry 10 --retry-delay 5 --retry-connrefused http://localhost:8080); then
    echo "Started"
else
    echo "Not started"
    cat test-server/keycloak/data/log/keycloak.log
fi