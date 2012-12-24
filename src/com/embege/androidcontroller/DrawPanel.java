package com.embege.androidcontroller;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JPanel;


public class DrawPanel extends JPanel {

	
	static Thread readThread;
	static Thread convertThread;
	static int lastRead = 0;
	static int lastConvert = 0;
	static BufferedImage bi;
	static boolean stop = false;
	static String currentDevice;
	static int width;
	static int height;
	static int bpp;
	static DrawPanel self;
	

	static void getFrameBuffer() {
		long t0 = System.nanoTime();
		try {
			ProcessBuilder pb = new ProcessBuilder(SDKPath.PATH2ADB, "-s", currentDevice, "pull", "/dev/graphics/fb0", SDKPath.TEMPFILE);
			pb.redirectOutput();
			
			Process p = pb.start();
			/*
			InputStream is = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
	
			while ((line = br.readLine()) != null) {
				Log.log(line);				
			}
			*/
			p.waitFor();
			
			
			
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
		//Log.log("frame of " + currentDevice);
		long t1 = System.nanoTime();
		float f = (float) ((t1-t0)/1000000000.0);
		Stats.jlRead.setText("Reading: "+f);
	}
	
	public static void fb2bi() {
		long t0 = System.nanoTime();
		InputStream is = null;
		
		try {
			is = new FileInputStream(SDKPath.TEMPFILE);
			
			switch (bpp) {

			case 16:
			{
				byte [] bytes = new byte[2];
				int [] rgbArray = new int[width*height];
				int index = 0;
				while (index < rgbArray.length) {					
					is.read(bytes);		
					
					int x = ((bytes[1] & 0xff) << 8) | (bytes[0] & 0xff);
					
					rgbArray[index++] =    (0xff << 24) | (255*(x & 0x001F))/32 | (255*((x & 0x07E0) >> 5)) /64 << 8 | (255*((x & 0xF800) >> 11)/32 << 16 );
				}
				
				
				bi.setRGB(0, 0, width, height, rgbArray, 0, width);
			}
				break;			
			case 32:
			{
				//bi = ImageIO.read(is);
				
				byte [] bytes = new byte[4];
				int [] rgbArray = new int[width*height];
				int index = 0;
				while (index < rgbArray.length) {					
					is.read(bytes);					
					rgbArray[index++] = byteToInt(bytes);					
				}
				
				bi.setRGB(0, 0, width, height, rgbArray, 0, width);
			}
				break;
			default:
				Log.log("invalid bpp: "+bpp);
				is.close();
				return;
			}
			

			
			is.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			
		}
		long t1 = System.nanoTime();
		float f = (float) ((t1-t0)/1000000000.0);
		Stats.jlConv.setText("Converting: "+f);
	}
	
	private static final int byteToInt(byte[] b)
	{
		return ((b[3]) << 24) | ((b[2] & 0xff) << 16) | ((b[1] & 0xff) << 8) | (b[0] & 0xff);
	}
	
	public static void start(String currentDevice, int width, int height, int bpp) 
	{
		DrawPanel.currentDevice = currentDevice;
		DrawPanel.width = width;
		DrawPanel.height = height;
		DrawPanel.bpp = bpp;
		
		bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		
		if (readThread != null ) {
			//stop = true;
			return;
		}
		
		readThread = new Thread("ReadThread")
		{
			@Override
			public void run() 
			{
			
				while (!stop)
				{
					getFrameBuffer();
					

					fb2bi();
					
					self.repaint();
					
					lastRead++;
					
				}
				Log.log("paintthread stopped");
			}
		};
		readThread.start();
		stop = false;
		
		convertThread = new Thread("convertThread")
		{
			public void run() {
				while (true)
				{
					if (lastConvert < lastRead)
					{
						fb2bi();
						
						self.repaint();
						
						lastConvert++;
					}
					else
					{
						Thread.yield();
					}
				}
			};
		
		};
//		convertThread.start();
		
	}
	
	public static void down(int x, int y)
	{
		System.out.println("Down "+x+" "+y);
		try {
			
			new ProcessBuilder(SDKPath.PATH2ADB, "-s", currentDevice, "shell", "sendevent", "/dev/input/event2", "3", "57", "1234" ).start().waitFor();	
			
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void up(int x, int y)
	{
		pos(x,y);
		
		
		System.out.println("Up "+x+" "+y);
		try {
			
			new ProcessBuilder(SDKPath.PATH2ADB, "-s", currentDevice, "shell", "sendevent", "/dev/input/event2", "3", "58", "0" ).start().waitFor();	
			
			new ProcessBuilder(SDKPath.PATH2ADB, "-s", currentDevice, "shell", "sendevent", "/dev/input/event2", "0", "0", "0" ).start().waitFor();	
			
			new ProcessBuilder(SDKPath.PATH2ADB, "-s", currentDevice, "shell", "sendevent", "/dev/input/event2", "3", "57", "-1" ).start().waitFor();				
			
			new ProcessBuilder(SDKPath.PATH2ADB, "-s", currentDevice, "shell", "sendevent", "/dev/input/event2", "0", "0", "0" ).start().waitFor();	
			
			
			
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void pos(int x, int y)
	{
		System.out.println("Move "+x+" "+y);
		
		float nx = (float)x/ (float)self.getWidth();
		int ix = (int)(nx * width);
		
		float ny = (float)y/ (float)self.getHeight();
		int iy = (int)(ny * height);
		
		System.out.println("     "+ix+" "+iy);
		
		try {
			
			new ProcessBuilder(SDKPath.PATH2ADB, "-s", currentDevice, "shell", "sendevent", "/dev/input/event2", "3", "58", "30" ).start().waitFor();	
			new ProcessBuilder(SDKPath.PATH2ADB, "-s", currentDevice, "shell", "sendevent", "/dev/input/event2", "3", "48", "8" ).start().waitFor();

			new ProcessBuilder(SDKPath.PATH2ADB, "-s", currentDevice, "shell", "sendevent", "/dev/input/event2", "3", "53", ix+"" ).start().waitFor();
			
			new ProcessBuilder(SDKPath.PATH2ADB, "-s", currentDevice, "shell", "sendevent", "/dev/input/event2", "3", "54", iy+"" ).start().waitFor();
			
			new ProcessBuilder(SDKPath.PATH2ADB, "-s", currentDevice, "shell", "sendevent", "/dev/input/event2", "0", "0", "0" ).start().waitFor();
			
			
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
	
	public DrawPanel() {
		self = this;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				down(e.getX(), e.getY());
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				up(e.getX(), e.getY());
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				pos(e.getX(), e.getY());
			}
		});
	}
	
	@Override
	public void paint(Graphics g) {
	
		//System.out.println("repainting");
		super.paint(g);
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		if (bi != null)
		{
			/*
			if (width/height < getWidth()/getHeight())
			{				
				g.drawImage(bi, 0, 0, getHeight() * width/height, getHeight(), null);
			} 
			else
			{
				g.drawImage(bi, 0, 0, getWidth(), getWidth() * height/width, null);
			}
			*/
			g.drawImage(bi, 0, 0, getWidth(), getHeight(), null);
		}
		else
		{
			g.setColor(Color.red);
			g.drawString("image null", 20, 20);
		}
	}
	
}
