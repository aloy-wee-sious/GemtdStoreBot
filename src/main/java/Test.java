import Connector.HTTPConnector;
import configs.BotConfig;

public class Test {

    public static void main(String[] args) throws Exception {
        BotConfig config = new BotConfig();
        //System.out.print(Connector.HTTPConnector.getGoods());
/*        HTTPConnector.postDiscord(
                config.getDiscordConfig().getWebhook(),
                config.getDiscordConfig().getUsername(),
                config.getDiscordConfig().getAvatarUrl(),
                "integration with java connector test");*/
    }
}
