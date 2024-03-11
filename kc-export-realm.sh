#!/bin/bash

target/keycloak-24.0.1/bin/kc.sh export --realm test --users same_file --file src/test/resources/testrealm.json
