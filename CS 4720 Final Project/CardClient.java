import java.io.*;
import java.util.*;
import java.net.*;

public class CardClient{
	static ArrayList<Deck> deckList = new ArrayList<>();
	
	/*class Card: the basic data type for the program
	Constructors: Card(name)
	Card(name, text).
	Methods: show: returns name
	read: returns text.
	*/
	public static class Card{
		String name;
		String text;
		
		Card(String incomingName){
			this.name = incomingName;
			this.text = "Blank";
		}
		
		Card(String incomingName, String incomingText){
			this.name = incomingName;
			this.text = incomingText;
		} 
		
		public String show(){
			return name;
		}
		
		public String passName(){
				return name.replaceAll("\\s","_"); 
			}
		
		public String read(){
			return text;
		}
	}
	
	/*class Deck: covers the utility for any group of cards
	Constructor: Deck(name)
	Methods: size: returns size of cardDeck
	addCard(card):adds card to stack.
	drawCard(): draws card from top of stack and returns it.
	show(): shows the names of each card in deck.
	search(cardname): returns first card with a name that matches the query.
	play(cardName):removes and returns the first card with a name matching the query.
	
	*/
	public static class Deck{
		private Deque<Card> cardDeck= new LinkedList<Card>();
		public String name;
		
		Deck(String name){
			this.name = name;
		}
		
		public int size(){
			return cardDeck.size();
		}
		
		public void addCard(Card newCard){
			cardDeck.addFirst(newCard);
		}
		
		public Card drawCard(){
			if(cardDeck.size()!=0)
				return cardDeck.removeFirst();
			return null;
		}
		
		public void shuffle(){
			ArrayList<Card> shuffler = new ArrayList<Card>(cardDeck.size());
			//System.out.println("cardDeck.size() = " + cardDeck.size()); 
			int size = cardDeck.size();
			for(int i=0; i<size; i++){ 
				shuffler.add(cardDeck.removeFirst());
				//System.out.println(shuffler.get(i).show());
			}
			Collections.shuffle(shuffler);
			for(int i=0; i<shuffler.size(); i++)
				cardDeck.addFirst(shuffler.get(i));
		}
		
		public void show(){
			for(Card thisCard: cardDeck)
				System.out.println(thisCard.show());
		}
		
		public void showNumbered(){
			int i=0;
			for (Card thisCard: cardDeck){
				System.out.println(i + ". " + thisCard.show());
			}
		}
		
		public Card search(String query){
			for(Card thisCard: cardDeck){
				if(thisCard.show().equals(query))
					return thisCard;
			}
			return null;
		}
		
		public Card play(String cardName){
			ArrayList<Card> deck = new ArrayList<Card>(cardDeck.size());
			Card yourCard;
			int size = cardDeck.size();
			for(int i=0; i<size; i++)
				deck.add(cardDeck.removeFirst());
			for(int i=0; i<size; i++){
				if(deck.get(i).show().equals(cardName)){
					yourCard = deck.get(i);
					deck.remove(i);
					for(Card thatCard: deck){
						cardDeck.addLast(thatCard);
					}
					return yourCard;	
				}
			}
			return null;
		}
		
	}
	
	/*Incoming command syntax: 
	DRAW: draw [number] [source#] [destination#]
	PLAY: play [cardName] [source#] [destination#]
	NEWDECK: newdeck [deckname]
	NEWCARD: newcard [cardName] [cardText] [destination#]
	EMPTYDECK: empty [deck#]
	*/ 	 
	static void commandInterface(String command) throws Exception{
		String[] comSplit = command.split("\\s");
		switch (comSplit[0].toLowerCase()){
			case "draw":
				for(int i=0; i< Integer.parseInt(comSplit[1]); i++){
					deckList.get(Integer.parseInt(comSplit[3])).addCard(
						deckList.get(Integer.parseInt(comSplit[2])).drawCard());
				}
				break;
			
			case "play":
				deckList.get(Integer.parseInt(comSplit[3])).addCard(
					deckList.get(Integer.parseInt(comSplit[2])).play(comSplit[1]));
				break;
			
			case "newdeck":
				Deck newDeck = new Deck(comSplit[1].replaceAll("_"," "));
				deckList.add(newDeck);
				break;
			
			case "newcard":
				Card newCard = new Card (
					comSplit[1].replaceAll("_", "\\s"), comSplit[2].replaceAll("_", " "));
				deckList.get(Integer.parseInt(comSplit[3])).addCard(newCard);
				break;
				
			case "empty":
				int n = Integer.parseInt(comSplit[1]);
				Deck emptyDeck = new Deck(deckList.get(n).name);
				deckList.remove(n);
				deckList.add(n , emptyDeck);
				break;
			default:
				throw new Exception("Invalid command recieved.");
		}
	}
	
	static void listDecks(){
		for(int i=0; i<deckList.size(); i++)
			System.out.println(i + ". " + deckList.get(i).name);
	}
	
	
	
