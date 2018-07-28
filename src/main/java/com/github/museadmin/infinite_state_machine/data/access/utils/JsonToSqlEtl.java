package com.github.museadmin.infinite_state_machine.data.access.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Extract Transform and Load SQL from a JSON file
 */
public class JsonToSqlEtl {

  // TODO This will need to have an interface and a different version for each RDBMS
  // or might be better to just move JsonToSqlEtl.parseSqlFromFile into each dao

  /**
   * Return an array list of SQL statements as parsed from an Action
   * pack's pack_data.json file that has been read into a JSONObject
   *
   * @param jsonObject The raw data  in json form
   * @return ArrayList of SQL statements
   */
  public static ArrayList <String> parseSqlFromJson(JSONObject jsonObject) {

    ArrayList <String> statements  = new ArrayList<>();
    JSONArray items = jsonObject.getJSONArray("items");

    for (int i = 0; i < items.length(); i++) {
      String crud = items.getJSONObject(i).getJSONObject("meta").get("crud").toString();
      if (crud.equals("insert")) {
        statements.addAll(parseInsertStatements(items.getJSONObject(i)));
      } else if (crud.equals("select")) {
        statements.add(parseSelectStatement(items.getJSONObject(i)));
      } else if (crud.equals("update")) {
        statements.add(parseUpdateStatement(items.getJSONObject(i)));
      } else if (crud.equals("delete")) {
        statements.add(parseDeleteStatement(items.getJSONObject(i)));
      }
    }

    return statements;
  }

  private static String parseDeleteStatement(JSONObject jsonObject) {

    String tableName = jsonObject.getJSONObject("meta").get("table").toString();

    // Build the start of the select statement
    StringBuilder start = new StringBuilder();
    start.append("DELETE FROM " + tableName + " ");

    String beginning = start.toString();

    // Do we have a where clause?
    String where = "";
    if (jsonObject.has("where")) {
      where = constructWhereClause(jsonObject);
    } else {
      beginning = beginning.trim();
    }

    String end = ";";

    return beginning + where.trim() + end;
  }

  /**
   * Create SQL INSERT statement defined in an Action Pack's pack_data JSONObject
   * @param jsonObject One of the items from an Action Pack's pack_data file
   * @return ArrayList<String>The SQL INSERT statements</String>
   */
  private static ArrayList <String> parseInsertStatements(JSONObject jsonObject) {
    ArrayList <String> statements  = new ArrayList<>();
    String tableName = jsonObject.getJSONObject("meta").get("table").toString();

    // Build the front end of the insert statement
    StringBuilder start = new StringBuilder();
    start.append("INSERT INTO " + tableName + " (");

    // Add the columns
    start = addColumns(jsonObject, start);

    start.append(") values (");
    String beginning = start.toString();

    // Create the end of the insert statement
    String end = ");";

    // Create the middle of each insert statement
    JSONArray valuesArray = jsonObject.getJSONArray("values");
    for (int i = 0; i < valuesArray.length(); i++) {
      StringBuilder middle = new StringBuilder();
      JSONArray values = valuesArray.getJSONArray(i);
      for (int j = 0; j < values.length(); j++) {
        middle.append("'" + values.get(j).toString() + "', ");
      }
      middle = removeLastComma(middle);

      // Push the statement into the statements list
      statements.add(beginning + middle.toString() + end);
    }

    return statements;
  }

  /** Create SQL UPDATE statement defined in an Action Pack's pack_data JSONObject
   * @param jsonObject One of the items from an Action Pack's pack_data file
   * @return String. The SQL UPDATE Statement
   */
  private static String parseUpdateStatement(JSONObject jsonObject) {

    String tableName = jsonObject.getJSONObject("meta").get("table").toString();

    // Build the start of the select statement
    StringBuilder start = new StringBuilder();
    start.append("UPDATE " + tableName + " SET ");

    // Should have same number of values as columns to update
    JSONArray columns = jsonObject.getJSONArray("columns");
    JSONArray values = jsonObject.getJSONArray("values");
    if (columns.length() != values.length()) {
      throw new SqlFromJsonParsingException(
        "Unequal number of fields and values for update");
    }

    // Set the values
    for (int i = 0; i < columns.length(); i++) {
      start.append(columns.get(i) + " = '" + values.get(i) + "', ");
    }
    start = removeLastComma(start);
    String beginning = start.toString();

    // Do we have a where clause?
    String where = "";
    if (jsonObject.has("where")) {
      where = constructWhereClause(jsonObject);
    } else {
      beginning = beginning.trim();
    }

    String end = ";";

    return beginning + where.trim() + end;
  }

  /**
   * Create SQL SELECT statement defined in an Action Pack's pack_data JSONObject
   * @param jsonObject One of the items from an Action Pack's pack_data file
   * @return String. The SQL SELECT Statement
   */
  private static String parseSelectStatement(JSONObject jsonObject) {

    String tableName = jsonObject.getJSONObject("meta").get("table").toString();

    // Build the start of the select statement
    StringBuilder start = new StringBuilder();
    start.append("SELECT ");

    // Add the columns
    start = addColumns(jsonObject, start);

    // From
    start.append("FROM " + tableName + " ");
    String beginning = start.toString();

    // Do we have a where clause?
    String where = "";
    if (jsonObject.has("where")) {
      where = constructWhereClause(jsonObject);
    } else {
      beginning = beginning.trim();
    }

    String end = ";";

    return beginning + where.trim() + end;
  }

  /**
   * Add the columns to a SQL statement
   * @param jsonObject JSONObject containing the column names
   * @param start StringBuilder. The beginning of the SQL statement
   * @return StringBuilder. The beginning plus the columns
   */
  private static StringBuilder addColumns(JSONObject jsonObject,
                                          StringBuilder start) {
    // Add the columns being selected
    JSONArray columns = jsonObject.getJSONArray("columns");
    for (int i = 0; i < columns.length(); i++) {
      start.append(columns.get(i) + ", ");
    }
    return removeLastComma(start);
  }

  /**
   * Construct a where clause form a where element in the JSON and return
   * it as a string to the calling builder method.
   * @param jsonObject The object containing the where definition
   * @return String. The constructed where clause
   */
  private static String constructWhereClause(JSONObject jsonObject) {

    StringBuilder whereClause = new StringBuilder();
    JSONObject where;

    whereClause.append("WHERE");
    where = jsonObject.getJSONObject("where");

    JSONArray predicates;
    if (where.has("predicates")) {
      predicates = where.getJSONArray("predicates");
      for (int i = 0; i < predicates.length(); i++) {
        JSONObject predicate = predicates.getJSONObject(i);
        String logicalOperator = (predicate.has("logical_operator")) ?
          predicate.getString("logical_operator") : "";
        String left = predicate.getString("left");
        String right = predicate.getString("right");
        String operator = predicate.getString("operator");
        whereClause.append(
          logicalOperator + " " +
            left + " " +
            operator + " " +
            "'" + right + "' ");
      }
    }

    return whereClause.toString();
  }

  /**
   * Utility method to remove the last comma from a StringBuilder
   * @param target The StringBuilder to modify
   * @return The modified StringBuilder
   */
  public static StringBuilder removeLastComma(StringBuilder target) {
    return target.deleteCharAt(target.toString().lastIndexOf(','));
  }
}
