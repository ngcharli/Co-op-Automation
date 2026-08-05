package com.automation.framework;

import com.automation.exceptions.FrameworkConfigException;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.win32.W32APIOptions;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class DesktopAuthHandler {

  private interface CustomUser32 extends User32 {
    CustomUser32 INSTANCE = Native.load("user32", CustomUser32.class, W32APIOptions.DEFAULT_OPTIONS);

    boolean SetForegroundWindow(HWND hWnd);
    boolean GetWindowRect(HWND hWnd, RECT rect);
  }

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
    System.out.println("[*] Intercepting Entrust dialog via Absolute Coordinate Click + Hardware Input...");

    String password = config.getString("password");
    boolean success = injectCredentialsAbsolute(password, 15);

    if (!success) {
      throw new FrameworkConfigException("[!] Timed out or failed to interact with Entrust dialog.");
    }

    System.out.println("[+] Native credential injection completed successfully.");
  }

  private boolean injectCredentialsAbsolute(String password, int timeoutSeconds) {
    long startTime = System.currentTimeMillis();
    long maxDuration = timeoutSeconds * 1000L;

    while ((System.currentTimeMillis() - startTime) < maxDuration) {
      HWND windowHwnd = CustomUser32.INSTANCE.FindWindow(null, "Entrust Security Store Unlock");

      if (windowHwnd != null) {
        System.out.println("[+] Found Entrust Dialog Window Handle: " + windowHwnd);

        try {
          // 1. Force window focus
          CustomUser32.INSTANCE.SetForegroundWindow(windowHwnd);
          Thread.sleep(500);

          // 2. Locate window coordinates
          RECT rect = new RECT();
          CustomUser32.INSTANCE.GetWindowRect(windowHwnd, rect);

          int windowX = rect.left;
          int windowY = rect.top;
          int windowWidth = rect.right - rect.left;
          int windowHeight = rect.bottom - rect.top;

          System.out.println(String.format("[*] Dialog Position: X=%d, Y=%d, Width=%d, Height=%d",
              windowX, windowY, windowWidth, windowHeight));

          // 3. Calculate middle-center offset for password field and click
          int clickX = windowX + (windowWidth / 2);
          int clickY = windowY + (int) (windowHeight * 0.45);

          Robot robot = new Robot();
          robot.setAutoDelay(40); // Uniform delay between native events to prevent dropping inputs

          System.out.println("[*] Moving mouse to (" + clickX + ", " + clickY + ") and clicking...");
          robot.mouseMove(clickX, clickY);
          robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
          robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
          robot.delay(300);

          // 4. Clear field using Ctrl+A + Backspace
          robot.keyPress(KeyEvent.VK_CONTROL);
          robot.keyPress(KeyEvent.VK_A);
          robot.keyRelease(KeyEvent.VK_A);
          robot.keyRelease(KeyEvent.VK_CONTROL);
          robot.keyPress(KeyEvent.VK_BACK_SPACE);
          robot.keyRelease(KeyEvent.VK_BACK_SPACE);
          robot.delay(200);

          // 5. Safely type password
          System.out.println("[*] Typing password with explicit key mapping...");
          typePasswordSafely(robot, password);
          robot.delay(300);

          // 6. Navigate to OK button via TAB and trigger with SPACE
          System.out.println("[*] Submitting dialog via TAB + SPACE...");
          robot.keyPress(KeyEvent.VK_TAB);
          robot.keyRelease(KeyEvent.VK_TAB);
          robot.delay(200);

          robot.keyPress(KeyEvent.VK_SPACE);
          robot.keyRelease(KeyEvent.VK_SPACE);

          System.out.println("[+] Keystrokes sent.");
          return true;

        } catch (Exception e) {
          System.out.println("[!] Error during injection: " + e.getMessage());
          return false;
        }
      }

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return false;
      }
    }

    return false;
  }

  private void typePasswordSafely(Robot robot, String text) {
    for (char c : text.toCharArray()) {
      boolean requireShift = Character.isUpperCase(c) || isShiftSymbol(c);
      int keyCode = getKeyCodeForChar(c);

      if (keyCode != KeyEvent.VK_UNDEFINED) {
        if (requireShift) {
          robot.keyPress(KeyEvent.VK_SHIFT);
          robot.delay(20);
        }

        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);

        if (requireShift) {
          robot.delay(20);
          robot.keyRelease(KeyEvent.VK_SHIFT);
        }
        robot.delay(50);
      }
    }
  }

  private boolean isShiftSymbol(char c) {
    String shiftSymbols = "~!@#$%^&*()_+{}|:\"<>?";
    return shiftSymbols.indexOf(c) != -1;
  }

  private int getKeyCodeForChar(char c) {
    switch (c) {
      case '!': return KeyEvent.VK_1;
      case '@': return KeyEvent.VK_2;
      case '#': return KeyEvent.VK_3;
      case '$': return KeyEvent.VK_4;
      case '%': return KeyEvent.VK_5;
      case '^': return KeyEvent.VK_6;
      case '&': return KeyEvent.VK_7;
      case '*': return KeyEvent.VK_8;
      case '(': return KeyEvent.VK_9;
      case ')': return KeyEvent.VK_0;
      case '_': return KeyEvent.VK_MINUS;
      case '+': return KeyEvent.VK_EQUALS;
      case '~': return KeyEvent.VK_BACK_QUOTE;
      case '{': return KeyEvent.VK_OPEN_BRACKET;
      case '}': return KeyEvent.VK_CLOSE_BRACKET;
      case '|': return KeyEvent.VK_BACK_SLASH;
      case ':': return KeyEvent.VK_SEMICOLON;
      case '"': return KeyEvent.VK_QUOTE;
      case '<': return KeyEvent.VK_COMMA;
      case '>': return KeyEvent.VK_PERIOD;
      case '?': return KeyEvent.VK_SLASH;
      default: return KeyEvent.getExtendedKeyCodeForChar(c);
    }
  }
}