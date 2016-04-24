package battleship.model.server;

import javafx.beans.property.SimpleStringProperty;

/**
 * Klasa reprezentujaca pojedynczy serwer rozpoczynajacy gre
 * @author Wojciech Antczak
 *
 */
public class Server {
	private final SimpleStringProperty serverIP;
    private final SimpleStringProperty serverStartedGame;
    private final SimpleStringProperty serverPlayerName;

    
    public Server(String serverIP,String serverStartedGame, String serverPlayerName){
    	this.serverIP = new SimpleStringProperty(serverIP);
    	this.serverStartedGame = new SimpleStringProperty(serverStartedGame);
    	this.serverPlayerName = new SimpleStringProperty(serverPlayerName);
    }
    
    public String getServerIP(){
    	return serverIP.get();
    }
    
    public void setServerIP(String serverIP){
    	this.serverIP.set(serverIP);
    }
    
    public String getServerStartedGame(){
    	return serverStartedGame.get();
    }
    
    public void setServerStartedGame(String serverStartedGame){
    	this.serverStartedGame.set(serverStartedGame);
    }

    public String getServerPlayerName(){
    	return serverPlayerName.get();
    }
    
    public void setServerPlayerName(String serverPlayerName){
    	this.serverPlayerName.set(serverPlayerName);
    }

    
    


}
