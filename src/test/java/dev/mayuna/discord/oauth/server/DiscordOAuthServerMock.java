package dev.mayuna.discord.oauth.server;

import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.javalin.http.Context;
import lombok.Getter;

import java.util.Random;
import java.util.UUID;

@Getter
public class DiscordOAuthServerMock {

    private final int port;
    private final Javalin javalin;

    private String clientId;
    private String clientSecret;
    private String code;
    private String redirectUrl;
    private String scope;

    private String lastAccessToken;
    private String lastRefreshToken;

    /**
     * Creates a new DiscordOAuthServerMock instance.
     *
     * @param clientId     The client ID.
     * @param clientSecret The client secret.
     * @param code         The code.
     * @param redirectUrl  The redirect URL.
     */
    public DiscordOAuthServerMock(String clientId, String clientSecret, String code, String redirectUrl, String scope) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.code = code;
        this.redirectUrl = redirectUrl;
        this.scope = scope;

        this.port = new Random().nextInt(65535 - 1024) + 1024;
        this.javalin = Javalin.create();
    }

    /**
     * Gets the URL of the server.
     *
     * @return The URL of the server.
     */
    public String getUrl() {
        return "http://localhost:" + port;
    }

    /**
     * Starts the server.
     */
    public void start() {
        prepareEndpoints();

        javalin.start("localhost", port);
    }

    /**
     * Stops the server.
     */
    public void stop() {
        javalin.stop();
    }

    private void prepareEndpoints() {
        javalin.post("/oauth2/token", ctx -> {
            String contentType = ctx.header("Content-Type");

            if (contentType == null || !contentType.equals("application/x-www-form-urlencoded")) {
                processCtxAsError(ctx, "invalid_request (contentType is null or is not application/x-www-form-urlencoded)", null);
                return;
            }

            String body = ctx.body();

            String[] bodyParts = body.split("&");

            String requestClientId = null;
            String requestClientSecret = null;
            String requestCode = null;
            String requestRedirectUrl = null;
            String grantType = null;
            String refreshToken = null;

            for (String bodyPart : bodyParts) {
                String[] bodyPartParts = bodyPart.split("=");

                String key = bodyPartParts[0];
                String value = bodyPartParts[1];

                switch (key) {
                    case "client_id":
                        requestClientId = value;
                        break;
                    case "client_secret":
                        requestClientSecret = value;
                        break;
                    case "code":
                        requestCode = value;
                        break;
                    case "redirect_uri":
                        requestRedirectUrl = value;
                        break;
                    case "grant_type":
                        grantType = value;
                        break;
                    case "refresh_token":
                        refreshToken = value;
                        break;
                }
            }

            if (requestClientId == null || !requestClientId.equals(clientId)) {
                processCtxAsError(ctx, "invalid_client (client_id is missing or unknown)", "Invalid client ID.");
                return;
            }

            if (requestClientSecret == null || !requestClientSecret.equals(clientSecret)) {
                processCtxAsError(ctx, "invalid_client (client_secret is missing or unknown", "Invalid client secret.");
                return;
            }

            if (grantType == null) {
                processCtxAsError(ctx, "unsupported_grant_type (grant_type is missing)", null);
                return;
            }

            switch (grantType) {
                case "authorization_code":
                    if (requestRedirectUrl == null || !requestRedirectUrl.equals(redirectUrl)) {
                        processCtxAsError(ctx, "invalid_request (redirect_url is missing or unknown)", "Invalid redirect URL.");
                        return;
                    }

                    if (requestCode == null || !requestCode.equals(code)) {
                        processCtxAsError(ctx, "invalid_request (code is missing or unknown)", "Invalid code.");
                        return;
                    }

                    lastAccessToken = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
                    lastRefreshToken = UUID.randomUUID().toString().replace("-", "").substring(0, 32);

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("access_token", lastAccessToken);
                    jsonObject.addProperty("token_type", "Bearer");
                    jsonObject.addProperty("expires_in", 604800);
                    jsonObject.addProperty("refresh_token", lastRefreshToken);
                    jsonObject.addProperty("scope", scope);

                    ctx.status(200);
                    ctx.contentType("application/json");
                    ctx.result(jsonObject.toString());
                    break;
                case "refresh_token":
                    if (lastRefreshToken == null) {
                        processCtxAsError(ctx, "invalid_request (lastRefreshToken is null (you must fetch tokens before refreshing them!))", "Invalid refresh token.");
                        return;
                    }

                    if (refreshToken == null || !refreshToken.equals(lastRefreshToken)) {
                        processCtxAsError(ctx, "invalid_request (refresh_token is missing or unknown)", "Invalid refresh token.");
                        return;
                    }

                    lastAccessToken = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
                    lastRefreshToken = UUID.randomUUID().toString().replace("-", "").substring(0, 32);

                    JsonObject jsonObjectRefresh = new JsonObject();
                    jsonObjectRefresh.addProperty("access_token", lastAccessToken);
                    jsonObjectRefresh.addProperty("token_type", "Bearer");
                    jsonObjectRefresh.addProperty("expires_in", 604800);
                    jsonObjectRefresh.addProperty("refresh_token", lastRefreshToken);
                    jsonObjectRefresh.addProperty("scope", scope);

                    ctx.status(200);
                    ctx.contentType("application/json");
                    ctx.result(jsonObjectRefresh.toString());
                    break;
                default:
                    processCtxAsError(ctx, "unsupported_grant_type (grant_type is not authorization_code or refresh_token)", null);
                    break;
            }
        });

        javalin.post("/oauth2/token/revoke", ctx -> {
            String contentType = ctx.header("Content-Type");

            if (contentType == null || !contentType.equals("application/x-www-form-urlencoded")) {
                processCtxAsError(ctx, "invalid_request (contentType is null or is not application/x-www-form-urlencoded)", null);
                return;
            }

            String body = ctx.body();

            String[] bodyParts = body.split("&");
            String requestClientId = null;
            String requestClientSecret = null;
            String requestToken = null;

            for (String bodyPart : bodyParts) {
                String[] bodyPartParts = bodyPart.split("=");

                String key = bodyPartParts[0];
                String value = bodyPartParts[1];

                switch (key) {
                    case "client_id":
                        requestClientId = value;
                        break;
                    case "client_secret":
                        requestClientSecret = value;
                        break;
                    case "token":
                        requestToken = value;
                        break;
                }
            }

            if (requestClientId == null || !requestClientId.equals(clientId)) {
                processCtxAsError(ctx, "invalid_client (client_id is missing or unknown)", "Invalid client ID.");
                return;
            }

            if (requestClientSecret == null || !requestClientSecret.equals(clientSecret)) {
                processCtxAsError(ctx, "invalid_client (client_secret is missing or unknown", "Invalid client secret.");
                return;
            }

            if (requestToken == null || !requestToken.equals(lastAccessToken)) {
                processCtxAsError(ctx, "unsupported_grant_type (requestToken is missing or unknown (check if access_token is passed))", null);
                return;
            }

            lastAccessToken = null;
            lastRefreshToken = null;

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result("{}");
        });
    }

    private void processCtxAsError(Context ctx, String error, String errorDescription) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("error", error);
        jsonObject.addProperty("error_description", errorDescription);

        ctx.status(400);
        ctx.contentType("application/json");
        ctx.result(jsonObject.toString());
    }
}
