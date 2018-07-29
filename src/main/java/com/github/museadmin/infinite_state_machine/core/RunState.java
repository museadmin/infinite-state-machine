package com.github.museadmin.infinite_state_machine.core;

import com.github.museadmin.infinite_state_machine.core.action_pack.ISMCoreActionPack;
import com.github.museadmin.infinite_state_machine.data.access.action.IAction;
import com.github.museadmin.infinite_state_machine.data.access.dal.DataAccessLayer;
import com.github.museadmin.infinite_state_machine.lib.PropertyCache;

import java.util.ArrayList;

public class RunState {

  public ArrayList<IAction> actions = new ArrayList<>();

  public String epochSeconds = Long.toString(System.currentTimeMillis());
  public DataAccessLayer dataAccessLayer = null;
  public ISMCoreActionPack ismCoreActionPack = new ISMCoreActionPack();
  public PropertyCache propertyCache = new PropertyCache("config.properties");
  public String getRdbms() {
    return rdbms;
  }

  // Runtime directory structure and files
  public String dbFile;
  public String rdbms;
  public String runRoot;

  public ArrayList<String> statements = new ArrayList<>();

}
