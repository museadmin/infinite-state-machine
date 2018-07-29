package com.github.museadmin.infinite_state_machine.data.access.action;

import com.github.museadmin.infinite_state_machine.data.access.dal.DataAccessLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * Parent for all actions.
 */
public abstract class Action implements IAction{

  private static final Logger LOGGER = LoggerFactory.getLogger(Action.class.getName());

  public String actionName = getClass().getSimpleName();
  public DataAccessLayer dataAccessLayer;
  public String runRoot;

  // ==================== Getter + Setters ====================

  /**
   * Assigne a reference to the DAL to the action
   * @param dataAccessLayer The DAL
   */
  public void setDataAccessLayer(DataAccessLayer dataAccessLayer) {
    this.dataAccessLayer = dataAccessLayer;
  }

  /**
   * Reference to the top level run directory created for the run
   * @param runRoot Fully qualified path to the run root directory
   */
  public void setRunRoot(String runRoot) {
    this.runRoot = runRoot;
  }

  // ==================== Helper Methods for Actions =====================

  /**
   * Activate an action.
   */
  private void activate() {

  }

  /**
   * Create directory/s under the run root
   * @param directory the directory or directories to append. Responsibility for ensuring
   *                  the correct separator lies with the developer and is assumed to be
   *                  correct.
   */
  public void createRunDirectory(String directory) {

    String path = runRoot + File.separator + directory;

    File target = new File(path);
    if (! target.isDirectory()) { target.mkdirs(); }
  }

  /**
   * Deactivate the current action
   */
  public void deactivate() {
    dataAccessLayer.deactivate(actionName);
  }

  /**
   * Deactivate an action.
   * @param actionFlag The name of the action to deactivate
   */
  public void deactivate(String actionFlag) {
    dataAccessLayer.deactivate(actionFlag);
  }

  /**
   * Test if this action is active
   * @return True or False for not active
   */
  public Boolean notActive() {
    return dataAccessLayer.notActive(actionName);
  }

  /**
   * Query a property in the properties table
   * @param property Name of the property
   * @return value of the property
   */
  public String queryProperty(String property) {
    return dataAccessLayer.queryProperty(property);
  }

  /**
   * Return the payload for this action read from the database.
   */
  public void payload() {

  }


}
