package Server;

import console.AbstractUser;
import console.Doc;

import java.io.Serializable;
import java.util.Hashtable;

public class REQUEST {
    public static class readUserAndFileInfo implements Serializable {
    }

    public static class returnedUserAndFileInfo implements Serializable {
        private final Hashtable<String, Doc> fileInfo;
        private final Hashtable<String, AbstractUser> userInfo;

        public returnedUserAndFileInfo(Hashtable<String, Doc> fileInfo, Hashtable<String, AbstractUser> userInfo) {
            this.fileInfo = fileInfo;
            this.userInfo = userInfo;
        }

        public Hashtable<String, Doc> getFileInfo() {
            return fileInfo;
        }

        public Hashtable<String, AbstractUser> getUserInfo() {
            return userInfo;
        }
    }

    public static class downloadFile implements Serializable {
        private final String fileName;

        public downloadFile(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }
    }

    public static class returnedFile implements Serializable {
        private final byte[] fileContent;

        public returnedFile(byte[] fileContent) {
            this.fileContent = fileContent;
        }

        public byte[] getFileContent() {
            return fileContent;
        }
    }

    public static class updateUserInfo implements Serializable {
        private final int MODIFY;  //0:添加用户  1:删除用户  2:修改用户密码
        private final String userName;
        private final String userPassword;
        private final String userRole;

        public updateUserInfo(int MODIFY, String userName, String userPassword, String userRole) {
            this.MODIFY = MODIFY;
            this.userName = userName;
            this.userPassword = userPassword;
            this.userRole = userRole;
        }

        public int getMODIFY() {
            return MODIFY;
        }

        public String getUserName() {
            return userName;
        }

        public String getUserPassword() {
            return userPassword;
        }

        public String getUserRole() {
            return userRole;
        }
    }

    public static class uploadFileInfo implements Serializable {
        private final Doc doc;
        private final byte[] fileContent;

        public uploadFileInfo(Doc doc, byte[] bytes) {
            this.doc = doc;
            this.fileContent = bytes;
        }

        public byte[] getFileContent() {
            return fileContent;
        }

        public Doc getDoc() {
            return doc;
        }
    }

    public static class respond implements Serializable {
        private final String flag;

        public respond(String flag) {
            this.flag = flag;
        }

        public String getFlag() {
            return flag;
        }
    }

    public static class notConnectedToMysql implements Serializable {
    }
}
