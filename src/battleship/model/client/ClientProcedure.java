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
		DEPLOY_SHIPS,
		READY_TO_START,
		PLAYING_GAME
	}

	Procedure procedure;
	
	public ClientProcedure(Procedure procedure){
		this.procedure = procedure;
	}
	
	public Procedure getServerProcedure(){
		return procedure;
	}
	
	public void setServerProcedure(Procedure procedure){
		this.procedure = procedure;
	}
}

