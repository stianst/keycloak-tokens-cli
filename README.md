# Keycloak Token CLI

Keycloak Token CLI (`kct`) provides a CLI interface to obtain tokens from an OpenID Connect provider.

Features include:

* Multiple configuration contexts to easily switch between different issuers, flows, or clients
* Several OAuth flows
    * Authorization Code + PKCE
    * Device Code
    * Password Grant
    * Client Credentials Grant
* Decode tokens
* UserInfo endpoint
* Token store
* Single sign-on through retrieving scoped tokens from a refresh token with a larger scope

Additional features that are coming soon:

* Token Introspection Endpoint

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

An issuer can also specify reusable clients that can be used in contexts:

```
issuers:
    local-test:
        url: http://localhost:8080/realms/test
        clients:
            the-client:
                id: the-client-id
                secret: the-client-secret
                flow: device
contexts:
    test-device:
        issuer-ref: local-test
        client-ref: the-client
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

### Configuring a truststore

If the identity provider is using self-signed certificates, or certificates signed by a certificate authority not
trusted by the operating system, the certificate needs to be added to a truststore.

First create a Java truststore with the certificate imported:

```
keytool -import -file <path to certificate> -keystore myTrustStore
```

Then configure `truststore-path` and `truststore-password` in the `config.yaml` file, for example:

```
---
default: "tls"
store-tokens: true
truststore-path: /path/myTrustStore
truststore-password: <truststore password>
```

Alternatively, you can use the environment variable `KCT_TRUSTSTORE_PATH` and `KCT_TRUSTSTORE_PASSWORD`.

## Using

### Help

Run `kct --help` or `kct <command> --help`

## Grab a token

Simply run `kct token` and it'll output the token. As logging is redirected to stderr you can easily pass the token
to an environment variable or another process, for example:

```
mycommand --token $(kct token)
```

## Kubernetes command line tool (`kubectl`)

`kct` can be used as a plugin to `kubectl` to enable seamless authentication to a
[Kubernetes cluster configured to support OpenID Connect Tokens](https://kubernetes.io/docs/reference/access-authn-authz/authentication/#openid-connect-tokens)
for authentication.

First step is to copy `kct` to `kubectl-kct` and make it available on the path. After that run the following to
verify it works:

```
kubectl kct token
```

Configure credentials for `kubectl` to enable using `kct` to obtain tokens. For example:

```
kubectl config set-credentials kct --exec-api-version=client.authentication.k8s.io/v1 --exec-command='kubectl' --exec-arg='kct' --exec-arg='token'
```

If you want to use a specific `kct` configuration context for `kubectl` add `--exec-arg='--context=<context name>' at
the
end of the command above.

`kubectl config set-credentials` doesn't currently allow specifying `interactiveMode`, so you need to
edit `.kube/config`,
search for `kct`, and add `interactiveMode: IfAvailable` like shown below:

```
- name: kct
  user:
    exec:
      apiVersion: client.authentication.k8s.io/v1
      args:
      - kct
      - token
      command: kubectl
      env: null
      provideClusterInfo: false
      interactiveMode: IfAvailable
```

Next, you need to create a context entry that uses the previously configured credentials. For example:

```
kubectl config set-context kct --cluster=minikube --user kct
kubectl config use-context kct
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
