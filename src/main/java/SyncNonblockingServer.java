public class SyncNonblockingServer {
    private static SyncNonblockingServerHandler serverHandler;
    public static void start(){
        int DEFAULT_PORT = 8080;
        start(DEFAULT_PORT);
    }
    public static synchronized void start(int port){
        if(serverHandler !=null)
            serverHandler.stop();
        serverHandler = new SyncNonblockingServerHandler(port);
        new Thread(serverHandler,"Server").start();
    }
}