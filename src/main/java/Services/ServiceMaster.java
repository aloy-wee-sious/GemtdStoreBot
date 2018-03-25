package Services;

import Services.Service.DiscordService;
import Services.Service.Service;
import Services.ServiceConfigs.DiscordConfig;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ServiceMaster {

    public static Map<String, Service> initServices() throws IOException {
        Map<String, Service> services = new HashMap<>();
        File directory = new File("Gemtd");
        if (!directory.exists()) {
            directory.mkdir();
        }
        String content = new String(Files.readAllBytes(Paths.get("Gemtd/services.json")));
        JSONObject configJson = new JSONObject(content);
        JSONObject discordConfigJson = configJson.has("discord_config") ? configJson.getJSONObject("discord_config") : null;
        if (discordConfigJson != null) {
            DiscordService discordService = new DiscordService(new DiscordConfig(discordConfigJson));
            services.put("discord", discordService);
        }
        return services;
    }
}
