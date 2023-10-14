package dev.mayuna.discord.api;

import dev.mayuna.discord.Utils;
import dev.mayuna.discord.api.entities.DiscordUser;
import dev.mayuna.discord.api.server.DiscordApiMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DiscordApiTest {

    private final static String testAccessToken = "abcdefg";
    private final static DiscordUser testUser = createTestUser();

    private static DiscordApi discordApi;
    private static DiscordApiMock discordApiMock;

    @BeforeAll
    public static void prepare() {
        discordApiMock = new DiscordApiMock(testAccessToken, testUser);
        discordApiMock.start();

        discordApi = new DiscordApi(discordApiMock.getUrl());
    }

    @AfterAll
    public static void stop() {
        discordApiMock.stop();
    }

    @Test
    public void testGetApiUrl() {
        Assertions.assertEquals(discordApiMock.getUrl(), discordApi.getApiUrl());

        var anotherDiscordApi = new DiscordApi();
        Assertions.assertEquals(DiscordApi.DEFAULT_API_URL, anotherDiscordApi.getApiUrl());
    }

    @Test
    public void testConstructors() {
        Assertions.assertThrows(NullPointerException.class, () -> new DiscordApi(null));
    }

    @Test
    public void testFetchingWithNulls() {
        Assertions.assertThrows(NullPointerException.class, () -> discordApi.fetchUser(null).sendAsync().join());
    }

    @Test
    public void testFetchUser() {
        DiscordUser user = discordApi.fetchUser(testAccessToken).sendAsync().join();

        Assertions.assertEquals(testUser.getId(), user.getId());
        Assertions.assertEquals(testUser.getUsername(), user.getUsername());
        Assertions.assertEquals(testUser.getGlobalName(), user.getGlobalName());
        Assertions.assertEquals(testUser.getAvatarHash(), user.getAvatarHash());
        Assertions.assertEquals(testUser.getDiscriminator(), user.getDiscriminator());
        Assertions.assertEquals(testUser.getBot(), user.getBot());
        Assertions.assertEquals(testUser.getSystem(), user.getSystem());
        Assertions.assertEquals(testUser.getMfaEnabled(), user.getMfaEnabled());
        Assertions.assertEquals(testUser.getAccentColor(), user.getAccentColor());
        Assertions.assertEquals(testUser.getLocale(), user.getLocale());
        Assertions.assertEquals(testUser.getVerified(), user.getVerified());
        Assertions.assertEquals(testUser.getEmail(), user.getEmail());
        Assertions.assertEquals(testUser.getFlags(), user.getFlags());
        Assertions.assertEquals(testUser.getPremiumType(), user.getPremiumType());
        Assertions.assertEquals(testUser.getPublicFlags(), user.getPublicFlags());
        Assertions.assertEquals(testUser.getAvatarDecorationHash(), user.getAvatarDecorationHash());

        Assertions.assertEquals(testUser.getIdAsLong(), user.getIdAsLong());
    }

    @Test
    public void testErrors() {
        DiscordUser user = discordApi.fetchUser("invalid_token").sendAsync().join();

        Assertions.assertNotNull(user.getError());
        Assertions.assertNotNull(user.getErrorDescription());
    }

    private static DiscordUser createTestUser() {
        DiscordUser user = new DiscordUser();

        Utils.setField(user, "id", "677516608778928129");
        Utils.setField(user, "username", "TestUser");
        Utils.setField(user, "globalName", "Test User");
        Utils.setField(user, "avatarHash", "some_hash");
        Utils.setField(user, "discriminator", "6969");
        Utils.setField(user, "bot", true);
        Utils.setField(user, "system", true);
        Utils.setField(user, "mfaEnabled", true);
        Utils.setField(user, "accentColor", 4203035);
        Utils.setField(user, "locale", "en-US");
        Utils.setField(user, "verified", true);
        Utils.setField(user, "email", "testuser@example.com");
        Utils.setField(user, "flags", 1);
        Utils.setField(user, "premiumType", 2);
        Utils.setField(user, "publicFlags", 3);
        Utils.setField(user, "avatarDecorationHash", "some_hash");

        return user;
    }

}
