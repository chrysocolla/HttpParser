import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        new Thread(() -> {
            try {
                SyncBlockingServer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
