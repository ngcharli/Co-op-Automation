package com.automation.framework;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Scanner;

public class CLI {
  public static String[] promptSelections(JSONObject config) {
    // Initialize interactive scanner reading standard input streams
    Scanner scanner = new Scanner(System.in);

    System.out.println("\n[!] Standalone Automation Prompt Utility Running");
    System.out.println("==================================================");

    // Navigate into the correct selection options block matching your generic JSON structure
    JSONObject optionsObj = config.getJSONObject("selection_options");

    // 1. Process Regions Array
    JSONArray regionsArr = optionsObj.getJSONArray("regions");

    System.out.println("\nAvailable Target Environments / Regions:");
    for (int i = 0; i < regionsArr.length(); i++) {
      System.out.println(" [" + (i + 1) + "] " + regionsArr.getString(i));
    }
    System.out.print("Select target destination index number: ");
    int regionChoice = Integer.parseInt(scanner.nextLine().trim()) - 1;
    String selectedRegion = regionsArr.getString(regionChoice);

    // 2. Process Roles Array
    JSONArray rolesArr = optionsObj.getJSONArray("roles");

    System.out.println("\nAvailable Functional Roles:");
    for (int i = 0; i < rolesArr.length(); i++) {
      System.out.println(" [" + (i + 1) + "] " + rolesArr.getString(i));
    }
    System.out.print("Select execution user role index number: ");
    int roleChoice = Integer.parseInt(scanner.nextLine().trim()) - 1;
    String selectedRole = rolesArr.getString(roleChoice);

    System.out.println("==================================================");
    System.out.println("[+] Selection confirmed: Target=" + selectedRegion + " | Role=" + selectedRole);
    System.out.println("[+] Handing execution thread to engine...\n");

    return new String[]{selectedRegion, selectedRole};
  }
}