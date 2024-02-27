## Running tests

### Keycloak running locally

Before running tests start a Keycloak server locally:
```
mkdir -p $KEYCLOAK_HOME/data/import
cp cp src/test/resources/testrealm.json $KEYCLOAK_HOME/data/import/
$KEYCLOAK_HOME/bin/kc.sh start-dev --import-realm
```

Then run tests with `kc.container.mode=manual`:
```
./mvnw test -Dkc.container.mode=manual
```

### Keycloak container

Just run the tests:
```
./mvnw test
```

### Keycloak fast dev container

The fast dev container starts in ~30% of the time as it's an optimized container, with H2 DB pre-initialized.

First build the container with:

```
podman build containers/keycloak-fast-dev -t keycloak-fast-dev
```

Then run the tests with:
```
./mvnw test -Dkc.container.mode=fast
```

### Enabling log output from Keycloak container

To redirect log output from the Keycloak container use `kc.container.log`:
```
./mvnw test -Dkc.container.log
```