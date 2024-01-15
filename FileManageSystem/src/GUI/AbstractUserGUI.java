package GUI;

import console.AbstractUser;
import console.DataProcessing;
import console.Doc;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public class AbstractUserGUI extends JFrame {
    protected final JButton jButtonFlush = new JButton("刷新");                                     //重新从数据库读取信息
    private final JButton jButtonFileList = new JButton("文件列表");                               //文件管理有关的按钮
    private final JButton jButtonDownload = new JButton("下载");                                  //文件下载按钮
    private final JLabel jLabelChangInfo = new JLabel("<html><u>修改密码</u></html>");            //修改密码
    private final JLabel jLabelExit = new JLabel("<html><u>注销</u></html>");                    //注销
    private final JTable jTableFileTable = new JTable();                                             //文件列表
    protected JPanel jPanelNorth = new JPanel(new BorderLayout());                             //放置北边的JPanel
    protected JPanel jPanelNorthWest = new JPanel(new FlowLayout(FlowLayout.LEFT));            //主JPanel北边的西边
    protected JPanel jPanelNorthEast = new JPanel(new GridLayout(2, 2));            //主JPanel北边的东边
    protected JPanel jPanelSouthFileAdmin = new JPanel(new FlowLayout(FlowLayout.RIGHT));      //放置文件处理按钮
    private final JScrollPane jScrollPaneFile = new JScrollPane(jTableFileTable);                                                 //放置文件列表

    AbstractUserGUI(AbstractUser user) {
        super();

        setThisLayout(user);
        setComponentProperty(user);
        addCloseJFrameAction();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int setX = (screenSize.width - 400) / 2;
        int setY = (screenSize.height - 200) / 2;
        setBounds(setX, setY, 800, 400);
        setTitle(user.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    /**
     * 加入组件
     */
    private void setThisLayout(AbstractUser user) {
        jPanelNorthWest.add(jButtonFlush);
        jPanelNorth.add(jPanelNorthWest, BorderLayout.WEST);
        jPanelNorthWest.add(jButtonFileList);
        jPanelNorth.add(jPanelNorthWest, BorderLayout.WEST);
        add(jPanelNorth, BorderLayout.NORTH);

        jPanelNorthEast.add(jLabelChangInfo);

        //用户名
        JLabel thisUserName = new JLabel("<html><b>&#8194;" + user.getName() + "</b></html>");
        thisUserName.setFont(new Font("宋体", Font.BOLD, 25));
        thisUserName.setForeground(Color.darkGray);
        jPanelNorthEast.add(thisUserName);

        jPanelNorthEast.add(jLabelExit, BorderLayout.EAST);
        jPanelNorth.add(jPanelNorthEast, BorderLayout.EAST);

        jPanelSouthFileAdmin.add(jButtonDownload);
    }

    /**
     * 设置组件属性
     */
    private void setComponentProperty(AbstractUser user) {
        jButtonFlush.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        jButtonFlush.addActionListener(e -> {
            DataProcessing.readUserAndFileInfo();
            setFileTable();
        });
        jButtonFlush.setFocusable(false);

        jButtonFileList.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        jButtonFileList.addActionListener(e -> setFileListButtonListener());
        jButtonFileList.setFocusable(false);

        jLabelChangInfo.setFont(new Font("宋体", Font.PLAIN, 15));
        setChangeInfoJLabelListener(user);               //为"修改信息"JLabel设置点击闪烁已经对应的消息

        jLabelExit.setFont(new Font("宋体", Font.PLAIN, 15));
        setExitJLabelListener();

        //设置表头
        JTableHeader header = jTableFileTable.getTableHeader();
        header.setFont(new Font("宋体", Font.BOLD, 20));
        header.setForeground(Color.red);

        jTableFileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);             //不可多选
        jTableFileTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTableFileTable.setGridColor(Color.black);                                         //边框颜色为黑色
        jTableFileTable.setFont(new Font(null, Font.PLAIN, 15));
        jTableFileTable.setRowHeight(30);

        jButtonDownload.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        jButtonDownload.setFocusable(false);
        jButtonDownload.addActionListener(e -> setDownloadButtonListener());
    }

    /**
     * "文件列表"按钮事件
     */
    private void setFileListButtonListener() {
        if (!isHasComponent(jScrollPaneFile)) {
            for (Component comp : getContentPane().getComponents()) {
                if (((BorderLayout) getContentPane().getLayout()).getConstraints(comp) == BorderLayout.CENTER) {
                    getContentPane().remove(comp);
                }
                if (((BorderLayout) getContentPane().getLayout()).getConstraints(comp) == BorderLayout.SOUTH) {
                    getContentPane().remove(comp);
                }
            }
            setFileTable();
            getContentPane().add(jScrollPaneFile, BorderLayout.CENTER);
            getContentPane().add(jPanelSouthFileAdmin, BorderLayout.SOUTH);
            revalidate();
            repaint();
            setVisible(true);
        } else {
            remove(jScrollPaneFile);
            remove(jPanelSouthFileAdmin);
            //重新绘制窗口
            revalidate();
            repaint();
        }
    }

    /**
     * “下载”按钮事件
     */
    private void setDownloadButtonListener() {
        if (jTableFileTable.getSelectedRow() == -1 || !isHasComponent(jScrollPaneFile)) {
            JOptionPane.showMessageDialog(null, "请选择您需要下载的文件!");
            return;
        }
        // 获取用户选择的行的索引
        int row = jTableFileTable.getSelectedRow();
        // 获取用户选择的文件名或文件路径
        String fileName = jTableFileTable.getModel().getValueAt(row, 0).toString();
        // 创建一个文件选择器
        JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        // 设置只能选择目录
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);  //只能选择目录
        // 弹出一个保存对话框
        int option = chooser.showSaveDialog(null);
        // 如果用户点击了保存按钮
        if (option == JFileChooser.APPROVE_OPTION) {
            // 获取用户选择的目标路径
            File destDir = chooser.getSelectedFile();
            // 创建目标文件的路径
            File destFile = new File(destDir, fileName);

            //当文件已经存在时，是否覆盖文件
            try {
                if (!destFile.exists()) {
                    destFile.createNewFile();
                } else {
                    if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
                            "文件已存在，是否继续保存以覆盖旧文件？", "提示", JOptionPane.YES_NO_OPTION)) {
                        destFile.delete();
                        destFile.createNewFile();
                    } else {
                        return;
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "AbstractUserGUI中下载文件时创建文件失败!");
                return;
            }

            if (DataProcessing.sendDownloadFileRequest(destFile)) {
                JOptionPane.showMessageDialog(jTableFileTable, "文件下载成功！");
            } else {
                JOptionPane.showMessageDialog(jTableFileTable, "文件下载失败！");
            }

        }
    }

    /**
     * 设置文件列表内容
     */
    protected void setFileTable() {
        Vector<String> columns = new Vector<>();
        columns.add("文件名");
        columns.add("编号");
        columns.add("创建者");
        columns.add("创建时间");
        columns.add("文件备注");

        //读取数据库的文件信息
        Vector<Vector<String>> data = new Vector<>();
        Enumeration<Doc> Docs = null;
        try {
            Docs = DataProcessing.listDoc();
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(this, e.getMessage());
        }
        if (Docs != null) {
            while (Docs.hasMoreElements()) {
                Doc doc1 = Docs.nextElement();
                Vector<String> tmp = new Vector<>();
                tmp.add(doc1.getFilename());
                tmp.add(doc1.getID());
                tmp.add(doc1.getCreator());
                tmp.add(doc1.getTimestamp().toString());
                tmp.add(doc1.getDescription());
                data.add(tmp);
            }
        } else {
            JOptionPane.showConfirmDialog(this, "文件库中文件为空!");
            return;
        }

        jTableFileTable.setModel(new DefaultTableModel(data, columns) {                 //用读取到的信息填充表格，并设置不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) { //设置列类型
                return String.class;
            }
        });
    }

    /**
     * “修改密码”JLabel事件，以及点击闪烁
     */
    private void setChangeInfoJLabelListener(AbstractUser user) {
        //为修改密码标签设置鼠标消息
        jLabelChangInfo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                jLabelChangInfo.setText("<html><font color=\"red\"><u>修改密码</u></font></html>");
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                jLabelChangInfo.setText("<html><u>修改密码</u></html>");
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new ChangeInfo(AbstractUserGUI.this, user);  //新建修改密码的JDialog类
            }
        });
    }

    /**
     * “注销”JLabel事件
     */
    private void setExitJLabelListener() {
        jLabelExit.addMouseListener(new MouseAdapter() {
            //为退出标签设置鼠标消息
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                jLabelExit.setText("<html><font color=\"red\"><u>注销</u></font></html>");
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                jLabelExit.setText("<html><u>注销</u></html>");
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                dispose();
                new LoginGUI();
            }
        });

    }

    /**
     * 添加关闭窗口事件
     */
    private void addCloseJFrameAction() {
        //若用户退出登录或者关闭了界面，便重新创建登录框
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
//                DataProcessing.saveInfo();
                new LoginGUI();
            }
        });
    }

    /**
     * 检查当前内容面板中是否包含指定的组件
     */
    protected boolean isHasComponent(Component comp) {
        for (Component component : getContentPane().getComponents()) {
            if (component == comp) {
                return true;
            }
        }
        return false;
    }
}

