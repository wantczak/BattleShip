package battleship.model.client;

public class ClientProcedure {
	public enum Procedure {
		START_GAME,
		CHOOSE_SERVER,
		DEPLOY_SHIPS,
		READY_TO_START
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

