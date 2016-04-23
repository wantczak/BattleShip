package battleship.gui.game;

import battleship.model.server.ServerProcedure.Procedure;

public interface GameViewController {
	
	public void setTextAreaLogi(String message);
	public void setProcedure(Procedure procedure);

}
