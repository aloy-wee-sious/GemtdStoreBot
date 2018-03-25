import Connector.HTTPConnector;
import Services.Service.Service;
import Services.ServiceMaster;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import configs.BotConfig;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GemTDBot extends TelegramLongPollingBot {
    private final Logger log = LoggerFactory.getLogger(GemTDBot.class);

    private BotConfig botConfig;
    private Long adminId;
    private Set<Long> subscribers = new HashSet<>();
    private Map<String, Service> services = new HashMap<>();

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private Future<?> future = null;
    private String previousGoods = "";

    //TODO proper logging

    public GemTDBot() throws IOException, ParseException {
        this.botConfig = new BotConfig();
        try {
            load();
            adminId = botConfig.getAdminId();
            subscribers.add(adminId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().isUserMessage() ||
                !update.getMessage().hasText() || update.getMessage().getText().isEmpty()) {
            return;
        }

        Message message = update.getMessage();
        Long id = message.getChatId();
        String[] commands = message.getText().split(" ");

        //Admin commands
        if (id.equals(adminId)) {
            Parser.AdminCommands adminCommand = Parser.parseAdminCommand(commands[0]);
            switch (adminCommand) {
                case START:
                    try {
                        String goods = HTTPConnector.getGoods();
                        publishResults(goods);
                        startSlave(HTTPConnector.getExpire());
                        sendTextMessages(adminId, "Started");
                    } catch (Exception e) {
                        sendTextMessages(adminId, e.getMessage());
                        log.warn("Failed to connect: " + e.getMessage() + " @ " + System.currentTimeMillis());
                    }
                    return;
                case KILL:
                    if (future != null && !future.isCancelled()) {
                        restSlave();
                        sendTextMessages(adminId, "Stopped");
                    }
                    return;
                case STOP:
                case RESUME:
                    if (services.containsKey(commands[1].toLowerCase())) {
                        services.get(commands[1]).toggleEnabled();
                        sendTextMessages(adminId, commands[1].toLowerCase() + " service stopped");
                    } else {
                        sendTextMessages(adminId, "Service not found");
                    }
                    return;
                case HELP:
                    sendTextMessages(adminId, Parser.ADMIN_HELP);
                    return;
                case SHOW:
                    sendTextMessages(adminId, previousGoods);
                    return;
                case INVALID:
                    sendTextMessages(adminId, "Invalid command. Type \"help\" to view commands");
                    return;
            }
            //User commands
        } else {
            Parser.UserCommands userCommand = Parser.parseUserCommand(commands[0]);
            switch (userCommand) {
                case SHOW:
                    sendTextMessages(adminId, previousGoods);
                    return;
                case STOP:
                    if (subscribers.contains(id)) {
                        subscribers.remove(id);
                    }
                    sendTextMessages(id, "Unsubscribed");
                    try {
                        save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                case HELP:
                    sendTextMessages(adminId, Parser.USER_HELP);
                    return;
                case SUB:
                    if (!subscribers.contains(id)) {
                        subscribers.add(id);
                    }
                    return;
                case INVALID:
                    sendTextMessages(adminId, "Invalid command. Type \"help\" to view commands");
                    return;
            }
        }

    }

    /////////////////////////// Communication methods ///////////////////////////

    private void publishResults(String goods) {
        if (!goods.equals(previousGoods)) {
            subscribers.forEach(id -> sendTextMessages(id, goods));
            previousGoods = goods;

            services.forEach((String name, Service service) -> {
                if (service.isEnabled()) {
                    service.handleService(goods);
                }
            });
        }
    }

    private void sendTextMessages(Long id, String text) {
        try {
            execute(new SendMessage().setChatId(id).setText(text));
        } catch (TelegramApiException e) {
            log.warn("Send reply to " + id + ": " + e.getMessage() + " @ " + System.currentTimeMillis());
            e.printStackTrace();
        }
    }

    /////////////////////////// Memory methods ///////////////////////////

    private void load() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File userFile = new File("Gemtd/users.json");
        if (userFile.exists()) {
            subscribers = objectMapper.readValue(userFile, new TypeReference<HashSet<Long>>() {
            });
        } else {
            userFile.createNewFile();
        }

        this.services = ServiceMaster.initServices();
    }

    private void save() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("Gemtd/users.json"), subscribers);
    }

    /////////////////////////// Slave methods ///////////////////////////

    private void startSlave() {
        startSlave(0);
    }

    private void startSlave(int initialDelay) {
        future = scheduler.scheduleAtFixedRate(new Slave(), initialDelay + 300, 43200, TimeUnit.SECONDS);
    }

    private void restSlave() {
        future.cancel(true);
    }

    private class Slave implements Runnable {
        public void run() {
            try {
                String goods = HTTPConnector.getGoods();
                publishResults(goods);
            } catch (Exception e) {
                sendTextMessages(adminId, e.getMessage());
                log.warn("Failed to connect: " + e.getMessage() + " @ " + System.currentTimeMillis());
            }
        }
    }

    /////////////////////////// Bot stuff ///////////////////////////
    @Override
    public String getBotUsername() {
        return botConfig.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }
}
