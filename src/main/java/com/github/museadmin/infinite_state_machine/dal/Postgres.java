package com.github.museadmin.infinite_state_machine.dal;

import com.github.museadmin.infinite_state_machine.lib.PropertyCache;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Access Layer for when using Postgres
 */
public class Postgres implements IDataAccessLayer {

    private Map dbData = new HashMap<String, String>();

    public Postgres(PropertyCache propertyCache, String epochSeconds) {

        dbData.put("dbHost", propertyCache.getProperty("dbHost"));
        dbData.put("dbName", propertyCache.getProperty("dbName") + epochSeconds);
        dbData.put("dbPassword", propertyCache.getProperty("dbPassword"));
        dbData.put("dbPort", propertyCache.getProperty("dbPort"));
        dbData.put("dbUser", propertyCache.getProperty("dbUser"));

        createDatabase(dbData.get("dbName").toString());
    }

    public boolean createDatabase(String database) {

        try {
            Connection connection = getConnection("postgres");
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE DATABASE " + database);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(1);
        }


        return true;
    }

    private Connection getConnection(String database) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://" +
                getDbData("dbHost") + ":" + getDbData("dbPort") + "/" + database,
                getDbData("dbUser"), getDbData("dbPassword"));
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(1);
        }
        return connection;
    }

    private String getDbData(String key) {
        return dbData.get(key).toString();
    }
}
