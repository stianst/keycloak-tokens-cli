# Keycloak Token CLI

Keycloak Token CLI (`kct`) is a tool to interact with an OAuth 2.0 Authorization Server or OpenID Connect provider. The
main use-case is to obtain tokens used by other commands. It can also be a great tool to learn and experiement with 
OAuth 2.0 and OpenID Connect.

Features include:

* Multiple configuration context to switch between different Identity Providers, Clients, or User accounts
* Persist token and automatically refresh tokens on demand
* Several authentication flows
    * Browser - leverages the system browser to authenticate the user ([Authorization Code + PKCE](https://oauth.net/2/grant-types/authorization-code/))
    * Device - login via the system browser on a separate computer ([Device Code](https://oauth.net/2/grant-types/device-code/))
    * Password - login directly via username and password ([Password Grant](https://oauth.net/2/grant-types/password/))
    * Client - login as a service account ([Client Credentials](https://oauth.net/2/grant-types/client-credentials/))
* Retrieve and refresh tokens
* Decode tokens
* [Userinfo endpoint](https://openid.net/specs/openid-connect-core-1_0.html#UserInfo)
* [Token revocation](https://oauth.net/2/token-revocation/)
* [Token exchange](https://oauth.net/2/token-exchange/)
* [Dynamic Client Registration](https://openid.net/specs/openid-connect-registration-1_0.html) to automatically register clients with an OpenID Connect provider
* `kubectl` plugin to seamlessly authenticate with Kubernetes clusters secured with a OpenID Connect provider

Tested with [Keycloak](https://www.keycloak.org/), but should work with any OpenID Connect provider or
OAuth 2.0 Authorization Server.

## Installing

Grab the `kct-<architecture>` binary from `https://github.com/stianst/keycloak-tokens-cli/releases` and you are ready to
go.

### Linux:

```
curl -L $(curl --silent https://api.github.com/repos/stianst/keycloak-tokens-cli/releases/latest | grep 'browser_download_url.*kct-linux-amd64' | cut -d '"' -f 4) -o kct
chmod +x kct
```

## Configuring

`kct` stores it's configuration in `.kct/config.yaml`, but you don't need to edit this file yourself as `kct` provides
commands to update the configuration.

### Creating a configuration context

First step to using `kct` is to create a configuration context.

For example to create a context for a service account run:

```
kct config context create --iss=http://localhost:8080/realms/myrealm --context=mycontext --client=myclient --client-secret=secret --flow=client --default
```

For more details see `kct config context -h`.

### Creating an identity provider

Configuration context are associated with an identity provider. This makes it easier to delete all context for a given
identity provider, or to update the URL for example.

When creating a configuration context an identity provider is automatically created, but you can also specify the
identity provider first:

```
kct config issuer create --iss=myissuer --url=http://localhost:8080/realms/myrealm
```

Now when creating a configuration context you can refer to the provider by the alias, rather than the url:

```
kct config context create --iss=myissuer --context=mycontext --client=myclient --client-secret=secret --flow=client --default
```

You can also use environment variables to set the URL for example:

```
export ISSUER_URL=http://localhost:8080/realms/myrealm
kct config issuer create --iss=myissuer --url='${issuer.url}'
```

### Configuring a truststore

If the Identity Provider is using self-signed certificates, or a certificate signed by a certificate authority not
trusted by the operating system, the certificate needs to be added to a truststore.

`kct` supports truststores in PKCS#12 or Java KeyStore formats.

To create a Java Keystore you need Java installed, then run:

```
keytool -import -alias your-alias -keystore <truststore.jks> -file <cert.pem> -storepass mypassword -noprompt
```

Then configure `truststore-path` and `truststore-password` options:

```
kct config update --truststore-path=<truststore.jks> --truststore-password=mypassword
```

## Using

### Help

Run `kct --help` or `kct <command> --help`

## Grab a token

Simply run `kct token` and it'll output the token. As logging is redirected to stderr you can easily pass the token
to an environment variable or another process, for example:

```
mycommand --token $(kct token)
```

## Switching between contexts

`kct` has the concept of a default context, which can be set either as part of the configuration or when creating/updating
contexts.

For example to swith the default context:
```
kct config update --default=context=myothercontext
```

Or, to set a context as the default when creating:
```
kct config context create --context myothercontext ... --default
```

To use the non-default context use the `-c` or `--context` option, for example:
```
kct token -c=myothercontext
```

## Dynamic client registration

To enable dynamic client registration you first need to create an identity provider and a context that can be used
to obtain tokens to create the clients.

For example:
```
kct config issuer create --iss=myissuer --url=http://localhost:8080/realms/myrealm
kct config context create --context=client-registration --iss=myissuer --client=client-registration --client-secret=mysecret --flow=client
kct config issuer update --iss=myissuer --client-registration-context=client-registration
```

Now when creating a context you can include the `--create-client` option which will dynamically register the client
with the identity provider.

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

To enable logging information and stack-traces for errors use `-v`: 

```
kct token -v
```

You can also enable logging information with setting the `KCT_VERBOSE` environment variable:

```
KCT_VERBOSE=true kct token -v 
```

To enable HTTP request and response output use `-X`:

```
kct token -X
```
