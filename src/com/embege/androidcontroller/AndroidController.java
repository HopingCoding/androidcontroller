package com.embege.androidcontroller;

import java.awt.BorderLayout;
import java.io.FileInputStream;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class AndroidController extends JFrame {
	
	public static Properties props;
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Log.log("TEMP PATH: "+SDKPath.TEMPFILE);
		
		try {
			props = new Properties();
			FileInputStream in = new FileInputStream("settings");
			props.load(in);
			SDKPath.PATH2ADB = props.getProperty("adb","adb");
			in.close();
			
		} catch (Exception e)
		{
			
		}
		
		new AndroidController();
	}

	public AndroidController() {

		setLocationByPlatform(true);
		setSize(800, 600);
		setTitle("AndroidController");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDefaultLookAndFeelDecorated(true);
		//setLayout(new BorderLayout());

		setVisible(true);
		JPanel left = new JPanel();
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left,new DrawPanel());
		
		add (split);
		
		
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

		left.add( new SDKPath() );
		
		left.add( new DeviceSelector() );
		
		left.add( new Stats() );
		
		left.add( Box.createVerticalGlue());
		
		left.add( Log.get() );
		
		doLayout();
		validate();
		doLayout();
		
		split.setDividerLocation(0.5);
	}
	
	
	
}
