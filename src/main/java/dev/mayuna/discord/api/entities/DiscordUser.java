package dev.mayuna.discord.api.entities;

import com.google.gson.annotations.SerializedName;
import dev.mayuna.discord.api.DiscordApiResponse;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public class DiscordUser extends DiscordApiResponse {

    private String id;
    private String username;
    private String discriminator;
    private @SerializedName("global_name") String globalName;
    private @SerializedName("avatar") String avatarHash;
    private @Nullable Boolean bot;
    private @Nullable Boolean system;
    private @Nullable @SerializedName("mfa_enabled") Boolean mfaEnabled;
    private @Nullable @SerializedName("accent_color") Integer accentColor;
    private @Nullable String locale;
    private @Nullable Boolean verified;
    private @Nullable String email;
    private @Nullable Integer flags;
    private @Nullable @SerializedName("premium_type") Integer premiumType;
    private @Nullable @SerializedName("public_flags") Integer publicFlags;
    private @Nullable @SerializedName("avatar_decoration") String avatarDecorationHash;

    /**
     * Gets the user's ID as a long.
     *
     * @return The user's ID as a long.
     */
    public long getIdAsLong() {
        return Long.parseLong(id);
    }
}
