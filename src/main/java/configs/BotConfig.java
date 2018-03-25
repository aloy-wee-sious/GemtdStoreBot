package configs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BotConfig {
    private final String BOT_USERNAME;
    private final String BOT_TOKEN;
    private final Long ADMIN_ID;

    public BotConfig() throws IOException, JSONException {
        File directory = new File("Gemtd");
        if (!directory.exists()) {
            directory.mkdir();
        }
        String content = new String(Files.readAllBytes(Paths.get("Gemtd/config.json")));
        JSONObject configJson = new JSONObject(content);
        this.BOT_TOKEN = configJson.getString("bot_token");
        this.BOT_USERNAME = configJson.getString("bot_username");
        this.ADMIN_ID = configJson.getLong("admin_id");
    }

    public String getBotToken() {
        return BOT_TOKEN;
    }

    public String getBotUsername() {
        return BOT_USERNAME;
    }

    public Long getAdminId() {
        return ADMIN_ID;
    }
}
