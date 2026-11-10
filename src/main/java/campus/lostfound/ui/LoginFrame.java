package campus.lostfound.ui;

import campus.lostfound.dao.UserDAO;
import campus.lostfound.model.User;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final JTextField email=new JTextField(22);
    private final JPasswordField password=new JPasswordField(22);
    private final UserDAO users=new UserDAO();

    public LoginFrame(){
        setTitle("Campus Lost & Found | Login");setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(440,330);setLocationRelativeTo(null);
        JPanel p=new JPanel(new GridBagLayout());p.setBorder(BorderFactory.createEmptyBorder(20,28,20,28));
        GridBagConstraints g=new GridBagConstraints();g.insets=new Insets(7,7,7,7);g.fill=GridBagConstraints.HORIZONTAL;
        JLabel title=new JLabel("Campus Lost & Found",SwingConstants.CENTER);title.setFont(new Font("Arial",Font.BOLD,22));
        g.gridx=0;g.gridy=0;g.gridwidth=2;p.add(title,g);
        g.gridwidth=1;g.gridy++;p.add(new JLabel("Email"),g);g.gridx=1;p.add(email,g);
        g.gridx=0;g.gridy++;p.add(new JLabel("Password"),g);g.gridx=1;p.add(password,g);
        JButton login=new JButton("Login");g.gridx=0;g.gridy++;p.add(login,g);
        JButton register=new JButton("Register Student");g.gridx=1;p.add(register,g);
        JButton bootstrap=new JButton("Create first admin");g.gridx=0;g.gridy++;g.gridwidth=2;p.add(bootstrap,g);
        login.addActionListener(e->doLogin());register.addActionListener(e->register());bootstrap.addActionListener(e->createAdmin());
        add(p);
    }
    private void doLogin(){
        try{
            User u=users.login(email.getText().trim(),new String(password.getPassword()));
            if(u==null){JOptionPane.showMessageDialog(this,"Invalid credentials or inactive account.");return;}
            dispose();new DashboardFrame(u).setVisible(true);
        }catch(Exception ex){showError(ex);}
    }
    private void register(){
        JTextField name=new JTextField(),code=new JTextField(),mail=new JTextField();
        JPasswordField pass=new JPasswordField();
        Object[] fields={"Full name",name,"Student ID (optional)",code,"Email",mail,"Password (8+ characters)",pass};
        if(JOptionPane.showConfirmDialog(this,fields,"Student registration",JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION)return;
        String pw=new String(pass.getPassword());
        if(name.getText().isBlank()||mail.getText().isBlank()||pw.length()<8){JOptionPane.showMessageDialog(this,"Name, email and an 8+ character password are required.");return;}
        try{users.registerStudent(name.getText().trim(),code.getText().trim(),mail.getText().trim(),pw);JOptionPane.showMessageDialog(this,"Student account created. You can now log in.");}
        catch(Exception ex){showError(ex);}
    }
    private void createAdmin(){
        try{if(users.adminExists()){JOptionPane.showMessageDialog(this,"An admin already exists. Ask the existing administrator.");return;}}
        catch(Exception ex){showError(ex);return;}
        JTextField name=new JTextField(),mail=new JTextField();JPasswordField pass=new JPasswordField();
        Object[] fields={"Admin name",name,"Admin email",mail,"Password (8+ characters)",pass};
        if(JOptionPane.showConfirmDialog(this,fields,"First admin setup",JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION)return;
        String pw=new String(pass.getPassword());
        if(name.getText().isBlank()||mail.getText().isBlank()||pw.length()<8){JOptionPane.showMessageDialog(this,"All fields required; password must be 8+ characters.");return;}
        try{users.createFirstAdmin(name.getText().trim(),mail.getText().trim(),pw);JOptionPane.showMessageDialog(this,"First admin created. Log in now.");}
        catch(Exception ex){showError(ex);}
    }
    private void showError(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
}
