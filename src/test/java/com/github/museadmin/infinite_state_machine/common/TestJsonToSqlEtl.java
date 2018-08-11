package com.github.museadmin.infinite_state_machine.common;


import com.github.museadmin.infinite_state_machine.common.utils.JsonToSqlEtl;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class TestJsonToSqlEtl {

  protected JSONObject jsonObject;

  @Before
  public void setup() throws Exception {

    jsonObject = new JSONObject(
      IOUtils.toString(
        this.getClass().getResource("/test_data/sql_test_data.json"),
        "UTF-8"
      )
    );
  }

  @Test
  public void testEtlReturnsSqlSelectStatementWithoutWhereClause() {
    ArrayList<String> statements = JsonToSqlEtl.parseSqlFromJson(jsonObject);
    Assert.assertEquals(
      "SELECT status, state_flag, note FROM states;",
      statements.get(0));
  }

  @Test
  public void testEtlReturnsSqlSelectStatementWithWhereClause() {
    ArrayList<String> statements = JsonToSqlEtl.parseSqlFromJson(jsonObject);
    Assert.assertEquals(
      "SELECT status, state_flag, note FROM states " +
        "WHERE status = 'false';",
      statements.get(1));
  }

  @Test
  public void testEtlReturnsSqlSelectStatementWithWhereClauseAndAnotherClause() {
    ArrayList<String> statements = JsonToSqlEtl.parseSqlFromJson(jsonObject);
    Assert.assertEquals(
      "SELECT status, state_flag, note FROM states " +
        "WHERE status = 'false' AND state_flag = 'READY_TO_RUN';",
      statements.get(2));
  }

  @Test
  public void testEtlReturnsSqlSelectStatementWithWildcard() {
    ArrayList<String> statements = JsonToSqlEtl.parseSqlFromJson(jsonObject);
    Assert.assertEquals(
      "SELECT * FROM states;",
      statements.get(3));
  }

  @Test
  public void testEtlReturnsSqlUpdateStatementWithWhereClauseAndAnotherClause(){
    ArrayList<String> statements = JsonToSqlEtl.parseSqlFromJson(jsonObject);
    Assert.assertEquals(
      "UPDATE states SET status = 'true', " +
        "state_flag = 'TEST_FLAG', note = 'Test Note' " +
        "WHERE status = 'false' AND state_flag = 'READY_TO_RUN';",
      statements.get(4));
  }

  @Test
  public void testEtlReturnsSqlDeleteStatementWithWhereClause(){
    ArrayList<String> statements = JsonToSqlEtl.parseSqlFromJson(jsonObject);
    Assert.assertEquals(
      "DELETE FROM states WHERE status = 'false';",
      statements.get(5));
  }

  @Test
  public void testEtlReturnsSqlDeleteStatementWithWhereClauseAndAnotherClause(){
    ArrayList<String> statements = JsonToSqlEtl.parseSqlFromJson(jsonObject);
    Assert.assertEquals(
      "DELETE FROM states WHERE status = 'false'" +
        " AND state_flag = 'READY_TO_RUN';",
      statements.get(6));
  }

}
