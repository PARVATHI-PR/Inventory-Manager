package edu.inventory.ui;

import edu.inventory.model.Session;
import edu.inventory.service.InventoryService;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public final class LoginFrame extends JFrame {
    private final JTextField username=new JTextField(22);
    private final JPasswordField password=new JPasswordField(22);
    private final JButton submit=new JButton("Sign in");
    private final InventoryService service=new InventoryService();
    public LoginFrame(){
        super("Inventory & Stock Manager — Sign in");setDefaultCloseOperation(EXIT_ON_CLOSE);setResizable(false);
        JPanel form=new JPanel(new GridBagLayout());form.setBorder(BorderFactory.createEmptyBorder(26,30,22,30));
        GridBagConstraints g=new GridBagConstraints();g.insets=new Insets(7,7,7,7);g.fill=GridBagConstraints.HORIZONTAL;
        addRow(form,g,0,"Username",username);addRow(form,g,1,"Password",password);
        g.gridx=1;g.gridy=2;form.add(submit,g);getRootPane().setDefaultButton(submit);
        JLabel title=new JLabel("Inventory & Stock Manager",SwingConstants.CENTER);title.setFont(title.getFont().deriveFont(Font.BOLD,20f));
        add(title,BorderLayout.NORTH);add(form,BorderLayout.CENTER);submit.addActionListener(e->login());pack();setLocationRelativeTo(null);
    }
    private static void addRow(JPanel p,GridBagConstraints g,int y,String label,JComponent field){g.gridx=0;g.gridy=y;p.add(new JLabel(label),g);g.gridx=1;p.add(field,g);}
    private void login(){String user=username.getText().trim();char[] secret=password.getPassword();submit.setEnabled(false);
        new SwingWorker<Session,Void>(){protected Session doInBackground(){return service.login(user,secret);}protected void done(){submit.setEnabled(true);password.setText("");try{Session s=get();if(s==null){JOptionPane.showMessageDialog(LoginFrame.this,"Username or password is incorrect.","Sign in failed",JOptionPane.ERROR_MESSAGE);return;}new AppFrame(service,s).setVisible(true);dispose();}catch(InterruptedException ex){Thread.currentThread().interrupt();error();}catch(ExecutionException ex){showError(ex.getCause());}}}.execute();
    }
    private void error(){JOptionPane.showMessageDialog(this,"Sign in was interrupted.","Error",JOptionPane.ERROR_MESSAGE);}
    static void showError(Throwable t){String m=t==null?null:t.getMessage();if(m==null||m.isBlank())m="The operation could not be completed.";JOptionPane.showMessageDialog(null,m,"Inventory Manager",JOptionPane.ERROR_MESSAGE);}
}
