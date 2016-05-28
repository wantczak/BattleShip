package battleship.model.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import battleship.gui.game.GameChooserViewController;
import battleship.model.server.Server;
import javafx.collections.ObservableSet;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableView;

/**
 * Klasa odpowiedzialna za uruchomienie wyboru gry w osobnym watku.
 * 
 * @author Wojciech Antczak
 *
 */
public class GameChooserThread extends Thread {
    private static int connectionPort = 12345; //zmienna Integer serverPort
    private InetAddress host;
    private static DatagramSocket socket;
    private static DatagramPacket packet;
    private boolean serverChoosen = false;
    private ObservableSet<Server> serverObservableSet;
    private static GameChooserViewController gameChooserViewController;
    private Set<String> serversIP = new HashSet<String>();
    private Timer timerServersAvailable;
    public GameChooserThread(ObservableSet<Server> serverObservableSet, GameChooserViewController gameChooserViewController){
    	this.serverObservableSet = serverObservableSet;
    	this.gameChooserViewController = gameChooserViewController;
    }
   
    /**
     * Metoda uruchamiajaca osobny watek do obslugi wyboru typu gry
     */
	public void run() {
		try{	
			System.out.println("Start chooser thread");
			host = InetAddress.getLocalHost();
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			socket.setSoTimeout(2000);
			byte[] sendData = "LOOKING_FOR_SERVERS".getBytes();
			
			while(!gameChooserViewController.ServerSelected()){
				try{
				    packet = new DatagramPacket(sendData, sendData.length,InetAddress.getByName("255.255.255.255"), connectionPort);
				    socket.send(packet);
				    Thread.sleep(1000);
				    byte[] recvBuf = new byte[15000];
				    DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
				    socket.receive(receivePacket);
	                String pakiet = new String(receivePacket.getData()).trim();
	                String[] pakietArray = pakiet.split(",");

	                if (pakietArray[0].equals("SERVER_AVAILABLE")){
	                	if (!serversIP.contains(receivePacket.getAddress().getHostAddress())){
		                	Server serverAvailable = new Server(receivePacket.getAddress().getHostAddress(),"BattleShip",pakietArray[1]);
		                	serversIP.add(receivePacket.getAddress().getHostAddress());
		                	serverObservableSet.addAll(Arrays.asList(serverAvailable));
			    			System.out.println("OS TH: "+serverObservableSet);
			    			gameChooserViewController.refreshTableView();
		                	System.out.println("[CLIENT] Otrzymano odpowied� z : " + receivePacket.getAddress().getHostAddress());
	                	}
	                }
				}
				
				
				catch (SocketTimeoutException ex){
					ex.printStackTrace();
				} 
				
				catch (SocketException ex){
					continue;
				}
			}
		}
		
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
	/**
	 * Metoda realizujaca wykonanie polaczenia sieciowego
	 * @param ServerIP numer IP servera
	 * @return wartosc boolowska czy polaczenie zostalo nawiazane.
	 */
	public static boolean connectToServer(String ServerIP){
		boolean connection = false;

		while(!connection){
		try{
			socket = new DatagramSocket();
			byte[] sendData = "CONNECTION_WANTED".getBytes();
		    packet = new DatagramPacket(sendData, sendData.length,InetAddress.getByName(ServerIP), connectionPort);
		    socket.send(packet);
		   // Thread.sleep(500);
		    byte[] recvBuf = new byte[15000];
		    DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
		    socket.receive(receivePacket);
            String pakiet = new String(receivePacket.getData()).trim();
            String[] pakietArray = pakiet.split(",");
            
        	System.out.println(pakietArray[0]);

            if (pakietArray[0].equals("SERVER_CLIENT_CONNECTION_OPEN")){
            	socket.close();
            	connection = true;
            }
		}
		
		catch (Exception ex){
			ex.printStackTrace();
        	socket.close();
			return connection;
		}
	}
		return connection;
	}
	
	


}
