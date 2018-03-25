package Services.ServiceConfigs;

import Services.Service.DiscordService;
import Services.Service.Service;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DiscordConfig extends ServicesConfig {
    private final String WEB_HOOK;
    private final String USERNAME;
    private final String AVATAR_URL;

    public DiscordConfig(JSONObject discordConfigJson) {
        this.WEB_HOOK = discordConfigJson.getString("discord_webhook");
        this.USERNAME = discordConfigJson.getString("discord_username");
        this.AVATAR_URL = discordConfigJson.getString("discord_avatar");
        super.setEnabled(discordConfigJson.getBoolean("discord_enable_on_start"));
    }


    public Map<String, Service> initServices(JSONObject configJson) {
        Map<String, Service> services = new HashMap<>();

        DiscordConfig discordConfig;
        discordConfig = configJson.has("discord_config") ? new DiscordConfig(configJson.getJSONObject("discord_config")) : null;
        if (discordConfig != null) {
            services.put("discord", new DiscordService(discordConfig));
        }

        return services;
    }

    public String getWebhook() {
        return WEB_HOOK;
    }

    public String getUsername() {
        return USERNAME;
    }

    public String getAvatarUrl() {
        return AVATAR_URL;
    }
}
