import Connector.HTTPConnector;
import Services.Service.Service;
import Services.ServiceMaster;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import configs.BotConfig;
import org.json.JSONException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class GemTDBot extends TelegramLongPollingBot {
    private java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GemTDBot.class.getName());
    private FileHandler handler;

    private BotConfig botConfig;
    private Long adminId;
    private Set<Long> subscribers = new HashSet<>();
    private Map<String, Service> services = new HashMap<>();

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private Future<?> future = null;
    private String previousGoods = "";

    public GemTDBot() {
        try {
            this.botConfig = new BotConfig();
            load();
            adminId = botConfig.getAdminId();
            subscribers.add(adminId);
        } catch (JSONException e) {
            logger.warning("Failed to parse JSON file\n\n" + e.getMessage() + "\n" + "@" + System.currentTimeMillis() + "\n" + "\n\n");
        } catch (IOException e) {
            logger.warning("Failed to read file data\n\n" + e.getMessage() + "\n" + "@" + System.currentTimeMillis() + "\n" + "\n\n");
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().isUserMessage() ||
                !update.getMessage().hasText() || update.getMessage().getText().isEmpty()) {
            logger.info("Empty update received from " + update.getMessage().getChatId() + " @ " + System.currentTimeMillis());
            return;
        }

        Message message = update.getMessage();
        Long id = message.getChatId();
        String[] commands = message.getText().split(" ");

        //Admin commands
        if (id.equals(adminId)) {
            Parser.AdminCommands adminCommand = Parser.parseAdminCommand(commands[0]);
            logger.info("Admin command received: " + adminCommand + " @ " + System.currentTimeMillis());
            switch (adminCommand) {
                case START:
                    try {
                        String goods = HTTPConnector.getGoods();
                        publishResults(goods);
                        startSlave(HTTPConnector.getExpire());
                        sendTextMessages(adminId, "Started");
                    } catch (IOException e) {
                        sendTextMessages(adminId, "Failed to connect: " + e.getMessage());
                        logger.warning("Failed to get goods\n\n" + e.getMessage() + "\n" + "@" + System.currentTimeMillis() + "\n" + "\n\n");
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
                    String reply = (commands[1] + " service");
                    reply = reply.substring(0, 1).toUpperCase() + reply.substring(1);
                    if (services.containsKey(commands[1].toLowerCase())) {
                        services.get(commands[1]).toggleEnabled();
                        sendTextMessages(adminId, (services.get(commands[1]).isEnabled() ? reply + " resumed" : reply + " stopped"));
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
            logger.info("User command received: " + userCommand + " @ " + System.currentTimeMillis());
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
                        logger.warning("Failed to save user file\n\n" + e.getMessage() + "\n" + "@" + System.currentTimeMillis() + "\n" + "\n\n");
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

    /////////////////////////// Logging methods ///////////////////////////

    private void initLog() {
        File logsDirectory = new File("Gemtd/Logs");
        if (!logsDirectory.exists()) {
            logsDirectory.mkdir();
        }

        DateFormat dateFormat = new SimpleDateFormat("dd_MM_yy_HH.mm.ss");
        try {
            handler = new FileHandler(logsDirectory + "/" + dateFormat.format(new Date()) + ".log");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        logger.addHandler(handler);
        SimpleFormatter formatter = new SimpleFormatter();
        handler.setFormatter(formatter);
    }


    /////////////////////////// Communication methods ///////////////////////////

    private void publishResults(String goods) {
        if (!goods.equals(previousGoods)) {
            subscribers.forEach(id -> sendTextMessages(id, goods));
            previousGoods = goods;
            List<String> enabledService = new ArrayList<>();
            for (Map.Entry<String, Service> service : services.entrySet()) {
                if (service.getValue().isEnabled()) {
                    enabledService.add(service.getKey());
                }
            }
            logger.info("Slave sent update to\nSubscribers:\n" + subscribers + "\nServices:\n" + enabledService + "\n");
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
            logger.warning("Failed to reply user\n\n" + e.getMessage() + "\n" + "@" + System.currentTimeMillis() + "\n" + "\n\n");
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
        initLog();
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

    /////////////////////////// Bot stuff ///////////////////////////
    @Override
    public String getBotUsername() {
        return botConfig.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    private class Slave implements Runnable {
        public void run() {
            try {
                String goods = HTTPConnector.getGoods();
                publishResults(goods);

            } catch (IOException e) {
                sendTextMessages(adminId, e.getMessage());
                logger.warning("Failed to get goods\n\n" + e.getMessage() + "\n" + "@" + System.currentTimeMillis() + "\n" + "\n\n");
            }
        }
    }
}
