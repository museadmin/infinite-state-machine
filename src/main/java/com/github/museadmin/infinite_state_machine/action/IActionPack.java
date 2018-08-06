package com.github.museadmin.infinite_state_machine.action;

import com.github.museadmin.infinite_state_machine.data.access.dal.DataAccessLayer;
import org.json.JSONObject;
import java.util.ArrayList;

public interface IActionPack {
  JSONObject getJsonObjectFromResourceFile(String fileName);
  ArrayList getActionsFromActionPack(
    DataAccessLayer dataAccessLayer,
    String runRoot
  );
}
