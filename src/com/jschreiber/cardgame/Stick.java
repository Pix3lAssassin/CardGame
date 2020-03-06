package com.jschreiber.cardgame;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Stick {

	//List of rules to check
	private Rule[] rules;
	//The string that defines all the rules on the stick
	private String strRule;
	
	//For rendering
	private boolean isFaceUp;
	private double drawX, drawY;

	public Stick(Rule... rules) {
		this.rules = rules;
		strRule = "";
		for (int i = 0; i < rules.length; i++) {
			strRule = strRule + rules[i].toString();
			if (i < rules.length - 1) {
				strRule = strRule + ", ";
			}
		}
		isFaceUp = false;
	}

	//Check if a stick the cards passed in meet the requirements of the rules on the stick
	public boolean checkStick(ArrayList<Card> cards) {
		return forwardCheck(cards) || reverseCheck(cards);
	}

	private boolean forwardCheck(ArrayList<Card> cards) {
		ArrayList<Card> unusedCards = new ArrayList<Card>();
		for (Card card : cards) {
			unusedCards.add(card);
		}
		int counter = 0;
		for (int i = 0; i < rules.length; i++) {
			ArrayList<Card> usedCards = new ArrayList<Card>();
			if (rules[i].checkRule(unusedCards, usedCards)) {
				for (Card card : usedCards) {
					for (Card checkCard : unusedCards) {
						if (card.equals(checkCard)) {
							unusedCards.remove(checkCard);
							break;
						}
					}
				}
				counter++;
			}
		}
		return counter >= rules.length;
	}
	
	private boolean reverseCheck(ArrayList<Card> cards) {
		ArrayList<Card> unusedCards = new ArrayList<Card>();
		for (Card card : cards) {
			unusedCards.add(card);
		}
		int counter = 0;
		for (int i = rules.length-1; i >= 0; i--) {
			ArrayList<Card> usedCards = new ArrayList<Card>();
			if (rules[i].checkRule(unusedCards, usedCards)) {
				for (Card card : usedCards) {
					for (Card checkCard : unusedCards) {
						if (card.equals(checkCard)) {
							unusedCards.remove(checkCard);
							break;
						}
					}
				}
				counter++;
			}
		}
		return counter >= rules.length;
	}
	
	//Draw the stick at x, y
	public void drawStick(double x, double y, Graphics g) {
		drawX = x;
		drawY = y;
		g.setColor(new Color(205, 170, 100));
		g.fillRect((int) drawX, (int) drawY, 700, 35);
		g.drawRect((int) drawX, (int) drawY, 700, 35);
		if (isFaceUp) {
			g.setColor(new Color(0, 0, 0));
			drawCenteredString(g, strRule, new Rectangle((int) drawX, (int) drawY, 700, 35),
					new Font("TimesRoman", Font.BOLD, 30));
		}
	}

	//Render a string in the center of a box
	public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
		// Get the FontMetrics
		FontMetrics metrics = g.getFontMetrics(font);
		// Determine the X coordinate for the text
		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
		// Determine the Y coordinate for the text (note we add the ascent, as in java
		// 2d 0 is top of the screen)
		int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
		// Set the font
		g.setFont(font);
		// Draw the String
		g.drawString(text, x, y);
	}

	public boolean isFaceUp() {
		return isFaceUp;
	}

	public void setFaceUp(boolean isFaceUp) {
		this.isFaceUp = isFaceUp;
	}

	public Rule[] getRules() {
		return rules;
	}
}
