package com.automation.framework;

import com.automation.exceptions.FrameworkConfigException;
import java.io.File;
import java.io.FileReader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Main {
  public static void main(String[] args) {
    try {
      System.out.println("--------------------------------------------------");
      System.out.println("[*] App Root Path: " + new File(".").getAbsolutePath());
      System.out.println("--------------------------------------------------");

      // 1. Load the configuration file explicitly from the project root
      File configFile = new File("config.json").getAbsoluteFile();
      if (!configFile.exists()) {
        throw new FrameworkConfigException("Configuration file missing! Expected path: " + configFile.getPath());
      }

      System.out.println("[+] Found config.json at: " + configFile.getPath());

      JSONObject config;
      try (FileReader fileReader = new FileReader(configFile)) {
        JSONTokener tokener = new JSONTokener(fileReader);
        config = new JSONObject(tokener);
      } catch (Exception e) {
        throw new FrameworkConfigException("Failed to read or parse config.json at path: " + configFile.getPath(), e);
      }

      // 2. Ensure the local automation download directory path exists
      try {
        if (!config.has("app_config")) {
          throw new FrameworkConfigException("Missing required object key 'app_config' in config.json.");
        }

        String downloadPath = config.getJSONObject("app_config").getString("download_dir");
        File downloadDir = new File(downloadPath);
        if (!downloadDir.exists() && !downloadDir.mkdirs()) {
          throw new FrameworkConfigException("Failed to create download directory at path: " + downloadPath);
        }
      } catch (JSONException e) {
        throw new FrameworkConfigException("Missing 'download_dir' key under 'app_config' in config.json.", e);
      }

      // 3. Trigger interactive CLI selection tools
      String[] selections = CLI.promptSelections(config);
      String region = selections[0];
      String role = selections[1];

      // 4. Instantiate and execute the core automation engine
      AutomationEngine engine = new AutomationEngine(config);
      engine.runAutomation(region, role);

    } catch (FrameworkConfigException e) {
      System.err.println("\n[!] FRAMEWORK CONFIGURATION ERROR: " + e.getMessage());
      if (e.getCause() != null) {
        System.err.println("    Caused by: " + e.getCause().getMessage());
      }
      e.printStackTrace();
    } catch (Exception e) {
      System.err.println("\n[-] Fatal error triggered at primary entry thread: " + e.getMessage());
      e.printStackTrace();
    }
  }
}