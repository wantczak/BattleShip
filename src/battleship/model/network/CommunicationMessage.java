package battleship.model.network;

import battleship.model.board.BoardState;
import battleship.model.user.Player;
/**
 * Klasa odpowiedzialna za przechowywanie informacji przesylanej przez internet
 * po oddaniu strzalu i jako odpowiedz na strzal.
 * 
 * @author Pawel Czernek
 *
 */
public class CommunicationMessage {
	
	private int coordinateX; //
	private int coordinateY;
	private Player owner;
	private Command command;
	private BoardState state = null;
	
	public CommunicationMessage(Command command,Player own, int x, int y) {
		this.command = command;
		this.owner = own;
		this.coordinateX = x;
		this.coordinateY = y;
	}
	
	public CommunicationMessage(Command command,Player own) {
		this.command = command;
		this.owner = own;
	}

	
	public CommunicationMessage(Command command,Player own, int x, int y, BoardState state) {
		this(command, own, x, y);
		this.state = state;
	}
	
	public int getCoordinateX() {
		return coordinateX;
	}
	public int getCoordinateY() {
		return coordinateY;
	}
	public Player getOwner() {
		return owner;
	}
	public Command getCommand() {
		return command;
	}
	public BoardState getState() {
		return state;
	}
	
	public String toString(){
		if(state == null){
			return command+";"+owner+";"+coordinateX+";"+coordinateY;
		}else{
			return command+";"+owner+";"+coordinateX+";"+coordinateY+";"+state;
		}
	}
	
}
