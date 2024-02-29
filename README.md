# Installing

Grab the `kct` binary from `https://github.com/stianst/keycloak-tokens-cli/releases` and you are ready to go.

# Configuring

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


# Using

## Help

Run `kct `

## Grab a token

Simply run `kct token` and it'll output the token. As logging is redirected to stderr you can easily pass the token
to an environment variable or another process, for example:

```
mycommand --token $(kct token)
```