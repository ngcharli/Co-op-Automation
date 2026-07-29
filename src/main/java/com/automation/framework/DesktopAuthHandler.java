package com.automation.framework;

import com.automation.exceptions.FrameworkConfigException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import org.json.JSONException;
import org.json.JSONObject;

public class DesktopAuthHandler {
  private final JSONObject config;

  public DesktopAuthHandler(JSONObject config) {
    if (config == null || !config.has("auth_security_store")) {
      throw new FrameworkConfigException("Missing required key 'auth_security_store' in config.");
    }

    try {
      this.config = config.getJSONObject("auth_security_store");
      if (!this.config.has("password")) {
        throw new FrameworkConfigException("Missing 'password' field inside 'auth_security_store'.");
      }
    } catch (JSONException e) {
      throw new FrameworkConfigException("Failed to parse 'auth_security_store' from config", e);
    }
  }

  public void login() {
    System.out.println("[*] Pausing browser thread to handle native system dialog...");
    try {
      Thread.sleep(3000);

      Robot robot = new Robot();
      String password = config.getString("password");

      System.out.println("[*] Injecting credentials via Native AWT Engine...");

      // Copy password to clipboard
      StringSelection stringSelection = new StringSelection(password);
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

      // Paste using Ctrl + V
      robot.keyPress(KeyEvent.VK_CONTROL);
      robot.keyPress(KeyEvent.VK_V);
      robot.keyRelease(KeyEvent.VK_V);
      robot.keyRelease(KeyEvent.VK_CONTROL);

      robot.delay(100);

      // Submit native dialog via ENTER key
      robot.keyPress(KeyEvent.VK_ENTER);
      robot.keyRelease(KeyEvent.VK_ENTER);

      System.out.println("[+] System credentials dispatched successfully.");
      Thread.sleep(2000);

    } catch (Exception e) {
      System.err.println("[-] Native dialog automation failed: " + e.getMessage());
    }
  }
}