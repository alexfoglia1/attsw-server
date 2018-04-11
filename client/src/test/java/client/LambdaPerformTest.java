package client;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import client.gui.GUI;

public class LambdaPerformTest {
	
	private GUI frame;
	@Before
	public void setUp() {
		frame=Mockito.spy(GUI.createGui(true));
		frame.mockClient(Mockito.mock(IClient.class));
		frame.setGridEnabled(true);
		frame.setPathEnabled(true);
	}

	@Test
	public void testDoPerformWhenNoClientIsProvided() {
		frame.mockClient(null);
		frame.doPerform();
		verify(frame,times(1)).alert(GUI.NO_CONNECTOR);
		
	}
	@Test
	public void testDoPerformWhenOpIsRequestAll() {
		frame.setSelectedAction(0);
		frame.doPerform();
		verify(frame,times(1)).requestAll();
	}
	@Test
	public void testDoPerformWhenOpIsRequestAGrid() {
		frame.setSelectedAction(1);
		frame.doPerform();
		verify(frame,times(1)).caseRequestGrid(anyString());
	}
	@Test
	public void testDoPerformWhenOpIsRequestPath() {
		frame.setSelectedAction(2);
		frame.doPerform();
		verify(frame,times(1)).caseRequestPath(anyString(),anyString(),anyString());
	}

}
