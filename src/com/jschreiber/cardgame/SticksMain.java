/**
 * Justin Schreiber
 * CSC 161-101
 * Nov 4, 2019
 *
 * This program lets you play sticks against an bot.
 * My javafx didn't work so I used swing for the GUI
 */


package com.jschreiber.cardgame;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTextField;

//Launcher (Most of the game is in the screen class)
public class SticksMain {
	
	static Dimension ScreenSize = new Dimension(800, 600);
	static JTextField TF;
	
	public static void main(String[] args)
	{
		JFrame F = new JFrame();
		Screen ScreenObject = new Screen();
		F.add(ScreenObject);
	    F.setSize(ScreenSize);
		F.setVisible(true);
		F.setTitle("Sticks");
		F.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
