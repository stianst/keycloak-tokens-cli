# Contributing

Contributions are more than welcome, simply open an issue in `https://github.com/stianst/keycloak-tokens-cli` and
send a PR ;)

# Running tests

### Keycloak running locally

Before running tests start a Keycloak server locally:

```
./kc-test-server.sh
```

Then run tests with `kc.container.mode=manual`:

```
./mvnw test -Dkc.container.mode=manual
```

To always run tests with local mode create the file `.env` with the contents:

```
KC_CONTAINER_MODE=manual
```

### Keycloak container

Just run the tests:

```
./mvnw test
```

### Enabling log output from Keycloak container

To redirect log output from the Keycloak container use `kc.container.log`:

```
./mvnw test -Dkc.container.log
```