	static String localInputMaster()throws IOException{
		String command ="";
		String input;
		int n= -1;
		BufferedReader reader = new BufferedReader
			(new InputStreamReader(System.in));
		System.out.println("What would you like to do?\n " +
		"1. draw a card?\n" +
		"2. play a card?\n" +
		"3. create a deck?\n" +
		"4. create a card?\n" +
		"5. look through a deck? \n" +
		"6. examine a specific card?\n" +
		"7. shuffle a deck?");
		
		switch (input=reader.readLine()){
			case "1":		//DRAW: draw [number] [source#] [destination#]
				command+="draw ";
				System.out.println("How many cards do you want to draw? ");
				if((n=Integer.parseInt(reader.readLine()))!= -1){
					command += n + " ";
					n= -1;
					System.out.println("Which deck should we draw from?");
					listDecks();
					if((n = Integer.parseInt(reader.readLine()))!= -1){
						command += n + " ";  
						n=-1;
						System.out.println("Where should the cards go?");
						listDecks();
						if((n = Integer.parseInt(reader.readLine()))!= -1){
						command += n;
						n=-1;
					}
					else{
						System.out.println("invalid input");
						command = localInputMaster();
					}
					}
					else{
						System.out.println("invalid input");
						command = localInputMaster();
						break;
					}
				}
				else {
					System.out.println("invalid input");
					command = localInputMaster();
				}
				break;
			case "2":		//PLAY: play [cardName] [source#] [destination#]
				command += "play ";
				System.out.println("which deck is the card in?");
				listDecks();
				if((n = Integer.parseInt(reader.readLine()))!= -1){
					System.out.println("which card should be played?");
					deckList.get(n).show();
					input =reader.readLine();
					if(deckList.get(n).search(input) != null){
						command+= deckList.get(n).play(input).passName();
						command+= n + " ";
						n = -1;
						System.out.println("Where should the card go?");
						listDecks();
						if((n = Integer.parseInt(reader.readLine()))!= -1){
							command+= n + " ";
							n= -1;
						}
						else{
							System.out.println("invalid input");
						command = localInputMaster();
						}
					}
					else{
							System.out.println("invalid input");
						command = localInputMaster();
						}
				}
				else{
							System.out.println("invalid input");
						command = localInputMaster();
						}
				break;
			case "3":		//NEWDECK: newdeck [deckname]
				command+= "newdeck ";
				System.out.println("What will you name this deck?");
				input = reader.readLine();
				command+= input.replaceAll("\\s","_");
				break;
			case "4":		//NEWCARD: newcard [cardName] [cardText] [destination#]
				command+= "newcard ";
				System.out.println("What should this card be called?");
				input = reader.readLine();
				command+= input.replaceAll(" ", "_")+ " ";
				System.out.println("What will the card say?");
				input = reader.readLine();
				command+= input.replaceAll(" ", "_")+ " ";
				System.out.println("Where should the card go?");
						listDecks();
						if((n = Integer.parseInt(reader.readLine()))!= -1){
							command+= n + " ";
							n= -1;
						}
						else{
							System.out.println("invalid input");
						command = localInputMaster();
						}
				break;
			case "5":		//look through a deck
				System.out.println("which deck should we examine?");
				listDecks();
						if((n = Integer.parseInt(reader.readLine()))!= -1){
							deckList.get(n).show();
							command = localInputMaster();
						}
						else{
							System.out.println("invalid input");
							command = localInputMaster();
						}
				break;
			case "6":		//examine a card
				System.out.println("Which deck is the card in?");
				listDecks();
				if((n = Integer.parseInt(reader.readLine()))!= -1){
					System.out.print("Which card do you want to read?");
					deckList.get(n).show();
					input = reader.readLine();
					deckList.get(n).search(input).read();
					command = localInputMaster();
				}
				else {
					System.out.println("invalid input");
					command = localInputMaster();
				}
				break;
			case "7":		//shuffle!
				command = "shuffle";
				break;
		}
		return command;
	}
	
	public static class play implements Runnable{
		Socket serverSocket;
		OutputStream outStream;
		
		play(Socket incoming){
			this.serverSocket = incoming;
		}
		
		@Override public void run(){
			try{
				this.outStream = serverSocket.getOutputStream();
				PrintWriter scribe = new PrintWriter(outStream, true);
				BufferedReader reader = new BufferedReader
					(new InputStreamReader(System.in));
				String command;
				while(true){
					command = localInputMaster();
					if(command.equals("shuffle")){
						scribe.println(command);
						boolean shuffleCheck = true;
						while(shuffleCheck){
							System.out.println("which deck should be shuffled?");
							listDecks();
							int n = -1;
							if((n=Integer.parseInt(reader.readLine()))!= -1){
								scribe.println("empty " + n);
								deckList.get(n).shuffle();
								Card nextCard;
								while((nextCard = deckList.get(n).drawCard()) !=null){
									scribe.println("newcard " + nextCard.name.replaceAll(" ","_") 
										+ " " + nextCard.text.replaceAll(" ","_") + " " + n);
								}
								scribe.println("endshuffle");
								shuffleCheck = false;
							}
							else{
								System.out.println("you must choose an appropriate deck.");
							}
						}
					}
					else{
						scribe.println(command);
						//System.out.println("sending command " + command);
					}
				}
			}catch(Exception err){
				System.err.println(err);
				}
		}
	}
	
	public static class commandHandler implements Runnable{
		Socket commandSocket;
		InputStream inStream;
		
		commandHandler(Socket incoming){
			this.commandSocket = incoming;
		}
		
		@Override public void run(){
			try{
				this.inStream = commandSocket.getInputStream();
				BufferedReader commander = new BufferedReader(
				new InputStreamReader(inStream));
				String command;
				while((command = commander.readLine()) !=null) {
					commandInterface(command);
				}
			}catch(Exception err){
				System.err.println(err);
			}finally {
						try {
							commandSocket.close();		
						}catch(IOException err){
							System.err.println(err);
						} 
		}
	}
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("CardClient requires a host and port.");
			return;
		}
		
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		Socket server = new Socket(host, port);
		
		Thread implementer = new Thread(new commandHandler(server));
		implementer.start();
		Thread input = new Thread(new play(server));
		input.start();
		
		
	}
}