package com.alexfoglia.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.isA;
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
		assertThat(li).isEqualTo(3);
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
}
