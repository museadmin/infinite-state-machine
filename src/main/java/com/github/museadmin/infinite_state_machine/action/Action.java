package com.github.museadmin.infinite_state_machine.action;

import com.github.museadmin.infinite_state_machine.data.access.dal.DataAccessLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * Parent for all actions.
 */
public abstract class Action implements IAction{

  public static final Logger LOGGER = LoggerFactory.getLogger(Action.class.getName());

  private String actionName = getClass().getSimpleName();
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

  // ================= Hooks =================

  /**
   * Check if all "After" actions have completed so that we can
   * change state to STOPPED.
   * @return True if not all complete
   */
  public Boolean afterActionsComplete() {
    return dataAccessLayer.afterActionsComplete();
  }

  /**
   * Check if all "Before" actions have completed so that we can
   * change state to running.
   * @return True if all complete
   */
  public Boolean beforeActionsComplete() {
    return dataAccessLayer.beforeActionsComplete();
  }

  // ================= Activation =================

  /**
   * Test if this action is active
   * @return True or False for not active
   */
  public Boolean active() {
    return dataAccessLayer.active(actionName);
  }

  /**
   * Test if a named action is active
   * @param action String name of action
   * @return true if active
   */
  public boolean active(String action) {
    return dataAccessLayer.active(action);
  }

  /**
   * Activate an action.
   * @param actionName The name of the axction to activate
   */
  public void activate(String actionName) {
    dataAccessLayer.activate(actionName);
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

  // ================= Run phase Manipulation =================

  /**
   * Set the run state. The run states are an option group
   * Hence the special method for setting these.
   * EMERGENCY_SHUTDOWN
   * NORMAL_SHUTDOWN
   * RUNNING
   * STARTING
   * STOPPED
   * @param runPhase Name of state to change to
   */
  public void changeRunPhase(String runPhase) {
    dataAccessLayer.changeRunPhase(runPhase);
  }

  /**
   * Return the active run phase
   * @return The name of the active run phase
   */
  public String queryRunPhase() {
    return dataAccessLayer.queryRunPhase();
  }

  // ================= Property Manipulation =================

  /**
   * Insert a new property into the properties table
   * @param property The name of the property
   * @param value The value of the property
   */
  public void insertProperty(String property, String value) {
    dataAccessLayer.insertProperty(property, value);
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
   * Update an existing property in the properties table
   * @param property The name of the property
   * @param value The value of the property
   */
  public void updateProperty(String property, String value) {
    dataAccessLayer.updateProperty(property, value);
  }

  // ================= State Manipulation =================

  /**
   * Set a state in the state table
   * @param stateName The name of the state
   */
  public void setState(String stateName) {
    dataAccessLayer.setState(stateName);
  }

  /**
   * Unset a state in the state table
   * @param stateName The name of the state
   */
  public void unsetState(String stateName) {
    dataAccessLayer.unsetState(stateName);
  }

  // ================= File Manipulation =================

  /**
   * Create directory/s under the run root
   * @param directory the directory or directories to append. Responsibility for ensuring
   *                  the correct separator lies with the developer and is assumed to be
   *                  correct.
   */
  public String createRunDirectory(String directory) {

    String path = runRoot + File.separator + directory;

    File target = new File(path);
    if (! target.isDirectory()) {
      target.mkdirs();
    }
    return path;
  }


}
