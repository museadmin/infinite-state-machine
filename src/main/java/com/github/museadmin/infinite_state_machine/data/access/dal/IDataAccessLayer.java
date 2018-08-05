package com.github.museadmin.infinite_state_machine.data.access.dal;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * The DAL mirrors the methods in each DAO and acts
 * as a pass through to the DAO in use
 */
public interface IDataAccessLayer {
  void activate(String actionName);
  Boolean afterActionsComplete();
  void changeRunPhase(String runPhase);
  void createTable(JSONObject table);
  void deactivate(String actionName);
  Boolean executeSqlStatement(String sql);
  ArrayList<String> executeSqlQuery(String sql);
  Boolean active(String actionName);
  Boolean beforeActionsComplete();
  void insertProperty(String property, String value);
  String queryProperty(String property);
  String queryRunPhase();
  void setState(String stateName);
  void updateProperty(String property, String value);
  void unsetState(String stateName);
}
