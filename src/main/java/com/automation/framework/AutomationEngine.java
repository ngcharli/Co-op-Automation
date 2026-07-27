package com.automation.framework;

import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class AutomationEngine {
  private final JSONObject config;
  private WebDriver driver;
  private WebDriverWait wait;

  public AutomationEngine(JSONObject config) {
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
      throw new RuntimeException(e);
    }
  }

  public void runAutomation(String region, String role) {
    try {
      // Will be updated to match the generic "app_config" key in config.example.json
      String targetUrl = config.getJSONObject("app_config").getString("url");
      System.out.println("[*] Navigating to target application: " + targetUrl);
      System.out.println("[*] Configuration Context - Region: " + region + " | Role: " + role);

      this.driver.get(targetUrl);

      System.out.println("\n==================================================");
      System.out.println("[+] Live Browser Page Title: " + this.driver.getTitle());
      System.out.println("==================================================\n");

      // Execution step simulation delay
      Thread.sleep(5000);

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