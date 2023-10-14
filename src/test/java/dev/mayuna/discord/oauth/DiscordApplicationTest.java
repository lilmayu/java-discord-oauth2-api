package dev.mayuna.discord.oauth;

import dev.mayuna.discord.Utils;
import dev.mayuna.discord.api.DiscordApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DiscordApplicationTest {

    private final String apiUrl = "https://discord.com/api";
    private final String clientId = "1234";
    private final String clientSecret = "5678";
    private final String redirectUrl = "https://localhost:8080";
    private final String[] scopes = new String[]{"identify", "guilds"};

    @Test
    public void testBuilder() {
        DiscordApplication.Builder builder = new DiscordApplication.Builder();

        DiscordOAuthAuthorizationUrlFactory factory = new DiscordOAuthAuthorizationUrlFactory();

        builder.withApiUrl("https://discord.com/api");
        builder.withClientId("1234");
        builder.withClientSecret("5678");
        builder.withRedirectUrl("https://localhost:8080");
        builder.withScopes("identify", "guilds");
        builder.withAuthorizationUrlFactory(factory);

        DiscordApplication application = builder.build();

        Assertions.assertEquals("https://discord.com/api", application.getApiUrl());
        Assertions.assertEquals("1234", application.getClientId());
        Assertions.assertEquals("5678", application.getClientSecret());
        Assertions.assertEquals("https://localhost:8080", application.getRedirectUrl());
        Utils.AssertStringArraysEquals(new String[]{"identify", "guilds"}, application.getScopes());
        Assertions.assertEquals(factory, application.getAuthorizationUrlFactory());

        Assertions.assertEquals(clientId, builder.getClientId());
        Assertions.assertEquals(clientSecret, builder.getClientSecret());
        Assertions.assertEquals(redirectUrl, builder.getRedirectUrl());
        Utils.AssertStringArraysEquals(scopes, builder.getScopes());

        builder = new DiscordApplication.Builder();
        Assertions.assertThrows(IllegalStateException.class, builder::build);

        builder.withClientId("xd");
        Assertions.assertThrows(IllegalStateException.class, builder::build);

        builder.withClientSecret("xd");
        Assertions.assertThrows(IllegalStateException.class, builder::build);

        builder.withRedirectUrl("xd");
        Assertions.assertDoesNotThrow(builder::build);

        DiscordApplication.Builder finalBuilder = new DiscordApplication.Builder();
        Assertions.assertThrows(NullPointerException.class, () -> finalBuilder.withApiUrl(null));
        Assertions.assertThrows(NullPointerException.class, () -> finalBuilder.withClientId(null));
        Assertions.assertThrows(NullPointerException.class, () -> finalBuilder.withClientSecret(null));
        Assertions.assertThrows(NullPointerException.class, () -> finalBuilder.withRedirectUrl(null));
        Assertions.assertThrows(NullPointerException.class, () -> finalBuilder.withScopes((String[]) null));
        Assertions.assertThrows(NullPointerException.class, () -> finalBuilder.withAuthorizationUrlFactory(null));

        builder = new DiscordApplication.Builder();
        Assertions.assertEquals(DiscordApi.DEFAULT_API_URL, builder.getApiUrl());

        builder.withApiUrl("https://example.com");
        Assertions.assertEquals("https://example.com", builder.getApiUrl());

        builder.withAuthorizationUrlFactory(factory);
        Assertions.assertEquals(factory, builder.getAuthorizationUrlFactory());
    }

    @Test
    public void testFullConstructor() {
        DiscordOAuthAuthorizationUrlFactory factory = new DiscordOAuthAuthorizationUrlFactory();

        DiscordApplication application = new DiscordApplication(apiUrl, clientId, clientSecret, redirectUrl, factory, scopes);

        Assertions.assertEquals("https://discord.com/api", application.getApiUrl());
        Assertions.assertEquals("1234", application.getClientId());
        Assertions.assertEquals("5678", application.getClientSecret());
        Assertions.assertEquals("https://localhost:8080", application.getRedirectUrl());
        Utils.AssertStringArraysEquals(new String[]{"identify", "guilds"}, application.getScopes());
        Assertions.assertEquals(factory, application.getAuthorizationUrlFactory());
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    public void testConstructorNullExceptions() {
        Assertions.assertThrows(NullPointerException.class, () -> new DiscordApplication(apiUrl, clientId, clientSecret, redirectUrl, null, (String) null));
        Assertions.assertThrows(NullPointerException.class, () -> new DiscordApplication(null, clientId, clientSecret, redirectUrl, null, (String) null));
        Assertions.assertThrows(NullPointerException.class, () -> new DiscordApplication(apiUrl, null, clientSecret, redirectUrl, null, (String) null));
        Assertions.assertThrows(NullPointerException.class, () -> new DiscordApplication(apiUrl, clientId, null, redirectUrl, null, (String) null));
        Assertions.assertThrows(NullPointerException.class, () -> new DiscordApplication(apiUrl, clientId, clientSecret, null, null, (String) null));
    }

    @Test
    public void testCreateAuthorizationUrl() {
        DiscordApplication.Builder builder = new DiscordApplication.Builder();

        DiscordOAuthAuthorizationUrlFactory factory = new DiscordOAuthAuthorizationUrlFactory();

        builder.withApiUrl("https://discord.com/api");
        builder.withClientId("1234");
        builder.withClientSecret("5678");
        builder.withRedirectUrl("https://localhost:8080");
        builder.withScopes("identify", "guilds");
        builder.withAuthorizationUrlFactory(factory);

        DiscordApplication application = builder.build();

        Assertions.assertEquals(
                factory.createAuthorizationUrl(application),
                application.createAuthorizationUrl()
        );

        Assertions.assertEquals(
                factory.createAuthorizationUrl(application, "state"),
                application.createAuthorizationUrl("state")
        );

        Assertions.assertEquals(
                factory.createAuthorizationUrl(application, "state", "code"),
                application.createAuthorizationUrl("state", "code")
        );
    }
}
