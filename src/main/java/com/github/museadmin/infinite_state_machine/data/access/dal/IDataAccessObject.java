package com.github.museadmin.infinite_state_machine.data.access.dal;

import java.util.ArrayList;

import org.json.JSONObject;

public interface IDataAccessObject {
  void createDatabase(String database);
  void createTable(JSONObject table);
  Boolean executeSqlStatement(String sql);
  ArrayList<String> executeSqlQuery(String sql);
}
