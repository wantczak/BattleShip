package battleship.model.board;

import battleship.gui.game.GameViewController;
import battleship.model.board.ShipFactory.Kierunek;

/**
 * Klasa modelu planszy przechowujacej informacje o rozmieszczeniu i stanie
 * statkow oraz o oddanych strzalach
 * 
 * @author Pawel Czernek
 * 
 */
public class Board {

	private BoardState[][] board = new BoardState[11][11]; // Tablica ze stanami
															// planszy
	private GameViewController viewController;

	/**
	 * Konstruktor klasy board - przy wywolaniu ustawia wszystkie pola planszy
	 * na puste
	 * 
	 * @author Pawel Czernek
	 * 
	 */
	public Board() {
		resetBoard();
	}

	/**
	 * Ustawienie referencji do kontrolera widoku dla gry.
	 * 
	 * @param controller referencja w postaci interfejsu {@see GameViewController} do kontrolera widoku planszy w trybie klient lub serwer.
	 */
	public void setViewControllerReference(GameViewController controller) {
		this.viewController = controller;
	}

	/**
	 * Metoda zwracajaca stan tablicy {@see Board} przechowujacej informacje o
	 * lokalizacji statkow i oddanych strzalach.
	 * 
	 * @return dwuwymiarowa tablica zapisujaca wartosci Enum typu
	 *         {@see BoardState}
	 */
	public BoardState[][] getBoardState() {
		return board;
	}

	/**
	 * Metoda ustawiajaca stan pojedynczego pola tablicy {@see Board} poprzez
	 * przypisanie wartosci typu {@see BoardState}
	 * 
	 * @param x wspolrzedna X pola strzalu
	 * @param y wspolrzedna Y pola strzalu
	 * @param state parametr typu wyliczeniowego {@see BoardState} okreslajacy stan pola strzalu
	 */
	public void setBoardCell(int x, int y, BoardState state) {
		board[x][y] = state;
	}

	/**
	 * Metoda zwracajaca stan pojedynczego pola tablicy {@see Board}.
	 * 
	 * @param x wspolrzedna X pola strzalu
	 * @param y wspolrzedna Y pola strzalu
	 * @return wartosc Enum typu {@see BoardState}
	 */
	public BoardState getBoardCell(int x, int y) {
		return board[x][y];
	}

	/**
	 * Metoda resetujaca plansze i ustawiajaca wszystkie pola na puste
	 * 
	 */
	public void resetBoard() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				board[i][j] = BoardState.PUSTE_POLE;
			}
		}
	}

	/**
	 * Metoda obslugujaca oddany strzal
	 * 
	 * @param x wspolrzedna X pola strzalu
	 * @param y wspolrzedna Y pola strzalu
	 * 
	 * @return Stan planszy po oddaniu strzalu
	 */
	public BoardState shot(int x, int y) {
		try {
			if (board[x][y] == BoardState.PUSTE_POLE) {
				board[x][y] = BoardState.PUDLO;
				viewController.setTextAreaLogi("Pudlo!");
				return BoardState.PUDLO;
			} else {
				board[x][y] = BoardState.STATEK_TRAFIONY;
				if (isSunk(x, y)) {
					board[x][y] = BoardState.STATEK_ZATOPIONY;
					setSunk(x, y);
					viewController.setTextAreaLogi("Trafiony zatopiony!");
					return BoardState.STATEK_ZATOPIONY;
				}
				viewController.setTextAreaLogi("Trafiony!");
				return BoardState.STATEK_TRAFIONY;
			}
		}

		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Metoda pomocnicza sprawdzająca czy po strzale statek zostal zatopiony
	 * 
	 * @param x wspolrzedna X pola strzalu
	 * @param y wspolrzedna Y pola strzalu
	 * @return czy statek zostal zatopiony
	 */
	private boolean isSunk(int x, int y) {
		try {
			int t = x;
			while (--t >= 0 && (board[t][y] == BoardState.STATEK || board[t][y] == BoardState.STATEK_TRAFIONY))
				if (board[t][y] == BoardState.STATEK)
					return false;
			t = x;
			while (++t < 11 && (board[t][y] == BoardState.STATEK || board[t][y] == BoardState.STATEK_TRAFIONY))
				if (board[t][y] == BoardState.STATEK)
					return false;
			t = y;
			while (--t >= 0 && (board[x][t] == BoardState.STATEK || board[x][t] == BoardState.STATEK_TRAFIONY))
				if (board[x][t] == BoardState.STATEK)
					return false;
			t = y;
			while (++t < 11 && (board[x][t] == BoardState.STATEK || board[x][t] == BoardState.STATEK_TRAFIONY))
				if (board[x][t] == BoardState.STATEK)
					return false;
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Metoda oznaczajaca statek jako zatopiony
	 * 
	 * @param x wspolrzedna X pola strzalu
	 * @param y wspolrzedna Y pola strzalu
	 */
	public void setSunk(int x, int y) {
		int t = x;
		while (t >= 0 && (board[t][y] == BoardState.STATEK_TRAFIONY || board[t][y] == BoardState.STATEK_ZATOPIONY)) {
			board[t][y] = BoardState.STATEK_ZATOPIONY;
			for (int i = -1; i < 2; i++) {
				for (int j = -1; j < 2; j++) {
					if (board[limitValue(t + i)][limitValue(y + j)] == BoardState.PUSTE_POLE)
						board[limitValue(t + i)][limitValue(y + j)] = BoardState.PUDLO;
				}
			}
			--t;
		}
		t = x;
		while (t < 11 && (board[t][y] == BoardState.STATEK_TRAFIONY || board[t][y] == BoardState.STATEK_ZATOPIONY)) {
			board[t][y] = BoardState.STATEK_ZATOPIONY;
			for (int i = -1; i < 2; i++) {
				for (int j = -1; j < 2; j++) {
					if (board[limitValue(t + i)][limitValue(y + j)] == BoardState.PUSTE_POLE)
						board[limitValue(t + i)][limitValue(y + j)] = BoardState.PUDLO;
				}
			}
			++t;
		}
		t = y;
		while (t >= 0 && (board[x][t] == BoardState.STATEK_TRAFIONY || board[x][t] == BoardState.STATEK_ZATOPIONY)) {
			board[x][t] = BoardState.STATEK_ZATOPIONY;
			for (int i = -1; i < 2; i++) {
				for (int j = -1; j < 2; j++) {
					if (board[limitValue(x + i)][limitValue(t + j)] == BoardState.PUSTE_POLE)
						board[limitValue(x + i)][limitValue(t + j)] = BoardState.PUDLO;
				}
			}
			--t;
		}
		t = y;
		while (t < 11 && (board[x][t] == BoardState.STATEK_TRAFIONY || board[x][t] == BoardState.STATEK_ZATOPIONY)) {
			board[x][t] = BoardState.STATEK_ZATOPIONY;
			for (int i = -1; i < 2; i++) {
				for (int j = -1; j < 2; j++) {
					if (board[limitValue(x + i)][limitValue(t + j)] == BoardState.PUSTE_POLE)
						board[limitValue(x + i)][limitValue(t + j)] = BoardState.PUDLO;
				}
			}
			++t;
		}
	}

	// ograniczenie współrzędnych do przedziału 0-10.
	private int limitValue(int param) {
		if (param < 0)
			return 0;
		else if (param > 10)
			return 10;
		return param;
	}
}
