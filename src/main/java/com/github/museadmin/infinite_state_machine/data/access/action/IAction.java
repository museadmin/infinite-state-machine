package com.github.museadmin.infinite_state_machine.data.access.action;

import com.github.museadmin.infinite_state_machine.data.access.dal.DataAccessLayer;

public interface IAction {
  void execute();
  void activate(String actionName);
  Boolean active();
  Boolean afterActionsComplete();
  Boolean beforeActionsComplete();
  void insertProperty(String property, String value);
  void setDataAccessLayer(DataAccessLayer dataAccessLayer);
  void setRunRoot(String runRoot);
  String queryProperty(String property);
  String queryRunPhase();
  void setState(String stateName);
  void updateProperty(String property, String value);
  void unsetState(String stateName);
}
