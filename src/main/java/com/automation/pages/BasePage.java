package com.automation.pages;
import com.automation.exceptions.ElementInteractionException;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class BasePage {
  protected WebDriver driver;
  protected WebDriverWait wait;

  public BasePage(WebDriver driver) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  // Wrapped Click Handler
  protected void click(By locator, String elementName) {
    try {
      wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    } catch (TimeoutException e) {
      throw new ElementInteractionException(
          "Timed out waiting to click on: " + elementName + " using locator: " + locator, e);
    } catch (ElementClickInterceptedException e) {
      throw new ElementInteractionException(
          "Element was intercepted/overlayed: " + elementName + " using locator: " + locator, e);
    } catch (Exception e) {
      throw new ElementInteractionException(
          "Failed to click on: " + elementName, e);
    }
  }

  // Wrapped Text Input
  protected void sendKeys(By locator, String text, String fieldName) {
    try {
      WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
      element.clear();
      element.sendKeys(text);
    } catch (Exception e) {
      throw new ElementInteractionException(
          "Failed to enter text into field: " + fieldName, e);
    }
  }

  // Safe Check for UI States (Alternative Paths)
  protected boolean isElementDisplayed(By locator) {
    try {
      return driver.findElement(locator).isDisplayed();
    } catch (NoSuchElementException | StaleElementReferenceException e) {
      return false;
    }
  }
}
