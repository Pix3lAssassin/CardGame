package com.jschreiber.cardgame;

import java.util.ArrayList;

//A rule defining a set of cards with the same number of a 
//specific size (Can ask for more than one set)
public class SuitSet extends Rule {

	public SuitSet(int size, int suit) {
		this.size = size;
		this.suit = Math.max(suit, 0);
		this.number = -1;
	}

	@Override
	public boolean checkRule(ArrayList<Card> cards, ArrayList<Card> results) {
		//Define cards that contain the suit asked for by the rule
		ArrayList<Card> numberCards = new ArrayList<Card>();
		for (Card card : cards) {
			if (card.getSuit() == suit) {
				numberCards.add(card);
			}
		}
		//If cards of the rule suit is greater than the size
		//Return the cards used to pass the rule
		if (numberCards.size() >= size) {
			for (int i = 0; i < size; i++) {
				results.add(numberCards.get(i));
			}
			return true;
		} else { //Else return the cards that could satisfy the rule
			for (int i = 0; i < numberCards.size(); i++) {
				results.add(numberCards.get(i));
			}
			return false;
		}
	}

	@Override
	public String toString() {
		String strSetNum = "Set of ";
		String strSuit = suit == Card.Diamonds ? " Diamonds" : 
			suit == Card.Spades ? " Spades" :
			suit == Card.Clubs ? " Clubs" :
			suit == Card.Hearts ? " Hearts" : " of Any Suit";
		return strSetNum + size + strSuit;
	}

	@Override
	public double[][] getCardsNeeded(ArrayList<Card> cards) {
		double[][] cardsNeeded = new double[4][13];
		
		if (!checkRule(cards, new ArrayList<Card>())) {
			if (number >= 0) {
				for (int i = 0; i < 13; i++) {
					cardsNeeded[suit][i] = 1;
				}
			} else {
				for (Card card : cards) {
					for (int i = 0; i < 13; i++) {
						cardsNeeded[card.getSuit()][i] += 1/size-1;
					}
				}
			}
		}
		return cardsNeeded;
	}
	
	@Override
	public double[] deckWeight(ArrayList<Card> cards) {
		double[] weights = new double[cards.size()];
		for (int i = 0; i < weights.length; i++) {
			if (cards.get(i).getSuit() == suit) {
				weights[i] += 1;
			}
		}
		return normalizeWeights(weights);
	}

}
