package com.github.museadmin.infinite_state_machine.data.access.dal;

import java.util.ArrayList;

import org.json.JSONObject;

/**
 * The DAL mirrors the methods in each DAO and acts
 * as a pass through to the DAO in use
 */
public interface IDataAccessLayer {
  void createTable(JSONObject table);
  Boolean executeSqlStatement(String sql);
  ArrayList<String> executeSqlQuery(String sql);
}
