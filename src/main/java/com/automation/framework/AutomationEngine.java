package com.automation.framework;

import com.automation.exceptions.FrameworkConfigException;
import java.time.Duration;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AutomationEngine {
  private final JSONObject config;
  private WebDriver driver;
  private WebDriverWait wait;

  public AutomationEngine(JSONObject config) {
    if (config == null) {
      throw new FrameworkConfigException("Provided JSONObject config cannot be null.");
    }
    this.config = config;
    setupDriver();
  }

  private void setupDriver() {
    System.out.println("[*] Initializing Edge Driver session...");

    try {
      EdgeOptions options = new EdgeOptions();
      options.addArguments("--start-maximized");

      // Standard Selenium 4 EdgeDriver initialization
      this.driver = new EdgeDriver(options);
      this.wait = new WebDriverWait(this.driver, Duration.ofSeconds(30));

      System.out.println("[+] Browser session established successfully!");

    } catch (Exception e) {
      System.err.println("[-] Driver initialization failed: " + e.getMessage());
      throw new FrameworkConfigException("Failed to initialize EdgeDriver session", e);
    }
  }

  public void runAutomation(String region, String role) {
    try {
      if (!config.has("app_config")) {
        throw new FrameworkConfigException("Missing required object key 'app_config' in configuration.");
      }

      String targetUrl = config.getJSONObject("app_config").getString("url");
      System.out.println("[*] Navigating to target application: " + targetUrl);
      System.out.println("[*] Configuration Context - Region: " + region + " | Role: " + role);

      this.driver.get(targetUrl);

      System.out.println("\n==================================================");
      System.out.println("[+] Live Browser Page Title: " + this.driver.getTitle());
      System.out.println("==================================================\n");

      // Execution step simulation delay
      Thread.sleep(5000);

    } catch (JSONException e) {
      throw new FrameworkConfigException("Failed to extract target URL from 'app_config.url'", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.err.println("[-] Execution thread interrupted: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("[-] Error encountered during execution: " + e.getMessage());
      e.printStackTrace();
    } finally {
      if (this.driver != null) {
        System.out.println("[*] Closing browser session...");
        this.driver.quit();
      }
    }
  }
}