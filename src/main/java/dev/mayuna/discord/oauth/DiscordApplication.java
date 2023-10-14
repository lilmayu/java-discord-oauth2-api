package dev.mayuna.discord.oauth;

import dev.mayuna.discord.api.DiscordApi;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Holds Discord application's information, such as the client ID, client secret, redirect URL and scopes.<br>You may also here change the Discord's
 * API URL to request and the authorization URL format.
 */
@Getter
public class DiscordApplication {

    private final String apiUrl;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUrl;
    private final String[] scopes;
    private final DiscordOAuthAuthorizationUrlFactory authorizationUrlFactory;

    /**
     * Creates a new DiscordApplication instance.
     *
     * @param apiUrl                  Nonnull Discord's API URL.
     * @param clientId                Nonnull client ID of the application.
     * @param clientSecret            Nonnull client secret of the application.
     * @param redirectUrl             Nonnull redirect URL of the application.
     * @param authorizationUrlFactory Nonnull authorization URL factory.
     * @param scopes                  The scopes of the application.
     */
    public DiscordApplication(@NonNull String apiUrl, @NonNull String clientId, @NonNull String clientSecret, @NonNull String redirectUrl, @NonNull DiscordOAuthAuthorizationUrlFactory authorizationUrlFactory, @Nullable String... scopes) {
        this.apiUrl = apiUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUrl = redirectUrl;
        this.authorizationUrlFactory = authorizationUrlFactory;
        this.scopes = scopes;
    }

    /**
     * Creates an authorization URL with the given state and prompt.
     *
     * @param state  State
     * @param prompt Prompt
     *
     * @return The authorization URL.
     */
    public String createAuthorizationUrl(@Nullable String state, @Nullable String prompt) {
        return authorizationUrlFactory.createAuthorizationUrl(this, state, prompt);
    }

    /**
     * Creates an authorization URL with the given state.
     *
     * @param state State
     *
     * @return The authorization URL.
     */
    public String createAuthorizationUrl(@Nullable String state) {
        return authorizationUrlFactory.createAuthorizationUrl(this, state);
    }

    /**
     * Creates an authorization URL.
     * @return The authorization URL.
     */
    public String createAuthorizationUrl() {
        return authorizationUrlFactory.createAuthorizationUrl(this);
    }

    /**
     * Builder for {@link DiscordApplication}.
     */
    @Getter
    public static class Builder {

        private String apiUrl = DiscordApi.DEFAULT_API_URL;
        private String clientId;
        private String clientSecret;
        private String redirectUrl;
        private String[] scopes = null;
        private DiscordOAuthAuthorizationUrlFactory authorizationUrlFactory = new DiscordOAuthAuthorizationUrlFactory();

        /**
         * Creates new {@link DiscordApplication.Builder} instance.<br>API URL is set to {@link DiscordApi#DEFAULT_API_URL}
         */
        public Builder() {
        }

        /**
         * Sets the Discord's API URL.
         *
         * @param apiUrl Nonnull Discord's API URL.
         *
         * @return The DiscordApplication.Builder instance.
         */
        public @NotNull Builder withApiUrl(@NonNull String apiUrl) {
            this.apiUrl = apiUrl;
            return this;
        }

        /**
         * Sets the client ID of the application.
         *
         * @param clientId Nonnull client ID of the application.
         *
         * @return The DiscordApplication.Builder instance.
         */
        public @NotNull Builder withClientId(@NonNull String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         * Sets the client secret of the application.
         *
         * @param clientSecret Nonnull client secret of the application.
         *
         * @return The DiscordApplication.Builder instance.
         */
        public @NotNull Builder withClientSecret(@NonNull String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        /**
         * Sets the redirect URL of the application.
         *
         * @param redirectUrl Nonnull redirect URL of the application.
         *
         * @return The DiscordApplication.Builder instance.
         */
        public @NotNull Builder withRedirectUrl(@NonNull String redirectUrl) {
            this.redirectUrl = redirectUrl;
            return this;
        }

        /**
         * Sets the scopes of the application.
         *
         * @param scopes Nonnull scopes of the application.
         *
         * @return The DiscordApplication.Builder instance.
         */
        public @NotNull Builder withScopes(@NonNull String... scopes) {
            this.scopes = scopes;
            return this;
        }

        /**
         * Sets the authorization URL factory of the application.
         *
         * @param authorizationUrlFactory Nonnull authorization URL factory of the application.
         *
         * @return The DiscordApplication.Builder instance.
         */
        public @NotNull Builder withAuthorizationUrlFactory(@NonNull DiscordOAuthAuthorizationUrlFactory authorizationUrlFactory) {
            this.authorizationUrlFactory = authorizationUrlFactory;
            return this;
        }

        /**
         * Builds the DiscordApplication instance.
         *
         * @return The DiscordApplication instance.
         */
        public @NotNull DiscordApplication build() {
            if (clientId == null || clientSecret == null || redirectUrl == null) {
                throw new IllegalStateException("Client ID, client secret and redirect URL must be set.");
            }

            return new DiscordApplication(apiUrl, clientId, clientSecret, redirectUrl, authorizationUrlFactory, scopes);
        }
    }
}
