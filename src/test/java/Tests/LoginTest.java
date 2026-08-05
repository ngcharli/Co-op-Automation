package Tests;

import com.automation.framework.DesktopAuthHandler;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;

public class LoginTest extends BaseTest {

  @Test
  public void testSunnyPathLogin() {
    // Your sunny path steps here
  }


  @Test
  public void testAlternativePathInvalidCredentials() {
    // Your alternative path steps here
  }

  @Test
  public void testEpkAuthenticationFlow() throws Exception {
    // 1. Read config.json from root directory
    File configFile = new File("config.json").getAbsoluteFile();
    Assert.assertTrue(configFile.exists(), "config.json missing at: " + configFile.getPath());

    JSONTokener tokener = new JSONTokener(new FileReader(configFile));
    JSONObject config = new JSONObject(tokener);

    // 2. Extract values based on your JSON keys
    String targetUrl = config.getJSONObject("app_config").getString("url");

    // 3. Navigate to target application
    getDriver().get(targetUrl);

    // 4. Pass full config to DesktopAuthHandler (it handles fetching 'auth_security_store' internally)
    DesktopAuthHandler authHandler = new DesktopAuthHandler(config);
    authHandler.login();

    // 5. Verification
    Assert.assertNotNull(getDriver().getCurrentUrl(), "URL should be accessible after .epk auth execution.");
  }
}
