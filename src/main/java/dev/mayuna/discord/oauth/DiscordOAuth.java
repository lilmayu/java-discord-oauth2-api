package dev.mayuna.discord.oauth;

import dev.mayuna.discord.api.DiscordApiResponse;
import dev.mayuna.discord.oauth.entities.DiscordAccessToken;
import dev.mayuna.simpleapi.ApiRequest;
import dev.mayuna.simpleapi.RequestHeader;
import dev.mayuna.simpleapi.RequestMethod;
import dev.mayuna.simpleapi.WrappedApi;
import lombok.NonNull;

import java.net.http.HttpRequest;

public class DiscordOAuth implements WrappedApi {

    private final DiscordApplication application;

    /**
     * Creates a new DiscordOAuth instance.
     *
     * @param application The Discord application.
     */
    public DiscordOAuth(@NonNull DiscordApplication application) {
        this.application = application;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getDefaultUrl() {
        return application.getApiUrl();
    }

    @Override
    public RequestHeader[] getDefaultRequestHeaders() {
        return new RequestHeader[]{
                RequestHeader.of("User-Agent", "Java-Discord-OAuth2 (dev.mayuna, 1.0)")
        };
    }

    // API requests

    /**
     * Fetches the tokens from the Discord API.<br> Endpoint: {@code /oauth2/token} (<a
     * href="https://discord.com/developers/docs/topics/oauth2#authorization-code-grant-access-token-exchange-example">Discord's documentation</a>).
     *
     * @param code Nonnull code from the URL that the Discord redirected the user to.
     *
     * @return The API request.
     */
    public ApiRequest<DiscordAccessToken> fetchAccessToken(@NonNull String code) {
        String body = "";

        body += "client_id=" + application.getClientId();
        body += "&client_secret=" + application.getClientSecret();
        body += "&grant_type=authorization_code";
        body += "&code=" + code;
        body += "&redirect_uri=" + application.getRedirectUrl();

        return ApiRequest.builder(this, DiscordAccessToken.class)
                         .withEndpoint("/oauth2/token")
                         .withRequestMethod(RequestMethod.POST)
                         .withRequestHeader(RequestHeader.ofContentType("application/x-www-form-urlencoded"))
                         .withBodyPublisher(HttpRequest.BodyPublishers.ofString(body))
                         .build();
    }

    /**
     * Refreshes the access token.<br> Endpoint: {@code /oauth2/token} (<a
     * href="https://discord.com/developers/docs/topics/oauth2#authorization-code-grant-refresh-token-exchange-example">Discord's documentation</a>
     *
     * @param refreshToken Nonnull refresh token.
     *
     * @return The API request.
     */
    public ApiRequest<DiscordAccessToken> refreshAccessToken(@NonNull String refreshToken) {
        String body = "";

        body += "client_id=" + application.getClientId();
        body += "&client_secret=" + application.getClientSecret();
        body += "&grant_type=refresh_token";
        body += "&refresh_token=" + refreshToken;

        return ApiRequest.builder(this, DiscordAccessToken.class)
                         .withEndpoint("/oauth2/token")
                         .withRequestMethod(RequestMethod.POST)
                         .withRequestHeader(RequestHeader.ofContentType("application/x-www-form-urlencoded"))
                         .withBodyPublisher(HttpRequest.BodyPublishers.ofString(body))
                         .build();
    }

    /**
     * Revoke tokens.<br> Endpoint: {@code /oauth2/token/revoke} (<a
     * href="https://discord.com/developers/docs/topics/oauth2#authorization-code-grant-token-revocation-example">Discord's documentation</a>).<br>
     * <br> Note: This request will not return any response when successful.
     *
     * @param accessToken Nonnull access token
     *
     * @return The API request.
     */
    public ApiRequest<DiscordApiResponse> revokeTokens(@NonNull String accessToken) {
        String body = "";

        body += "token=" + accessToken;
        body += "&client_id=" + application.getClientId();
        body += "&client_secret=" + application.getClientSecret();

        return ApiRequest.builder(this, DiscordApiResponse.class)
                         .withEndpoint("/oauth2/token/revoke")
                         .withRequestMethod(RequestMethod.POST)
                         .withRequestHeader(RequestHeader.ofContentType("application/x-www-form-urlencoded"))
                         .withBodyPublisher(HttpRequest.BodyPublishers.ofString(body))
                         .build();
    }

}
