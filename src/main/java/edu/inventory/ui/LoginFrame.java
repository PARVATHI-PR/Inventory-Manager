package edu.inventory.ui;

import edu.inventory.model.Session;
import edu.inventory.model.Role;
import edu.inventory.service.InventoryService;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public final class LoginFrame extends JFrame {
    private final JTextField username=new JTextField(26);
    private final JPasswordField password=new JPasswordField(26);
    private final JButton submit=AppTheme.primaryButton("SIGN IN");
    private final InventoryService service=new InventoryService();
    public LoginFrame(){
        super("Inventory & Stock Manager | Sign in");setDefaultCloseOperation(EXIT_ON_CLOSE);setMinimumSize(new Dimension(420,390));
        JPanel card=new JPanel(new GridBagLayout());card.setBackground(AppTheme.SURFACE);card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER),BorderFactory.createEmptyBorder(26,34,30,34)));
        GridBagConstraints g=new GridBagConstraints();g.insets=new Insets(8,4,8,4);g.fill=GridBagConstraints.HORIZONTAL;g.weightx=1;
        JLabel title=new JLabel("Inventory & Stock Manager");title.setFont(new Font("SansSerif",Font.BOLD,23));title.setForeground(AppTheme.NAVY);g.gridx=0;g.gridy=0;card.add(title,g);
        JLabel sub=new JLabel("Manage inventory with confidence");sub.setForeground(AppTheme.MUTED);g.gridy=1;card.add(sub,g);
        JLabel u=new JLabel("Username");u.setFont(u.getFont().deriveFont(Font.BOLD));u.setForeground(AppTheme.TEXT);g.gridy=2;card.add(u,g);username.setPreferredSize(new Dimension(300,40));g.gridy=3;card.add(username,g);
        JLabel p=new JLabel("Password");p.setFont(p.getFont().deriveFont(Font.BOLD));p.setForeground(AppTheme.TEXT);g.gridy=4;card.add(p,g);password.setPreferredSize(new Dimension(300,40));g.gridy=5;card.add(password,g);
        submit.setPreferredSize(new Dimension(300,44));g.gridy=6;g.insets=new Insets(18,4,4,4);card.add(submit,g);getRootPane().setDefaultButton(submit);
        JLabel footer=new JLabel("Authorized access only",SwingConstants.CENTER);footer.setBorder(BorderFactory.createEmptyBorder(10,0,12,0));footer.setForeground(AppTheme.MUTED);JPanel wrap=new JPanel(new GridBagLayout());wrap.setBackground(AppTheme.CANVAS);wrap.add(card);add(wrap,BorderLayout.CENTER);add(footer,BorderLayout.SOUTH);submit.addActionListener(e->login());AppTheme.styleInputs(this);addWindowListener(new java.awt.event.WindowAdapter(){@Override public void windowOpened(java.awt.event.WindowEvent e){username.requestFocusInWindow();}});pack();setSize(470,470);setLocationRelativeTo(null);
    }
    private void login(){String user=username.getText().trim();char[] secret=password.getPassword();submit.setEnabled(false);submit.setText("SIGNING IN...");
        new SwingWorker<Session,Void>(){protected Session doInBackground(){return service.login(user,secret);}protected void done(){submit.setEnabled(true);submit.setText("SIGN IN");password.setText("");try{Session s=get();if(s==null){JOptionPane.showMessageDialog(LoginFrame.this,"Username or password is incorrect, or the supplier account is not linked.","Sign in failed",JOptionPane.ERROR_MESSAGE);password.requestFocusInWindow();return;}if(s.role()==Role.SUPPLIER)new SupplierFrame(service,s).setVisible(true);else new AppFrame(service,s).setVisible(true);dispose();}catch(InterruptedException ex){Thread.currentThread().interrupt();error();}catch(ExecutionException ex){showError(ex.getCause());}}}.execute();
    }
    private void error(){JOptionPane.showMessageDialog(this,"Sign in was interrupted.","Error",JOptionPane.ERROR_MESSAGE);}
    static void showError(Throwable t){String m=t==null?null:t.getMessage();if(m==null||m.isBlank())m="The operation could not be completed.";JOptionPane.showMessageDialog(null,m,"Inventory Manager",JOptionPane.ERROR_MESSAGE);}
}
