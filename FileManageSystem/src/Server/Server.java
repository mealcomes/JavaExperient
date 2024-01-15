package Server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(10086);
        System.out.println("服务器启动，等待客户端连接...");
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println(new Date().toString() + socket.getInetAddress() + " 已连接");
            new ServerAcceptThread(socket).start();
        }
    }
}
