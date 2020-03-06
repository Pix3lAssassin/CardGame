package com.jschreiber.cardgame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Card implements Comparable<Card> {

	private int suit, number;
	//Where to draw the card on the screen
	private double drawX, drawY, size;
	//Images for the front and back of a card
	private BufferedImage cardImage, cardBack;
	//Booleans for if it is being selected and whether it is face up or face down
	private boolean isActivated, isFaceUp;
	//Useful definitions
	public static final int Any = -1, Spades = 0, Clubs = 1, Hearts = 2, Diamonds = 3;
	
	public Card(int suit, int number, BufferedImage cardImage, BufferedImage cardBack) {
		this.suit = suit;
		this.number = number;
		this.cardImage = cardImage;
		this.cardBack = cardBack;
		this.isActivated = false;
		this.isFaceUp = false;
	}
	
	//Draw the card to the screen at x, y at a specific size 0.5 being half size
	public void drawCard(double x, double y, double size, Graphics g) {
		drawX = x;
		drawY = y;
		this.size = size;
		g.setColor(isActivated ? new Color(255, 0, 0) : new Color(64, 64, 64));
		g.fillRect((int)drawX, (int)drawY, (int)(cardImage.getWidth()*size+2), (int)(cardImage.getHeight()*size+2));
		if (isFaceUp) {
			g.drawImage(cardImage, (int)drawX+1, (int)drawY+1, (int)(cardImage.getWidth()*size), (int)(cardImage.getHeight()*size), null);
		} else {
			g.drawImage(cardBack, (int)drawX+1, (int)drawY+1, (int)(cardImage.getWidth()*size), (int)(cardImage.getHeight()*size), null);
		}
	}
	
	//Check if a point is inside the card on screen (Useful for mouse position checks)
	public boolean checkInside(int x, int y) {
		if (x >= drawX && x < drawX + cardImage.getWidth()*size &&
				y >= drawY && y < drawY + cardImage.getHeight()*size) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}

	public int getSuit() {
		return suit;
	}

	public int getNumber() {
		return number;
	}

	public BufferedImage getCardImage() {
		return cardImage;
	}

	public void setDrawX(double drawX) {
		this.drawX = drawX;
	}

	public void setDrawY(double drawY) {
		this.drawY = drawY;
	}

	public boolean isFaceUp() {
		return isFaceUp;
	}

	public void setFaceUp(boolean isFaceUp) {
		this.isFaceUp = isFaceUp;
	}

	//Cards are the same if their numbers and suits are the same
	public boolean equals(Card card) {
		return card.getNumber() == this.number && card.getSuit() == this.suit;
	}

	@Override
	public int compareTo(Card card) {
		return number < card.getNumber() ? -1 : number > card.getNumber() ? -1 : 0;
	}
	
	//Returns a string that describes what card it is
	public String toString() {
		String strSuit = suit == Diamonds ? " Diamonds" : 
			suit == Spades ? " Spades" :
			suit == Clubs ? " Clubs" :
			suit == Hearts ? " Hearts" : " Any Suit";
		return number + " of " + strSuit;
	}
}
