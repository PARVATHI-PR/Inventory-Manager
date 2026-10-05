package edu.inventory;

import edu.inventory.dao.InventoryDao;
import java.io.Console;
import java.util.Arrays;

/** Interactive first-admin bootstrap. Password is neither hard-coded nor echoed. */
public final class BootstrapAdmin {
    public static void main(String[] args) {
        Console console=System.console();
        if(console==null){System.err.println("Run this command in a terminal so the password can be entered securely.");System.exit(2);}
        String username=console.readLine("Initial Admin username: ");
        char[] one=console.readPassword("Initial Admin password (10+ characters): ");
        char[] two=console.readPassword("Confirm password: ");
        if(username==null||username.isBlank()||username.trim().length()>60||one==null||one.length<10||!Arrays.equals(one,two)){System.err.println("Choose a username up to 60 characters and enter matching passwords of at least 10 characters.");if(one!=null)Arrays.fill(one,'\0');if(two!=null)Arrays.fill(two,'\0');return;}
        try {
            new InventoryDao().bootstrapAdmin(username,one);
            System.out.println("Initial Admin account created.");
        } catch(Exception e){System.err.println("Admin bootstrap failed. Verify Oracle configuration, run database/seed.sql, and ensure this username is not already used.");System.exit(1);}
        finally {if(one!=null)Arrays.fill(one,'\0');if(two!=null)Arrays.fill(two,'\0');}
    }
}
