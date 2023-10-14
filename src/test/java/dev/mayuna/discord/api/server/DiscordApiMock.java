package dev.mayuna.discord.api.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.mayuna.discord.api.entities.DiscordUser;
import io.javalin.Javalin;
import io.javalin.http.Context;
import lombok.Getter;

import java.util.Random;

@Getter
public class DiscordApiMock {

    private final int port;
    private final Javalin javalin;

    private final String userAccessToken;
    private final DiscordUser discordUser;

    public DiscordApiMock(String userAccessToken, DiscordUser discordUser) {
        this.userAccessToken = userAccessToken;
        this.discordUser = discordUser;

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
        javalin.get("/users/@me", this::handleGetUser);
    }

    private void handleGetUser(Context context) {
        // Check access token
        String authorization = context.header("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            processCtxAsError(context, "invalid_request", "Missing or invalid Authorization header");
            return;
        }

        String accessToken = authorization.substring("Bearer ".length());

        if (!accessToken.equals(userAccessToken)) {
            processCtxAsError(context, "invalid_token", "Invalid access token");
            return;
        }

        context.status(200);
        context.result(new Gson().toJsonTree(discordUser).toString());
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
