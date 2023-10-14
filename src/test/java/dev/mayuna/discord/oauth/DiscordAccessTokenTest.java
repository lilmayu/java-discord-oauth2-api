package dev.mayuna.discord.oauth;

import dev.mayuna.discord.oauth.entities.DiscordAccessToken;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static dev.mayuna.discord.Utils.AssertStringArraysEquals;

public class DiscordAccessTokenTest {

    @Test
    public void getScopesMultiple() {
        AssertStringArraysEquals(new String[] {"identify", "guilds"}, getTestToken("identify guilds").getScopes());
    }

    @Test
    public void getScopesSingle() {
        AssertStringArraysEquals(new String[] {"identify"}, getTestToken("identify").getScopes());
    }

    @Test
    public void getScopesEmpty() {
        AssertStringArraysEquals(new String[] {}, getTestToken("").getScopes());
    }

    @Test
    public void getScopesNull() {
        AssertStringArraysEquals(new String[] {}, getTestToken(null).getScopes());
    }

    @Test
    public void iSAccessTokenExpiredInPast() {
        Assertions.assertTrue(getTestToken(System.currentTimeMillis() - 1001, 1).isAccessTokenExpired());
    }

    @Test
    public void iSAccessTokenExpiredInFuture() {
        Assertions.assertFalse(getTestToken(System.currentTimeMillis() + 1001, 1).isAccessTokenExpired());
    }

    @Test
    public void getFetchedAtTest() {
        DiscordAccessToken token = getTestToken(System.currentTimeMillis() - 1001, 1);
        Assertions.assertTrue(token.getFetchedAt() < System.currentTimeMillis());
        Assertions.assertFalse(token.getFetchedAt() > System.currentTimeMillis() - 1001);
    }

    @SneakyThrows
    private DiscordAccessToken getTestToken(String scope) {
        DiscordAccessToken token = new DiscordAccessToken();

        // Use reflection to set the private field
        Field scopesField = token.getClass().getDeclaredField("scope");
        scopesField.setAccessible(true);
        scopesField.set(token, scope);

        return token;
    }

    @SneakyThrows
    private DiscordAccessToken getTestToken(long fetchedAt, long expiresInSeconds) {
        DiscordAccessToken token = new DiscordAccessToken();

        // Use reflection to set the private field
        Field scopesField = token.getClass().getDeclaredField("fetchedAt");
        scopesField.setAccessible(true);
        scopesField.setLong(token, fetchedAt);

        scopesField = token.getClass().getDeclaredField("expiresInSeconds");
        scopesField.setAccessible(true);
        scopesField.setLong(token, expiresInSeconds);

        return token;
    }
}
