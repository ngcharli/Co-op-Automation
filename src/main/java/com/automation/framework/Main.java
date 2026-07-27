package com.automation.framework;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;

public class Main {
  public static void main(String[] args) {
    try {
      System.out.println("--------------------------------------------------");
      System.out.println("[*] App Root Path: " + new File(".").getAbsolutePath());
      System.out.println("--------------------------------------------------");

      // 1. Load the configuration file explicitly from the project root
      File configFile = new File("config.json").getAbsoluteFile();
      if (!configFile.exists()) {
        System.err.println("[-] Configuration missing! Expected it at: " + configFile.getPath());
        return;
      }

      System.out.println("[+] Found config.json at: " + configFile.getPath());
      JSONTokener tokener = new JSONTokener(new FileReader(configFile));
      JSONObject config = new JSONObject(tokener);

      // 2. Ensure the local automation download directory path exists
      // Will update key lookup from "cpin" to "app_config"
      String downloadPath = config.getJSONObject("app_config").getString("download_dir");
      new File(downloadPath).mkdirs();

      // 3. Trigger interactive CLI selection tools
      String[] selections = CLI.promptSelections(config);
      String region = selections[0];
      String role = selections[1];

      // 4. Instantiate and execute the core automation engine
      AutomationEngine engine = new AutomationEngine(config);
      engine.runAutomation(region, role);

    } catch (Exception e) {
      System.err.println("[-] Fatal error triggered at primary entry thread: " + e.getMessage());
      e.printStackTrace();
    }
  }
}