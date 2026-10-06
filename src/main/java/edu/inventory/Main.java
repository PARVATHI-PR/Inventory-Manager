package edu.inventory;

import edu.inventory.ui.LoginFrame;
import javax.swing.*;
import java.awt.*;

public final class Main {
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings","on");
        SwingUtilities.invokeLater(()->{
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch(Exception ignored){}
            edu.inventory.ui.AppTheme.install();
            if(GraphicsEnvironment.isHeadless()){System.err.println("A desktop environment is required to run the Swing application.");return;}
            new LoginFrame().setVisible(true);
        });
    }
}
