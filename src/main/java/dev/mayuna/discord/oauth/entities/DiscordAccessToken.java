package dev.mayuna.discord.oauth.entities;

import com.google.gson.annotations.SerializedName;
import dev.mayuna.discord.api.DiscordApiResponse;
import lombok.Getter;

/**
 * Represents Discord's access token response. Visit <a
 * href="https://discord.com/developers/docs/topics/oauth2#authorization-code-grant-access-token-response">Discord's documentation</a> for more
 * information.
 */
@Getter
public class DiscordAccessToken extends DiscordApiResponse {

    /**
     * Gets the time when the access token was fetched, e.g., when the {@link DiscordAccessToken} instance was created.
     *
     * @return The time in milliseconds.
     */
    private final @Getter long fetchedAt = System.currentTimeMillis();

    private @SerializedName("access_token") String accessToken;
    private @SerializedName("token_type") String tokenType;
    private @SerializedName("expires_in") long expiresInSeconds;
    private @SerializedName("refresh_token") String refreshToken;
    private String scope;

    /**
     * Determines if access token are expired
     *
     * @return boolean
     */
    public boolean isAccessTokenExpired() {
        return System.currentTimeMillis() - fetchedAt > expiresInSeconds * 1000;
    }

    /**
     * Gets the scopes returned by the Discord API.<br> If the scope is null or empty, then an empty array will be returned.
     *
     * @return The scopes.
     */
    public String[] getScopes() {
        if (scope == null || scope.isEmpty()) {
            return new String[]{};
        }

        return scope.split(" ");
    }
}
