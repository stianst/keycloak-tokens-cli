#!/bin/bash

test-server/keycloak/bin/kc.sh export --realm test --users same_file --file src/test/resources/testrealm.json
