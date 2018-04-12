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
		SpringApplicationBuilder builder1 = new SpringApplicationBuilder(ServerApplication.class);
		builder1.run(new String[] { "" });
	}

}
