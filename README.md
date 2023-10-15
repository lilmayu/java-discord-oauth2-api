# Java Wrapper for Discord's OAuth2 API

![Coverage](.github/badges/jacoco.svg)

Simple and lightweight Java wrapper for Discord's OAuth2 API.

## Contents
- [Features](#features)
- [Super-quick-showcase](#super-quick-showcase)
- [Installation](#installation)
    - [Gradle](#gradle)
    - [Maven](#maven)
- [Documentation](#documentation)
    - [Creating DiscordApplication instance](#creating-discordapplication-instance)
        - [Using builder (recommended)](#using-builder-recommended)
    - [Getting authorization URL](#getting-authorization-url)
    - [Creating DiscordOAuth instance](#creating-discordoauth-instance)
        - [Fetching access/refresh token](#fetching-accessrefresh-token)
        - [Refreshing access token](#refreshing-access-token)
        - [Revoking tokens](#revoking-tokens)

## Features

- OAuth2 endpoints
    - Fetch Access Token `/oauth2/token`
    - Refresh Access Token `/oauth2/token`
    - Revoke Access Token `/oauth2/token/revoke`
- API endpoints
    - Get User data `/users/@me`
- Authorization URL creation

### Notes
More API endpoints will be added in the future, if requested.

Simple webserver for handling the redirect URI will be added in the future, if requested.

## Super-quick-showcase

```java
// Creating DiscordApplication instance
DiscordApplication.Builder builder = new DiscordApplication.Builder();

builder.withDiscordApiUrl(DISCORD_API_URL) // (1)
       .withClientId(CLIENT_ID) // Required, (2)
       .withClientSecret(CLIENT_SECRET)  // Required, (3)
       .withRedirectUri(REDIRECT_URI) // Required, (4)
       .withAuthorizationUrlFactory(DiscordOAuthAuthorizationUrlFactory) // (5)
       .withScopes(SCOPES); // (6)

DiscordApplication discordApplication = builder.build();

// Creating DiscordOAuth instance
DiscordOAuth discordOAuth = new DiscordOAuth(discordApplication);

// Fetching access token
DiscordAccessToken tokens = discordOAuth.fetchAccessToken("code").send(); // (7)

// Refreshing access token
DiscordAccessToken tokens = discordOAuth.refreshAccessToken("refresh_token").send();

// Revoking access token
discordOAuth.revokeTokens("access_token").send();
```

Notes:
1. This defaults to `DiscordApiUrls.DEFAULT`. However, you can specify your own URL if you want to.
2. This is the client ID of your application. You can find it in the [Discord Developer Portal](https://discord.com/developers/applications).
3. This is the client secret of your application. You can find it in the [Discord Developer Portal](https://discord.com/developers/applications).
4. This is the redirect URI of your application. You can find it in the [Discord Developer Portal](https://discord.com/developers/applications).
5. This is the factory that is used for creating the authorization URL. You can use the default implementation or create your own by overriding the class.
6. These are the scopes that you want to request from the user. You can find the list of all scopes [here](https://discord.com/developers/docs/topics/oauth2#shared-resources-oauth2-scopes).
7. This is the code that you get from the redirect URI, after the user authorizes your application and Discord redirects them there.

Asynchronous requests are also supported.
You can find more information about them in the [Fetching access/refresh token](#fetching-accessrefresh-token).

## Installation

- Java >= 11

### Gradle

```groovy
repositories {
    mavenCentral()
}

dependencies {
    // Library itself
    implementation 'dev.mayuna:java-discord-oauth2-api:VERSION'

    // Required for the library to work
    implementation 'dev.mayuna:simple-java-api-wrapper:2.2'
    implementation 'com.google.code.gson:gson:2.10'
}
```

### Maven

```xml
<!-- Library itself -->
<dependency>
    <groupId>dev.mayuna</groupId>
    <artifactId>java-discord-oauth2-api</artifactId>
    <version>VERSION</version>
</dependency>

<!-- Required for the library to work -->
<dependency>
    <groupId>dev.mayuna</groupId>
    <artifactId>simple-java-api-wrapper</artifactId>
    <version>2.2</version>
</dependency>
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10</version>
</dependency>
```

You can find the latest version [here](https://mvnrepository.com/artifact/dev.mayuna/java-discord-oauth2-api).

## Documentation

### Creating DiscordApplication instance

`DiscordApplication` is used in the `DiscordOAuth` constructor. It holds the client ID, client secret, redirect URI, and
other various information about the application.

**You must specify at least the client ID, client secret, and redirect URI.**

#### Using builder (recommended)

```java
DiscordApplication.Builder builder = new DiscordApplication.Builder();

builder.withDiscordApiUrl(DISCORD_API_URL)
       .withClientId(CLIENT_ID)
       .withClientSecret(CLIENT_SECRET)
       .withRedirectUri(REDIRECT_URI)
       .withAuthorizationUrlFactory(DiscordOAuthAuthorizationUrlFactory)
       .withScopes(SCOPES);

DiscordApplication discordApplication = builder.build();
```

<details>
  <summary><b><i>Using constructor (not recommended)</i></b></summary>

```java
DiscordApplication discordApplication = new DiscordApplication(
        DISCORD_API_URL,
        CLIENT_ID,
        CLIENT_SECRET,
        REDIRECT_URI,
        DiscordOAuthAuthorizationUrlFactory,
        SCOPES
        );
```
</details>

### Getting authorization URL

`DiscordApplication` allows you to create the authorization URL that will user use to authorize your application.

```java
DiscordApplication discordApplication = /* ... */;
String url;

// You will mostlikely use this one
url = discordApplication.createAuthorizationUrl("some-state");

url = discordApplication.createAuthorizationUrl("some-state", "some-prompt");
url = discordApplication.createAuthorizationUrl();
```

<details>
  <summary><b><i>DiscordOAuthAuthorizationUrlFactory Info</i></b></summary>

As you can see in the previous section, you can specify the `DiscordOAuthAuthorizationUrlFactory` in
the `DiscordApplication` constructor/builder.

This class is used for creating the authorization URL. You can use the default implementation or create your own by
overriding the class.

</details>

### Creating DiscordOAuth instance

`DiscordOAuth` is used for getting the access/refresh token from Discord's API.

```java
DiscordApplication discordApplication = /* ... */;
DiscordOAuth discordOAuth = new DiscordOAuth(discordApplication);
```

#### Fetching access/refresh token

`code` is the code that you get from the redirect URI, after the user authorizes your application and Discord redirects
them there.

```java
DiscordOAuth discordOAuth = /* ... */;

// Synchonous
DiscordAccessToken tokens = discordOAuth.fetchAccessToken("code").send();

// Asynchrounous
discordOAuth.fetchAccessToken("code")
            .sendAsync()
            .whenCompleteAsync((tokens, throwable) -> {
                if (throwable != null) {
                    // Handle error
                    return;
                }

                // Use tokens
            });
```

#### Refreshing access token

```java
DiscordOAuth discordOAuth = /* ... */;

// Synchonous
DiscordAccessToken tokens = discordOAuth.refreshAccessToken("refresh_token").send();

// Asynchrounous
discordOAuth.refreshAccessToken("refresh_token")
            .sendAsync()
            .whenCompleteAsync((tokens, throwable) -> {
                if (throwable != null) {
                    // Handle error
                    return;
                }

                // Use tokens
            });
```

#### Revoking tokens

```java
DiscordOAuth discordOAuth = /* ... */;

// Synchonous
DiscordAccessToken tokens = discordOAuth.revokeTokens("access_token").send();

// Asynchrounous
discordOAuth.revokeTokens("access_token")
            .sendAsync()
            .whenCompleteAsync((tokens, throwable) -> {
                if (throwable != null) {
                    // Handle error
                    return;
                }

                // Request completed
            });
```

## Handling errors

### HTTP Errors

If the request fails, the `send()` method will throw an exception. You can catch it and handle it.

If you use `sendAsync()`, the exception will be passed into the `CompletableFuture` that is returned.

### Request errors

Sometimes, Discord will return an error in the response. You can check if the request was successful by using
the `getError()`

```java
DiscordOAuth discordOAuth = /* ... */;

DiscordAccessToken tokens = discordOAuth.revokeTokens("access_token").send();

if (tokens.getError() != null) {
    System.out.println("Error: " + tokens.getError());
    
    // Error description can be null even when error is not null
    System.out.println("Error description: " + tokens.getErrorDescription());
}
```