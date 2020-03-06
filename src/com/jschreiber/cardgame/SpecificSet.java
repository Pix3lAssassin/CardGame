package com.jschreiber.cardgame;

import java.util.ArrayList;

//Tests for a specific set of cards
public class SpecificSet extends Rule {

	ArrayList<Card> specificSet;
	
	public SpecificSet(ArrayList<Card> cards) {
		this.specificSet = cards;
		this.size = specificSet.size();
		this.suit = -1;
		this.number = -1;
	}

	@Override
	public boolean checkRule(ArrayList<Card> cards, ArrayList<Card> results) { 
		int counter = 0;
		for (Card card: cards) {
			for (Card specificCard : specificSet) {
				if (card.equals(specificCard)) {
					int usedCounter = 0;
					for (Card usedCard : results) {
						if (!card.equals(usedCard)) {
							usedCounter++;
						}
					}
					if (usedCounter >= results.size()) {
						results.add(card);
						counter++;
					} 
				}
			}
		}
		return counter >= size;
	}

	@Override
	public String toString() {
		String allCards = "";
		for (int i = 0; i < specificSet.size(); i++) {
			allCards = allCards + specificSet.get(i).toString();
			if (i < specificSet.size()-1) {
				allCards = allCards + ", ";
			}
		}
		return allCards;
	}

	@Override
	public double[][] getCardsNeeded(ArrayList<Card> cards) {
		double[][] cardsNeeded = new double[4][13];
		
		if (!checkRule(cards, new ArrayList<Card>())) {
			for (Card setCard : specificSet) {
				boolean inDeck = false;
				for (Card card : cards) {
					if (card.equals(setCard)) {
						inDeck = true;
					}
				}
				if (!inDeck) {
					cardsNeeded[setCard.getSuit()][setCard.getNumber()] = 1;
				}
			}
		}
		return cardsNeeded;
	}

	@Override
	public double[] deckWeight(ArrayList<Card> cards) {
		double[] weights = new double[cards.size()];
		for (int i = 0; i < weights.length; i++) {
			for (Card card : specificSet) {
				if (cards.get(i).equals(card)) {
					weights[i] += 1;
				}
			}
		}
		return normalizeWeights(weights);
	}

}
