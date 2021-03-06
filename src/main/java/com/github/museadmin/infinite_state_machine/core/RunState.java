package com.github.museadmin.infinite_state_machine.core;

import com.github.museadmin.infinite_state_machine.common.action.IAction;
import com.github.museadmin.infinite_state_machine.common.dal.DataAccessLayer;
import com.github.museadmin.infinite_state_machine.common.lib.PropertyCache;

import java.util.ArrayList;

public class RunState {

  public ArrayList<IAction> actions = new ArrayList<>();

  public String epochSeconds = Long.toString(System.currentTimeMillis());
  public DataAccessLayer dataAccessLayer = null;
  public PropertyCache propertyCache = new PropertyCache();

  // The array of actions
  public ArrayList<String> statements = new ArrayList<>();

  public Boolean running = true;
}
