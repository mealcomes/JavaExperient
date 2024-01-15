package GUI;

import console.AbstractUser;
import console.DataProcessing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Vector;

public class AdministratorGUI extends AbstractUserGUI {
    private JPanel jPanelSouthUserAdmin = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    private JButton usersAdmin = new JButton("用户管理");
    private JButton jButtonAddUser = new JButton("添加用户");
    private JButton jButtonDeleteUser = new JButton("删除");
    private JButton jButtonResetPassword = new JButton("重置密码");
    private JTable userTable = new JTable();
    private JScrollPane jScrollPaneUsers = new JScrollPane(userTable);
    ;

    AdministratorGUI(AbstractUser user) {
        super(user);

        addButton(user);
        setUsersTable();
        setCompanionAttribute(user);

        jPanelNorthWest.add(usersAdmin);
        usersAdmin.setFocusable(false);
        usersAdmin.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        usersAdmin.addActionListener(e -> setUsersAdminAction());
    }

    /**
     * 设置“用户管理”事件
     */
    private void setUsersAdminAction() {
        if (!isHasComponent(jScrollPaneUsers)) {
            for (Component comp : getContentPane().getComponents()) {
                if (((BorderLayout) getContentPane().getLayout()).getConstraints(comp) == BorderLayout.CENTER) {
                    getContentPane().remove(comp);
                }
                if (((BorderLayout) getContentPane().getLayout()).getConstraints(comp) == BorderLayout.SOUTH) {
                    getContentPane().remove(comp);
                }
            }
            add(jPanelSouthUserAdmin, BorderLayout.SOUTH);
            add(jScrollPaneUsers, BorderLayout.CENTER);
            //重新绘制窗口
            revalidate();
            repaint();
            setVisible(true);
        } else {
            remove(jScrollPaneUsers);
            remove(jPanelSouthUserAdmin);
            //重新绘制窗口
            revalidate();
            repaint();
        }
    }

    /**
     * 点击“用户管理”按钮后应在布局上添加的部件
     */
    private void addButton(AbstractUser user) {
        jPanelSouthUserAdmin.add(jButtonAddUser);

        jPanelSouthUserAdmin.add(jButtonResetPassword);

        jPanelSouthUserAdmin.add(jButtonDeleteUser);
    }

    /**
     * 设置“用户列表”信息
     */
    private void setUsersTable() {
        Vector<String> columns = new Vector<>();
        columns.add("用户名");
        columns.add("密码");
        columns.add("身份");

        //读取数据库的文件信息
        Vector<Vector<String>> data = new Vector<>();
        Enumeration<AbstractUser> Users = null;
        Users = DataProcessing.listUser();
        if (Users != null) {
            while (Users.hasMoreElements()) {
                AbstractUser user1 = Users.nextElement();
                Vector<String> tmp = new Vector<>();
                tmp.add(user1.getName());
                tmp.add(user1.getPassword());
                tmp.add(user1.getRole());
                data.add(tmp);
            }
        } else {
            JOptionPane.showConfirmDialog(this, "文件库中文件为空!");
            return;
        }

        userTable.setModel(new DefaultTableModel(data, columns) {                      //用读取到的信息填充表格，并设置不可编辑
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
     * 设置组件属性
     */
    private void setCompanionAttribute(AbstractUser user) {
        jButtonFlush.addActionListener(e -> setUsersTable());

        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);             //不可多选
        userTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        userTable.setGridColor(Color.black);                                         //边框颜色为黑色
        userTable.setFont(new Font(null, Font.PLAIN, 15));
        userTable.setRowHeight(30);

        //设置表头
        JTableHeader header = userTable.getTableHeader();
        header.setFont(new Font("宋体", Font.BOLD, 20));
        header.setForeground(Color.red);

        jButtonAddUser.setFocusable(false);
        jButtonAddUser.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        jButtonAddUser.addActionListener(e -> {
            new AddUserDialog(AdministratorGUI.this, user);
            //更新用户列表
            setUsersTable();
        });

        jButtonResetPassword.setFocusable(false);
        jButtonResetPassword.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        jButtonResetPassword.addActionListener(e -> resetPasswordAction());

        jButtonDeleteUser.setFocusable(false);
        jButtonDeleteUser.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        jButtonDeleteUser.addActionListener(e -> deleteUserAction(user));
    }

    /**
     * 设置“删除”事件
     */
    private void deleteUserAction(AbstractUser user) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先点击用户列表以进行用户选择!");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "确定要删除所选用户吗?", "删除用户",
                JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
            return;
        String userName = userTable.getValueAt(selectedRow, 0).toString();
        if (userName.equals(user.getName())) {
            JOptionPane.showMessageDialog(this, "无法删除自己!");
            return;
        }

        if (!DataProcessing.deleteUserInfoInDatabase(DataProcessing.searchUser(userName))) {
            JOptionPane.showMessageDialog(this, "删除失败!");
            return;
        }
        DataProcessing.deleteUser(userName);
        setUsersTable();
        JOptionPane.showMessageDialog(this, "删除成功!");
    }

    /**
     * “重置密码”事件
     */
    private void resetPasswordAction() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先点击用户列表以进行用户选择!");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "确定重置密码吗？", "提示", JOptionPane.YES_NO_OPTION)
                == JOptionPane.NO_OPTION) {
            return;
        }
        String name = userTable.getValueAt(selectedRow, 0).toString();
        String role = userTable.getValueAt(selectedRow, 2).toString();
        DataProcessing.updateUser(name, "123", role);
        if (!DataProcessing.changeInfoInDatabase(DataProcessing.searchUser(name))) {
            JOptionPane.showMessageDialog(this, "重置密码失败!");
            return;
        }

