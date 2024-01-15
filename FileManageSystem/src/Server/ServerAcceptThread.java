package Server;

import java.io.IOException;
import java.net.Socket;

public class ServerAcceptThread extends Thread {
    private final Socket socket;

    public ServerAcceptThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            new ClientHandler(socket).startHandle();
        } catch (IOException e) {
            System.out.println("ServerAcceptThread: " + socket.getInetAddress() + " 客户端已断开");
        }
    }
}
