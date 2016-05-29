package battleship.model.procedure;

public class GameProcedure {
	public enum Procedure {
		/**
		 * Procedura startu gry.
		 */
		START_GAME,
		/**
		 * Procedura utworzenia polaczenia internetowego.
		 */
		OPEN_CONNECTION,
		/**
		 * Procedura wyboru servera z ktorym laczy sie klient.
		 */
		CHOOSE_SERVER,
		/**
		 * Procedura polaczenia serwera do klienta.
		 */
		CONNECT_TO_CLIENT,
		/**
		 * Procedura polaczenia klienta do servera.
		 */
		CONNECT_TO_SERVER,
		/**
		 * Procedura rozkladania statkow na planszy.
		 */
		DEPLOY_SHIPS,
		/**
		 * Procedura gotowosci do rozpoczecia gry.
		 */
		READY_TO_START,
		/**
		 * Procedura realizujaca rozgrywke.
		 */
		PLAYING_GAME
	}

	Procedure procedure;
	
	/**
	 * Metoda ustawiajaca procedure poczatkowa
	 * 
	 * @param startGame procedura startu gry
	 */
	public GameProcedure(Procedure startGame){
		this.procedure = startGame;
	}
	
	/**
	 * Metoda pobierajaca aktualna procedure gry
	 * 
	 * @return typ wyliczeniowy procedury gry
	 */
	public Procedure getProcedure(){
		return procedure;
	}
	
	/**
	 * Metoda ustawiajaca procedure gry
	 * 
	 * @param procedure
	 */
	public void setProcedure(Procedure procedure){
		this.procedure = procedure;
	}
}
