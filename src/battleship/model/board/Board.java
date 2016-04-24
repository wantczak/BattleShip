package battleship.model.board;


import battleship.gui.game.GameViewController;
import battleship.model.board.ShipFactory.Kierunek;

/**
 * Model planszy przechowującej stany poszczegulnych pól gry
 * 
 * @author Paweł Czernek
 * 
 */

public class Board {
	
	private BoardState[][] board = new BoardState[11][11]; //Tablica ze stanami planszy
	private GameViewController viewController;
	
	public Board() {
		resetBoard();
	}
	
	// Ustawienie referencji do kontrolera widoku dla serwera
	public void setViewControllerReference(GameViewController controller){
		this.viewController = controller;
	}
	
	//metoda zwracająca stan tablicy
		public BoardState[][] getBoardState(){
			return board;
		}
	
	//Metoda ustawiająca pole tablicy
	public void setBoardCell(int x, int y, BoardState state){
		board[x][y] = state;
	}
	
	//metoda pobierająca pole tablicy
	public BoardState getBoardCell(int x, int y){
		return board[x][y];
	}
	
	/**
	 * Metoda resetująca planszę i ustawiająca wszystkie pola na puste
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
	 * Metoda obsługująca oddany strzał 
	 * 
	 * @param x współrzędna X pola strzału
	 * @param y współrzędna Y pola strzału
	 */
	public BoardState shot(int x, int y){
		if (board[x][y] == BoardState.PUSTE_POLE){
			board[x][y] = BoardState.PUDLO;
			viewController.setTextAreaLogi("Pudło!");
			return BoardState.PUDLO;
		} else {
			board[x][y] = BoardState.STATEK_TRAFIONY;
			if(isSunk(x,y)){
				board[x][y] = BoardState.STATEK_ZATOPIONY;
				setSunk(x, y);
				viewController.setTextAreaLogi("Trafiony zatopiony!");
				return BoardState.STATEK_ZATOPIONY;
			}
			viewController.setTextAreaLogi("Trafiony!");
			return BoardState.STATEK_TRAFIONY;
		}
	}
	
	/**
	 * Metoda pomocnicza sprawdzająca czy po strzale statek został zatopiony
	 * 
	 * @param x współrzędna X pola strzału
	 * @param y współrzędna Y pola strzału
	 * @return czy statek został zatopiony
	 */
	private boolean isSunk(int x, int y){
		int t = x;
		while (--t >= 0
				&& (board[t][y] == BoardState.STATEK || board[t][y] == BoardState.STATEK_TRAFIONY))
			if (board[t][y] == BoardState.STATEK)
				return false;
		t = x;
		while (++t < 11
				&& (board[t][y] == BoardState.STATEK || board[t][y] == BoardState.STATEK_TRAFIONY))
			if (board[t][y] == BoardState.STATEK)
				return false;
		t = y;
		while (--t >= 0
				&& (board[x][t] == BoardState.STATEK || board[x][t] == BoardState.STATEK_TRAFIONY))
			if (board[x][t] == BoardState.STATEK)
				return false;
		t = y;
		while (++t < 11
				&& (board[x][t] == BoardState.STATEK || board[x][t] == BoardState.STATEK_TRAFIONY))
			if (board[x][t] == BoardState.STATEK)
				return false;
		return true;
	}
	
	/**
	 * Metoda oznaczająca statek jako zatopiony
	 * 
	 * @param x współrzędna X pola strzału
	 * @param y współrzędna Y pola strzału
	 */
	public void setSunk(int x, int y) {
		int t = x;
		while (t >= 0 && (board[t][y] == BoardState.STATEK_TRAFIONY || board[t][y] == BoardState.STATEK_ZATOPIONY)){
			board[t][y] = BoardState.STATEK_ZATOPIONY;
			for(int i=-1; i<2;i++){
				for(int j=-1; j<2; j++){
					if (board[limitValue(t+i)][limitValue(y+j)] == BoardState.PUSTE_POLE)
					board[limitValue(t+i)][limitValue(y+j)] = BoardState.PUDLO;
				}
			}
			--t;
		}
		t = x;
		while (t < 11 && (board[t][y] == BoardState.STATEK_TRAFIONY || board[t][y] == BoardState.STATEK_ZATOPIONY)){
			board[t][y] = BoardState.STATEK_ZATOPIONY;
			for(int i=-1; i<2;i++){
				for(int j=-1; j<2; j++){
					if (board[limitValue(t+i)][limitValue(y+j)] == BoardState.PUSTE_POLE)
					board[limitValue(t+i)][limitValue(y+j)] = BoardState.PUDLO;
				}
			}
			++t;
		}
		t = y;
		while (t >= 0 && (board[x][t] == BoardState.STATEK_TRAFIONY || board[x][t] == BoardState.STATEK_ZATOPIONY)){
			board[x][t] = BoardState.STATEK_ZATOPIONY;
			for(int i=-1; i<2;i++){
				for(int j=-1; j<2; j++){
					if (board[limitValue(x+i)][limitValue(t+j)] == BoardState.PUSTE_POLE)
					board[limitValue(x+i)][limitValue(t+j)] = BoardState.PUDLO;
				}
			}
			--t;
		}
		t = y;
		while (t < 11 && (board[x][t] == BoardState.STATEK_TRAFIONY || board[x][t] == BoardState.STATEK_ZATOPIONY)){
			board[x][t] = BoardState.STATEK_ZATOPIONY;
			for(int i=-1; i<2;i++){
				for(int j=-1; j<2; j++){
					if (board[limitValue(x+i)][limitValue(t+j)] == BoardState.PUSTE_POLE)
					board[limitValue(x+i)][limitValue(t+j)] = BoardState.PUDLO;
				}
			}
			++t;
		}
	}
	//ograniczenie współrzędnych do przedziału 0-10.
	private int limitValue(int param){
		if(param<0) return 0;
		else if (param>10) return 10;
		return param;
	}
}

