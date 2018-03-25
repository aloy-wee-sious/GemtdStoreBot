package Services.Service;

import Connector.HTTPConnector;
import Services.ServiceConfigs.DiscordConfig;

public class DiscordService implements Service {

    private final DiscordConfig config;

    public DiscordService(DiscordConfig config) {
        this.config = config;
    }

    @Override
    public void handleService(String msg) {
        HTTPConnector.postDiscord(config.getWebhook(),
                config.getUsername(),
                config.getAvatarUrl(),
                msg);

    }

    @Override
    public Boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public void toggleEnabled() {
        config.setEnabled(!config.isEnabled());
    }
}
