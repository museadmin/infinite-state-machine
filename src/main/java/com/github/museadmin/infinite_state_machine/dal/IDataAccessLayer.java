package com.github.museadmin.infinite_state_machine.dal;

import org.json.JSONObject;

public interface IDataAccessLayer {
    void createDatabase(String database);
    void createTable(JSONObject table);
    Boolean executeSqlStatement(String sql);
}
