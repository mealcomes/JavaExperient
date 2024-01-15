package Server;

import console.*;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Hashtable;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientHandler {
    private static final String UPLOAD_FILE_PATH = "FileManageSystem/src/data/download/";
    private Connection connection;
    private boolean isConnectedToMysql = false;
    private final Socket socket;
    private ObjectOutputStream oos;
    private final Lock lock = new ReentrantLock();

    ClientHandler(Socket socket) {
        this.socket = Objects.requireNonNull(socket);
        enableConnectToMysql();
    }

    /**
     * 启动请求处理
     */
    public void startHandle() throws IOException {
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        while (true) {
            Object tmp = null;
            try {
                tmp = ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(new java.util.Date() + "ClientHandler.startHandle:客户端("
                        + socket.getInetAddress() + ")断开连接");
                break;
            }

            if (!isConnectedToMysql) {
                oos.writeObject(new REQUEST.notConnectedToMysql());
            }

            if (tmp != null) {
                System.out.println(new java.util.Date() + "客户端(" + socket.getInetAddress() + ")的请求为" + tmp.getClass().getSimpleName());
            }

            lock.lock();
            if (tmp instanceof REQUEST.readUserAndFileInfo) {         //读取文件和用户信息请求
                readFileAndUserInfoHandle();
            } else if (tmp instanceof REQUEST.uploadFileInfo) {        //上传文件请求
                if (uploadFileHandle((REQUEST.uploadFileInfo) tmp))
                    oos.writeObject(new REQUEST.respond("Y"));
                else
                    oos.writeObject(new REQUEST.respond("N"));
            } else if (tmp instanceof REQUEST.downloadFile) {          //下载文件请求
                downloadFileHandle((REQUEST.downloadFile) tmp);
            } else if (tmp instanceof REQUEST.updateUserInfo) {        //更新用户信息请求
                if (updateUserInfoHandle((REQUEST.updateUserInfo) tmp)) {
                    oos.writeObject(new REQUEST.respond("Y"));
                } else
                    oos.writeObject(new REQUEST.respond("N"));
            }
            lock.unlock();
        }
    }

    /**
     * 处理客户端读取文件和用户信息请求
     */
    private void readFileAndUserInfoHandle() {
        Hashtable<String, Doc> docs = new Hashtable<>();
        Hashtable<String, AbstractUser> users = new Hashtable<>();
        if (readFileAndUserInfo(docs, users)) {
            try {
                oos.writeObject(new REQUEST.returnedUserAndFileInfo(docs, users));
                oos.flush();
            } catch (IOException e) {
                System.out.println("ClientHandler.readFileAndUserInfoHandle:流错误");
            }
        } else {
            try {
                oos.writeObject(new REQUEST.respond("N"));
                oos.flush();
            } catch (IOException e) {
                System.out.println("ClientHandler.readFileAndUserInfoHandle:流错误");
            }
        }
    }

    /**
     * 处理客户端的更新用户信息请求
     */
    private boolean updateUserInfoHandle(REQUEST.updateUserInfo request) {
        if (request.getMODIFY() == 0) {
            try {
                String sql = "insert into user values (?, ?, ?)";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, request.getUserName());
                ps.setString(2, request.getUserPassword());
                ps.setString(3, request.getUserRole());
                ps.executeUpdate();

                ps.close();
                return true;
            } catch (SQLException e) {
                System.out.println("ClientHandler.updateUserInfoHandle: 数据库添加用户信息出错!");
                return false;
            }
        } else if (request.getMODIFY() == 1) {
            try {
                String sql = "delete from user where userName = " + "'" + request.getUserName() + "'";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.executeUpdate();
                ps.close();
                return true;
            } catch (SQLException e) {
                System.out.println("ClientHandler.updateUserInfoHandle: 数据库删除用户信息出错!");
                return false;
            }
        } else {
            try {
                String sql = "update user set password = '" + request.getUserPassword() +
                        "' where userName = '" + request.getUserName() + "'";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.executeUpdate();
                ps.close();
                return true;
            } catch (SQLException e) {
                System.out.println("ClientHandler.updateUserInfoHandle: 数据库修改用户信息出错!");
                return false;
            }
        }
    }

    /**
     * 处理客户端的下载文件请求
     */
    private void downloadFileHandle(REQUEST.downloadFile request) {
        try {
            BufferedInputStream bos =
                    new BufferedInputStream(Files.newInputStream(Paths.get(UPLOAD_FILE_PATH + request.getFileName())));
            byte[] bytes = new byte[(int) (new File(UPLOAD_FILE_PATH + request.getFileName()).length())];
            bos.read(bytes);
            bos.close();
            oos.writeObject(new REQUEST.returnedFile(bytes));
        } catch (IOException e) {
            System.out.println("ClientHandler.downloadFileHandle:流打开失败");
        }
    }

    /**
     * 处理客户端的上传文件请求
     */
    private boolean uploadFileHandle(REQUEST.uploadFileInfo request) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream
                    (Files.newOutputStream(Paths.get(UPLOAD_FILE_PATH + request.getDoc().getFilename())));

            bos.write(request.getFileContent());
            bos.flush();
            bos.close();

            return updateFileInfo(request.getDoc());
        } catch (IOException e) {
            System.out.println("ClientHandler.uploadFileHandle中的流打开失败");
            return false;
        }
    }

    /**
     * 读取数据库中的文件和用户信息
     */
    private boolean readFileAndUserInfo(Hashtable<String, Doc> docs, Hashtable<String, AbstractUser> users) {
        try {
            Statement statement;
            ResultSet resultSet;

            statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            //读取文件信息
            String fileSql = "select * from file";
            resultSet = statement.executeQuery(fileSql);
            while (resultSet.next()) {
                String id = resultSet.getString("number");
                String name = resultSet.getString("filename");
                String creator = resultSet.getString("creator");
                Timestamp creatTime = resultSet.getTimestamp("creatTime");
                String fileDescribe = resultSet.getString("fileDescribe");
                Doc doc = new Doc(id, creator, creatTime, fileDescribe, name);
                docs.put(id, doc);
            }

            //读取用户信息
            String userSql = "select * from user";
            resultSet = statement.executeQuery(userSql);
            while (resultSet.next()) {
                String userName = resultSet.getString("userName");
                String userPassword = resultSet.getString("password");
                String role = resultSet.getString("role");
                switch (role) {
                    case "administrator":
                        users.put(userName, new Administrator(userName, userPassword, role));
                        break;
                    case "operator":
                        users.put(userName, new Operator(userName, userPassword, role));
                        break;
                    case "browser":
                        users.put(userName, new Browser(userName, userPassword, role));
                        break;
                }
            }

            resultSet.close();
            statement.close();
            return true;
        } catch (SQLException e) {
            System.out.println("ClientHandler.readFileAndUserInfo:读取信息失败!");
            return false;
        } catch (NullPointerException e) {
            System.out.println("ClientHandler.readFileAndUserInfo:数据库未连接!");
            return false;
        }
    }

    /**
     * 更新数据库中的文件信息
     */
    private boolean updateFileInfo(Doc doc) {
        try {
            String sql = "insert into file values (?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, doc.getFilename());
            ps.setString(2, doc.getID());
            ps.setString(3, doc.getCreator());
            ps.setTimestamp(4, doc.getTimestamp());
            ps.setString(5, doc.getDescription());

            ps.executeUpdate();

            ps.close();
            System.out.println(doc.getFilename() + " 文件上传成功");
            return true;
        } catch (SQLException e) {
            System.out.println("ClientHandler.updateFileInfo中的数据库信息更新失败");
            return false;
        }
    }

    /**
     * 服务端连接数据库
     */
    private void enableConnectToMysql() {
        String driverName = "com.mysql.cj.jdbc.Driver"; // 加载数据库驱动类
        String url = "jdbc:mysql://127.0.0.1:3306/JavaData"; // 声明数据库的URL
        String dsUser = "root"; // 数据库用户
        String password = "123456";
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection(url, dsUser, password); // 建立数据库连接
            isConnectedToMysql = true;
        } catch (ClassNotFoundException e) {
            System.out.println("ClientHandler.enableConnectToMysql:数据库驱动加载失败");
        } catch (SQLException e) {
            System.out.println("ClientHandler.enableConnectToMysql:数据库连接失败");
        }
    }
}
