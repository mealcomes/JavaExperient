package console;


import Server.REQUEST;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Objects;

/**
 * 数据处理类
 */
public class DataProcessing {
    static Socket socket;
    static Hashtable<String, AbstractUser> users;
    static Hashtable<String, Doc> docs;
    public static boolean isConnectedToServer = true;
    public static boolean isReadInfoSuccess = true;
    static ObjectOutputStream oos;
    static ObjectInputStream ois;

    public static void init() {
        connectToServer();
//        readUserAndFileInfo();
    }

    /**
     * 向服务端发送读取用户和文件信息请求
     */
    public static void readUserAndFileInfo() {
        try {
            oos.writeObject(new REQUEST.readUserAndFileInfo());
            oos.flush();
            Object tmp = ois.readObject();
            if (tmp instanceof REQUEST.returnedUserAndFileInfo) {
                docs = ((REQUEST.returnedUserAndFileInfo) tmp).getFileInfo();
                users = ((REQUEST.returnedUserAndFileInfo) tmp).getUserInfo();
            } else {
                System.out.println("DataProcessing.readUserAndFileInfo:读取文件和用户信息失败");
                isReadInfoSuccess = false;
            }
        } catch (IOException e) {
            isReadInfoSuccess = false;
            System.out.println("DataProcessing.readUserAndFileInfo():流错误");
        } catch (ClassNotFoundException e) {
            isReadInfoSuccess = false;
            System.out.println("DataProcessing.readUserAndFileInfo():类错误");
        }
    }

