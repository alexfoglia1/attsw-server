package org.end2end;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class BrowserEnd2EndStepsDefinition {
	private static HtmlUnitDriver browser = new HtmlUnitDriver() {
		@Override
		protected WebClient modifyWebClient(WebClient client) {
			DefaultCredentialsProvider creds = new DefaultCredentialsProvider();
			creds.addCredentials("user", "password");
			client.setCredentialsProvider(creds);
			return client;
		}
	};
	private static final String BASEURL="http://localhost:9999";
	private static final String CLEARURL=BASEURL+"/cleardb";
	private static final String POPURL=BASEURL+"/populate";
	private static final String VIEWURL=BASEURL+"/viewdb";
	private static final String GRIDTABLE="grid_table";
	@Given("^The database is empty$")
	public void thedatabaseisempty()   {
		browser.get(CLEARURL);
	}

	@Given("^The database is not empty$")
	public void thedatabaseisnotempty()  {
		browser.get(POPURL);
	}

	@When("^The user is on Home Page$")
	public void theuserisonHomePage()  {
		browser.get(VIEWURL);
	}

	@Then("^A message \"(.*?)\" must be shown$")
	public void amessagemustbeshown(String arg1)  {
		assertTrue(browser.getPageSource().contains(arg1));
	}

	@Then("^A list of grids must be shown with a deletion link$")
	public void alistofgridsmustbeshownwithadeletionlink()  {
		WebElement table = browser.findElementById(GRIDTABLE);
		assertTableAsExpected("ID N (.*?) 0 Delete (.*?) 1 Delete (.*?) 3 Delete",table.getText());
		browser.get(CLEARURL);
	}

	private void assertTableAsExpected(String regex, String html) {
		assertTrue(html.matches(regex));
	}
	
	@When("^The user deletes one grid$")
	public void theuserdeletesonegrid()  {
		browser.get(VIEWURL);
		List<WebElement> buttons=browser.findElementsByLinkText("Delete");
		buttons.get(0).click();
	}

	@Then("^A list of grids must be shown where there is not the deleted grid$")
	public void alistofgridsmustbeshownwherethereisnotthedeletedgrid()  {
		WebElement table = browser.findElementById(GRIDTABLE);
		assertTableAsExpected("ID N (.*?) 1 Delete (.*?) 3 Delete",table.getText());
		browser.get(CLEARURL);
	}
	
	@Given("^The server is running$")
	public void theserverisrunning()  {
	    /*  Server is always running
	     *  because it  is  launched
	     *  before the  test   class
	     */
	}

	@When("^The user connects to the server$")
	public void theuserconnectstotheserver()  {
	    browser.get(BASEURL);
	}

	@Then("^A welcome page with link to view db and insert table must be shown$")
	public void awelcomepagewithlinktoviewdbandinserttablemustbeshown()  {
		assertEquals("Home Page",browser.getTitle());
		assertTrue(browser.getPageSource().contains("Manage Database"));
		browser.findElementByLinkText("View contents").click();
		assertEquals("Database Contents View",browser.getTitle());
		browser.get(BASEURL);
		browser.findElementByLinkText("Add table").click();
		assertEquals("Add Table",browser.getTitle());
	}

	@When("^The user inserts one grid$")
	public void theuserinsertsonegrid()  {
	   browser.get("http://localhost:9999/addtable");
	   assertTrue(browser.getPageSource().contains("Choose the size of the matrix"));
	   WebElement insertNumber=browser.findElementById("fname");
	   insertNumber.sendKeys("1");
	   browser.findElementById("button").submit();
	}

	@Then("^A list with that grid must be shown$")
	public void alistwiththatgridmustbeshown()  {
	   assertEquals("Database Contents View",browser.getTitle());
	   WebElement table = browser.findElementById(GRIDTABLE);
	   assertTableAsExpected("ID N (.*?) 1 Delete",table.getText());
	   browser.get(CLEARURL);
	}

}
