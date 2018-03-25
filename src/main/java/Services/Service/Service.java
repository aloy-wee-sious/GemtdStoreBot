package Services.Service;

public interface Service {
    void handleService(String msg);

    Boolean isEnabled();

    void toggleEnabled();
}
