package battleship.model.board;
/**
 * Klasa Enum przechowujaca wartosci jakie moga przyjmowac poszczegolne pola gry
 * 
 * @author Pawel Czernek
 *
 */
public enum BoardState {
	/**
	 * Pole puste planszy, przed oddaniem strzalu
	 */
	PUSTE_POLE,
	/**
	 * Pole z ustawionym statkiem, przed oddaniem strzalu
	 */
	STATEK,
	/**
	 * Pole puste planszy, po oddaniu strzalu
	 */
	PUDLO,
	/**
	 * Pole z ustawionym statkiem, po oddaniu strzalu, przed zatopieniem okretu
	 */
	STATEK_TRAFIONY,
	/**
	 * Pole z ustawionym statkiem, po oddaniu strzalu, po zatopieniu okretu
	 */
	STATEK_ZATOPIONY,
}
