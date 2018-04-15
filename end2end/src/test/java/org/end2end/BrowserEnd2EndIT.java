package org.end2end;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.alexfoglia.server.ServerApplication;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/resources")
public class BrowserEnd2EndIT {

	@BeforeClass
	public static void setupClass() {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(ServerApplication.class);
		builder.headless(false);
		builder.run(new String[] { "--server.port=9999" });
	}

}
