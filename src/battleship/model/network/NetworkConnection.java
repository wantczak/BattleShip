package battleship.model.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class NetworkConnection {
	
	public String getLocalIP(){
	    String ip = "";
        InetAddress addr;
	    try {
	    	addr = InetAddress.getLocalHost();
	    	ip = addr.getHostAddress();
			System.out.println("Current IP address : " + ip);   
	    } 
	    
	    catch (UnknownHostException  e) {
	        throw new RuntimeException(e);
	    }
	    return ip;
	}

}
