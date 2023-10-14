package dev.mayuna.discord.oauth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.UUID;

public class DiscordOAuthAuthorizationUrlFactoryTest {

    private static final String clientId = new Random().nextLong() + "";
    private static final String clientSecret = UUID.randomUUID().toString().replace("-", "");
    private static final String redirectUrl = "https://localhost:8080";

    @Test
    public void getBaseAuthorizeUrlTest() {
        DiscordOAuthAuthorizationUrlFactory factory = new DiscordOAuthAuthorizationUrlFactory();
        Assertions.assertEquals(DiscordOAuthAuthorizationUrlFactory.DEFAULT_AUTHORIZE_URL, factory.getBaseAuthorizeUrl());

        factory = new DiscordOAuthAuthorizationUrlFactory("https://example.com");
        Assertions.assertEquals("https://example.com", factory.getBaseAuthorizeUrl());
    }

    @Test
    public void createAuthorizationUrlTest() {
        DiscordApplication application = new DiscordApplication.Builder()
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withRedirectUrl(redirectUrl)
                .withScopes("identify", "guilds")
                .build();

        DiscordApplication applicationWithoutScopes = new DiscordApplication.Builder()
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withRedirectUrl(redirectUrl)
                .build();

        String state = UUID.randomUUID().toString().replace("-", "");

        DiscordOAuthAuthorizationUrlFactory factory = new DiscordOAuthAuthorizationUrlFactory();

        String url = factory.createAuthorizationUrl(application, state, "consent");
        String simulatedUrl = "https://discord.com/oauth2/authorize?client_id=" + clientId + "&redirect_uri=" + redirectUrl + "&response_type=code&scope=identify%20guilds&state=" + state + "&prompt=consent";
        Assertions.assertEquals(simulatedUrl, url);

        url = factory.createAuthorizationUrl(application);
        simulatedUrl = "https://discord.com/oauth2/authorize?client_id=" + clientId + "&redirect_uri=" + redirectUrl + "&response_type=code&scope=identify%20guilds";
        Assertions.assertEquals(simulatedUrl, url);

        url = factory.createAuthorizationUrl(application, null, "consent");
        simulatedUrl = "https://discord.com/oauth2/authorize?client_id=" + clientId + "&redirect_uri=" + redirectUrl + "&response_type=code&scope=identify%20guilds&prompt=consent";
        Assertions.assertEquals(simulatedUrl, url);

        url = factory.createAuthorizationUrl(application, state);
        simulatedUrl = "https://discord.com/oauth2/authorize?client_id=" + clientId + "&redirect_uri=" + redirectUrl + "&response_type=code&scope=identify%20guilds&state=" + state;
        Assertions.assertEquals(simulatedUrl, url);

        url = factory.createAuthorizationUrl(applicationWithoutScopes, state);
        simulatedUrl = "https://discord.com/oauth2/authorize?client_id=" + clientId + "&redirect_uri=" + redirectUrl + "&response_type=code&state=" + state;
        Assertions.assertEquals(simulatedUrl, url);

        Assertions.assertThrows(NullPointerException.class, () -> factory.createAuthorizationUrl(null));
        Assertions.assertThrows(NullPointerException.class, () -> factory.createAuthorizationUrl(null, state));
        Assertions.assertThrows(NullPointerException.class, () -> factory.createAuthorizationUrl(null, state, "consent"));
        Assertions.assertDoesNotThrow(() -> factory.createAuthorizationUrl(application, null));
        Assertions.assertDoesNotThrow(() -> factory.createAuthorizationUrl(application, state, null));

    }
}
