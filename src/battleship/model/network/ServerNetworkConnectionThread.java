package battleship.model.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import battleship.gui.game.GameServerViewController;
import battleship.model.procedure.GameProcedure;
import battleship.model.procedure.GameProcedure.Procedure;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Klasa odpowiedzialna za watek nasluchiwania na odpytywania klientow
 * i rozpoczecia gry przez jednego klienta
 * 
 * @author Wojciech Antczak
 *
 */
public class ServerNetworkConnectionThread extends Thread {
    private DatagramSocket serverUDPSocket;
    private DatagramPacket packet;
    
    private int connectionPort = 12345; //zmienna Integer serverPort
    private TextArea textLogServer;
    private TextField textFieldServerGame;
    private GameProcedure serverProcedure;
    private boolean clientConnectionOpen = false;
	private GameServerViewController gameServerViewController;
    
    public ServerNetworkConnectionThread(TextArea textLogServer, GameProcedure serverProcedure, GameServerViewController gameServerViewController, TextField textFieldServerGame){
    	this.textLogServer = textLogServer;
    	this.serverProcedure = serverProcedure;
    	this.gameServerViewController = gameServerViewController;
    	this.textFieldServerGame = textFieldServerGame;
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
		    setServerUDPSocket(new DatagramSocket(connectionPort, InetAddress.getByName("0.0.0.0"))); //LISTEN NA WIADOMOSC OD KLIENTA 
		    getServerUDPSocket().setBroadcast(true); //ODPALENIE BROADCASTINGU			
		} catch (Exception ex) {
            System.out.println("Problem z utworzeniem socketu na porcie: " + connectionPort );
		}
        
        while (!clientConnectionOpen){
        	try{
                //Receive a packet
        		byte[] recvBuf = new byte[15000];
        		packet = new DatagramPacket(recvBuf, recvBuf.length);
        		getServerUDPSocket().setSoTimeout(2000);
                getServerUDPSocket().receive(packet);
                String pakiet = new String(packet.getData()).trim();
                                
                switch (pakiet){
                case "LOOKING_FOR_SERVERS":{
                    byte[] sendData = ("SERVER_AVAILABLE"+","+textFieldServerGame.getText()).getBytes();//Imie usera oczekujacego na gre wstawiono na stale
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    getServerUDPSocket().send(sendPacket);
                	break;
                }
                
                case "CONNECTION_WANTED":{
                	Platform.runLater(()->gameServerViewController.setTextAreaLogi("[SERVER] Otrzymano zapytanie o polaczenie od klienta"));
                    byte[] sendData = ("SERVER_CLIENT_CONNECTION_OPEN").getBytes();//Imie usera oczekujacego na gre wstawiono na stale
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    getServerUDPSocket().send(sendPacket);
                    Platform.runLater(()->gameServerViewController.setTextAreaLogi("[SERVER] Wyslano gotowosc do polaczenia  klienta"));
        			gameServerViewController.setClientIP(packet.getAddress().getHostAddress());
        			gameServerViewController.setProcedure(Procedure.CONNECT_TO_CLIENT);
        			clientConnectionOpen = true;
        			getServerUDPSocket().close();
                	break;
                }
                
                default:{
                	break;
                }
                }
        	}
        	
        	
        	catch (SocketTimeoutException e){
                Platform.runLater(()->gameServerViewController.setTextAreaLogi("Brak requestu od klienta"));

        	} 
        	
        	catch (IOException e) {
				e.printStackTrace();
			}
        	finally{
        		
        	}
        }
        getServerUDPSocket().close();
	}

	public DatagramSocket getServerUDPSocket() {
		return serverUDPSocket;
	}

	public void setServerUDPSocket(DatagramSocket serverUDPSocket) {
		this.serverUDPSocket = serverUDPSocket;
	}
}
