package com.jschreiber.cardgame;

import java.awt.Graphics;
import java.util.ArrayList;

public class Player {

	protected int score;
	protected ArrayList<Card> hand;
	protected Stick stick;
	
	public Player() {
		this.score = 0;
		this.hand = new ArrayList<Card>();
	}
	
	public void addToScore(int addition) {
		score += addition;
	}
	
	public int getScore() {
		return score;
	}
	
	public void getNewStick(ArrayList<Stick> sticks) {
		stick = sticks.remove((int)(Math.random()*sticks.size()));
	}
	
	public Stick getStick() {
		return stick;
	}
	
	public ArrayList<Card> getHand() {
		return hand;
	}
	
	public int getHandSize() {
		return hand.size();
	}
	
	public void clearHand() {
		hand.clear();
	}
	
	public void fillHand(ArrayList<Card> cards, int handSize) {
		for (int i = 0; i < handSize; i++) {
			hand.add(cards.remove((int)(Math.random()*cards.size())));
		}
	}
	
	public void takeCard(ArrayList<Card> cards) {
		int random = (int)(Math.random()*cards.size());
		hand.add(cards.remove(random));
	}
	
	public void takeCardAt(ArrayList<Card> cards, int index) {
		hand.add(cards.remove(index));
	}
	
	public void drawHand(double width, double y, double size, Graphics g) {
		for (int i = 0; i < hand.size(); i++) {
			hand.get(i).drawCard(i*((width-50)/hand.size())+25, y, size, g);
		}
	}
	
	public void drawStick(double x, double y, Graphics g) {
		stick.drawStick(x, y, g);
	}
	
	public boolean checkStick() {
		return stick.checkStick(hand);
	}
	
	public boolean checkStick(ArrayList<Card> cards) {
		return stick.checkStick(cards);
	}
	
	public void setStickFaceUp(boolean isFaceUp) {
		stick.setFaceUp(isFaceUp);
	}
	
	public void setCardFaceUp(boolean isFaceUp, int index) {
		hand.get(index).setFaceUp(isFaceUp);
	}
}
