package GUI;

import console.AbstractUser;
import console.DataProcessing;
import console.Doc;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.Timestamp;

public class OperatorGUI extends AbstractUserGUI {
    private final JButton jButtonUploadFile = new JButton("上传文件");
    OperatorGUI(AbstractUser user) {
        super(user);
        setThisLayout();
        setComponentProperty(user);
    }

    /**
     * 加入组件
     */
    private void setThisLayout() {
        jPanelSouthFileAdmin.add(jButtonUploadFile);
    }

    /**
     * 设置组件属性
     */
    private void setComponentProperty(AbstractUser user) {
        jButtonUploadFile.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        jButtonUploadFile.setFocusable(false);
        jButtonUploadFile.addActionListener(e -> {
            new UploadFile(OperatorGUI.this, user);
            setFileTable();
        });
    }
}

class UploadFile extends JDialog {
    private final JLabel jLabelTitle = new JLabel("上传文件");
    //文件选择器
    private final JLabel jLabelFileId = new JLabel("档案编号:");
    private final JTextField jTextFieldFileId = new JTextField(12);
    private final JLabel jLabelFilePath = new JLabel("文件路径:");
    private final JTextField jTextFieldFilePath = new JTextField(12);
    private final JLabel jLabelFileName = new JLabel("文件名:");
    private final JTextField jTextFieldFileName = new JTextField(12);
    private final JLabel jLabelFileDescribe = new JLabel("文件备注:");
    private final JTextArea jTextAreaFileDescribe = new JTextArea(4, 12);
    private final JButton jButtonChooseFile = new JButton("选择文件");
    private final JButton jButtonUpload = new JButton("上     传");
    private final JPanel jPanelNorth = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private final SpringLayout springLayout = new SpringLayout();
    private final JPanel jPanelCenter = new JPanel(springLayout);
    private final JPanel jPanelSouth = new JPanel(new FlowLayout(FlowLayout.CENTER));

    UploadFile(JFrame parent, AbstractUser user) {
        super(parent, user.getName(), true);
        setBounds(400, 200, 400, 350);

        setThisLayout();
        setComponentsProperty(user);
        addKeyAction();

        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /**
     * 添加组件以设置布局
     */
    private void setThisLayout() {
        //北部
        jPanelNorth.add(jLabelTitle);
        add(jPanelNorth, BorderLayout.NORTH);

        //中部
        jPanelCenter.add(jLabelFileId);
        jPanelCenter.add(jTextFieldFileId);
        jPanelCenter.add(jLabelFilePath);
        jPanelCenter.add(jTextFieldFilePath);
        jPanelCenter.add(jLabelFileName);
        jPanelCenter.add(jTextFieldFileName);
        jPanelCenter.add(jLabelFileDescribe);
        jPanelCenter.add(jTextAreaFileDescribe);
        add(jPanelCenter, BorderLayout.CENTER);

        springLayout.putConstraint(SpringLayout.NORTH, jLabelFileId, 25, SpringLayout.NORTH, jPanelCenter);
        springLayout.putConstraint(SpringLayout.WEST, jLabelFileId, 65, SpringLayout.WEST, jPanelCenter);
        springLayout.putConstraint(SpringLayout.NORTH, jTextFieldFileId, 0, SpringLayout.NORTH, jLabelFileId);
        springLayout.putConstraint(SpringLayout.WEST, jTextFieldFileId, 10, SpringLayout.EAST, jLabelFileId);
        springLayout.putConstraint(SpringLayout.NORTH, jLabelFilePath, 20, SpringLayout.SOUTH, jLabelFileId);
        springLayout.putConstraint(SpringLayout.EAST, jLabelFilePath, 0, SpringLayout.EAST, jLabelFileId);
        springLayout.putConstraint(SpringLayout.NORTH, jTextFieldFilePath, 0, SpringLayout.NORTH, jLabelFilePath);
        springLayout.putConstraint(SpringLayout.WEST, jTextFieldFilePath, 10, SpringLayout.EAST, jLabelFilePath);
        springLayout.putConstraint(SpringLayout.NORTH, jLabelFileName, 20, SpringLayout.SOUTH, jLabelFilePath);
        springLayout.putConstraint(SpringLayout.EAST, jLabelFileName, 0, SpringLayout.EAST, jLabelFilePath);
        springLayout.putConstraint(SpringLayout.NORTH, jTextFieldFileName, 0, SpringLayout.NORTH, jLabelFileName);
        springLayout.putConstraint(SpringLayout.WEST, jTextFieldFileName, 10, SpringLayout.EAST, jLabelFileName);
        springLayout.putConstraint(SpringLayout.NORTH, jLabelFileDescribe, 20, SpringLayout.SOUTH, jLabelFileName);
        springLayout.putConstraint(SpringLayout.EAST, jLabelFileDescribe, 0, SpringLayout.EAST, jLabelFileName);
        springLayout.putConstraint(SpringLayout.NORTH, jTextAreaFileDescribe, 0, SpringLayout.NORTH, jLabelFileDescribe);
        springLayout.putConstraint(SpringLayout.WEST, jTextAreaFileDescribe, 10, SpringLayout.EAST, jLabelFileDescribe);

        //南部
        jPanelSouth.add(jButtonChooseFile);
        jPanelSouth.add(jButtonUpload);
        add(jPanelSouth, BorderLayout.SOUTH);
    }

    /**
     * 设置组件属性
     */
    private void setComponentsProperty(AbstractUser user) {
        jLabelTitle.setFont(new Font("楷体", Font.BOLD, 30));

        jLabelFileId.setFont(new Font("宋体", Font.PLAIN, 15));
        jLabelFilePath.setFont(new Font("宋体", Font.PLAIN, 15));
        jLabelFileName.setFont(new Font("宋体", Font.PLAIN, 15));
        jLabelFileDescribe.setFont(new Font("宋体", Font.PLAIN, 15));

        jTextFieldFileId.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        jTextFieldFilePath.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        jTextFieldFileName.setFont(new Font("微软雅黑", Font.PLAIN, 15));

        jTextAreaFileDescribe.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        jTextAreaFileDescribe.setAutoscrolls(true);
        jTextAreaFileDescribe.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        jTextAreaFileDescribe.setLineWrap(true);

        jButtonChooseFile.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        jButtonChooseFile.setFocusable(false);
        jButtonChooseFile.addActionListener(e -> chooseFileButtonListener());
        jButtonUpload.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        jButtonUpload.setFocusable(false);
        jButtonUpload.addActionListener(e -> uploadButtonActionListener(user));
    }

    /**
     * “选择文件”按钮事件
     */
    private void chooseFileButtonListener() {
        JFileChooser jFileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  //只能选择文件
        int option = jFileChooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            // 获取用户选择的目标文件
            File fileSelected = jFileChooser.getSelectedFile();
            if (!fileSelected.isFile()) {
                JOptionPane.showMessageDialog(null, "选择类型必须为文件！");
                return;
            }
            jTextFieldFilePath.setText(fileSelected.getParent().replaceAll("\\\\", "/") + "/");
            jTextFieldFileName.setText(fileSelected.getName());
        }
    }

