package com.github.museadmin.infinite_state_machine.core;

import com.github.museadmin.infinite_state_machine.common.action.Action;
import com.github.museadmin.infinite_state_machine.common.dal.IDataAccessLayer;
import com.github.museadmin.infinite_state_machine.core.action_pack.ISMCoreActionPack;
import com.github.museadmin.infinite_state_machine.lib.PropertyCache;

import java.util.ArrayList;

public class RunState {

  public ArrayList<Action> actions = new ArrayList<>();
  public String dbFile;
  public String epochSeconds = Long.toString(System.currentTimeMillis());
  public IDataAccessLayer iDataAccessLayer = null;
  public ISMCoreActionPack ismCoreActionPack = new ISMCoreActionPack();
  public PropertyCache propertyCache = new PropertyCache("config.properties");
  public String getRdbms() {
    return rdbms;
  }
  public String rdbms;
  public String runRoot;

}
