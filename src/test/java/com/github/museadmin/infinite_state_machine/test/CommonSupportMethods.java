package com.github.museadmin.infinite_state_machine.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class CommonSupportMethods {

  /**
   * Create a one off test properties file
   * @param file
   */
  public static void createTmpPropertiesFile(String file, String tmpDir) {

    FileOutputStream output = null;
    try {
      output = new FileOutputStream(file);
      Properties prop = new Properties();
      prop.setProperty("rdbms", "sqlite3");
      prop.setProperty("runRoot", tmpDir);
      prop.store(output,null);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (output != null) {
        try {
          output.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