    /**
     * “上传”按钮事件
     */
    private void uploadButtonActionListener(AbstractUser user) {
        if (jTextFieldFileId.getText().isEmpty() || !jTextFieldFileId.getText().matches("[0-9]+")) {
            JOptionPane.showMessageDialog(null, "请输入正确的文件编号!");
            return;
        }
        if (jTextFieldFilePath.getText().isEmpty() || jTextFieldFileName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "请输入文件路径和文件名!");
            return;
        }
        if (jTextAreaFileDescribe.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "请输入文件描述!");
            return;
        }

        String sourceFileId = jTextFieldFileId.getText();
        String sourceFilePath = jTextFieldFilePath.getText();
        String sourceFileName = jTextFieldFileName.getText();
        String fileDescribe = jTextAreaFileDescribe.getText();
        File sourceFile = new File(sourceFilePath + sourceFileName);
        if (DataProcessing.searchDoc(sourceFileId) != null) {
            if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog
                    (null, "文件已存在，是否继续上传并覆盖？", "提示", JOptionPane.YES_NO_OPTION)) {
                return;
            }
        }
        if (!DataProcessing.uploadFile(new Doc(sourceFileId, user.getName(), new Timestamp(System.currentTimeMillis()),
                fileDescribe, sourceFileName), sourceFile)) {
            System.out.println("Operator.uploadButtonActionListener:文件传输失败！");
            JOptionPane.showMessageDialog(null, "上传失败!");
            return;
        }
        //信息更新
        DataProcessing.insertDoc(sourceFileId, user.getName(), new Timestamp(System.currentTimeMillis()),
                fileDescribe, sourceFileName);
        JOptionPane.showMessageDialog(null, "上传成功!");
        dispose();
    }

    private void addKeyAction() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                jButtonUpload.doClick();
            }
        });
        jTextFieldFileId.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    jTextFieldFilePath.requestFocus();
                }
            }
        });
        jTextFieldFilePath.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    jTextFieldFileName.requestFocus();
            }
        });
        jTextFieldFileName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    jTextAreaFileDescribe.requestFocus();
            }
        });
        jTextAreaFileDescribe.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    jButtonUpload.doClick();
            }
        });
    }
}