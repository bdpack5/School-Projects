import java.io.*;
import java.net.*;
import java.util.*;

public class CardServer{
	
	static ArrayList<String> commandlog = new ArrayList<>();
	
	static class Listener implements Runnable{
		
		Socket clientSocket;
		InputStream inStream;
		Listener(Socket incoming){
			this.clientSocket = incoming;
			
		}
	
		@Override public void run(){
					try {
						this.inStream = clientSocket.getInputStream();
						BufferedReader snoop = new BufferedReader(
						new InputStreamReader(inStream));
						String commander;
						//System.out.println("ready to recieve");
						while((commander = snoop.readLine()) !=null) {
							//System.out.println("parsing command.");
							synchronized(commandlog){
								//System.out.println("parsing command.");
								if(commander.equals("shuffle")){
									commander = snoop.readLine();
									while(!(commander.equals("endshuffle"))){
										commandlog.add(commander);
									}
								}
								else {
									//System.out.println("command recieved");
									commandlog.add(commander);
								}
								commandlog.notifyAll();
							}
						}
						//System.out.println("disaster!");
					}catch(IOException err){
						System.err.println(err);
					}finally {
						try {
							clientSocket.close();		
						}catch(IOException err){
							System.err.println(err);
						} 
					}
		
					}
					
	}
	
	static class Relay implements Runnable {
		
		Socket clientSocket;
		OutputStream outStream;
		
		Relay(Socket incoming){
			this.clientSocket = incoming;
		}
		
		@Override public void run(){
			try {
				this.outStream = clientSocket.getOutputStream();
				PrintWriter scribe = new PrintWriter(outStream);
				int n=0;
				synchronized(commandlog){
					while(true) {
						if(n<commandlog.size()){
							while (n<commandlog.size()) {
								String message = (String) commandlog.get(n);
								scribe.println(message);
								n++;
							}
							scribe.flush();
						}
						try{commandlog.wait();}catch(InterruptedException err){System.err.println(err);}
					}
				}
			}catch(IOException err){
						System.err.println(err);
					}finally {
						try {
							clientSocket.close();		
						}catch(IOException err){
							System.err.println(err);
						}
			
					}
		
		}
	}
	
	public static void main(String[] args) throws IOException{
		if (args.length!=1) {
			System.err.println("CardServer requires port number.");
			return;
		}
		
		System.err.println("host= " + InetAddress.getLocalHost());
		
		int port = Integer.parseInt(args[0]);
		
		ServerSocket server = new ServerSocket(port);
		commandlog.add("newdeck play_area");
		commandlog.add("newdeck main_deck");
		
		int n=1;
		
		while (true){
			Socket connection = server.accept();
			System.err.println("connection from " + connection.getRemoteSocketAddress()
			+ " to " + connection.getLocalSocketAddress());
			commandlog.add("newdeck player" + n + "_deck");
			commandlog.add("newdeck player" + n + "_hand");
			commandlog.add("newdeck player" + n + "_play_area");
			n++;
			Thread ear = new Thread(new Listener(connection));
			ear.start();
			Thread mouth = new Thread(new Relay(connection));
			mouth.start();
		}
	}
}