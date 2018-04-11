package org.end2end;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.alexfoglia.server.ServerApplication;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;

import client.gui.GUI;
import client.gui.GUIpanel;

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
	
	private void retrieveAllGrids() {
		window.textBox("portField").setText("8080");
		window.button("btnCreateConn").click();
		window.comboBox("actionsCombo").selectItem(0);
		window.button("btnPerform").click();
	}

	private void addGrids() {
		browser.get("http://localhost:8080/populate");
	}

	@Test
	public void testCanRetrieveGrids() {
		addGrids();
		retrieveAllGrids();
		window.comboBox("gridCombo").requireItemCount(3);
		window.label("lblOutput").requireText(GUI.OPERATION_OK);
	}
	
	private void requestAGrid() {
		window.comboBox("actionsCombo").selectItem(1);
		window.comboBox("gridCombo").selectItem(2);
		window.button("btnPerform").click();
	}
	
	private GUIpanel getGuiPanel() {
		return (GUIpanel) (window.panel("guiPanel").target());
	}
	
	@Test
	public void testRetrieveOneGrid() {
		addGrids();
		retrieveAllGrids();
		requestAGrid();
		GUIpanel pan=getGuiPanel();
		assertEquals(Color.RED, pan.getColorInPoint(0, 0));
		assertEquals(Color.RED, pan.getColorInPoint(0, 1));
		assertEquals(Color.RED, pan.getColorInPoint(0, 2));

		assertEquals(Color.RED, pan.getColorInPoint(1, 0));
		assertEquals(Color.BLACK, pan.getColorInPoint(1, 1));
		assertEquals(Color.RED, pan.getColorInPoint(1, 2));

		assertEquals(Color.RED, pan.getColorInPoint(2, 0));
		assertEquals(Color.RED, pan.getColorInPoint(2, 1));
		assertEquals(Color.BLACK, pan.getColorInPoint(2, 2));
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				String expected_name = "";
				if (pan.getColorInPoint(i, j).equals(Color.RED))
					expected_name = String.format("%d_%d", i, j);
				assertEquals(expected_name, pan.getPrintedNameIn(i, j));
			}
		}
		window.comboBox("gridCombo").requireDisabled();
		window.label("lblOutput").requireText(GUI.OPERATION_OK);
	}
	
	private void requestShortestPath() {
		window.textBox("sourceField").setText("0_0");
		window.textBox("sinkField").setText("0_2");
		window.comboBox("actionsCombo").selectItem(2);
		window.button("btnPerform").click();
	}
	
	@Test
	public void testRequestShortestPath() {
		addGrids();
		retrieveAllGrids();
		requestAGrid();
		requestShortestPath();
		GUIpanel pan=getGuiPanel();
		assertEquals(GUIpanel.DARKGREEN, pan.getColorInPoint(0, 0));
		assertEquals(GUIpanel.DARKGREEN, pan.getColorInPoint(0, 1));
		assertEquals(GUIpanel.DARKGREEN, pan.getColorInPoint(0, 2));
		window.label("lblOutput").requireText(GUI.OPERATION_OK);
	}


	@After
	public void reset() {
		browser.get("http://localhost:8080/cleandb");
		window.button("btnReset").click();
	}


}
