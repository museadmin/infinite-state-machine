package com.github.museadmin.infinite_state_machine.data.access.action;

import com.github.museadmin.infinite_state_machine.core.InfiniteStateMachine;
import com.github.museadmin.infinite_state_machine.data.access.dal.DataAccessLayer;
import org.json.JSONObject;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ActionPack implements IActionPack {

  private static final Logger LOGGER = LoggerFactory.getLogger(InfiniteStateMachine.class.getName());

  /**
   * Read in a JSON file and return it as a JSONObject. This method resides
   * here to ensure that the resource file read in is from the inheriting
   * action pack, not the state machine resource directory.
   * @param fileName The unqualified file name for the resource
   * @return JSONObject containing the data
   */
  public JSONObject getJsonObjectFromResourceFile(String fileName) {
    InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
    InputStreamReader isr;
    BufferedReader br;
    StringBuilder sb = new StringBuilder();
    String content;
    try {
      isr = new InputStreamReader(is);
      br = new BufferedReader(isr);
      while ((content = br.readLine()) != null) {
        sb.append(content);
      }
      isr.close();
      br.close();
    } catch (IOException e) {
      LOGGER.error(e.getClass().getName() + ": " + e.getMessage());
      System.exit(1);
    }
    return new JSONObject(sb.toString());
  }

  /**
   * Use introspection to read in all of the classes in a given action pack
   * Filter out the ones that are actions and return them in an array
   */
  public ArrayList getActionsFromActionPack(
    DataAccessLayer dataAccessLayer,
    String runRoot
  ) {
    String packageName = this.getClass().getPackage().getName();

    List<ClassLoader> classLoadersList = new LinkedList<>();
    classLoadersList.add(ClasspathHelper.contextClassLoader());
    classLoadersList.add(ClasspathHelper.staticClassLoader());

    Reflections reflections = new Reflections(new ConfigurationBuilder()
        .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
        .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
        .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName))));

    Set<Class<? extends Action>> classes = reflections.getSubTypesOf(Action.class);
    ArrayList<IAction> actions = new ArrayList<>();

    for (Class a : classes) {
      try {
        Action action = (Action) Class.forName(a.getName()).newInstance();
        // Give the action access to the DAL and path to control directory
        action.setDataAccessLayer(dataAccessLayer);
        action.setRunRoot(runRoot);
        actions.add(action);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
        System.exit(1);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
        System.exit(1);
      } catch (InstantiationException e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
        System.exit(1);
      }
    }
    return actions;
  }

}
