package battleship.model.client;

/**
 * 
 * @author Wojciech Antczak
 *
 */
public class ClientProcedure {
	public enum Procedure {
		START_GAME,
		CHOOSE_SERVER,
		CONNECT_TO_SERVER,
		DEPLOY_SHIPS,
		READY_TO_START,
		PLAYING_GAME
	}

	Procedure procedure;
	
	public ClientProcedure(Procedure startGame){
		this.procedure = startGame;
	}
	
	public Procedure getClientProcedure(){
		return procedure;
	}
	
	public void setClientProcedure(Procedure procedure){
		this.procedure = procedure;
	}
}