    /**
     * 读取目标文件到byte数组中，并调用本类中的其他方法向服务端发送上传文件请求
     */
    public static boolean uploadFile(Doc doc, File sourceFile) {
        try {
            BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(sourceFile.toPath()));
            byte[] buffer = new byte[(int) sourceFile.length()];
            bis.read(buffer);
            bis.close();

            return sendUploadFileRequest(doc, buffer);
        } catch (IOException e) {
            System.out.println("DataProcessing.uploadFile中发生错误");
            return false;
        }
    }

    /**
     * 向服务端发送更新数据库文件信息请求
     */
    public static boolean sendUploadFileRequest(Doc doc, byte[] file) {
        if (socket.isClosed()) {
            System.out.println("服务器已断开!");
            isConnectedToServer = false;
            return false;
        }
        try {
            oos.writeObject(new REQUEST.uploadFileInfo(doc, file));
            oos.flush();
            Object tmp = ois.readObject();
            REQUEST.respond respond = (REQUEST.respond) tmp;
            return Objects.equals(respond.getFlag(), "Y");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("DataProcessing.sendUploadFileRequest中发生错误");
            return false;
        }
    }

    /**
     * 向服务端发送下载文件的请求
     */
    public static boolean sendDownloadFileRequest(File destFile) {
        if (socket.isClosed()) {
            if (socket.isClosed()) {
                System.out.println("服务器已断开!");
                isConnectedToServer = false;
                return false;
            }
        }
        try {
            oos.writeObject(new REQUEST.downloadFile(destFile.getName()));
            oos.flush();
            Object tmp = ois.readObject();

            REQUEST.returnedFile returnedFile;
            if (tmp instanceof REQUEST.returnedFile) {
                returnedFile = (REQUEST.returnedFile) tmp;
            } else return false;

            byte[] fileContent = returnedFile.getFileContent();
            BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(destFile.toPath()));
            bos.write(fileContent);
            bos.close();
            return true;
        } catch (IOException e) {
            System.out.println("DataProcessing.sendDownloadFileRequest:流错误！");
            return false;
        } catch (ClassNotFoundException e) {
            System.out.println("DataProcessing.sendDownloadFileRequest:类错误！");
            return false;
        }
    }

    /**
     * 添加用户后更新数据库信息
     */
    public static boolean addUserInfoInDatabase(AbstractUser user) {
        return sendUpdateUserInfoRequest(user, 0);
    }

    /**
     * 删除用户后更新数据库信息
     */
    public static boolean deleteUserInfoInDatabase(AbstractUser user) {
        return sendUpdateUserInfoRequest(user, 1);
    }

    /**
     * 修改密码后更新数据库信息
     */
    public static boolean changeInfoInDatabase(AbstractUser user) {
        return sendUpdateUserInfoRequest(user, 2);
    }

    /**
     * 向服务端发送更新用户信息请求
     */
    public static boolean sendUpdateUserInfoRequest(AbstractUser user, int Modify) {
        try {
            oos.writeObject(new REQUEST.updateUserInfo(Modify, user.getName(), user.getPassword(), user.getRole()));
            oos.flush();

            REQUEST.respond tmp = (REQUEST.respond) ois.readObject();
            return Objects.equals(tmp.getFlag(), "Y");
        } catch (IOException e) {
            System.out.println("DataProcessing.addUserInfoInDatabase:流错误！");
            return false;
        } catch (ClassNotFoundException e) {
            System.out.println("DataProcessing.addUserInfoInDatabase:类错误！");
            return false;
        }
    }


    /**
     * 按档案编号搜索档案信息，返回null时表明未找到
     *
     * @return Doc
     */
    public static Doc searchDoc(String id) {
        if (docs.containsKey(id)) {
            return docs.get(id);
        }
        return null;
    }

    /**
     * 列出所有档案信息
     *
     * @return Enumeration<Doc>
     */
    public static Enumeration<Doc> listDoc() {
        return docs.elements();
    }

    /**
     * 插入新的档案
     */
    public static void insertDoc(String id, String creator, Timestamp timestamp, String description, String filename) {
        Doc doc;
        doc = new Doc(id, creator, timestamp, description, filename);
        docs.put(id, doc);
    }

    /**
     * 按用户名搜索用户，返回null时表明未找到符合条件的用户
     *
     * @param name 用户名
     * @return AbstractUser
     */
    public static AbstractUser searchUser(String name) {
        if (users.containsKey(name)) {
            return users.get(name);
        }
        return null;
    }

    /**
     * 按用户名、密码搜索用户，返回null时表明未找到符合条件的用户
     *
     * @param name     用户名
     * @param password 密码
     * @return AbstractUser
     */
    public static AbstractUser searchUser(String name, String password) {
        if (users.containsKey(name)) {
            AbstractUser temp = users.get(name);
            if ((temp.getPassword()).equals(password)) {
                return temp;
            }
        }
        return null;
    }

    /**
     * 取出所有的用户
     *
     * @return Enumeration<AbstractUser>
     */
    public static Enumeration<AbstractUser> listUser() {
        return users.elements();
    }

    /**
     * 修改本地用户信息
     */
    public static boolean updateUser(String name, String password, String role) {
        AbstractUser user;
        if (users.containsKey(name)) {
            switch (ROLE_ENUM.valueOf(role.toLowerCase())) {
                case administrator:
                    user = new Administrator(name, password, role);
                    break;
                case operator:
                    user = new Operator(name, password, role);
                    break;
                default:
                    user = new Browser(name, password, role);
            }
            users.put(name, user);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 在本地插入新用户
     */
    public static boolean insertUser(String name, String password, String role) {
        AbstractUser user;
        if (users.containsKey(name)) {
            return false;
        } else {
            switch (ROLE_ENUM.valueOf(role.toLowerCase())) {
                case administrator:
                    user = new Administrator(name, password, role);
                    break;
                case operator:
                    user = new Operator(name, password, role);
                    break;
                default:
                    user = new Browser(name, password, role);
            }
            users.put(name, user);
            return true;
        }
    }

    /**
     * 删除本地指定用户
     */
    public static boolean deleteUser(String name) {
        if (users.containsKey(name)) {
            users.remove(name);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 连接服务端
     */
    private static void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", 10086);
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("----DataProcessing.connectToServer:服务端连接成功");
        } catch (IOException e) {
            isConnectedToServer = false;
            System.out.println("----DataProcessing.connectToServer:服务端连接失败");
        }
    }

    public static enum ROLE_ENUM {
        /**
         * administrator
         */
        administrator("administrator"),
        /**
         * operator
         */
        operator("operator"),
        /**
         * browser
         */
        browser("browser");

        private final String role;

        ROLE_ENUM(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }
    }
}

