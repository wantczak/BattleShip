package battleship.model.procedure;

public class GameProcedure {
	public enum Procedure {
		START_GAME,
		OPEN_CONNECTION,
		CHOOSE_SERVER,
		CONNECT_TO_CLIENT,
		CONNECT_TO_SERVER,
		DEPLOY_SHIPS,
		READY_TO_START,
		PLAYING_GAME
	}

	Procedure procedure;
	
	public GameProcedure(Procedure startGame){
		this.procedure = startGame;
	}
	
	public Procedure getProcedure(){
		return procedure;
	}
	
	public void setProcedure(Procedure procedure){
		this.procedure = procedure;
	}
}
