package edu.inventory.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public final class Database {
    private static final Properties FILE = load();
    private Database() {}
    private static Properties load() {
        Properties p = new Properties(); Path path = Path.of("config", "application.properties");
        if (Files.isRegularFile(path)) try (var in = Files.newInputStream(path)) { p.load(in); }
        catch (IOException e) { throw new IllegalStateException("Cannot read config/application.properties", e); }
        return p;
    }
    private static String value(String env, String key) {
        String v = System.getenv(env); if (v == null || v.isBlank()) v = FILE.getProperty(key);
        if (v == null || v.isBlank() || v.equals("CHANGE_ME")) throw new IllegalStateException("Set " + env + " or " + key + " in config/application.properties.");
        return v;
    }
    public static Connection connect() throws SQLException {
        Connection c=DriverManager.getConnection(value("INVENTORY_DB_URL", "db.url"), value("INVENTORY_DB_USERNAME", "db.username"), value("INVENTORY_DB_PASSWORD", "db.password"));
        String schema=System.getenv("INVENTORY_DB_SCHEMA");if(schema==null||schema.isBlank())schema=FILE.getProperty("db.schema");
        if(schema!=null&&!schema.isBlank()){if(!schema.matches("[A-Za-z][A-Za-z0-9_$#]*")){c.close();throw new SQLException("Invalid db.schema setting.");}try(Statement st=c.createStatement()){st.execute("alter session set current_schema="+schema);}catch(SQLException e){c.close();throw e;}}
        return c;
    }
}
