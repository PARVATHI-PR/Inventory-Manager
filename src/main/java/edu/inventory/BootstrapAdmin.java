package edu.inventory;

import edu.inventory.dao.InventoryDao;
import java.io.Console;
import java.sql.SQLException;
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
        } catch(Exception e){reportFailure(e);System.exit(1);}
        finally {if(one!=null)Arrays.fill(one,'\0');if(two!=null)Arrays.fill(two,'\0');}
    }
    private static void reportFailure(Exception error){
        for(Throwable cause=error;cause!=null;cause=cause.getCause()){
            if(cause instanceof SQLException sql){
                int code=sql.getErrorCode();
                if(code==1){System.err.println("Admin bootstrap failed: that username is already in use. Choose a different username.");return;}
                if(code==1400){System.err.println("Admin bootstrap failed: the Admin role is missing. Run database/seed.sql once in this schema.");return;}
                if(code==1017){System.err.println("Admin bootstrap failed: Oracle rejected the database username or password. Check db.username and db.password.");return;}
                if(code==12514||code==12505||code==17002){System.err.println("Admin bootstrap failed: Oracle listener/service is unavailable. Check that Oracle is running and db.url names the correct service.");return;}
                if(code==904){System.err.println("Admin bootstrap failed: the database schema is behind the application. Apply the migration in database/migrations/add_supplier_login.sql, then retry.");return;}
                System.err.println("Admin bootstrap failed: Oracle error code "+code+" (SQL state "+sql.getSQLState()+"). Verify the schema and privileges.");return;
            }
            if(cause instanceof IllegalArgumentException){
                System.err.println("Admin bootstrap failed: a value already exists or violates a database rule. The username may already be in use.");return;
            }
        }
        System.err.println("Admin bootstrap failed: "+(error.getMessage()==null?error.getClass().getSimpleName():error.getMessage()));
    }
}
