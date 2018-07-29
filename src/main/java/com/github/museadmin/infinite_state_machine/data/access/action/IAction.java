package com.github.museadmin.infinite_state_machine.data.access.action;

import com.github.museadmin.infinite_state_machine.data.access.dal.DataAccessLayer;

public interface IAction {
  void execute();
  Boolean notActive();
  void setDataAccessLayer(DataAccessLayer dataAccessLayer);
  void setRunRoot(String runRoot);
}
