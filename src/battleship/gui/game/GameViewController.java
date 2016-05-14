package battleship.gui.game;

import battleship.model.procedure.GameProcedure.Procedure;

/**
 * Interfejs kontrolera widoku dla gry w trybie server oraz klient.
 * 
 * @author Pawel Czernek
 *
 */
public interface GameViewController {
	
	public void setTextAreaLogi(String message);
	public void setProcedure(Procedure procedure);

}
