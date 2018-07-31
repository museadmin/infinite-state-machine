package com.github.museadmin.infinite_state_machine.data.access.dal;

import org.json.JSONObject;

import java.util.ArrayList;

public interface IDataAccessObject {
  void activate(String actionName);
  void changeRunPhase(String runPhase);
  void createDatabase(String database);
  void createTable(JSONObject table);
  void deactivate(String actionName);
  Boolean executeSqlStatement(String sql);
  ArrayList<String> executeSqlQuery(String sql);
  Boolean active(String actionName);
  Boolean beforeActionsComplete();
  String queryProperty(String property);
  String queryRunPhase();
  void setState(String stateName);
  void unsetState(String stateName);
}
