package com.alexfoglia.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlUnorderedList;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = AWebController.class)
@Import(WebSecurityConfig.class)
public class GridHtmlUnitTest {

	@Autowired
	private WebClient webClient;

	@MockBean
	private IGridService gridService;
	
	@Test
	public void fakeTest() {
		
	}
	/*
	@Before
	public void setUp() {
		String username = "user";
		String password = "password";
		String base64encodedUsernameAndPassword = base64Encode(username + ":" + password);
		webClient.addRequestHeader("Authorization", "Basic " + base64encodedUsernameAndPassword);
	}

	private static String base64Encode(String stringToEncode) {
		return DatatypeConverter.printBase64Binary(stringToEncode.getBytes());
	}

	@Test
	public void welcomePageTest() throws Exception {

		HtmlPage page = this.webClient.getPage("/");
		assertThat(page.getTitleText()).isEqualTo("Home Page");
		List<DomElement> h1 = page.getElementsByTagName("h1");
		assertThat(h1.size()).isEqualTo(1);

		final HtmlDivision div = page.getHtmlElementById("choice");
		assertNotNull(div);
		final HtmlUnorderedList ul = page.getHtmlElementById("list");
		assertNotNull(ul);
		final int li = ul.getChildElementCount();
		assertThat(li).isEqualTo(2);
		final HtmlAnchor a = page.getAnchorByHref("/viewdb");
		final HtmlAnchor a1 = page.getAnchorByHref("/addtable");
		final HtmlAnchor a2 = page.getAnchorByHref("/remtable");
		assertEquals( "HtmlAnchor[<a href=\"/viewdb\">]",a.toString());
		assertEquals("HtmlAnchor[<a href=\"/addtable\">]",a1.toString());
		assertEquals( "HtmlAnchor[<a href=\"/remtable\">]",a2.toString());

		HtmlPage pageTemp = a.click();
		HtmlPage pageExpected = this.webClient.getPage("/viewdb");
		assertEquals(pageExpected.getTitleText(), pageTemp.getTitleText());

		pageTemp = a1.click();
		pageExpected = this.webClient.getPage("/addtable");
		assertEquals(pageExpected.getTitleText(), pageTemp.getTitleText());

		pageTemp = a2.click();
		pageExpected = this.webClient.getPage("/remtable");
		assertEquals(pageExpected.getTitleText(), pageTemp.getTitleText());
	}
	
	@Test
	public void tableAddTest() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = this.webClient.getPage("/addtable");
		List<DomElement> h1 = page.getElementsByTagName("h1");
		assertThat(h1.size()).isEqualTo(1);
		
		final HtmlForm form = page.getFormByName("form");
		form.getInputByName("number").setValueAttribute("2");
		form.getInputByName("content").setValueAttribute("1101");
		final HtmlButton submit = form.getButtonByName("submit");
		final HtmlPage page2 = submit.click();
		verifyInvokedStoreInDbWithArguments(2,new int[][] {{1,1},{0,1}});
		
		HtmlPage page1 = this.webClient.getPage("/");
		assertEquals(page1.getTitleText(), page2.getTitleText());
		assertGoBackIsWorking(page);
	}

	private void verifyInvokedStoreInDbWithArguments(int n, int[][] expmat) {
		ArgumentCaptor<Integer> c1=ArgumentCaptor.forClass(Integer.class);
		ArgumentCaptor<int[][]> c2=ArgumentCaptor.forClass(int[][].class);
		verify(gridService,times(1)).storeInDb(c1.capture(),c2.capture());
		assertEquals(n,c1.getValue().intValue());
		assertArrayEquals(expmat,c2.getValue());
	}
	
	private void assertGoBackIsWorking(HtmlPage page) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		final HtmlAnchor a = page.getAnchorByHref("/");
		assertEquals("HtmlAnchor[<a href=\"/\">]",a.toString());
		
		HtmlPage pageTemp = a.click();
		HtmlPage pageExpected = this.webClient.getPage("/");
		assertEquals(pageTemp.getTitleText(), pageExpected.getTitleText());
	}
	
	@Test
	public void tableAddResetTest() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = this.webClient.getPage("/addtable");
		final HtmlForm form = page.getFormByName("form");
		form.getInputByName("number").setValueAttribute("4");
		form.getInputByName("content").setValueAttribute("1101");
		final HtmlButton reset = form.getButtonByName("reset");
		reset.click();
		assertEquals("0",form.getInputByName("number").getAttribute("value"));
		assertEquals("",form.getInputByName("content").getValueAttribute());
	}
	
	
	
	

	@Test
	public void tableViewWithNoGridTest() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = this.webClient.getPage("/viewdb");
		assertThat(page.getTitleText()).isEqualTo("Database Contents View");
		assertThat(page.getBody().getTextContent()).contains("No Grid");
		assertGoBackIsWorking(page);
	}
	
	@Test
	public void HomePageWithGrids() throws Exception {
		int[][] matrix1 = new int[][] { { 0, 0 }, { 0, 0 } };
		int[][] matrix2 = new int[][] { { 1, 1, 1 }, { 1, 1, 1 }, { 1, 1, 1 } };
		DatabaseGrid expected1 = new DatabaseGrid(2,matrix1);
		DatabaseGrid expected2 = new DatabaseGrid(3,matrix2);
		expected1.setId("1");
		expected2.setId("2");
		when(gridService.findAllGridsInDb()).thenReturn(Arrays.asList(expected1, expected2));

		HtmlPage page = this.webClient.getPage("/viewdb");
		assertThat(page.getTitleText()).isEqualTo("Database Contents View");
		assertThat(page.getBody().getTextContent()).doesNotContain("No Grids");

		HtmlTable table = page.getHtmlElementById("grid_table");

		List<String> cells = new ArrayList<String>();
		for (final HtmlTableRow row : table.getRows()) {
			for (final HtmlTableCell cell : row.getCells()) {
				cells.add(cell.asText());
			}
		}
		
		List<String> expectedCells = new ArrayList<String>();
		expectedCells.add("ID");
		expectedCells.add("N");
		expectedCells.add("1");
		expectedCells.add("2");
		expectedCells.add("2");
		expectedCells.add("3");
		assertThat(cells).isEqualTo(expectedCells);
		assertGoBackIsWorking(page);
	}

	*/
}
