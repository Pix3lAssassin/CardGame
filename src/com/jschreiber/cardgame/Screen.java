package com.jschreiber.cardgame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Screen extends JPanel implements KeyListener, MouseListener, ActionListener {

	//Define sticks, images, and card piles
	ArrayList<Stick> sticks = new ArrayList<Stick>();
	BufferedImage[][] cardImages;
	BufferedImage cardBack;
	int numOfDecks = 2, handSize = 10, gameWon = -1, winningScore = 3;
	ArrayList<Card> playableCards = new ArrayList<Card>();
	ArrayList<Card> discardPile = new ArrayList<Card>();
	Card movingCard = null;
	
	int selectedDeck = 0;
	double[][] deckPos;
	
	public static final double SCREEN_WIDTH = SticksMain.ScreenSize.getWidth(), SCREEN_HEIGHT = SticksMain.ScreenSize.getHeight();
	
	//Define players
	Player[] players;
	int currentTurn = 0, phases = 8;
	boolean displayHand;
	
	JButton discardButton;

	boolean stickDebug = false;
	
	//Define back pattern
	int designSize = 5;
	double[][] design;

	// FPS is a bit primitive, you can set the MaxFPS as high as u want
	double drawFPS = 0, MaxFPS = 30, SleepTime = 1000.0 / MaxFPS, LastRefresh = 0,
			StartTime = System.currentTimeMillis(), LastFPSCheck = 0, Checks = 0;

	double animationStartTime = 0, movementTime = 1000.0;
	
	boolean[] Keys = new boolean[4];
	public Insets inserts;

	long repaintTime = 0;

	public Screen() {
		//Important init
		this.setLayout(null);
		this.addKeyListener(this);
		this.addMouseListener(this);
		setFocusable(true);

		//Init button
		inserts = this.getInsets(); 
		discardButton = new JButton("Discard");
		discardButton.addActionListener(this);
        Dimension size = discardButton.getPreferredSize();
        discardButton.setBounds(SticksMain.ScreenSize.width/2 - size.width/2, 3*SticksMain.ScreenSize.height/4, size.width, size.height);
		add(discardButton);
		
		deckPos = new double[3][2];
		deckPos[0][0] = (5*(SCREEN_WIDTH-50)/10)+25;
		deckPos[0][1] = SCREEN_HEIGHT/3+5;
		deckPos[1][0] = (4*(SCREEN_WIDTH-50)/10)+25;
		deckPos[1][1] = SCREEN_HEIGHT/3+5;
		deckPos[2][0] = SCREEN_WIDTH/2;
		deckPos[2][1] = 50;

		//Init card images
		cardImages = new BufferedImage[4][13];
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 13; y++) {
				BufferedImage img = null;
				try {
				    img = ImageIO.read(new File("cards/" + (y < 1 ? "a" : y >= 12 ? "k" : y >= 11 ? "q" : y >= 10 ? "j" : y >= 9 ? "t" : Integer.toString(y+1)) +
				    		(x < 1 ? "s" : x < 2 ? "c" : x < 3 ? "h" : "d") + ".gif"));
				} catch (IOException e) {
					System.out.println("Couldn't find " + "cards/" + (y < 1 ? "a" : y >= 12 ? "k" : y >= 11 ? "q" : y >= 10 ? "j" : y >= 9 ? "t" : Integer.toString(y+1)) +
				    		(x < 1 ? "s" : x < 2 ? "c" : x < 3 ? "h" : "d") + ".gif");
				}
				cardImages[x][y] = img;
			}
		}
		
		//Generate a design for the back of the card
		fillDesign();
		cardBack = generateBack(new Color(255, 0, 0));
		
		players = new Player[2];
		players[0] = new Player();
		players[1] = new Bot();
		
		//Init Sticks
		initSticks();
		players[0].getNewStick(sticks);
		players[0].setStickFaceUp(true);
		players[1].getNewStick(sticks);
		players[1].setStickFaceUp(false);
		
		initRound();
	}

	//Draw to the screen
	public void paintComponent(Graphics g) {
		
		//Background Color
		g.setColor(new Color(125, 125, 125));
		g.fillRect(0, 0, (int)SCREEN_WIDTH, (int)SCREEN_HEIGHT);

		if (gameWon >= 0) {
			g.setColor(new Color(0, 0, 0));
			drawCenteredString(g, "Player " + (gameWon + 1) + " Wins", 
					new Rectangle(0, 0, (int)(SCREEN_WIDTH), (int)(SCREEN_HEIGHT)), new Font("TimesRoman", Font.BOLD, 50));
			discardButton.setVisible(false);
		} else {
			int currentPhase = currentTurn%phases;
			//Check win conditions
			for (int i = 0; i < 2; i++) {
				if (players[i].getScore() > winningScore-1) {
					gameWon = i;
				}
			}
				
			//Draw hands
			for (int i = 0; i < 2; i++) {
				players[i].drawHand(SCREEN_WIDTH, Math.abs(i-1)*SCREEN_HEIGHT/2+50, 0.9, g);
			}
			players[0].drawStick(50, 13*SCREEN_HEIGHT/16, g);
			players[1].drawStick(50, 5, g);
			
			//Draw the deck, the discard pile, and players sticks
			for (int i = 0; i < Math.min(2, playableCards.size()); i++) {
				playableCards.get(i).drawCard((4*(SCREEN_WIDTH-50)/10)+25, SCREEN_HEIGHT/3+i*5, 0.9, g);
			}
			for (int i = Math.min(2, discardPile.size()); i > 0; i--) {
				discardPile.get(discardPile.size()-i).drawCard((5*(SCREEN_WIDTH-50)/10)+25, SCREEN_HEIGHT/3-(i-2)*5, 0.9, g);
			}
			
			Rectangle drawStringRect = currentPhase < 2 ? 
					new Rectangle((int)(SCREEN_WIDTH/2-100), (int)(SCREEN_HEIGHT/2), 200, 35) :
						new Rectangle((int)(SCREEN_WIDTH/2-100), (int)(SCREEN_HEIGHT/4), 200, 35);
			g.setColor(new Color(32, 32, 32));
			drawCenteredString(g, "Player " + (currentPhase < 2 ? "1" : "2") + "'s Turn", 
					drawStringRect, new Font("TimesRoman", Font.BOLD, 30));
			drawCenteredString(g, "Score: " + players[0].getScore(), 
					new Rectangle((int)(SCREEN_WIDTH/10-100), (int)(SCREEN_HEIGHT/2), 200, 35), new Font("TimesRoman", Font.BOLD, 30));
			drawCenteredString(g, "Score: " + players[1].getScore(), 
					new Rectangle((int)(SCREEN_WIDTH/10-100), (int)(SCREEN_HEIGHT/4), 200, 35), new Font("TimesRoman", Font.BOLD, 30));
			
			if (currentPhase > 1) {
				Bot bot = (Bot)players[1];
				if (currentPhase == 2) {
					selectedDeck = bot.selectCard(discardPile.get(discardPile.size()-1));
					if (selectedDeck == 0) {
						movingCard = discardPile.remove(discardPile.size()-1);
					} else {
						movingCard = playableCards.remove((int)(Math.random()*playableCards.size()));
					}
					currentTurn++;
					animationStartTime = System.currentTimeMillis();
				} else if (currentPhase == 3) {
					Vector movement = new Vector(deckPos[2][0]-deckPos[selectedDeck][0], deckPos[2][1]-deckPos[selectedDeck][1]);
					double currentTime = System.currentTimeMillis();
					double delta = 0;
					if (currentTime-animationStartTime < movementTime) {
						delta = (currentTime-animationStartTime)/1000;
						movingCard.drawCard(deckPos[selectedDeck][0]+(movement.getX()*movement.getMagnitude()*delta), 
								deckPos[selectedDeck][1]+(movement.getY()*movement.getMagnitude()*delta), 0.9, g);
					} else {
						currentTurn++;
					}
				} else if (currentPhase == 4) {
					movingCard.setFaceUp(false);
					bot.addCardToHand(movingCard);
					currentTurn++;
					animationStartTime = System.currentTimeMillis();
				} else if (currentPhase == 5) {
					double currentTime = System.currentTimeMillis();
					if (currentTime-animationStartTime > movementTime) {
						movingCard = bot.selectCardToDiscard();
						movingCard.setFaceUp(true);
						currentTurn++;
						animationStartTime = System.currentTimeMillis();
					} 
				} else if (currentPhase == 6){
					Vector movement = new Vector(deckPos[0][0]-deckPos[2][0], deckPos[0][1]-deckPos[2][1]);
					double currentTime = System.currentTimeMillis();
					double delta = 0;
					if (currentTime-animationStartTime < movementTime) {
						delta = (currentTime-animationStartTime)/1000;
						movingCard.drawCard(deckPos[2][0]+(movement.getX()*movement.getMagnitude()*delta), 
								deckPos[2][1]+(movement.getY()*movement.getMagnitude()*delta), 0.9, g);
					} else {
						currentTurn++;
					}
				} else {
					discardPile.add(movingCard);
					double currentTime = System.currentTimeMillis();
					if (displayHand) {
						if (currentTime-animationStartTime < movementTime*5) {
							players[1].getStick().setFaceUp(true);
							for (int i = 0; i < players[1].getHandSize(); i++) {
								players[1].setCardFaceUp(true, i);								
							}
						} else {
							//Get a new stick if the hand passes and award points
							players[1].addToScore(1);
							players[1].getNewStick(sticks);
							players[1].setStickFaceUp(false);
							initRound();
							currentTurn++;
						}
					} else {
						if (players[1].checkStick()) {
							displayHand = true;
							animationStartTime = System.currentTimeMillis();
						} else {
							currentTurn++;
						}
					}				
				}
			} 
		}
		SleepAndRefresh();
	}
			
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		//Discard button functionality
		if (src == discardButton) {
			//Check if it is the turn for using discard button
			if (currentTurn%phases == 1) {
				//Loop through the hand and remove the activated card if any
				int counter = 0;
				for (int i = 0; i < players[0].getHand().size(); i++) {
					if (players[0].getHand().get(i).isActivated()) {
						discardPile.add(players[0].getHand().remove(i));
						discardPile.get(discardPile.size()-1).setFaceUp(true);
						discardPile.get(discardPile.size()-1).setActivated(false);
						counter++;
					}
				}
				//If we removed a card
				if (counter > 0) {
					//Next turn
					currentTurn++;
					//Debugging
					if (stickDebug) {
						System.out.println("Debugging");
					}
					//Check if the hand completes the stick rules
					if (players[0].checkStick()) {
						//Get a new stick if the hand passes and award points
						players[0].addToScore(1);
						players[0].getNewStick(sticks);
						players[0].setStickFaceUp(true);
						initRound();
					}
				}
			}
		}
	}

	public void mouseClicked(MouseEvent mouse) {
		for (Card card : players[0].getHand()) {
			card.setActivated(card.checkInside(mouse.getX(), mouse.getY()));
		}
		if (currentTurn%phases == 0) {
			if (playableCards.size() > 0) {
				if (playableCards.get(0).checkInside(mouse.getX(), mouse.getY())) {
					players[0].takeCard(playableCards);
					players[0].setCardFaceUp(true, players[0].getHandSize()-1);
					currentTurn++;
				}
			}
			if (discardPile.size() > 0) {
				if (discardPile.get(discardPile.size()-1).checkInside(mouse.getX(), mouse.getY())) {
					players[0].takeCardAt(discardPile, discardPile.size()-1);
					currentTurn++;
				}			
			}
		}
	}

	//Add a card to hand from the deck
	public void takeCard(ArrayList<Card> hand) {
		int random = (int)(Math.random()*playableCards.size());
		hand.add(playableCards.remove(random));
	}
	
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

	public void initRound() {
		playableCards.clear();
		//Fill the deck
		for (int i = 0; i < numOfDecks; i++) {
			for (int x = 0; x < 4; x++) {
				for (int y = 0; y < 13; y++) {
					playableCards.add(new Card(x, y, cardImages[x][y], cardBack));				
				}
			}
		}
		
		//Init hands
		for (int i = 0; i < 2; i++) {
			players[i].clearHand();
			for (int x = 0; x < handSize; x++) {
				players[i].takeCard(playableCards);
				players[i].setCardFaceUp(i == 0, players[i].getHandSize()-1);
			}
		}
		
		//Init discard pile
		discardPile.clear();
		discardPile.add(playableCards.remove((int)(Math.random()*playableCards.size())));
		discardPile.get(discardPile.size()-1).setFaceUp(true);
	}
	
	public void initSticks() {
		sticks.add(new Stick(new Run(4, Card.Spades)));
		sticks.add(new Stick(new Run(4, Card.Any), new Set(4)));
		sticks.add(new Stick(new Run(7, Card.Clubs)));
		sticks.add(new Stick(new Run(5, Card.Diamonds)));
		sticks.add(new Stick(new Run(4, Card.Any)));
		sticks.add(new Stick(new Run(6, Card.Clubs)));
		sticks.add(new Stick(new Set(8)));
		sticks.add(new Stick(new Set(3, -1, 3)));
		sticks.add(new Stick(new Set(7)));
		sticks.add(new Stick(new Set(3, 0), new Set(3, 12), new Set(3, 11)));
		sticks.add(new Stick(new Set(4, 0), new Set(4, 9)));
		sticks.add(new Stick(new Set(3, 4), new Set(3, 9)));
		sticks.add(new Stick(new Run(6, Card.Any), new Set(4)));
		sticks.add(new Stick(new Set(4, -1, 2)));
		sticks.add(new Stick(new Run(4, Card.Diamonds)));
		sticks.add(new Stick(new Run(6, Card.Diamonds)));
		sticks.add(new Stick(new Set(3, 4), new Set(3, 9)));
	}
		
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_P) {
			stickDebug = !stickDebug;
			System.out.println("Debug: " + stickDebug);
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {	
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	private BufferedImage generateBack(Color tint) {
		int width = 73, height = 97;
		int[] pixels = new int[width * height * 4];
		double[][] grayscalePattern = generatePattern(width, height);
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color c;
				c = getBorderColor(x, y, width, height);
				if (c == null) {
					c = new Color((int)(255 - (255-tint.getRed())*grayscalePattern[x][y]), 
								  (int)(255 - (255-tint.getGreen())*grayscalePattern[x][y]), 
								  (int)(255 - (255-tint.getBlue())*grayscalePattern[x][y]));
				}
				pixels[(y * width + x) * 4] = c.getRed();
				pixels[(y * width + x) * 4+1] = c.getGreen();
				pixels[(y * width + x) * 4+2] = c.getBlue();
				pixels[(y * width + x) * 4+3] = c.getAlpha();
			}
		}
		
		return getImageFromArray(pixels, width, height);
	}
	
	public BufferedImage getImageFromArray(int[] pixels, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = (WritableRaster) image.getData();
        raster.setPixels(0,0,width,height,pixels);
        image.setData(raster);
        return image;
    }

	private Color getBorderColor(int x, int y, int width, int height) {
		if ((x == 0 || x == width-1)) {
			if (y == 0 || y == height-1) {
				return new Color(255, 255, 255, 0);
			} else if (y == 1 || y == height-2) {
				return new Color(192, 192, 192);
			} else {
				return new Color(0, 0, 0);
			}
		} else if ((y == 0 || y == height-1)) {
			if (x == 1 || x == width-2) {
				return new Color(192, 192, 192);
			} else {
				return new Color(0, 0, 0);
			}
		} else if ((x == 1 || x == width-2)) {
			if (y == 1 || y == height-2) {
				return new Color(128, 128, 128);
			} else {
				return new Color(255, 255, 255);
			}
		} else if ((y == 1 || y == height-2)) {
			if (x == 1 || x == width-2) {
				return new Color(128, 128, 128);
			} else {
				return new Color(255, 255, 255);
			}
		} else if ((y < 5 || y > height-6) || (x < 5 || x > width-6)) {
				return new Color(255, 255, 255);
		} else {
			return null;
		}
	}
	
	private double[][] generatePattern(int width, int height) {
		double[][] pattern = new double[width][height];
		double[][] newPattern = new double[width][height];
		pattern = setVisiblePixel(0, 0, width, height, pattern);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) { 
				int counter = 0;
				double total = 0;
				if (x > 0) {
					total += pattern[x-1][y];
					counter++;
				}
				if (x < width-1) {
					total += pattern[x+1][y];
					counter++;
				}
				if (y > 0) {
					total += pattern[x][y-1];
					counter++;
				}
				if (y < height-1) {
					total += pattern[x][y+1];
					counter++;
				}
				newPattern[x][y] = (pattern[x][y]*4+total)/(counter+4);
			}
		}
		return newPattern;
	}
	
	private double[][] setVisiblePixel(int x, int y, int width, int height, double[][] pattern) {
		int size = 2;
		
		if (pattern[x][y] != 1) {
			for (int drawY = 0; drawY < size; drawY++) {
				for (int drawX = 0; drawX < size; drawX++) {
					int pixelX = x + drawX;
					int pixelY = y + drawY;
					if (pixelX < width && pixelY < height) {
						pattern[pixelX][pixelY] = 1;
					}
				}
			}
			if (x+size*2 < width && y+size < height) {
				pattern = setVisiblePixel(x+size*2, y+size, width, height, pattern);
			}
			if (y+size*2 < height && x+size < width) {
				pattern = setVisiblePixel(x+size, y+size*2, width, height, pattern);
			}
			if (x+size*16 < width && y < height) {
				pattern = setVisiblePixel(x+size*16, y, width, height, pattern);
			}
			if (y+size*16 < height && x < width) {
				pattern = setVisiblePixel(x, y+size*16, width, height, pattern);
			}
//			if (y+size<height && x+size<width) {
//				pattern = setVisiblePixel(x+size, y+size, width, height, pattern);
//			}
		}
		return pattern;
	}
	
	@SuppressWarnings("unused")
	private double[][] placePattern(int x, int y, int width, int height, double[][] pattern) {
		
		if (pattern[x][y] != 1) {
			for (int drawY = 0; drawY < designSize; drawY++) {
				for (int drawX = 0; drawX < designSize; drawX++) {
					int pixelX = x + drawX;
					int pixelY = y + drawY;
					if (pixelX < width && pixelY < height) {
						pattern[pixelX][pixelY] = design[drawX][drawY];
					}
				}
			}
			if (x+designSize < width) {
				pattern = placePattern(x+designSize, y, width, height, pattern);
			}
			if (y+designSize < height) {
				pattern = placePattern(x, y+designSize, width, height, pattern);
			}
		}
		return pattern;
	}
	
	public void fillDesign() {
		design = new double[designSize][designSize];
		design[0][0] = 1;
		design[1][1] = 1;
		design[2][2] = 1;
		design[3][3] = 1;
		design[4][4] = 1;
		design[4][0] = 1;
		design[3][1] = 1;
		design[1][3] = 1;
		design[0][4] = 1;
//		design[4][3] = 1;
//		design[3][0] = 1;
//		design[0][1] = 1;
//		design[1][4] = 1;
	}

	void SleepAndRefresh() {
		long timeSLU = (long) (System.currentTimeMillis() - LastRefresh);

		Checks++;
		if (Checks >= 15) {
			drawFPS = Checks / ((System.currentTimeMillis() - LastFPSCheck) / 1000.0);
			LastFPSCheck = System.currentTimeMillis();
			Checks = 0;
		}

		if (timeSLU < 1000.0 / MaxFPS) {
			try {
				Thread.sleep((long) (1000.0 / MaxFPS - timeSLU));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		LastRefresh = System.currentTimeMillis();

		repaint();
	}
}
