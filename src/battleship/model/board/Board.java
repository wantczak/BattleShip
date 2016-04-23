package battleship.model.board;

import java.awt.Point;
import battleship.gui.game.GameViewController;
import battleship.model.server.ServerProcedure.Procedure;


/**
 * Model planszy przechowującej stany poszczegulnych pól gry
 * 
 * @author Paweł Czernek
 * 
 */

public class Board {
	private BoardState[][] board = new BoardState[11][11]; //Tablica ze stanami planszy
	private int iloscStatkow = 8;
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
			return BoardState.PUDLO;
		} else {
			board[x][y] = BoardState.STATEK_TRAFIONY;
			if(isSunk(x,y)){
				board[x][y] = BoardState.STATEK_ZATOPIONY;
				setSunk(x, y);
				return BoardState.STATEK_ZATOPIONY;
			}
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
					if (board[round(t+i)][round(y+j)] == BoardState.PUSTE_POLE)
					board[round(t+i)][round(y+j)] = BoardState.PUDLO;
				}
			}
			--t;
		}
		t = x;
		while (t < 11 && (board[t][y] == BoardState.STATEK_TRAFIONY || board[t][y] == BoardState.STATEK_ZATOPIONY)){
			board[t][y] = BoardState.STATEK_ZATOPIONY;
			for(int i=-1; i<2;i++){
				for(int j=-1; j<2; j++){
					if (board[round(t+i)][round(y+j)] == BoardState.PUSTE_POLE)
					board[round(t+i)][round(y+j)] = BoardState.PUDLO;
				}
			}
			++t;
		}
		t = y;
		while (t >= 0 && (board[x][t] == BoardState.STATEK_TRAFIONY || board[x][t] == BoardState.STATEK_ZATOPIONY)){
			board[x][t] = BoardState.STATEK_ZATOPIONY;
			for(int i=-1; i<2;i++){
				for(int j=-1; j<2; j++){
					if (board[round(x+i)][round(t+j)] == BoardState.PUSTE_POLE)
					board[round(x+i)][round(t+j)] = BoardState.PUDLO;
				}
			}
			--t;
		}
		t = y;
		while (t < 11 && (board[x][t] == BoardState.STATEK_TRAFIONY || board[x][t] == BoardState.STATEK_ZATOPIONY)){
			board[x][t] = BoardState.STATEK_ZATOPIONY;
			for(int i=-1; i<2;i++){
				for(int j=-1; j<2; j++){
					if (board[round(x+i)][round(t+j)] == BoardState.PUSTE_POLE)
					board[round(x+i)][round(t+j)] = BoardState.PUDLO;
				}
			}
			++t;
		}
	}


	private boolean czyPunktWstaw = false; // parametr okreslający czy w obecnej
											// iteracji metody wstawiany punkt
											// początkowy czy punkt wskazujący
											// kierunek
	private Point poczatek; // początek statku
	
	/**
	 * Metoda rozmieszczająca statki na planszy
	 * 
	 * @param x współrzędna X pola statku
	 * @param y współrzędna Y pola statku
	 */
	public void locateShips(int x, int y) {

		Point punktKierunku; // punkt wskazania w którym kierunku układać statek
		Point koniec = null; // punkt końcowy statku
		boolean czyRozmieszczony; // parametr określający czy statki zostały
									// prawidłowo rozmieszczone
		int dlugosc; // dlugość wstawianego statku
		
			if (iloscStatkow > 0) {
				if (iloscStatkow > 7)
					dlugosc = 5;
				else if (iloscStatkow > 6)
					dlugosc = 4;
				else if (iloscStatkow > 4)
					dlugosc = 3;
				else dlugosc = 2;
				

				if (!czyPunktWstaw) { //Wstawianie punktu początkowego dla statku
					poczatek = new Point(x, y);
					if(!czyJestStatekWsasiedztwie(poczatek)){
						board[poczatek.x][poczatek.y] = BoardState.STATEK;
						czyPunktWstaw = true;
						viewController.setTextAreaLogi("Wskaz kierunek.");
					}else{
						viewController.setTextAreaLogi("Pozycja za blisko istniejacego statku! Wstaw jeszcze raz.");
						return;
					}
				} else {  //Wstawianie punktu kierunkowego w którym ma być rozmieszczany statek
						punktKierunku = new Point(x, y);
						board[poczatek.x][poczatek.y] = BoardState.PUSTE_POLE; 	// tymczasowe usunięcie punktu
																				// aby nie powodował kolizji s 
																				// samym sobą w funkcji sprawdzania kolizji statków
						
						if(poczatek.x == punktKierunku.x){
							if(punktKierunku.y-poczatek.y>0){
								koniec = new Point(poczatek.x, poczatek.y+dlugosc-1);
								if(koniec.y>10){
									viewController.setTextAreaLogi("Statek wychodzi poza plansze, rozmiesc jeszcze raz");
									czyPunktWstaw = false;
									koniec = null;
									return;
								}
								for(int d=0; d<dlugosc;d++){
									if(czyJestStatekWsasiedztwie(new Point(poczatek.x, poczatek.y+d))){
										viewController.setTextAreaLogi("Za blisko innego statku, rozmiesc jeszcze raz");
										czyPunktWstaw = false;
										koniec = null;
										return;
									}
								}
								for(int d=0; d<dlugosc;d++){
									board[poczatek.x][poczatek.y+d] = BoardState.STATEK;
								}
								czyPunktWstaw = false;
								iloscStatkow--;
							} else {
								koniec = new Point(poczatek.x, poczatek.y-dlugosc+1);
								if(koniec.y<0){
									viewController.setTextAreaLogi("Statek wychodzi poza plansze, rozmiesc jeszcze raz");
									czyPunktWstaw = false;
									koniec = null;
									return;
								}
								for(int d=0; d<dlugosc;d++){
									if(czyJestStatekWsasiedztwie(new Point(poczatek.x, poczatek.y-d))){
										viewController.setTextAreaLogi("Za blisko innego statku, rozmiesc jeszcze raz");
										czyPunktWstaw = false;
										koniec = null;
										return;
									}
								}
								for(int d=0; d<dlugosc;d++){
									board[poczatek.x][poczatek.y-d] = BoardState.STATEK;
								}
								czyPunktWstaw = false;
								iloscStatkow--;
							}
							
						} else if (poczatek.y == punktKierunku.y){
							if(punktKierunku.x-poczatek.x>0){
								koniec = new Point(poczatek.x+dlugosc-1, poczatek.y);
								if(koniec.x>10){
									viewController.setTextAreaLogi("Statek wychodzi poza plansze, rozmiesc jeszcze raz");
									czyPunktWstaw = false;
									koniec = null;
									return;
								}
								for(int d=0; d<dlugosc;d++){
									if(czyJestStatekWsasiedztwie(new Point(poczatek.x+d, poczatek.y))){
										viewController.setTextAreaLogi("Za blisko innego statku, rozmiesc jeszcze raz");
										czyPunktWstaw = false;
										koniec = null;
										return;
									}
								}
								for(int d=0; d<dlugosc;d++){
									board[poczatek.x+d][poczatek.y] = BoardState.STATEK;
								}
								czyPunktWstaw = false;
								iloscStatkow--;
							} else {
								koniec = new Point(poczatek.x-dlugosc+1, poczatek.y);
								if(koniec.x<0){
									viewController.setTextAreaLogi("Statek wychodzi poza plansze, rozmiesc jeszcze raz");
									czyPunktWstaw = false;
									koniec = null;
									return;
								}
								for(int d=0; d<dlugosc;d++){
									if(czyJestStatekWsasiedztwie(new Point(poczatek.x-d, poczatek.y))){
										viewController.setTextAreaLogi("Za blisko innego statku, rozmiesc jeszcze raz");
										czyPunktWstaw = false;
										koniec = null;
										return;
									}
								}
								for(int d=0; d<dlugosc;d++){
									board[poczatek.x-d][poczatek.y] = BoardState.STATEK;
								}
								czyPunktWstaw = false;
								iloscStatkow--;
							}
						} else {
							board[poczatek.x][poczatek.y] = BoardState.STATEK;
							czyPunktWstaw = true;
						}
						
						if (iloscStatkow > 7) dlugosc = 5;
						else if (iloscStatkow > 6) dlugosc = 4;
						else if (iloscStatkow > 4) dlugosc = 3;
						else dlugosc = 2;
						
						if(iloscStatkow>0 && !czyPunktWstaw){
							viewController.setTextAreaLogi("Wskaz punkt poczatkowy "+dlugosc+" masztowca.");
						} else if(iloscStatkow>0 && czyPunktWstaw){
							viewController.setTextAreaLogi("Wskaz kierunek.");
						} else {
							viewController.setTextAreaLogi("Rozmieszczanie statkow zakonczone.");
							viewController.setProcedure(Procedure.READY_TO_START);
						}
						
				}
			}
		} //Koniec rozmieszczania statków
	
	
	/**
	 * Metoda pomocnicza sprawdzająca kolizje między statkami
	 * 
	 * @param point
	 * @return
	 */
	private boolean czyJestStatekWsasiedztwie(Point point) {
		Point p = new Point(point.x, point.y);
		for(int i=-1; i<2;i++){
			for(int j=-1; j<2; j++){
				if(board[round(p.x+i)][round(p.y+j)] == BoardState.STATEK) return true;
			}
		}
		return false;
	}
	
	//ograniczenie współrzędnych do przedziału 0-10.
	private int round(int param){
		if(param<0) return 0;
		else if (param>10) return 10;
		return param;
	}
}

