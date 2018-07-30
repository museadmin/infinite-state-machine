package com.github.museadmin.infinite_state_machine.data.access.dal;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * The DAL mirrors the methods in each DAO and acts
 * as a pass through to the DAO in use
 */
public interface IDataAccessLayer {
  void activate(String actionName);
  void changeRunPhase(String runPhase);
  void createTable(JSONObject table);
  void deactivate(String actionName);
  Boolean executeSqlStatement(String sql);
  ArrayList<String> executeSqlQuery(String sql);
  Boolean active(String actionName);
  Boolean beforeActionsComplete();
  String queryProperty(String property);
}
