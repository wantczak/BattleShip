package battleship.model.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import battleship.gui.game.GameChooserViewController;
import battleship.model.server.Server;
import javafx.collections.ObservableSet;
import javafx.scene.control.TableView;

public class GameChooserThread extends Thread {
    private int connectionPort = 8080; //zmienna Integer serverPort
    private InetAddress host;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private boolean serverChoosen = false;
    private ObservableSet<Server> serverObservableSet;
    private GameChooserViewController gameChooserViewController;
    private Set<String> serversIP = new HashSet<String>();
    private Timer timerServersAvailable;
    public GameChooserThread(ObservableSet<Server> serverObservableSet, GameChooserViewController gameChooserViewController){
    	this.serverObservableSet = serverObservableSet;
    	this.gameChooserViewController = gameChooserViewController;
    }
    
	public void run() {
		try{	
			System.out.println("Start chooser thread");
			host = InetAddress.getLocalHost();
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			byte[] sendData = "LOOKING_FOR_SERVERS".getBytes();
			
			/*
			timerServersAvailable = new Timer();
			timerServersAvailable.scheduleAtFixedRate((new TimerTask() {
				  @Override
				  public void run() {
					  System.out.println("TIMER EVENT");
					  serverObservableSet.clear();
					  serversIP.clear();
				  }
				}), 5000,5000);

			*/
			
			while(!gameChooserViewController.ServerSelected()){
				try{
				    packet = new DatagramPacket(sendData, sendData.length,InetAddress.getByName("255.255.255.255"), connectionPort);
				    socket.send(packet);
				    Thread.sleep(1000);
				    byte[] recvBuf = new byte[15000];
				    DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
				    socket.setSoTimeout(1000);
				    socket.receive(receivePacket);
	                String pakiet = new String(receivePacket.getData()).trim();
	                String[] pakietArray = pakiet.split(",");
	    			System.out.println("OS TH: "+serverObservableSet);

	                if (pakietArray[0].equals("SERVER_AVAILABLE")){
	                	if (!serversIP.contains(receivePacket.getAddress().getHostAddress())){
		                	Server serverAvailable = new Server(receivePacket.getAddress().getHostAddress(),"BattleShip",pakietArray[1]);
		                	serversIP.add(receivePacket.getAddress().getHostAddress());
		                	serverObservableSet.addAll(Arrays.asList(serverAvailable));
			    			System.out.println("OS TH: "+serverObservableSet);
			    			gameChooserViewController.refreshTableView();
		                	System.out.println("[CLIENT] Otrzymano odpowiedü z : " + receivePacket.getAddress().getHostAddress());
	                	}
	                }
				}
				
				
				catch (SocketTimeoutException ex){
					continue;
				}

			    
			}
		}
		
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	


}
