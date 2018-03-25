public class Parser {

    private static final String ADMIN_COMMAND_START = "START";
    private static final String ADMIN_COMMAND_KILL = "KILL";
    private static final String ADMIN_COMMAND_RESUME = "RESUME";
    private static final String ADMIN_COMMAND_STOP = "STOP";
    private static final String ADMIN_COMMAND_HELP = "HELP";
    private static final String ADMIN_COMMAND_SHOW = "SHOW";

    public static final String ADMIN_HELP = "Start to enslave\n" +
            "Resume to enslave\n" +
            "Stop (service name) to stop service\n" +
            "Kill to free slave\n" +
            "Show to see goods";

    private static final String USER_COMMAND_SUB = "SUB";
    private static final String USER_COMMAND_STOP = "STOP";
    private static final String USER_COMMAND_HELP = "HELP";
    private static final String USER_COMMAND_SHOW = "SHOW";

    public static final String USER_HELP = "Type anything to subscribe\n" +
            "Stop to Unsub\n" +
            "Show to see goods";


    public enum AdminCommands {
        START,
        KILL,
        RESUME,
        STOP,
        SHOW,
        HELP,
        INVALID
    }

    public enum UserCommands {
        STOP,
        SHOW,
        HELP,
        SUB,
        INVALID
    }

    public static AdminCommands parseAdminCommand(String command) {
        switch (command.toUpperCase()) {
            case ADMIN_COMMAND_START:
                return AdminCommands.START;
            case ADMIN_COMMAND_KILL:
                return AdminCommands.KILL;
            case ADMIN_COMMAND_RESUME:
                return AdminCommands.RESUME;
            case ADMIN_COMMAND_STOP:
                return AdminCommands.STOP;
            case ADMIN_COMMAND_SHOW:
                return AdminCommands.SHOW;
            case ADMIN_COMMAND_HELP:
                return AdminCommands.HELP;
            default:
                return AdminCommands.INVALID;
        }
    }

    public static UserCommands parseUserCommand(String command) {
        switch (command.toUpperCase()) {
            case USER_COMMAND_STOP:
                return UserCommands.STOP;
            case USER_COMMAND_SHOW:
                return UserCommands.SHOW;
            case USER_COMMAND_HELP:
                return UserCommands.HELP;
            case USER_COMMAND_SUB:
                return UserCommands.SUB;
            default:
                return UserCommands.INVALID;
        }
    }
}
