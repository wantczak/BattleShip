package battleship.model.server;

/**
 * 
 * @author Wojciech Antczak
 *
 */
public class ServerProcedure {
	public enum Procedure {
		START_GAME,
		OPEN_CONNECTION,
		CONNECT_TO_CLIENT,
		DEPLOY_SHIPS,
		READY_TO_START,
		PLAYING_GAME
	}

	Procedure procedure;
	
	public ServerProcedure(Procedure procedure){
		this.procedure = procedure;
	}
	
	public Procedure getServerProcedure(){
		return procedure;
	}
	
	public void setServerProcedure(Procedure procedure){
		this.procedure = procedure;
	}
}
