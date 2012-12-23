package com.embege.androidcontroller;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Stats extends JPanel {

	float avgRead = 1.0f;
	int numRead = 0;
	float avgConvert = 1.0f;
	int numConvert = 0;
	
	public static JLabel jlRead, jlConv;
	
	public Stats() {
		setBorder(BorderFactory.createTitledBorder("Stats"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		jlRead = (JLabel) add ( new JLabel("Reading: "+DrawPanel.lastRead) );
		jlConv = (JLabel) add ( new JLabel("Converting: "+DrawPanel.lastConvert) );
	}
}
