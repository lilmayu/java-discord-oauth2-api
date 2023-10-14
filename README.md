# Java Wrapper for Discord's OAuth2 API
![Coverage](.github/badges/jacoco.svg)

Simple and lightweight Java wrapper for Discord's OAuth2 API.

## Installation

### Gradle

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'dev.mayuna:java-discord-oauth2-api:VERSION'
}
```

### Maven

```xml

<dependency>
    <groupId>dev.mayuna</groupId>
    <artifactId>java-discord-oauth2-api</artifactId>
    <version>VERSION</version>
</dependency>
```

You can find the latest version [here](https://mvnrepository.com/artifact/dev.mayuna/java-discord-oauth2-api).

## Usage

### Creating DiscordApplication instance

`DiscordApplication` is used in the `DiscordOAuth` constructor. It holds the client ID, client secret, redirect URI, and
other various information about the application.

**You must specify at least the client ID, client secret, and redirect URI.**

#### Using constructor (not recommended)

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

#### DiscordOAuthAuthorizationUrlFactory

As you can see in the previous section, you can specify the `DiscordOAuthAuthorizationUrlFactory` in
the `DiscordApplication` constructor/builder.

This class is used for creating the authorization URL. You can use the default implementation or create your own by
overriding the class.

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

Sometimes, Discord will return an error in the response. You can check if the request was successful by using the `getError()`

```java
DiscordOAuth discordOAuth = /* ... */;

DiscordAccessToken tokens = discordOAuth.revokeTokens("access_token").send();

if (tokens.getError() != null) {
    System.out.println("Error: " + tokens.getError());
    
    // Error description can be null even when error is not null
    System.out.println("Error description: " + tokens.getErrorDescription());
}
```