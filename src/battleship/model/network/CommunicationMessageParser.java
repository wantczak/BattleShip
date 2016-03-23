package battleship.model.network;

import battleship.model.board.BoardState;
import battleship.model.user.Player;

/**
 * Klasa odpowiedzialna za zamianę informacji o strzale na tablicę bitową.
 * @author Paweł Czernek
 *
 */

public class CommunicationMessageParser {
	
	/**
	 * 
	 * @param message obiekt zawierający parametry strzału
	 * @return tablica bitowa z parametrami strzału
	 */
	public byte[] decodeMessage(CommunicationMessage message){
		byte[] table = message.toString().getBytes();
		return table;
	}
	
	/**
	 * 
	 * @param message parametry strzały w formie tablicy bitowej
	 * @return obiekt z zapisanymi parametrami strzału
	 */
	public CommunicationMessage encodeMessage(byte[] message){
		String strMessage;
		int coordinateX; 
		int coordinateY;
		Player owner;
		Command command;
		BoardState state;
		
		//Podział tablicy bitowej na poszczególne parametry 
		strMessage = new String(message);
		String[] argument = strMessage.split(";");
		command = Command.valueOf(argument[0]);
		owner = Player.valueOf(argument[1]);
		coordinateX = Integer.parseInt(argument[2]);
		coordinateY = Integer.parseInt(argument[3]);
		
		if(argument.length>4){
			state = BoardState.valueOf(argument[4]);
			return new CommunicationMessage(command, owner, coordinateX, coordinateY,state);
		}else return new CommunicationMessage(command, owner, coordinateX, coordinateY);
	}
	
	

}