//修改密码Dialog类
class ChangeInfo extends JDialog {
    private JLabel title = new JLabel("修改密码");
    private JLabel oldPasswd = new JLabel("原密码:");
    private JPasswordField oldPasswdInput = new JPasswordField(12);
    private JLabel newPasswd = new JLabel("新密码:");
    private JPasswordField newPasswdInput = new JPasswordField(12);
    private JLabel confirmNewPasswd = new JLabel("确认密码:");
    private JPasswordField confirmNewPasswdInput = new JPasswordField(12);
    private JLabel samePasswdError = new JLabel("两次密码不一致");
    private boolean blinkFlag = true;            //用于判断是否闪烁
    private int blinkCount = 0;                  //用于统计闪烁次数
    private JButton jButtonConfirm = new JButton("确定");
    private JButton jButtonExit = new JButton("退出");
    private JPanel jPanelNorth = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); //放置标题
    private SpringLayout springLayout = new SpringLayout();
    private JPanel jPanelCenter = new JPanel(springLayout);                                      //放置输入密码的提示和输入框
    private JPanel jPanelSouth = new JPanel(new FlowLayout(FlowLayout.CENTER));                  //放置按钮

    public ChangeInfo(JFrame parent, AbstractUser user) {
        super(parent, user.getName(), true);
        setLayout(new BorderLayout());
        setBounds(400, 200, 300, 250);

        setThisLayout();
        setComponentProperty();
        setAllListener(user);
        addKeyAction();

        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /**
     * 设置布局
     */
    private void setThisLayout() {
        //添加组件
        jPanelNorth.add(title);
        add(jPanelNorth, BorderLayout.NORTH);
        jPanelCenter.add(oldPasswd);
        jPanelCenter.add(oldPasswdInput);
        jPanelCenter.add(newPasswd);
        jPanelCenter.add(newPasswdInput);
        jPanelCenter.add(confirmNewPasswd);
        jPanelCenter.add(confirmNewPasswdInput);
        jPanelCenter.add(samePasswdError);
        add(jPanelCenter, BorderLayout.CENTER);
        jPanelSouth.add(jButtonConfirm);
        jPanelSouth.add(jButtonExit);
        add(jPanelSouth, BorderLayout.SOUTH);

        //组件布局
        springLayout.putConstraint(SpringLayout.NORTH, oldPasswd, 10, SpringLayout.NORTH, jPanelCenter);
        springLayout.putConstraint(SpringLayout.WEST, oldPasswd, 80, SpringLayout.WEST, jPanelCenter);
        springLayout.putConstraint(SpringLayout.NORTH, oldPasswdInput, 0, SpringLayout.NORTH, oldPasswd);
        springLayout.putConstraint(SpringLayout.WEST, oldPasswdInput, 10, SpringLayout.EAST, oldPasswd);
        springLayout.putConstraint(SpringLayout.NORTH, newPasswd, 10, SpringLayout.SOUTH, oldPasswd);
        springLayout.putConstraint(SpringLayout.EAST, newPasswd, 0, SpringLayout.EAST, oldPasswd);
        springLayout.putConstraint(SpringLayout.NORTH, newPasswdInput, 0, SpringLayout.NORTH, newPasswd);
        springLayout.putConstraint(SpringLayout.WEST, newPasswdInput, 10, SpringLayout.EAST, newPasswd);
        springLayout.putConstraint(SpringLayout.NORTH, confirmNewPasswd, 10, SpringLayout.SOUTH, newPasswd);
        springLayout.putConstraint(SpringLayout.EAST, confirmNewPasswd, 0, SpringLayout.EAST, newPasswd);
        springLayout.putConstraint(SpringLayout.NORTH, confirmNewPasswdInput, 0, SpringLayout.NORTH, confirmNewPasswd);
        springLayout.putConstraint(SpringLayout.WEST, confirmNewPasswdInput, 10, SpringLayout.EAST, confirmNewPasswd);
        springLayout.putConstraint(SpringLayout.NORTH, samePasswdError, 5, SpringLayout.SOUTH, confirmNewPasswdInput);
        springLayout.putConstraint(SpringLayout.WEST, samePasswdError, 0, SpringLayout.WEST, confirmNewPasswdInput);
    }

    /**
     * 设置属性
     */
    private void setComponentProperty() {
        title.setFont(new Font("楷体", Font.BOLD, 30));
        oldPasswd.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        newPasswd.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        confirmNewPasswd.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        samePasswdError.setForeground(Color.red);
        samePasswdError.setVisible(false);
        jButtonConfirm.setFocusable(false);
        jButtonExit.setFocusable(false);
    }

    /**
     * 全部事件监听器
     */
    private void setAllListener(AbstractUser user) {
        //监听两次密码是否相同
        newPasswdInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String newPasswd = new String(newPasswdInput.getPassword());
                String confirmNewPasswd = new String(confirmNewPasswdInput.getPassword());
                samePasswdError.setVisible(!newPasswd.isEmpty() && !confirmNewPasswd.isEmpty() && !newPasswd.equals(confirmNewPasswd));
            }
        });
        //监听两次密码是否相同
        confirmNewPasswdInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String newPasswd = new String(newPasswdInput.getPassword());
                String confirmNewPasswd = new String(confirmNewPasswdInput.getPassword());
                samePasswdError.setVisible(!newPasswd.isEmpty() && !confirmNewPasswd.isEmpty() && !newPasswd.equals(confirmNewPasswd));
            }
        });
        //确认修改密码按钮事件
        jButtonConfirm.addActionListener(e -> {
            String oldPasswd = new String(oldPasswdInput.getPassword());
            String newPasswd = new String(newPasswdInput.getPassword());
            String confirmNewPasswd = new String(confirmNewPasswdInput.getPassword());
            if (oldPasswd.isEmpty() || newPasswd.isEmpty() || confirmNewPasswd.isEmpty()) {
                JOptionPane.showMessageDialog(null, "请输入完整!");
                return;
            }
            if (samePasswdError.isVisible()) {
                //通过定时器，使"两次密码不一致"的提示闪烁
                Timer timer = getTimer();
                timer.start();
                return;
            }
            if (oldPasswd.equals(newPasswd)) {
                JOptionPane.showMessageDialog(null, "新密码不能与旧密码相同!");
                return;
            }

            //正式修改密码
            AbstractUser searchUserInfo = DataProcessing.searchUser(user.getName(), oldPasswd);
            if (searchUserInfo == null) {
                JOptionPane.showMessageDialog(null, "旧密码输入错误!");
            } else {
                user.setPassword(newPasswd);
                if (!DataProcessing.changeInfoInDatabase(user)) {
                    JOptionPane.showMessageDialog(null, "修改失败!");
                    return;
                }
                DataProcessing.updateUser(user.getName(), newPasswd, user.getRole());
                JOptionPane.showMessageDialog(null, "修改成功!");
                getOwner().dispose();
                new LoginGUI();
            }
        });
        //退出按钮事件
        jButtonExit.addActionListener(e -> dispose());
    }

    /**
     * 键盘行为监听器
     */
    private void addKeyAction() {
        //输入框获取回车消息
        oldPasswdInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    newPasswdInput.requestFocus();
                }
            }
        });
        newPasswdInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    confirmNewPasswdInput.requestFocus();
                }
            }
        });
        confirmNewPasswdInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    jButtonConfirm.doClick();
            }
        });
        jButtonConfirm.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    jButtonConfirm.doClick();
            }
        });
    }

    /**
     * 获取用于闪烁的定时器
     */
    private Timer getTimer() {
        Timer timer;
        // 设置闪烁频率
        timer = new Timer(30, e -> {
            // 判断是否显示
            if (blinkFlag) {
                samePasswdError.setVisible(false);
                blinkFlag = false;
                blinkCount++;
            } else {
                samePasswdError.setVisible(true);
                blinkFlag = true;
                blinkCount++;
            }
            // 判断是否停止闪烁
            if (blinkCount >= 4) {
                ((Timer) e.getSource()).stop();
                blinkCount = 0;
                blinkFlag = true;
            }
        });
        return timer;
    }
}
