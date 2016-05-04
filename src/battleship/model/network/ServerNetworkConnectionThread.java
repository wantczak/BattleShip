package battleship.model.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;

import battleship.gui.game.GameServerViewController;
import battleship.model.procedure.GameProcedure;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Klasa odpowiedzialna za w¹tek nas³uchiwania na odpytywania klientow
 * i rozpoczecia gry przez jednego klienta
 * @author Wojciech Antczak
 *
 */
public class ServerNetworkConnectionThread extends Thread {
    private DatagramSocket serverUDPSocket;
    private DatagramPacket packet;
    
    private int connectionPort = 12345; //zmienna Integer serverPort
    private TextArea textLogServer;
    private GameProcedure serverProcedure;
    private boolean clientConnectionOpen = false;
	private GameServerViewController gameServerViewController;
    
    public ServerNetworkConnectionThread(TextArea textLogServer, GameProcedure serverProcedure, GameServerViewController gameServerViewController){
    	this.textLogServer = textLogServer;
    	this.serverProcedure = serverProcedure;
    	this.gameServerViewController = gameServerViewController;
    }
    
    public boolean getClientConnectionOpen(){
    	return clientConnectionOpen;
    }
    
    public void setClientConnectionOpen(boolean clientConnectionOpen){
    	this.clientConnectionOpen = clientConnectionOpen;
    }
    
	public void run() {
        try {
			Platform.runLater(()->gameServerViewController.setTextAreaLogi("[SERVER] Rozpoczecie wysylania broadcastingu obecnosci w sieci"));
		    serverUDPSocket = new DatagramSocket(connectionPort, InetAddress.getByName("0.0.0.0")); //LISTEN NA WIADOMOSC OD KLIENTA 
		    serverUDPSocket.setBroadcast(true); //ODPALENIE BROADCASTINGU			
		} catch (Exception ex) {
            System.out.println("Problem z utworzeniem socketu na porcie: " + connectionPort );
		}
        
        while (!clientConnectionOpen){
        	try{
                //Receive a packet
        		byte[] recvBuf = new byte[15000];
        		packet = new DatagramPacket(recvBuf, recvBuf.length);
                serverUDPSocket.receive(packet);
                String pakiet = new String(packet.getData()).trim();
                                
                switch (pakiet){
                case "LOOKING_FOR_SERVERS":{
                    byte[] sendData = ("SERVER_AVAILABLE"+","+"Wojtek").getBytes();//Imie usera oczekujacego na gre wstawiono na stale
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    serverUDPSocket.send(sendPacket);
                	break;
                }
                
                case "CONNECTION_WANTED":{
                	Platform.runLater(()->gameServerViewController.setTextAreaLogi("[SERVER] Otrzymano zapytanie o polaczenie od klienta"));
                    byte[] sendData = ("SERVER_CLIENT_CONNECTION_OPEN").getBytes();//Imie usera oczekujacego na gre wstawiono na stale
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    serverUDPSocket.send(sendPacket);
                    Platform.runLater(()->gameServerViewController.setTextAreaLogi("[SERVER] Wyslano gotowosc do polaczenia  klienta"));
        			gameServerViewController.setClientIP(packet.getAddress().getHostAddress());
        			clientConnectionOpen = true;
        			serverUDPSocket.close();
                	break;
                }
                
                default:{
                	break;
                }
                }
        	}
        	
        	catch(Exception ex){
        		ex.printStackTrace();
        	}
        	finally{
        		
        	}
        }
        serverUDPSocket.close();
	}
}
