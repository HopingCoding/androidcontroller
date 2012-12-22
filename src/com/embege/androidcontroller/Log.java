package com.embege.androidcontroller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

public class Log extends JPanel {

	static Log instance;
	public static Log get()
	{
		if (instance == null) instance = new Log();
		return instance;
	}
	
	JTextPane textarea;
	private Log() {
	
		setPreferredSize(new Dimension(450, 110));
		setLayout(new BorderLayout());
//		setMinimumSize(new Dimension(100, 100));
//		setMaximumSize(new Dimension(10000, 200));
		
		textarea = new JTextPane();
		textarea.setEditable(false);
		
		textarea.setFont(new Font("Courier", Font.PLAIN, 10));
		
		JScrollPane pane = new JScrollPane(textarea);
		pane.setMinimumSize(new Dimension(100, 100));
		pane.setMaximumSize(new Dimension(10000, 200));
		pane.getViewport().setMaximumSize(new Dimension(10000, 200));
		
		add(pane);
		
	}
	
	
	public static void log(String msg) {
		msg = "[LOG] "+msg;
		System.out.println(msg);
		
		try {
			get().textarea.getDocument().insertString(get().textarea.getDocument().getLength(), "\n"+msg, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		get().textarea.setCaretPosition(get().textarea.getDocument().getLength() );
		
	}
}
