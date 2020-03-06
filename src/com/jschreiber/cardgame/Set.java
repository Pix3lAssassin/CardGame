package com.jschreiber.cardgame;

import java.util.ArrayList;

//A rule defining a set of cards with the same number of a 
//specific size (Can ask for more than one set)
public class Set extends Rule {

	// Number of sets if no number specified
	private int numOfSets;

	public Set(int size, int number, int numOfSets) {
		this.size = size;
		this.suit = -1;
		this.number = number;
		if (number < 0) {
			this.numOfSets = numOfSets;
		} else {
			this.numOfSets = 1;
		}
	}

	public Set(int size, int number) {
		this.size = size;
		this.suit = -1;
		this.number = number;
		this.numOfSets = 1;
	}

	public Set(int size) {
		this.size = size;
		this.suit = -1;
		this.number = -1;
		this.numOfSets = 1;
	}

	@Override
	public boolean checkRule(ArrayList<Card> cards, ArrayList<Card> results) {
		// Define cards that contain the number asked for by the rule
		ArrayList<Card> numberCards = new ArrayList<Card>();
		for (Card card : cards) {
			if (card.getNumber() == number || number < 0) {
				numberCards.add(card);
			}
		}
		// If a specific number is defined and numberCards size is greater that the size
		// of the set
		// Add cards used to the results and return true
		if (number > -1) {
			if (numberCards.size() >= size) {
				for (int i = 0; i < size; i++) {
					results.add(numberCards.get(i));
				}
				return true;
			} else {
				for (int i = 0; i < numberCards.size(); i++) {
					results.add(numberCards.get(i));
				}
				return false;
			}
		} else if (numberCards.size() > 0 && number < 0) {
			// Define and init an array that stores how many there are of each card from
			// number cards
			int[] numOfCards = new int[13];
			for (Card card : numberCards) {
				numOfCards[card.getNumber()]++;
			}
			int counter = 0;
			for (int i = 0; i < numOfCards.length; i++) {
				// Check if number of cards with a specific number is greater than the set size
				if (numOfCards[i] >= size) {
					// Get number of sets at that specific number
					int numSameCardSets = (int) (numOfCards[i] / size);
					int setCounter = 0;
					// Add all the cards used to the used cards list
					for (Card card : numberCards) {
						if (card.getNumber() == i && setCounter < size * numSameCardSets) {
							results.add(card);
							setCounter++;
						}
					}
					counter += numSameCardSets;
				}
			}
			if (counter < numOfSets) {
				results.clear();
			}
			// Checks if the there are enough sets as defined at rule initialization
			// Returns used cards if there are enough sets and null if not
			return counter >= numOfSets;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		String sizeS = size > 1 ? "s" : "";
		String strSetNum = numOfSets + " Set" + (numOfSets > 1 ? "s" : "") + " of ";
		String strSuit = number == -1 ? " of Any Card"
				: number == 0 ? " Ace" + sizeS
						: number == 10 ? " Jack" + sizeS
								: number == 11 ? " Queen" + sizeS
										: number == 12 ? " King" + sizeS : " " + (number + 1) + sizeS;
		return strSetNum + size + strSuit;
	}

	@Override
	public double[][] getCardsNeeded(ArrayList<Card> cards) {
		double[][] cardsNeeded = new double[4][13];
		
		if (!checkRule(cards, new ArrayList<Card>())) {
			if (number >= 0) {
				for (int i = 0; i < 4; i++) {
					cardsNeeded[i][number] = 1;
				}
			} else {
				for (Card card : cards) {
					for (int i = 0; i < 4; i++) {
						cardsNeeded[i][card.getNumber()] += 1/size-1;
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
			if (number >= 0) {
				if (cards.get(i).getNumber() == number) {
					weights[i] += 1;
				}
			} else {
				if (weights[i] < size) {
					for (int x = 0; x < weights.length; x++) {
						if (cards.get(i).getNumber() == cards.get(x).getNumber()) {
							weights[x]++;
						}
					}
				} else {
					weights[i] = 0;
				}
			}
		}
		return normalizeWeights(weights);
	}

}
