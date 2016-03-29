package battleship.model.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

public class GameChooserThread extends Thread {
    private String hostname= "localhost";
    private int connectionPort = 8080; //zmienna Integer serverPort
    private InetAddress host;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private boolean serverChoosen = false;
    
	public void run() {
		try{	
			System.out.println("Start chooser thread");
			host = InetAddress.getLocalHost();
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			byte[] sendData = "LOOKING_FOR_SERVERS".getBytes();

			while(!serverChoosen){
				try{
				    packet = new DatagramPacket(sendData, sendData.length,InetAddress.getByName("255.255.255.255"), connectionPort);
				    socket.send(packet);
				    Thread.sleep(1000);
				    byte[] recvBuf = new byte[15000];
				    DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
				    socket.setSoTimeout(1000);
				    socket.receive(receivePacket);
	                String pakiet = new String(receivePacket.getData()).trim();
	                if (pakiet.equals("SERVER_AVAILABLE")){
					    System.out.println(getClass().getName() + "Otrzymano odpowiedü z : " + receivePacket.getAddress().getHostAddress());
					    serverChoosen = true;
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
