package com.embege.androidcontroller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SDKPath extends JPanel {

	
	public static String PATH2ADB = "adb.exe";
	
	
	
	
	public static String TEMPFILE = System.getProperty("java.io.tmpdir")+"/vb0";
	
	JTextField tf;
	
	public SDKPath() 
	{
		setBorder(BorderFactory.createTitledBorder("Path to adb"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		tf = (JTextField) add (new JTextField(PATH2ADB) );
		
		JButton bu = (JButton) add(new JButton("Browse"));
		bu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					JFileChooser jfc = new JFileChooser(".");
					int result = jfc.showOpenDialog(SDKPath.this);
					if (result == JFileChooser.APPROVE_OPTION)
					{
						tf.setText(jfc.getSelectedFile().getAbsolutePath());
						PATH2ADB = tf.getText();
						AndroidController.props.setProperty("adb", tf.getText());
						
						FileOutputStream out = new FileOutputStream("settings");
						AndroidController.props.store(out,"AndroidController settings");
						out.close();
					}
				} catch (Exception e){
					e.printStackTrace();
				}
				
			}
		});
	}
	
}
