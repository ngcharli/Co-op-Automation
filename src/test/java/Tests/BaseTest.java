package Tests;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public abstract class BaseTest {

  private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

  public WebDriver getDriver() {
    return driver.get();
  }

  @BeforeMethod
  public void setUp() {
    EdgeOptions options = new EdgeOptions();
    options.addArguments("--start-maximized");

    // Selenium Manager automatically detects, downloads, and matches msedgedriver
    WebDriver localDriver = new EdgeDriver(options);
    localDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

    driver.set(localDriver);
  }

  @AfterMethod
  public void tearDown() {
    if (getDriver() != null) {
      getDriver().quit();
      driver.remove();
    }
  }
}