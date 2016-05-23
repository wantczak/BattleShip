package battleship.model.network;
/**
 * Klasa Enum przechowujaca wartosci opisujace rodzaj informacji 
 * przesylanej przez internet pomiedzy graczami
 * 
 * @author Wojciech Antczak
 *
 */
public enum Command {
	/**
	 * Przesylanie informacji o strzale
	 */
	SHOT, 
	/**
	 * Przesylanie odpowiedzi po oddaniu strzalu
	 */
	ANSWER,
	/**
	 * Przesylanie informacji o koncu gry
	 */
	END_GAME;
}
