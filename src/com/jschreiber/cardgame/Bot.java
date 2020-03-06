package com.jschreiber.cardgame;

import java.util.ArrayList;

public class Bot extends Player {

	//Assign a value to each of the cards in the 
	//bots hand based on necessity to complete the stick
	double[] cardWeights;
	
	public Bot() {
		super();
	}
	
	public void addCardToHand(Card card) {
		hand.add(card);
	}
	
	public Card discard(int index) {
		return hand.remove(index);
	}
	
	public void updateCardWeights() {
		cardWeights = new double[hand.size()];
		
		ArrayList<Integer> adjustedWeights = new ArrayList<Integer>();
		ArrayList<Card> usedCards = new ArrayList<Card>();
		
		Rule[] rules = stick.getRules();
		for (int i = 0; i < rules.length; i++) {
			
			rules[i].checkRule(hand, usedCards);
			if (usedCards.size() > 0) {
				
				for (Card card : usedCards) {
					for (int x = 0; x < hand.size(); x++) {
						if (hand.get(x).equals(card) && !adjustedWeights.contains(x)) {
							adjustedWeights.add(x);
							cardWeights[x] = 100;
						}
					}
				}
			} else {
				double[] ruleWeights = rules[i].deckWeight(hand);
				for (int x = 0; x < hand.size(); x++) {
					if (!adjustedWeights.contains(x)) {
						cardWeights[x] += ruleWeights[x];
					}
				}
			}
		}
	}
	
	public Card selectCardToDiscard() {
		updateCardWeights();
		int indexToRemove = 0;
		for (int i = 0; i < cardWeights.length; i++) {
//			System.out.printf("%.2f, ", cardWeights[i]);
			if (cardWeights[i] < cardWeights[indexToRemove]) {
				indexToRemove = i;
			}
		}
		System.out.printf("\n");
		return discard(indexToRemove);
	}
	
	public int selectCard(Card visibleCard) {
		int pile = 0;
		ArrayList<Card> cards = new ArrayList<Card>();
		for (Card card : hand) {
			cards.add(card);
		}
		cards.add(visibleCard);
		if (checkStick(cards)) {
			pile = 0;
		} else {
			Rule[] rules = stick.getRules();
			double[][] cardsNeeded = new double[4][13];
			for (int i = 0; i < rules.length; i++) {
				double[][] cardWeights = rules[i].getCardsNeeded(hand);
				for (int x = 0; x < 4; x++) {
					for (int y = 0; y < 13; y++) {
						cardsNeeded[x][y] += cardWeights[x][y];
					}	
				}
			}
//			System.out.printf("Chance: %.1f%%\n", cardsNeeded[visibleCard.getSuit()][visibleCard.getNumber()]*100);
			if (cardsNeeded[visibleCard.getSuit()][visibleCard.getNumber()] > Math.random()) {
				pile = 0;
			} else {
				pile = 1;
			}
		}
		return pile;
	}
}
