package com.automation.framework.auth;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import org.json.JSONObject;

public class DesktopAuthHandler {
  private final JSONObject config;

  public DesktopAuthHandler(JSONObject config) {
    // Key updated from 'entrust_security_store' to match config.example.json
    this.config = config.getJSONObject("auth_security_store");
  }

  public void login() {
    System.out.println("[*] Pausing browser thread to handle native system dialog...");
    try {
      // Buffer delay to allow native OS prompt to gain window focus
      Thread.sleep(3000);

      Robot robot = new Robot();
      String password = config.getString("password");

      System.out.println("[*] Injecting credentials via Native AWT Engine...");

      // Simulate physical keystrokes for each character
      for (char c : password.toCharArray()) {
        typeCharacter(robot, c);
      }

      // Submit native dialog via ENTER key
      robot.keyPress(KeyEvent.VK_ENTER);
      robot.keyRelease(KeyEvent.VK_ENTER);

      System.out.println("[+] System credentials dispatched successfully.");
      Thread.sleep(2000); // Verification buffer

    } catch (Exception e) {
      System.err.println("[-] Native dialog automation failed: " + e.getMessage());
    }
  }

  private void typeCharacter(Robot robot, char character) {
    int keyCode = KeyEvent.getExtendedKeyCodeForChar(character);

    if (Character.isUpperCase(character)) {
      robot.keyPress(KeyEvent.VK_SHIFT);
    }

    robot.keyPress(keyCode);
    robot.keyRelease(keyCode);

    if (Character.isUpperCase(character)) {
      robot.keyRelease(KeyEvent.VK_SHIFT);
    }

    robot.delay(50); // Inter-keystroke timing delay
  }
}