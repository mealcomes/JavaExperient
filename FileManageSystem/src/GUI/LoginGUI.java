package GUI;

import console.AbstractUser;
import console.DataProcessing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginGUI extends JFrame {
    private final JLabel title = new JLabel("档案管理系统");
    private final JLabel userName = new JLabel("用户名:");
    private final JLabel passwd = new JLabel("密码:");
    private final JTextField userNameText = new JTextField(13);
    private final JPasswordField passwdText = new JPasswordField(13);
    private final JButton login = new JButton("登录");
    private final JButton exit = new JButton("退出");

    public LoginGUI() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int setX = (screenSize.width - 280) / 2;
        int setY = (screenSize.height - 400) / 2;
        setBounds(setX, setY, 300, 330);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SpringLayout springLayout = new SpringLayout();
        JPanel jp = new JPanel(springLayout);
        add(jp, BorderLayout.CENTER);

        login.setFont(new Font("宋体", Font.PLAIN, 20));
        exit.setFont(new Font("宋体", Font.PLAIN, 20));

        jp.add(title);
        jp.add(userName);
        jp.add(userNameText);
        jp.add(passwd);
        ;
        jp.add(passwdText);
        jp.add(login);
        jp.add(exit);

        loginLayout(springLayout);

        addKeyActionListener();
        setVisible(true);
    }

    /**
     * 添加键盘消息监听器
     */
    private void addKeyActionListener() {
        //焦点不在文本框内时的回车便出发登录按钮
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login.doClick();
                }
            }
        });

        //文本框回车消息
        userNameText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passwdText.requestFocus();
            }
        });
        passwdText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login.doClick();
            }
        });
    }

    //页面布局
    private void loginLayout(SpringLayout springLayout) {
        //设置标题
        title.setFont(new Font("宋体", Font.BOLD, 30));
        SpringLayout.Constraints c = springLayout.getConstraints(title);
        c.setX(Spring.constant(45));
        c.setY(Spring.constant(30));
        //设置“用户名”文本
        userName.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        springLayout.putConstraint(SpringLayout.NORTH, userName, 30, SpringLayout.SOUTH, title);
        springLayout.putConstraint(SpringLayout.WEST, userName, -10, SpringLayout.WEST, title);
        //设置用户名框
        userNameText.setFont(new Font("宋体", Font.PLAIN, 20));
        springLayout.putConstraint(SpringLayout.WEST, userNameText, 10, SpringLayout.EAST, userName);
        springLayout.putConstraint(SpringLayout.NORTH, userNameText, 0, SpringLayout.NORTH, userName);
        //设置“密码”文本
        passwd.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        springLayout.putConstraint(SpringLayout.NORTH, passwd, 20, SpringLayout.SOUTH, userName);
        springLayout.putConstraint(SpringLayout.EAST, passwd, 0, SpringLayout.EAST, userName);
        //设置密码框
        passwdText.setFont(new Font("宋体", Font.PLAIN, 20));
        springLayout.putConstraint(SpringLayout.WEST, passwdText, 10, SpringLayout.EAST, passwd);
        springLayout.putConstraint(SpringLayout.SOUTH, passwdText, 0, SpringLayout.SOUTH, passwd);
        //设置登录按钮
        login.setFont(new Font("宋体", Font.PLAIN, 20));
        login.setFocusable(false);
        login.addActionListener(e -> setLoginAction());
        springLayout.putConstraint(SpringLayout.NORTH, login, 15, SpringLayout.SOUTH, passwd);
        springLayout.putConstraint(SpringLayout.EAST, login, 10, SpringLayout.EAST, passwd);
        //设置退出按钮
        exit.setFont(new Font("宋体", Font.PLAIN, 20));
        exit.setFocusable(false);
        exit.addActionListener(e -> {
//            DataProcessing.closeToMySql();
            dispose();
            System.exit(0);
        });
        springLayout.putConstraint(SpringLayout.SOUTH, exit, 0, SpringLayout.SOUTH, login);
        springLayout.putConstraint(SpringLayout.EAST, exit, -10, SpringLayout.EAST, passwdText);
    }

    //登录按钮监听
    private void setLoginAction() {
        if (!DataProcessing.isConnectedToServer) {
            JOptionPane.showMessageDialog(this, "无法连接到服务端！");
            return;
        }
        DataProcessing.readUserAndFileInfo();
        if (!DataProcessing.isReadInfoSuccess) {
            JOptionPane.showMessageDialog(this, "服务器拒绝登录!");
            return;
        }
        String userName = userNameText.getText();
        char[] pd = passwdText.getPassword();
        String passwd = new String(pd);

        //首先判断用户名和密码是否都不为空
        if (userName.isEmpty() || passwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名或密码不能为空！");
        } else {
            //寻找目标账户，若没找到便输出提示框
            AbstractUser user = DataProcessing.searchUser(userName);
            if (user != null) {
                //判断密码是否正确
                AbstractUser flag;
                flag = DataProcessing.searchUser(userName, passwd);
                if (flag != null) {
                    //关闭当前登录窗口
                    dispose();
                    switch (user.getRole()) {
                        case "administrator":
                            new AdministratorGUI(user);
                            break;
                        case "browser":
                            new BrowserGUI(user);
                            break;
                        case "operator":
                            new OperatorGUI(user);
                            break;
                    }
                    JOptionPane.showMessageDialog(this, "登录成功！");
                } else {
                    JOptionPane.showMessageDialog(this, "密码错误!");
                    passwdText.requestFocusInWindow();
                }
            } else
                JOptionPane.showMessageDialog(this, "该用户不存在!");
        }
    }
}