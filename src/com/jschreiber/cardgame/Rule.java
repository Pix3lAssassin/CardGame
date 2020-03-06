package com.jschreiber.cardgame;

import java.util.ArrayList;

//Used as a basis for defining rules
public abstract class Rule {

	protected int size, suit, number;
	
	//Checks if the list of cards passes the rule.
	//Returns the cards it used to pass the rule and null if it doesn't pass the rule
	public abstract boolean checkRule(ArrayList<Card> cards, ArrayList<Card> results);
	
	//Returns weights for cards that are important to completing the rule
	public abstract double[] deckWeight(ArrayList<Card> cards);
	
	//Returns the cards needed to complete the rule as a 
	//2D array of [Suit] and [Number] with a value for importance
	public abstract double[][] getCardsNeeded(ArrayList<Card> cards);
	
	//Returns a string with information about the rule
	public abstract String toString();
	
	public int size() {
		return size;
	}
	
	protected double getHighest(double[] weights) {
		double highest = 0.0001;
		for (int i = 0; i < weights.length; i++) {
			if (weights[i] > highest) {
				highest = weights[i];
			}
		}
		return highest;
	}
	
	protected double[] normalizeWeights(double[] weights) {
		double highest = getHighest(weights);
		for (int i = 0; i < weights.length; i++) {
			weights[i] /= highest;
		}
		return weights;
	}
}
