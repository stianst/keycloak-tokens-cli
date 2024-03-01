# Keycloak Token CLI

Keycloak Token CLI (`kct`) provides a CLI interface to obtain tokens from an OpenID Connect provider.

Features include:

* Multiple configuration contexts to easily switch between different issuers, flows, or clients
* Several OAuth flows
    * Device Code
    * Password Grant
* Decode tokens
* UserInfo endpoint
* Token store
* Single sign-on through retrieving scoped tokens from a refresh token with a larger scope

Additional features that are coming soon:

* Additional OAuth flows
    * Authorization Code + PKCE
    * Client Credentials
* Plugin mode for `kubectl`
* Token Introspection Endpoint
* Create config contexts with `kct config`

Tested with [Keycloak](https://www.keycloak.org/), but should work with any OpenID Connect provider or
OAuth Authorization Server.

## Installing

Grab the `kct-<architecture>` binary from `https://github.com/stianst/keycloak-tokens-cli/releases` and you are ready to
go.

### Linux:

```
curl -L $(curl --silent https://api.github.com/repos/stianst/keycloak-tokens-cli/releases/latest | grep 'browser_download_url.*kct-linux-amd64' | cut -d '"' -f 4) -o kct
chmod +x kct
```

## Configuring

`kct` provides a few different ways for configuration depending on the use-case, and supports multiple configuration
contexts to make it easy to switch between different issuers, flows, or clients.

### Configuration file

The standard way to configure `kct` is through `~/.kct/config.yaml`. This configuration approach supports multiple
configuration contexts. An example config file looks like:

```
default: test-password
contexts:
    test-password:
        issuer: http://localhost:8080/realms/test
        client: test-password
        flow: password
        user: test-user
        user-password: test-user-password
    test-device:
        issuer: http://localhost:8080/realms/test
        client: test-device
        flow: device
```

The default context to use is specified with the `default` field, but can be overwritten in most commands with
`--context=another-context` or using the environment variable `KC_CONTEXT=another-context`.

If multiple contexts are using the same issuer a global issuer can be defined, for example:

```
default: test-password
issuers:
    local-test:
      url: http://localhost:8080/realms/test
contexts:
    test-password:
        issuer-ref: local-test
        ...
    test-device:
        issuer-ref: local-test
        ...
```

It is also possible to specify an alternative location to the file with the `KCT_CONFIG_FILE` environment variable.

### Configuring using environment variables

If you want to quickly test with a single config this can be done with setting environment variables, which can also
be set in `$PWD/.env`. The following environment variables are available:

| Environment Variable |
|----------------------|
| KCT_CONTEXT          |
| KCT_ISSUER           |
| KCT_FLOW             |
| KCT_SCOPE            |
| KCT_CLIENT           |
| KCT_CLIENT_SECRET    |
| KCT_USER             |
| KCT_USER_PASSWORD    |

If the environment variable `KCT_ISSUER` is set the configuration file will be ignored (`~/.kct/config.yaml`).

## Using

### Help

Run `kct --help` or `kct <command> --help`

## Grab a token

Simply run `kct token` and it'll output the token. As logging is redirected to stderr you can easily pass the token
to an environment variable or another process, for example:

```
mycommand --token $(kct token)
```

## Debugging

To show the full stack trace for an error set `KCT_VERBOSE` environment variable:

```
export KCT_VERBOSE=true
```

Enable debug logging with:

```
export QUARKUS_LOG_CATEGORY__ORG_KEYCLOAK_CLI__LEVEL=DEBUG
```

The above can also be added to `$PWD/.env`.
