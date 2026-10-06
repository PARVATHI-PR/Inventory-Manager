package edu.inventory.ui;

import edu.inventory.model.InventoryData.*;
import edu.inventory.model.Session;
import edu.inventory.service.InventoryService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/** Read-only portal. Every dataset is supplier-scoped again in the service and DAO. */
public final class SupplierFrame extends JFrame {
    private final InventoryService service;
    private final Session session;
    private final JTabbedPane tabs=new JTabbedPane();
    private final JTable productTable=table(),stockTable=table(),activityTable=table();
    private final JTextField search=new JTextField(20);
    private final JPanel dashboard=new JPanel(new BorderLayout(12,12));
    private final JPanel profileView=new JPanel(new GridBagLayout());
    private final JLabel welcome=new JLabel("Loading supplier profile...");
    private final JPanel counts=new JPanel(new GridLayout(1,3,14,0));
    private SupplierProfile profile;
    private List<StockRow> rows=List.of();

    public SupplierFrame(InventoryService service,Session session){
        super("Inventory & Stock Manager | Supplier Portal");this.service=service;this.session=session;
        setDefaultCloseOperation(EXIT_ON_CLOSE);setMinimumSize(new Dimension(760,520));setSize(1080,700);setLocationRelativeTo(null);getContentPane().setBackground(AppTheme.CANVAS);
        JPanel header=new JPanel(new BorderLayout(12,0));header.setBorder(BorderFactory.createEmptyBorder(12,18,12,18));header.setBackground(AppTheme.NAVY);
        JLabel brand=new JLabel("INVENTORY  /  SUPPLIER PORTAL");brand.setForeground(Color.WHITE);brand.setFont(new Font("SansSerif",Font.BOLD,15));
        JButton logout=AppTheme.secondaryButton("Logout");logout.addActionListener(e->{dispose();new LoginFrame().setVisible(true);});header.add(brand,BorderLayout.WEST);header.add(logout,BorderLayout.EAST);
        dashboard.setLayout(new BoxLayout(dashboard,BoxLayout.Y_AXIS));dashboard.setBackground(AppTheme.CANVAS);dashboard.setBorder(BorderFactory.createEmptyBorder(22,24,22,24));welcome.setFont(new Font("SansSerif",Font.PLAIN,15));welcome.setForeground(AppTheme.NAVY);welcome.setAlignmentX(Component.LEFT_ALIGNMENT);dashboard.add(welcome);dashboard.add(Box.createRigidArea(new Dimension(1,22)));counts.setBackground(AppTheme.CANVAS);counts.setAlignmentX(Component.LEFT_ALIGNMENT);counts.setMaximumSize(new Dimension(Integer.MAX_VALUE,145));dashboard.add(counts);dashboard.add(Box.createVerticalGlue());
        tabs.addTab("Dashboard",dashboard);tabs.addTab("My Products",withToolbar(productTable,productToolbar()));tabs.addTab("Stock Status",withToolbar(stockTable,new JPanel()));tabs.addTab("Activity",withToolbar(activityTable,new JPanel()));tabs.addTab("My Profile",profilePanel());
        add(header,BorderLayout.NORTH);add(tabs,BorderLayout.CENTER);AppTheme.styleTable(productTable);AppTheme.styleTable(stockTable);AppTheme.styleTable(activityTable);
        AppTheme.styleInputs(this);reload();
    }
    private JPanel productToolbar(){JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT));p.add(new JLabel("Filter products"));p.add(search);JButton find=new JButton("Apply");find.addActionListener(e->refreshProducts());p.add(find);JButton refresh=new JButton("Refresh");refresh.addActionListener(e->reload());p.add(refresh);return p;}
    private JPanel profilePanel(){profileView.setBorder(BorderFactory.createEmptyBorder(25,30,25,30));return profileView;}
    private static JTable table(){JTable t=AppTheme.readOnlyTable();AppTheme.styleTable(t);return t;}
    private JPanel withToolbar(JTable t,JComponent bar){JPanel p=new JPanel(new BorderLayout(10,10));p.setBackground(AppTheme.CANVAS);p.setBorder(BorderFactory.createEmptyBorder(16,18,18,18));if(bar.getComponentCount()>0){bar.setBackground(AppTheme.CANVAS);p.add(bar,BorderLayout.NORTH);}p.add(new JScrollPane(t),BorderLayout.CENTER);return p;}
    private void reload(){setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));load(()->new Object[]{service.supplierProfile(session),service.supplierStock(session,""),service.supplierTransactions(session)},data->{setCursor(Cursor.getDefaultCursor());profile=(SupplierProfile)data[0];rows=castRows(data[1]);welcome.setText("<html><div style='font-size:22px;font-weight:bold;color:#1d304b'>Welcome, "+escape(profile.supplierName())+"</div><div style='font-size:14px;color:#546479;margin-top:5px'>Your supplied products, live stock position, and recent movements.</div></html>");renderProducts();renderStock();renderActivity(castTransactions(data[2]));counts.removeAll();counts.add(AppTheme.metricCard("My products",Integer.toString(rows.size()),"In your catalogue"));counts.add(AppTheme.metricCard("At reorder level",Long.toString(rows.stream().filter(StockRow::lowStock).count()),"Based on current stock"));counts.add(AppTheme.metricCard("Active products",Long.toString(rows.stream().filter(StockRow::active).count()),"Available items"));counts.revalidate();counts.repaint();renderProfile();});}
    @SuppressWarnings("unchecked") private static List<StockRow> castRows(Object x){return (List<StockRow>)x;}
    @SuppressWarnings("unchecked") private static List<TransactionRow> castTransactions(Object x){return (List<TransactionRow>)x;}
    private void refreshProducts(){load(()->service.supplierStock(session,search.getText()),list->{rows=list;renderProducts();renderStock();});}
    private void renderProducts(){String q=search.getText().trim().toLowerCase();List<Object[]> data=rows.stream().filter(x->q.isEmpty()||(x.sku()+" "+x.product()+" "+x.category()).toLowerCase().contains(q)).map(x->new Object[]{x.sku(),x.product(),x.category(),x.unit(),x.quantity(),x.reorderLevel(),status(x),x.active()?"Active":"Inactive"}).toList();fill(productTable,new String[]{"SKU","Product","Category","Unit","Current stock","Reorder level","Stock status","Product status"},data);AppTheme.styleStatusColumn(productTable,"Stock status");}
    private void renderStock(){List<Object[]> data=rows.stream().map(x->new Object[]{x.product(),x.quantity()+" "+x.unit(),x.reorderLevel(),status(x)}).toList();fill(stockTable,new String[]{"Product","Current stock","Reorder level","Status"},data);AppTheme.styleStatusColumn(stockTable,"Status");}
    private static String status(StockRow x){if(x.quantity().signum()<=0)return "OUT OF STOCK";return x.lowStock()?"LOW STOCK":"IN STOCK";}
    private void renderActivity(List<TransactionRow> list){List<Object[]> data=list.stream().map(x->new Object[]{x.occurredAt().toString().replace('T',' '),x.sku(),x.product(),x.type(),x.quantity(),x.reference()}).toList();fill(activityTable,new String[]{"Date/time","SKU","Product","Movement","Quantity","Reference"},data);}
    private void renderProfile(){if(profile==null)return;profileView.removeAll();GridBagConstraints g=new GridBagConstraints();g.gridx=0;g.gridy=0;g.anchor=GridBagConstraints.WEST;g.insets=new Insets(9,8,9,8);String[][] fields={{"Username",profile.username()},{"Supplier",profile.supplierName()},{"Email",blank(profile.email())},{"Phone",blank(profile.phone())},{"Address",blank(profile.address())},{"Account status",profile.active()?"Active":"Disabled"}};for(String[] f:fields){JLabel key=new JLabel(f[0]);key.setFont(key.getFont().deriveFont(Font.BOLD));profileView.add(key,g);g.gridx=1;profileView.add(new JLabel(f[1]),g);g.gridx=0;g.gridy++;}profileView.revalidate();profileView.repaint();}
    private static String blank(String v){return v==null||v.isBlank()?"Not provided":v;}
    private static String escape(String s){return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");}
    private static void fill(JTable t,String[] columns,List<Object[]> values){DefaultTableModel m=(DefaultTableModel)t.getModel();m.setDataVector(values.toArray(Object[][]::new),columns);AppTheme.styleTable(t);}
    private <T> void load(Callable<T> work,Consumer<T> success){new SwingWorker<T,Void>(){protected T doInBackground()throws Exception{return work.call();}protected void done(){try{success.accept(get());}catch(Exception ex){setCursor(Cursor.getDefaultCursor());Throwable cause=ex.getCause()==null?ex:ex.getCause();LoginFrame.showError(cause);}}}.execute();}
}
