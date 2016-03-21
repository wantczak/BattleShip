package battleship.model.server;

public class ServerProcedure {
	public enum Procedure {
		START_GAME,
		OPEN_CONNECTION,
		DEPLOY_SHIPS,
		READY_TO_START
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
