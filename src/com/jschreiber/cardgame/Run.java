package com.jschreiber.cardgame;

import java.util.ArrayList;

//A rule defining a run of cards with the same suit(if defined) of a specific size 
public class Run extends Rule {

	public Run(int size, int suit) {
		this.size = size;
		this.suit = suit;
		this.number = -1;
	}

	@Override
	public boolean checkRule(ArrayList<Card> cards, ArrayList<Card> results) {
		// Define and init suit cards to all cards with the suit specified by the rule
		ArrayList<Card> suitCards = new ArrayList<Card>();
		for (Card card : cards) {
			if (card.getSuit() == suit || suit < 0) {
				suitCards.add(card);
			}
		}
		// Check if suit cards is greater than the run size else return null
		if (suitCards.size() > size) {
			// Get the lowest number from suit cards
			int lowest = getLowest(suitCards);
			// Makes sure to exit the loop on the first passing run
			boolean completed = false;
			// Loop through the numbers and check if there are consecutive numbers of a
			// certain size
			for (int i = lowest; i < 14 - size; i++) {
				int counter = 0;
				for (int x = 0; x < size; x++) {
					for (Card card : suitCards) {
						if (card.getNumber() == i + x) {
							results.add(card);
							counter++;
							break;
						}
					}
				}
				if (counter >= size) {
					completed = true;
					break;
				}
			}
			if (!completed)
				results.clear();
			// Return used cards if a run was found
			return completed;
		} else {
			return false;
		}
	}

	// Returns the lowest number from a list of cards
	public int getLowest(ArrayList<Card> cards) {
		int lowest = 13;
		for (Card card : cards) {
			if (card.getNumber() < lowest) {
				lowest = card.getNumber();
			}
		}
		return lowest;
	}

	@Override
	public String toString() {
		String strSetNum = "Run of ";
		String strSuit = suit == Card.Diamonds ? " Diamonds"
				: suit == Card.Spades ? " Spades"
				: suit == Card.Clubs ? " Clubs" 
				: suit == Card.Hearts ? " Hearts" : " of Any Suit";
		return strSetNum + size + strSuit;
	}

	@Override
	public double[][] getCardsNeeded(ArrayList<Card> cards) {
		double[][] cardsNeeded = new double[4][13];

		if (!checkRule(cards, new ArrayList<Card>())) {
			ArrayList<Card> suitCards = new ArrayList<Card>();
			for (Card card : cards) {
				if (card.getSuit() == suit || suit < 0) {
					suitCards.add(card);
				}
			}
			if (suitCards.size() > size) {
				// Get the lowest number from suit cards
				int lowest = getLowest(suitCards);
				// Loop through the numbers and check if there are consecutive numbers of a
				// certain size
				for (int i = lowest; i < 14 - size; i++) {
					double[] runWeight = new double[size];
					for (int x = 0; x < runWeight.length; x++) {
						runWeight[x] = 1;
					}
					int counter = 0;
					for (int x = 0; x < size; x++) {
						for (Card card : suitCards) {
							if (card.getNumber() == i + x) {
								runWeight[x] = 0;
								counter++;
								break;
							}
						}
					}
					double modifier = counter / (size - 1);
					for (int x = 0; x < runWeight.length; x++) {
						runWeight[x] *= modifier;
					}
					for (int x = 0; x < size; x++) {
						if (suit < 0) {
							for (int y = 0; y < 4; y++) {
								if (runWeight[x] > cardsNeeded[y][i + x]) {
									cardsNeeded[y][i + x] = runWeight[x];
								}
							}
						} else {
							if (runWeight[x] > cardsNeeded[suit][i + x]) {
								cardsNeeded[suit][i + x] = runWeight[x];
							}
						}
					}

				}
			} else {
				for (int i = 0; i < 13; i++) {
					cardsNeeded[suit][i] = 0.5;
				}
			}
		}
		return cardsNeeded;
	}

	@Override
	public double[] deckWeight(ArrayList<Card> cards) {
		double[] weights = new double[cards.size()];
		ArrayList<Integer> usedNumbers = new ArrayList<Integer>();
		
		ArrayList<Card> suitCards = new ArrayList<Card>();
		for (Card card : cards) {
			if (card.getSuit() == suit) {
				suitCards.add(card);
			}
		}
		
		for (int i = 0; i < weights.length; i++) {
			if (suit >= 0) {
				if (suitCards.contains(cards.get(i)) && !usedNumbers.contains(cards.get(i).getNumber())) {
					int value = cards.get(i).getNumber();
					usedNumbers.add(value);
					for (int x = Math.max(0, value-(size-1)); x < Math.min(12, value+(size-1)); x++) {
						for (Card card : suitCards) {
							if (card.getNumber() == x) {
								weights[i]++;
								break;
							}
						}
					}
				}
			} else {
				if (!usedNumbers.contains(cards.get(i).getNumber())) {
					int value = cards.get(i).getNumber();
					usedNumbers.add(value);
					for (int x = Math.max(0, value-(size-1)); x < Math.min(12, value+(size-1)); x++) {
						for (Card card : cards) {
							if (card.getNumber() == x) {
								weights[i]++;
								break;
							}
						}
					}
				}
			}
		}
		
		return normalizeWeights(weights);
	}
}
