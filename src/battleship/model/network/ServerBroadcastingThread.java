package battleship.model.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import battleship.model.server.ServerProcedure;
import battleship.model.server.ServerProcedure.Procedure;
import javafx.scene.control.TextArea;

public class ServerBroadcastingThread extends Thread {
    private DatagramSocket socket;
    private DatagramPacket packet;
    private int connectionPort = 8080; //zmienna Integer serverPort
    private TextArea textLogServer;
    private ServerProcedure serverProcedure;
    private boolean ClientConnected = false;
    
    public ServerBroadcastingThread(TextArea textLogServer, ServerProcedure serverProcedure){
    	this.textLogServer = textLogServer;
    	this.serverProcedure = serverProcedure;
    }
    
    public boolean getClientConnected(){
    	return ClientConnected;
    }
    
	public void run() {
        try {
			textLogServer.appendText("[SERVER] Rozpoczecie wysylania broadcastingu obecnosci w sieci \n");
		    socket = new DatagramSocket(connectionPort, InetAddress.getByName("0.0.0.0")); //LISTEN NA WIADOMOSC OD KLIENTA 
		    socket.setBroadcast(true); //ODPALENIE BROADCASTINGU			
		} catch (Exception ex) {
            System.out.println("Problem z utworzeniem socketu na porcie: " + connectionPort );
		}
        
        while (!ClientConnected){
        	try{
                //Receive a packet
        		byte[] recvBuf = new byte[15000];
        		packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);
                String pakiet = new String(packet.getData()).trim();

                System.out.println("Odebrano: "+pakiet);
                System.out.println(pakiet.equals("LOOKING_FOR_SERVERS"));

                if(pakiet.equals("LOOKING_FOR_SERVERS")){
                	if (serverProcedure.getServerProcedure() == Procedure.READY_TO_START){
                        byte[] sendData = ("SERVER_AVAILABLE"+","+"Wojtek").getBytes();//Imie usera oczekujacego na gre wstawiono na stale
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                        socket.send(sendPacket);
                        System.out.println("[SERVER] Wyslano odpowiedz do : " + sendPacket.getAddress().getHostAddress());
                	}
      	
                }
        	}
        	
        	catch(Exception ex){
        		
        	}
        }
        
        packet = new DatagramPacket (new byte[1], 1);
	}

}
