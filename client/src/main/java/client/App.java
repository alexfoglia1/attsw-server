package client;


import java.awt.EventQueue;
import client.gui.GUI;

/**
 * Main application entry point
 *
 */
public class App 
{
	public static void main( String[] args )
	{
		EventQueue.invokeLater(()-> GUI.createGui(false));
	}
}
