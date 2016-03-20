package battleship.model.network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


/**
 * Klasa odpowiedzialna za obsluge polaczenia sieciowego
 * @author Wojciech Antczak
 */
public class NetworkConnection {

	//DEKLARACJA ZMIENNYCH
	ServerSocket serverSocket; //Deklaracja pojedynczego serverSocketa
    String localIP; //zmienna String serverIP
    int connectionPort = 8080; //zmienna Integer serverPort
    Socket serverConnection;
    boolean connectionStatus;
	/**
	 * Funkcja sprawdzajaca IP danego komputera i zwracajaca je w formie String
	 * @author Wojciech Antczak
	 * @return Adres IP komputera w formie String
	 */	
	public String getLocalIP(){
		localIP = "";
        InetAddress addr;
	    try {
	    	addr = InetAddress.getLocalHost();
	    	localIP = addr.getHostAddress();
			System.out.println("Current IP address : " + localIP);   
	    } 
	    
	    catch (UnknownHostException  e) {
	        throw new RuntimeException(e);
	    }
	    return localIP;
	}
	
	public int getConnectionPort(){
		return connectionPort;
	}
	
	/**
	 * 
	 * @author Wojciech Antczak
	 * @param textLogServer - TextArea do logowania
	 * @return connectionStatus - status polaczenia z klientem. Gdy brak polaczenia to zakoncz polaczenie
	 * @throws IOException 
	 */
	public boolean createServerConnection(TextArea textLogServer) throws IOException{
		try{
			serverSocket = new ServerSocket(connectionPort);
			serverSocket.setReuseAddress(true);

			textLogServer.appendText("[SERVER] Inicjalizacja polaczenia serwerowego \n");
			connectionStatus = false;
			while (connectionStatus == false){
				textLogServer.appendText("[SERVER] Oczekiwanie na polaczenie z klientem... \n");
				serverConnection = serverSocket.accept();
				textLogServer.appendText("[SERVER] Nazwiazano polaczenie z klientem: [IP]:"+serverConnection.getInetAddress()+" [PORT]:"+serverConnection.getPort()+"\n");
				connectionStatus = true;
			}
		}
		
		catch (Exception ex){
			ex.printStackTrace();
		}
		
		finally{
			if (connectionStatus == false){
				serverSocket.close();
			}
		}
		
		return connectionStatus;
	}

}
