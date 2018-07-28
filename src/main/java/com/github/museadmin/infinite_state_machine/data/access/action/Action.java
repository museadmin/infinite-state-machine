package com.github.museadmin.infinite_state_machine.data.access.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.museadmin.infinite_state_machine.data.access.dal.DataAccessLayer;


/**
 * Parent for all actions.
 */
public class Action {

  private static final Logger LOGGER = LoggerFactory.getLogger(Action.class.getName());

  public String actionName = getClass().getName();
  public String SQLTRUE = "1";
  public String SQLFALSE = "0";

  // ==================== Public Methods =====================
  public void setDataAccessLayer(DataAccessLayer dataAccessLayer) {
    this.dataAccessLayer = dataAccessLayer;
  }
  public DataAccessLayer dataAccessLayer;

  public void setRunRoot(String runRoot) {
    this.runRoot = runRoot;
  }
  public String runRoot;

  /**
   * Activate an action.
   */
  private void activate() {

  }

  /**
   * Test if action is active.
   * @return True if action is active.
   */
  public Boolean actionIsNotActive() {
    dataAccessLayer.executeSqlQuery(
      "SELECT active, phase from actions where action = '" + actionName + "';"
    );
    return true;
  }

  /**
   * Deactivate an action.
   * @param actionFlag The name of the action to deactivate
   */
  public void deactivate(String actionFlag) {

  }

  /**
   * Return the payload for this action read from the database.
   */
  public void payload() {

  }


}
