package hooks;

import org.testng.asserts.SoftAssert;

import base.BaseTest;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks extends BaseTest{

public class Hooks {
  
    @Before
    public void setBaseUrl() {
        BaseClass.setup();
    }
}