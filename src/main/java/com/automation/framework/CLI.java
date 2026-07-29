package com.automation.framework;

import com.automation.exceptions.FrameworkConfigException;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CLI {
  public static String[] promptSelections(JSONObject config) {
    Scanner scanner = new Scanner(System.in);

    System.out.println("\n[!] Standalone Automation Prompt Utility Running");
    System.out.println("==================================================");

    if (!config.has("selection_options")) {
      throw new FrameworkConfigException("Missing 'selection_options' block in configuration file.");
    }

    try {
      JSONObject optionsObj = config.getJSONObject("selection_options");

      // 1. Process Regions Array
      JSONArray regionsArr = optionsObj.getJSONArray("regions");
      if (regionsArr.isEmpty()) {
        throw new FrameworkConfigException("'regions' array in config is empty.");
      }

      System.out.println("\nAvailable Target Environments / Regions:");
      for (int i = 0; i < regionsArr.length(); i++) {
        System.out.println(" [" + (i + 1) + "] " + regionsArr.getString(i));
      }
      System.out.print("Select target destination index number: ");

      int regionChoice;
      try {
        regionChoice = Integer.parseInt(scanner.nextLine().trim()) - 1;
      } catch (NumberFormatException e) {
        throw new FrameworkConfigException("Invalid CLI input. Region selection must be a numeric integer.", e);
      }

      if (regionChoice < 0 || regionChoice >= regionsArr.length()) {
        throw new FrameworkConfigException("Selected region index [" + (regionChoice + 1) + "] is out of bounds.");
      }
      String selectedRegion = regionsArr.getString(regionChoice);

      // 2. Process Roles Array
      JSONArray rolesArr = optionsObj.getJSONArray("roles");
      if (rolesArr.isEmpty()) {
        throw new FrameworkConfigException("'roles' array in config is empty.");
      }

      System.out.println("\nAvailable Functional Roles:");
      for (int i = 0; i < rolesArr.length(); i++) {
        System.out.println(" [" + (i + 1) + "] " + rolesArr.getString(i));
      }
      System.out.print("Select execution user role index number: ");

      int roleChoice;
      try {
        roleChoice = Integer.parseInt(scanner.nextLine().trim()) - 1;
      } catch (NumberFormatException e) {
        throw new FrameworkConfigException("Invalid CLI input. Role selection must be a numeric integer.", e);
      }

      if (roleChoice < 0 || roleChoice >= rolesArr.length()) {
        throw new FrameworkConfigException("Selected role index [" + (roleChoice + 1) + "] is out of bounds.");
      }
      String selectedRole = rolesArr.getString(roleChoice);

      System.out.println("==================================================");
      System.out.println("[+] Selection confirmed: Target=" + selectedRegion + " | Role=" + selectedRole);
      System.out.println("[+] Handing execution thread to engine...\n");

      return new String[]{selectedRegion, selectedRole};

    } catch (JSONException e) {
      throw new FrameworkConfigException("Failed to parse 'selection_options' JSON structures", e);
    }
  }
}