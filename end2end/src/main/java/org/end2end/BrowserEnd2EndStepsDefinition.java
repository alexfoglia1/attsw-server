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
		protected WebClient modifyWebClient(WebClient client) {
			DefaultCredentialsProvider creds = new DefaultCredentialsProvider();
			creds.addCredentials("user", "password");
			client.setCredentialsProvider(creds);
			return client;
		}
	};

	@Given("^The database is empty$")
	public void the_database_is_empty() throws Throwable {
		browser.get("http://localhost:8080/cleardb");
	}

	@Given("^The database is not empty$")
	public void the_database_is_not_empty() throws Throwable {
		browser.get("http://localhost:8080/populate");
	}

	@When("^The user is on Home Page$")
	public void the_user_is_on_Home_Page() throws Throwable {
		browser.get("http://localhost:8080/viewdb");
	}

	@Then("^A message \"(.*?)\" must be shown$")
	public void a_message_must_be_shown(String arg1) throws Throwable {
		assertTrue(browser.getPageSource().contains("No Grids"));
	}

	@Then("^A list of grids must be shown with a deletion link$")
	public void a_list_of_grids_must_be_shown_with_a_deletion_link() throws Throwable {
		WebElement table = browser.findElementById("grid_table");
		assertTableAsExpected("ID N (.*?) 0 Delete (.*?) 1 Delete (.*?) 3 Delete",table.getText());
		browser.get("http://localhost:8080/cleardb");
	}

	private void assertTableAsExpected(String regex, String html) {
		assertTrue(html.matches(regex));
	}
	
	@When("^The user deletes one grid$")
	public void the_user_deletes_one_grid() throws Throwable {
		browser.get("http://localhost:8080/viewdb");
		List<WebElement> buttons=browser.findElementsByLinkText("Delete");
		buttons.get(0).click();
	}

	@Then("^A list of grids must be shown where there is not the deleted grid$")
	public void a_list_of_grids_must_be_shown_where_there_is_not_the_deleted_grid() throws Throwable {
		WebElement table = browser.findElementById("grid_table");
		assertTableAsExpected("ID N (.*?) 1 Delete (.*?) 3 Delete",table.getText());
		browser.get("http://localhost:8080/cleardb");
	}
	
	@Given("^The server is running$")
	public void the_server_is_running() throws Throwable {
	    /*  Server is always running
	     *  because it  is  launched
	     *  before the  test   class
	     */
	}

	@When("^The user connects to the server$")
	public void the_user_connects_to_the_server() throws Throwable {
	    browser.get("http://localhost:8080");
	}

	@Then("^A welcome page with link to view db and insert table must be shown$")
	public void a_welcome_page_with_link_to_view_db_and_insert_table_must_be_shown() throws Throwable {
		assertEquals("Home Page",browser.getTitle());
		assertTrue(browser.getPageSource().contains("Manage Database"));
		browser.findElementByLinkText("View contents").click();
		assertEquals("Database Contents View",browser.getTitle());
		browser.get("http://localhost:8080");
		browser.findElementByLinkText("Add table").click();
		assertEquals("Add Table",browser.getTitle());
	}

	@When("^The user inserts one grid$")
	public void the_user_inserts_one_grid() throws Throwable {
	   browser.get("http://localhost:8080/addtable");
	   assertTrue(browser.getPageSource().contains("Choose the size of the matrix"));
	   WebElement insertNumber=browser.findElementById("fname");
	   insertNumber.sendKeys("1");
	   browser.findElementById("button").submit();
	}

	@Then("^A list with that grid must be shown$")
	public void a_list_with_that_grid_must_be_shown() throws Throwable {
	   assertEquals("Database Contents View",browser.getTitle());
	   WebElement table = browser.findElementById("grid_table");
	   assertTableAsExpected("ID N (.*?) 1 Delete",table.getText());
	   browser.get("http://localhost:8080/cleardb");
	}

}
