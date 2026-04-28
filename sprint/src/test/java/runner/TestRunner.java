package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
		features = "src/test/resources/features",
		glue = {"stepDefinitions", "hooks"},
		plugin = {
							"pretty",
							"html:target/report.html"
		},
		monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests{

}