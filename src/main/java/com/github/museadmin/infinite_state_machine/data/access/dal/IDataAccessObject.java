package com.github.museadmin.infinite_state_machine.data.access.dal;

import org.json.JSONObject;

import java.util.ArrayList;

public interface IDataAccessObject {

  // ================= Setup =================
  Boolean active(String actionName);
  void activate(String actionName);
  void deactivate(String actionName);
  void clearPayload(String actionName);
  void updatePayload(String actionName, String payload);

  // ================= DB =================
  void createTable(JSONObject table);
  Boolean executeSqlStatement(String sql);
  ArrayList<JSONObject> executeSqlQuery(String sql);

  // ================= Hooks =================
  Boolean beforeActionsComplete();
  Boolean afterActionsComplete();

  // ================= Property  =================
  void insertProperty(String property, String value);
  String queryProperty(String property);
  void updateProperty(String property, String value);

  // ================= Run phase  =================
  String queryRunPhase();
  void updateRunPhase(String runPhase);

  // ================= State  =================
  void setState(String stateName);
  void unsetState(String stateName);

  // ================= Message  =================
  void insertMessage(JSONObject message);
  ArrayList<JSONObject> getUnprocessedMessages();
  void markMessageProcessed(Integer id);

}
