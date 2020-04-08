package com.github.museadmin.infinite_state_machine.ism;

import com.github.museadmin.infinite_state_machine.common.action.IAction;
import com.github.museadmin.infinite_state_machine.common.dal.DataAccessLayer;
import com.github.museadmin.infinite_state_machine.common.lib.PropertyCache;

import java.util.ArrayList;

public class RunState {

  public final ArrayList<IAction> actions = new ArrayList<>();
  public final String epochSeconds = Long.toString(System.currentTimeMillis());
  public DataAccessLayer dataAccessLayer = null;
  public final PropertyCache propertyCache = new PropertyCache();

  public RunState() {
    propertyCache.setProperty("epoch", epochSeconds);
  }
}
