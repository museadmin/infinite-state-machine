package com.github.museadmin.infinite_state_machine.dal;

public interface IDataAccessLayer {
    void createDatabase(String database);
    void createTables();
    Boolean executeSqlStatement(String sql);
}
