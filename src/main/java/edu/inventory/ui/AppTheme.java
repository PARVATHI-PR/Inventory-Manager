package edu.inventory.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import java.awt.*;

/** Shared visual tokens and reusable Swing styles used across every role. */
public final class AppTheme {
    public static final Color NAVY=new Color(29,48,75);
    public static final Color PRIMARY=new Color(34,91,150);
    public static final Color PRIMARY_HOVER=new Color(25,72,122);
    public static final Color CANVAS=new Color(243,246,250);
    public static final Color SURFACE=Color.WHITE;
    public static final Color TEXT=new Color(37,49,66);
    public static final Color MUTED=new Color(84,100,121);
    public static final Color BORDER=new Color(214,223,234);
    public static final Color SUCCESS=new Color(30,112,80);
    public static final Color WARNING=new Color(154,94,19);
    public static final Color DANGER=new Color(164,54,56);
    private static final Font BODY=new Font("SansSerif",Font.PLAIN,14);
    private static final Font STRONG=new Font("SansSerif",Font.BOLD,14);
    private AppTheme(){}

    public static void install(){
        UIManager.put("defaultFont",BODY);
        for(String key:new String[]{"Label.font","Button.font","ToggleButton.font","TextField.font","PasswordField.font","TextArea.font","ComboBox.font","CheckBox.font","RadioButton.font","TabbedPane.font","Table.font","List.font","OptionPane.messageFont","OptionPane.buttonFont","Menu.font","MenuItem.font"})UIManager.put(key,BODY);
        UIManager.put("Label.foreground",TEXT);UIManager.put("Panel.background",CANVAS);UIManager.put("OptionPane.background",CANVAS);UIManager.put("OptionPane.messageForeground",TEXT);
        UIManager.put("Button.background",new Color(232,238,246));UIManager.put("Button.foreground",NAVY);UIManager.put("Button.font",STRONG);UIManager.put("Button.margin",new Insets(7,13,7,13));UIManager.put("Button.focus",new Color(126,174,220));
        UIManager.put("TextField.background",SURFACE);UIManager.put("TextField.foreground",TEXT);UIManager.put("TextField.caretForeground",PRIMARY);UIManager.put("TextField.selectionBackground",new Color(192,216,241));
        UIManager.put("PasswordField.background",SURFACE);UIManager.put("PasswordField.foreground",TEXT);UIManager.put("PasswordField.caretForeground",PRIMARY);
        UIManager.put("ComboBox.background",SURFACE);UIManager.put("ComboBox.foreground",TEXT);UIManager.put("CheckBox.foreground",TEXT);
        UIManager.put("TabbedPane.background",CANVAS);UIManager.put("TabbedPane.foreground",TEXT);UIManager.put("TabbedPane.selected",SURFACE);UIManager.put("TabbedPane.contentAreaColor",SURFACE);UIManager.put("TabbedPane.focus",new Color(126,174,220));UIManager.put("TabbedPane.tabInsets",new Insets(9,14,9,14));
        UIManager.put("Table.background",SURFACE);UIManager.put("Table.foreground",TEXT);UIManager.put("Table.font",BODY);UIManager.put("Table.rowHeight",31);UIManager.put("Table.selectionBackground",new Color(222,235,249));UIManager.put("Table.selectionForeground",NAVY);UIManager.put("Table.gridColor",new Color(232,237,243));
        UIManager.put("TableHeader.background",new Color(232,238,246));UIManager.put("TableHeader.foreground",NAVY);UIManager.put("TableHeader.font",STRONG);
        UIManager.put("ScrollPane.background",SURFACE);UIManager.put("Viewport.background",SURFACE);UIManager.put("TextComponent.border",BorderFactory.createCompoundBorder(new LineBorder(BORDER),new EmptyBorder(5,8,5,8)));UIManager.put("TextField.border",UIManager.get("TextComponent.border"));UIManager.put("PasswordField.border",UIManager.get("TextComponent.border"));
    }
    public static JButton primaryButton(String text){return button(text,PRIMARY,Color.WHITE,PRIMARY_HOVER);}
    public static JButton secondaryButton(String text){return button(text,new Color(232,238,246),NAVY,new Color(216,226,238));}
    public static JButton dangerButton(String text){return button(text,DANGER,Color.WHITE,new Color(137,39,42));}
    private static JButton button(String text,Color base,Color foreground,Color hover){JButton b=new JButton(text);b.setFont(STRONG);b.setForeground(foreground);b.setBackground(base);b.setOpaque(true);b.setContentAreaFilled(true);b.setBorderPainted(false);b.setFocusPainted(true);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.setMargin(new Insets(8,14,8,14));b.setRolloverEnabled(true);b.getModel().addChangeListener(e->{ButtonModel m=(ButtonModel)e.getSource();b.setBackground(m.isEnabled()?(m.isRollover()||m.isPressed()?hover:base):new Color(220,225,232));b.setForeground(m.isEnabled()?foreground:new Color(83,94,108));});return b;}
    public static void styleTable(JTable t){t.setFont(BODY);t.setForeground(TEXT);t.setBackground(SURFACE);t.setSelectionBackground(new Color(222,235,249));t.setSelectionForeground(NAVY);t.setRowHeight(31);t.setShowHorizontalLines(true);t.setShowVerticalLines(false);t.setGridColor(new Color(232,237,243));t.setIntercellSpacing(new Dimension(0,1));t.setFillsViewportHeight(true);t.setAutoCreateRowSorter(true);t.getTableHeader().setFont(STRONG);t.getTableHeader().setForeground(NAVY);t.getTableHeader().setBackground(new Color(232,238,246));t.getTableHeader().setPreferredSize(new Dimension(10,36));t.getTableHeader().setReorderingAllowed(false);for(int i=0;i<t.getColumnModel().getColumnCount();i++){String col=t.getColumnName(i);int width=switch(col){case "SKU"->92;case "Date/time"->158;case "Product","Name"->190;case "Category","Supplier","Contact"->150;case "Description","Address","Note"->230;case "Reference"->170;case "Current stock","Reorder level","Reorder at","Quantity"->125;default->125;};t.getColumnModel().getColumn(i).setPreferredWidth(width);}}
    public static void styleStatusColumn(JTable table,String name){for(int i=0;i<table.getColumnModel().getColumnCount();i++)if(name.equals(table.getColumnName(i))){table.getColumnModel().getColumn(i).setCellRenderer((t,value,selected,focus,row,col)->{JLabel l=new JLabel(value==null?"":value.toString(),SwingConstants.CENTER);l.setOpaque(true);l.setFont(STRONG);String v=l.getText().toUpperCase();Color fg=v.contains("OUT")?DANGER:v.contains("LOW")||v.contains("REORDER")?WARNING:v.contains("IN STOCK")||v.equals("OK")?SUCCESS:TEXT;Color bg=v.contains("OUT")?new Color(252,232,232):v.contains("LOW")||v.contains("REORDER")?new Color(253,244,221):v.contains("IN STOCK")||v.equals("OK")?new Color(230,245,238):SURFACE;l.setForeground(selected?NAVY:fg);l.setBackground(selected?new Color(222,235,249):bg);return l;});return;}}
    public static void styleInputs(Container root){for(Component c:root.getComponents()){if(c instanceof JTextComponent field){field.setFont(BODY);field.setForeground(TEXT);field.setBackground(SURFACE);field.setBorder(BorderFactory.createCompoundBorder(new LineBorder(BORDER),new EmptyBorder(6,9,6,9)));field.addFocusListener(new java.awt.event.FocusAdapter(){public void focusGained(java.awt.event.FocusEvent e){field.setBorder(BorderFactory.createCompoundBorder(new LineBorder(PRIMARY,2),new EmptyBorder(5,8,5,8)));}public void focusLost(java.awt.event.FocusEvent e){field.setBorder(BorderFactory.createCompoundBorder(new LineBorder(BORDER),new EmptyBorder(6,9,6,9)));}});}if(c instanceof Container child)styleInputs(child);}}
    public static JLabel sectionTitle(String text){JLabel l=new JLabel(text);l.setFont(new Font("SansSerif",Font.BOLD,19));l.setForeground(NAVY);return l;}
    public static JPanel metricCard(String title,String value,String detail){JPanel p=new JPanel(new GridBagLayout());p.setBackground(SURFACE);p.setBorder(BorderFactory.createCompoundBorder(new LineBorder(BORDER),new EmptyBorder(16,18,16,18)));p.setMinimumSize(new Dimension(150,110));GridBagConstraints g=new GridBagConstraints();g.gridx=0;g.gridy=0;g.anchor=GridBagConstraints.WEST;g.fill=GridBagConstraints.HORIZONTAL;g.weightx=1;JLabel h=new JLabel(title);h.setFont(STRONG);h.setForeground(MUTED);p.add(h,g);g.gridy++;JLabel v=new JLabel(value);v.setFont(new Font("SansSerif",Font.BOLD,27));v.setForeground(NAVY);g.insets=new Insets(7,0,2,0);p.add(v,g);g.gridy++;JLabel d=new JLabel(detail);d.setForeground(MUTED);d.setFont(BODY.deriveFont(13f));g.insets=new Insets(0,0,0,0);p.add(d,g);return p;}
    public static JTable readOnlyTable(){return new JTable(new DefaultTableModel(){public boolean isCellEditable(int row,int column){return false;}}){@Override protected void paintComponent(Graphics graphics){super.paintComponent(graphics);if(getRowCount()==0){Graphics2D g=(Graphics2D)graphics.create();g.setColor(MUTED);g.setFont(BODY);String message="No records to display";FontMetrics fm=g.getFontMetrics();g.drawString(message,Math.max(12,(getWidth()-fm.stringWidth(message))/2),Math.max(fm.getAscent()+12,getHeight()/2));g.dispose();}}};}
}
