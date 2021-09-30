package wry;

import java.io.BufferedReader;
// import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
// import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SyncBlockingServerHandler implements Runnable {

    private Socket socket;

    SyncBlockingServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        // BufferedWriter out = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String middle;
            while((middle = in.readLine()) != null) { // fixme
                System.out.println(middle);
                out.println(middle);
            }
        } catch (Exception e) {
            // region Catch IOException
            e.printStackTrace();
            // endregion
        } finally {
            // region Clean up
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }
            if (out != null) {
                out.close();
                out = null;
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
                System.out.println("Connection closed");
            }
            // endregion
        }
    }
}
