package com.jschreiber.cardgame;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class StickTester {

	public static void main(String[] args) {
		Stick[] stick = new Stick[4];
		stick[0] = new Stick(new Run(4, Card.Any), new Set(4));
		stick[1] = new Stick(new Set(7));
		stick[2] = new Stick(new Run(4, Card.Diamonds));
		stick[3] = new Stick(new Set(3, -1, 3));
		
		ArrayList<Card> cards = new ArrayList<Card>();
		BufferedImage image = new BufferedImage(10, 10, 10);
		cards.add(new Card(Card.Diamonds, 1, image, image));
		cards.add(new Card(Card.Hearts, 1, image, image));
		cards.add(new Card(Card.Diamonds, 3, image, image));
		cards.add(new Card(Card.Clubs, 4, image, image));
		cards.add(new Card(Card.Diamonds, 5, image, image));
		cards.add(new Card(Card.Diamonds, 6, image, image));
		cards.add(new Card(Card.Spades, 1, image, image));
		cards.add(new Card(Card.Diamonds, 1, image, image));
		cards.add(new Card(Card.Diamonds, 2, image, image));
		
		for (int i = 0; i < stick.length; i++) {
			String bool = stick[i].checkStick(cards) ? "Valid" : "Incorrect";
			System.out.printf("Stick %d: %s\n", i+1, bool);
		}
		stick[3].checkStick(cards);
	}

}
