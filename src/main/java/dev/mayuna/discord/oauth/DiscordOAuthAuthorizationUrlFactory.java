package dev.mayuna.discord.oauth;

import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

/**
 * Used to create Discord's authorization URL for users.<br>Extend this class to create your own authorization URL factory.
 */
@Getter
public class DiscordOAuthAuthorizationUrlFactory {

    public static final String DEFAULT_AUTHORIZE_URL = "https://discord.com/oauth2/authorize";

    private final String baseAuthorizeUrl;

    /**
     * Creates a new DiscordOAuthAuthorizationUrlFactory instance.
     *
     * @param baseAuthorizeUrl The authorization URL.
     */
    public DiscordOAuthAuthorizationUrlFactory(String baseAuthorizeUrl) {
        this.baseAuthorizeUrl = baseAuthorizeUrl;
    }

    /**
     * Creates a new DiscordOAuthAuthorizationUrlFactory instance with the default URLs.
     */
    public DiscordOAuthAuthorizationUrlFactory() {
        this(DEFAULT_AUTHORIZE_URL);
    }

    /**
     * Creates full authorization URL with state.
     *
     * @param application Nonnull Discord application.
     * @param state       Nullable state.
     * @param prompt      Nullable prompt.
     *
     * @return The full authorization URL.
     */
    public String createAuthorizationUrl(@NonNull DiscordApplication application, @Nullable String state, @Nullable String prompt) {
        String url = baseAuthorizeUrl + "?client_id=" + application.getClientId() + "&redirect_uri=" + application.getRedirectUrl() + "&response_type=code";

        if (application.getScopes() != null) {
            url += "&scope=" + String.join("%20", application.getScopes());
        }

        if (state != null) {
            url += "&state=" + state;
        }

        if (prompt != null) {
            url += "&prompt=" + prompt;
        }

        return url;
    }

    /**
     * Creates full authorization URL with state.
     *
     * @param application The Discord application.
     * @param state       Nullable state.
     *
     * @return The full authorization URL.
     */
    public String createAuthorizationUrl(@NonNull DiscordApplication application, @Nullable String state) {
        return createAuthorizationUrl(application, state, null);
    }

    /**
     * Creates full authorization URL without state and prompt.
     *
     * @param application Nonnull Discord application.
     *
     * @return The full authorization URL.
     */
    public String createAuthorizationUrl(@NonNull DiscordApplication application) {
        return createAuthorizationUrl(application, null, null);
    }
}
