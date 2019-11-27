import java.io.IOException;                 // IOExceptions
import java.net.UnknownHostException;       // IOExceptions
import java.net.InetAddress;                // InetAddress
import java.net.ServerSocket;               // Sockets
import java.net.Socket;                     // Sockets
import java.util.concurrent.ExecutorService;// Cached Thread Pooling
import java.util.concurrent.Executors;      // Cached Thread Pooling

class SyncBlockingServer {

    private static ServerSocket server;
    private static ExecutorService executorService = Executors.newFixedThreadPool(200);

    static void start() throws IOException{
        try {
            int DEFAULT_BACKLOG = 1024;
            int DEFAULT_PORT = 8080;
            InetAddress DEFAULT_ADDR = InetAddress.getLocalHost();
            start(DEFAULT_PORT, DEFAULT_BACKLOG, DEFAULT_ADDR);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private synchronized static void start(int port, int backlog, InetAddress bindAddr) throws IOException{
        if (server != null) return;
        try {
            server = new ServerSocket(port, backlog, bindAddr);
            System.out.println("SyncBlockingServer initiated. Listening at: " + bindAddr + ":" + port + ", backlog = " + backlog);
            while (true) {
                Socket socket = server.accept();
                executorService.execute(new HttpParser(socket)); // N:M
//                new Thread(new HttpParser(socket)).start();      // 1:1
            }
        } finally {
            if (server != null) {
                System.out.println("SyncBlockingServer shut down.");
                server.close();
                server = null;
            }
        }
    }
}
