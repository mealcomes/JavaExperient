package GUI;

import console.DataProcessing;

import javax.swing.*;

public class MainGUI {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("----MainGUI.main:未知错误");
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        try {
            DataProcessing.init();
        } catch (Exception e) {
            System.out.println("----MainGUI.main:连接服务端失败,无法读取用户和文件信息！");
        }
        new LoginGUI();
    }
}