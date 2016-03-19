package battleship.model.server;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import battleship.model.network.NetworkConnection;

public class ServerProcedure {
	public enum Procedure {
		START_GAME,
		DEPLOY_SHIPS,
		OPEN_CONNECTION,
		WAITING_FOR_OPPONENT
	}
	private NetworkConnection networkConnection;
	private String ServerIP;
	private String ServerPort = "8080";

	//blok inicjalizujacy
	{
		networkConnection = new NetworkConnection();
	}

	Procedure procedure;
	
	public ServerProcedure(Procedure procedure){
		this.procedure = procedure;
	}
	
	public Procedure getServerProcedure(){
		return procedure;
	}
	
	public void setServerIP(){
		String IP = "";
		IP = networkConnection.getLocalIP();
		this.ServerIP = IP;
	}
	
	public String getServerIP(){
		return ServerIP;
	}
	
	public String getServerPort(){
		return ServerPort;
	}
	

}
