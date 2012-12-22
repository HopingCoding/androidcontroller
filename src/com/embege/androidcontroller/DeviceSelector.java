package com.embege.androidcontroller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;

public class DeviceSelector extends JPanel {

	JComboBox list;
	
	String currentDevice = null;
	
	public DeviceSelector() {
		list = new JComboBox();
		add(list);
		
		list.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				
				if (arg0.getStateChange() != ItemEvent.SELECTED) return;
				
				if (currentDevice != arg0.getItem())
				{
					currentDevice = (String) arg0.getItem();
					Log.log("Selecting device: "+currentDevice);
				
					getFormat();
				}
			}
		});

		JButton refresh = new JButton("Refresh");
		add(refresh);
		refresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refresh();
				
			}
		});
		
		refresh();		
	}
	
	public void getFormat() {
		try {
			ProcessBuilder pb = new ProcessBuilder(SDKPath.PATH, "-s", currentDevice, "shell", "ioctl", "-rl", "28", "/dev/graphics/fb0", "17920");
			pb.redirectOutput();
			
			Process p = pb.start();
			InputStream is = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
	
			String buffer = "";
			while ((line = br.readLine()) != null) {
				Log.log(line);
				buffer += line;
			}
			
			String [] parts = buffer.split("return buf: ");
			String [] hex = parts[1].split(" ");
			
			String hexa = hex[3]+hex[2]+hex[1]+hex[0];			
			int width = Integer.parseInt(hexa.toUpperCase(), 16);	
			
			String hexb = hex[7]+hex[6]+hex[5]+hex[4];			
			int height = Integer.parseInt(hexb.toUpperCase(), 16);		
			
			String hexc = hex[27]+hex[26]+hex[25]+hex[24];			
			int bpp = Integer.parseInt(hexc.toUpperCase(), 16);			
			Log.log("width: "+width + " height: "+height +" bpp: "+bpp);
			
			DrawPanel.start(currentDevice, width, height, bpp);
	
			int result = p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void refresh() {
		list.removeAllItems();
		
		try {
			ProcessBuilder pb = new ProcessBuilder(SDKPath.PATH, "devices");
			pb.redirectOutput();
			
			Process p = pb.start();
			InputStream is = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;

			while ((line = br.readLine()) != null) {
				Log.log(line);
				if (line.contains("List")) continue;
				if (line.length() < 2) continue;
				String s = line.trim().split("\t")[0].trim();
				list.addItem(s);
				
			}

			int result = p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
