package dev.mayuna.discord.oauth;

import dev.mayuna.discord.oauth.entities.DiscordAccessToken;
import dev.mayuna.discord.oauth.server.DiscordOAuthServerMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import static dev.mayuna.discord.Utils.AssertStringArraysEquals;

public class DiscordOAuthTest {

    private static final String clientId = new Random().nextLong() + "";
    private static final String clientSecret = UUID.randomUUID().toString().replace("-", "");
    private static final String redirectUrl = "https://localhost:8080";
    private static final String code = UUID.randomUUID().toString().replace("-", "");
    private static final String state = UUID.randomUUID().toString();
    private static final String[] scopes = new String[]{"identify", "guilds"};

    private static DiscordOAuthServerMock serverMock;
    private static DiscordOAuth discordOAuth;

    @BeforeAll
    public static void prepare() {
        serverMock = new DiscordOAuthServerMock(clientId, clientSecret, code, redirectUrl, String.join(" ", Arrays.asList(scopes)));
        serverMock.start();

        DiscordApplication application = new DiscordApplication.Builder()
                .withApiUrl(serverMock.getUrl())
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withRedirectUrl(redirectUrl)
                .withScopes(scopes)
                .build();

        discordOAuth = new DiscordOAuth(application);
    }

    @AfterAll
    public static void stop() {
        serverMock.stop();
    }

    @Test
    public void testNullInConstructor() {
        Assertions.assertThrows(NullPointerException.class, () -> new DiscordOAuth(null));
    }

    @Test
    public void testFetchingWithNulls() {
        Assertions.assertThrows(NullPointerException.class, () -> discordOAuth.fetchAccessToken(null).sendAsync().join());
        Assertions.assertThrows(NullPointerException.class, () -> discordOAuth.refreshAccessToken(null).sendAsync().join());
        Assertions.assertThrows(NullPointerException.class, () -> discordOAuth.revokeTokens(null).sendAsync().join());
    }

    @Test
    public void fetchTokens() {
        DiscordAccessToken token = discordOAuth.fetchAccessToken(code).sendAsync().join();

        Assertions.assertNull(token.getError());
        Assertions.assertNull(token.getErrorDescription());

        Assertions.assertNotNull(token);
        Assertions.assertNotNull(token.getAccessToken());
        Assertions.assertNotNull(token.getTokenType());
        Assertions.assertTrue(token.getExpiresInSeconds() != 0);
        Assertions.assertNotNull(token.getRefreshToken());
        Assertions.assertNotNull(token.getScope());
        AssertStringArraysEquals(scopes, token.getScopes());
    }

    @Test
    public void fetchAndRefreshTokens() {
        DiscordAccessToken token = discordOAuth.fetchAccessToken(code).sendAsync().join();
        DiscordAccessToken refreshedToken = discordOAuth.refreshAccessToken(token.getRefreshToken()).sendAsync().join();

        Assertions.assertNotNull(refreshedToken);
        Assertions.assertNotNull(refreshedToken.getAccessToken());
        Assertions.assertNotNull(refreshedToken.getTokenType());
        Assertions.assertTrue(refreshedToken.getExpiresInSeconds() != 0);
        Assertions.assertNotNull(refreshedToken.getRefreshToken());
        Assertions.assertNotNull(refreshedToken.getScope());
        AssertStringArraysEquals(scopes, token.getScopes());
    }

    @Test
    public void fetchAndRevokeTokens() {
        DiscordAccessToken token = discordOAuth.fetchAccessToken(code).sendAsync().join();

        discordOAuth.revokeTokens(token.getAccessToken()).sendAsync().join();
    }

    @Test
    public void fetchTokensWithErrors() {
        DiscordAccessToken token = discordOAuth.fetchAccessToken(code + "a").sendAsync().join();

        Assertions.assertNotNull(token.getError());

        Assertions.assertNull(token.getAccessToken());
        Assertions.assertNull(token.getTokenType());
        Assertions.assertEquals(0, token.getExpiresInSeconds());
        Assertions.assertNull(token.getRefreshToken());
        Assertions.assertNull(token.getScope());
    }
}
