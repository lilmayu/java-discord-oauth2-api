package dev.mayuna.discord.api;

import com.google.gson.annotations.SerializedName;
import dev.mayuna.discord.oauth.DiscordOAuth;
import dev.mayuna.simpleapi.GsonApiResponse;
import lombok.Getter;

@Getter
public class DiscordApiResponse extends GsonApiResponse<DiscordOAuth> {

    protected String error;
    protected @SerializedName("error_description") String errorDescription;

}
