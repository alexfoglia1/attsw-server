package org.end2end;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.alexfoglia.server.ServerApplication;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;

import client.gui.GUI;

public class End2EndIT {

	private static FrameFixture window;
	private static GUI frame;
	private static HtmlUnitDriver browser;

	@BeforeClass
	public static void setupClass() {

		frame = GuiActionRunner.execute(() -> GUI.createGui(false));
		window = new FrameFixture(frame.getFrame());
		window.show();

		SpringApplicationBuilder builder1 = new SpringApplicationBuilder(ServerApplication.class);
		builder1.run(new String[] { "" });
		browser = new HtmlUnitDriver() {
			protected WebClient modifyWebClient(WebClient client) {
				DefaultCredentialsProvider creds = new DefaultCredentialsProvider();
				creds.addCredentials("user", "password");
				client.setCredentialsProvider(creds);
				return client;
			}
		};
	}

	@Test
	public void testConnection() {
		window.textBox("portField").setText("8080");
		window.button("btnCreateConn").click();
		window.label("lblOutput").requireText(GUI.OPERATION_OK);

	}

	@Test
	public void testConnWhenServerIsUnreacheable() {
		window.textBox("portField").setText("8079");
		window.button("btnCreateConn").click();
		window.label("lblOutput").requireText(GUI.SERVER_UNREACHEABLE);
	}

	@Test
	public void testBrowserCanConnect() {
		browser.get("http://localhost:8080");
		assertNotNull(browser.findElementByLinkText("View contents"));
		assertNotNull(browser.findElementByLinkText("Add table"));
	}

	@Test
	public void testAllGrids() {

	}

}
