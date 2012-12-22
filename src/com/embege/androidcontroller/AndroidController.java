package com.embege.androidcontroller;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class AndroidController extends JFrame {
	
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new AndroidController();
	}

	public AndroidController() {

		setLocationByPlatform(true);
		setSize(400, 600);
		setTitle("AndroidController");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDefaultLookAndFeelDecorated(true);
		setLayout(new BorderLayout());

		setVisible(true);
		
		
		
		add( new DeviceSelector(), BorderLayout.NORTH );
	
		add( Log.get(), BorderLayout.SOUTH );
		
		add ( new DrawPanel(), BorderLayout.CENTER);
		
		validate();
	}
	
	
	
}
