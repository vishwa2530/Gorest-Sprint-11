package hooks;

import org.testng.asserts.SoftAssert;

import base.BaseTest;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks extends BaseTest{

    public static SoftAssert softAssert;
    public static Scenario scenario;

	@Before
    public void beforeScenario(Scenario scenario) {
        setup();
        Hooks.softAssert = new SoftAssert();
        Hooks.scenario = scenario;
        
        System.out.println("----- Test Started -----");
    }

    @After
    public void afterScenario() {
        try {
            softAssert.assertAll();
        } finally {
            System.out.println("----- Test Finished -----");
        }
    }
	
}