package dev.mayuna.discord.api;

import dev.mayuna.discord.api.entities.DiscordUser;
import dev.mayuna.simpleapi.ApiRequest;
import dev.mayuna.simpleapi.RequestHeader;
import dev.mayuna.simpleapi.RequestMethod;
import dev.mayuna.simpleapi.WrappedApi;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class DiscordApi implements WrappedApi {

    public static final String DEFAULT_API_URL = "https://discord.com/api/v10";

    private final String apiUrl;

    /**
     * Creates a new DiscordApi instance.
     *
     * @param apiUrl The API URL.
     */
    public DiscordApi(@NonNull String apiUrl) {
        this.apiUrl = apiUrl;
    }

    /**
     * Creates a new DiscordApi instance with the default API URL ({@link DiscordApi#DEFAULT_API_URL}).
     */
    public DiscordApi() {
        this(DEFAULT_API_URL);
    }

    @Override
    public String getDefaultUrl() {
        return apiUrl;
    }

    @Override
    public RequestHeader[] getDefaultRequestHeaders() {
        return new RequestHeader[]{
                RequestHeader.of("User-Agent", "Java-Discord-OAuth2 (dev.mayuna, 1.0)")
        };
    }

    /**
     * Fetches user from the Discord API by their access token.<br> Endpoint: {@code /users/@me} (<a
     * href="https://discord.com/developers/docs/resources/user#get-current-user">Discord's documentation</a>).<br>Required scope: {@code identify}
     * (and for email and verified filed: {@code email})
     *
     * @param accessToken Access token of the user.
     *
     * @return The API request.
     */
    public ApiRequest<DiscordUser> fetchUser(@NonNull String accessToken) {
        return ApiRequest.builder(this, DiscordUser.class)
                         .withEndpoint("/users/@me")
                         .withRequestMethod(RequestMethod.GET)
                         .withRequestHeader(RequestHeader.of("Authorization", "Bearer " + accessToken))
                         .build();
    }
}