        //更新用户列表
        setUsersTable();
        JOptionPane.showMessageDialog(this, "重置密码成功!");
    }
}

class AddUserDialog extends JDialog {
    private final JLabel jLabelTitle = new JLabel("添加用户");
    private final JLabel jLabelUserName = new JLabel("用户名:");
    private final JLabel jLabelUserRole = new JLabel("身份:");
    private final JTextField jTextFieldUserName = new JTextField(8);
    private final JComboBox<String> jComboBoxUserRole = new JComboBox<>
            (new String[]{"请选择身份", "administrator", "operator", "browser"});
    private final JButton jButtonAddUser = new JButton("添加");
    private final JButton jButtonCancel = new JButton("取消");
    private final JPanel jPanelNorthTitle = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    private final SpringLayout springLayout = new SpringLayout();
    private final JPanel jPanelCenterMain = new JPanel(springLayout);
    private final JPanel jPanelSouthButton = new JPanel(new FlowLayout(FlowLayout.CENTER));

    public AddUserDialog(JFrame parent, AbstractUser user) {
        super(parent, user.getName(), true);

        setThisLayout();
        setComponentProperty();
        addCloseAction();
        addEnterButtonAction();

        setBounds(400, 200, 300, 240);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    /**
     * 加入组件以设置布局
     */
    private void setThisLayout() {
        //北部
        jPanelNorthTitle.add(jLabelTitle);
        add(jPanelNorthTitle, BorderLayout.NORTH);

        //中部
        jPanelCenterMain.add(jLabelUserName);
        jPanelCenterMain.add(jTextFieldUserName);
        jPanelCenterMain.add(jLabelUserRole);
        jPanelCenterMain.add(jComboBoxUserRole);
        add(jPanelCenterMain, BorderLayout.CENTER);

        //中部组件位置调整
        springLayout.putConstraint(SpringLayout.NORTH, jLabelUserName, 20, SpringLayout.NORTH, jPanelCenterMain);
        springLayout.putConstraint(SpringLayout.WEST, jLabelUserName, 50, SpringLayout.WEST, jPanelCenterMain);
        springLayout.putConstraint(SpringLayout.NORTH, jTextFieldUserName, 0, SpringLayout.NORTH, jLabelUserName);
        springLayout.putConstraint(SpringLayout.WEST, jTextFieldUserName, 10, SpringLayout.EAST, jLabelUserName);
        springLayout.putConstraint(SpringLayout.NORTH, jLabelUserRole, 15, SpringLayout.SOUTH, jLabelUserName);
        springLayout.putConstraint(SpringLayout.EAST, jLabelUserRole, 0, SpringLayout.EAST, jLabelUserName);
        springLayout.putConstraint(SpringLayout.NORTH, jComboBoxUserRole, 0, SpringLayout.NORTH, jLabelUserRole);
        springLayout.putConstraint(SpringLayout.WEST, jComboBoxUserRole, 10, SpringLayout.EAST, jLabelUserRole);

        //南部
        jPanelSouthButton.add(jButtonAddUser);
        jPanelSouthButton.add(jButtonCancel);
        add(jPanelSouthButton, BorderLayout.SOUTH);
    }

    /**
     * 设置组件属性
     */
    private void setComponentProperty() {
        jLabelTitle.setFont(new Font("楷体", Font.BOLD, 30));

        jLabelUserName.setFont(new Font("宋体", Font.PLAIN, 15));
        jLabelUserRole.setFont(new Font("宋体", Font.PLAIN, 15));

        jTextFieldUserName.setFont(new Font("微软雅黑", Font.PLAIN, 15));

        jComboBoxUserRole.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        jComboBoxUserRole.setFocusable(false);
        jComboBoxUserRole.setEditable(false);

        jButtonAddUser.setFocusable(false);
        jButtonAddUser.addActionListener(e -> setAddUserButtonListener());
        jButtonCancel.setFocusable(false);
        jButtonCancel.addActionListener(e -> setCancelButtonListener());
    }

    /**
     * “添加”按钮事件
     */
    private void setAddUserButtonListener() {
        String userName = jTextFieldUserName.getText();
        String userRole = jComboBoxUserRole.getSelectedItem().toString();
        if (userName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "用户名不能为空");
            return;
        }
        if (userRole.equals("请选择身份")) {
            JOptionPane.showMessageDialog(null, "请选择用户身份！");
            return;
        }
        if (DataProcessing.searchUser(userName) != null) {
            JOptionPane.showMessageDialog(null, "该用户已存在！");
            return;
        }
        DataProcessing.insertUser(userName, "123", userRole);
        if (!DataProcessing.addUserInfoInDatabase(DataProcessing.searchUser(userName))) {
            JOptionPane.showMessageDialog(null, "用户添加失败！");
            return;
        }
        JOptionPane.showMessageDialog(null, "新用户添加成功！\n默认密码:123\n请及时修改密码以保证账号安全！");
        dispose();
    }

    /**
     * “取消”按钮事件
     */
    private void setCancelButtonListener() {
        dispose();
    }

    /**
     * 关闭窗口事件监听
     */
    private void addCloseAction() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                dispose();
            }
        });
    }

    /**
     * 回车事件
     */
    private void addEnterButtonAction() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    jButtonAddUser.doClick();
                }
            }
        });
        jTextFieldUserName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    jButtonAddUser.doClick();
                }
            }
        });
    }
}
