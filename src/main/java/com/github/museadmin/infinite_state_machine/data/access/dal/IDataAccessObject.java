package com.github.museadmin.infinite_state_machine.data.access.dal;

import org.json.JSONObject;

import java.util.ArrayList;

public interface IDataAccessObject {
  void createDatabase(String database);
  void createTable(JSONObject table);
  void deactivate(String actionName);
  Boolean executeSqlStatement(String sql);
  ArrayList<String> executeSqlQuery(String sql);
  Boolean notActive(String actionName);
  String queryProperty(String property);
}
