package battleship.model;

import java.awt.Point;
import java.io.IOException;
import battleship.gui.game.GameServerViewController;

/*====== Model planszy przechowującej stany poszczegulnych pól gry=========
 * 
 * @author Paweł Czernek
 * 
 */
public class Board {

	private BoardState[][] board = new BoardState[11][11];
	private Player boardOwner = null;
	private int iloscStatkow = 8;

	public Board() {
	}
	
	public BoardState[][] getBoardState(){
		return board;
	}

	// Metoda resetująca planszę do stanu przed rozmieszczeniem statków
	public void resetBoard() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				board[i][j] = BoardState.PUSTE_POLE;
			}
		}
	}

	private enum Kierunek {
		PRAWO, LEWO, GORA, DOL
	}

	private boolean czyPunktWstaw = false; // parametr okreslający czy w obecnej
											// iteracji metody wstawiany punkt
											// początkowy czy punkt wskazujący
											// kierunek
	private Point poczatek; // początek statku

	// Metoda rozmieszczająca statki na planszy
	public void locateShips(int x, int y) {

		Point punktKierunku; // punkt wskazania w którym kierunku układać statek
		Point koniec = null; // punkt końcowy statku
		Kierunek kierunek; // w którym kierunku ma być umieszczony statek
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
			

			if (!czyPunktWstaw) {
				poczatek = new Point(x, y);
				board[poczatek.x][poczatek.y] = BoardState.STATEK;
				czyPunktWstaw = true;
			} else {
				
				do {
					punktKierunku = new Point(x, y);
					if ((poczatek.y == punktKierunku.y) && (poczatek.x < punktKierunku.x)) {
						kierunek = Kierunek.PRAWO;
					} else if ((poczatek.y == punktKierunku.y) && (poczatek.x > punktKierunku.x)) {
						kierunek = Kierunek.LEWO;
					} else if ((poczatek.x == punktKierunku.x) && (poczatek.y < punktKierunku.y)) {
						kierunek = Kierunek.DOL;
					} else if ((poczatek.x == punktKierunku.x) && (poczatek.y > punktKierunku.y)) {
						kierunek = Kierunek.GORA;
					} else
						kierunek = null;
				} while (kierunek == null);

				try {
					if (kierunek == Kierunek.PRAWO) {
						koniec = new Point(poczatek.x + dlugosc, poczatek.y);
					} else if (kierunek == Kierunek.LEWO) {
						koniec = new Point(poczatek.x - dlugosc, poczatek.y);
					}else if (kierunek == Kierunek.GORA) {
						koniec = new Point(poczatek.x, poczatek.y - dlugosc);
					} else if (kierunek == Kierunek.DOL) {
						koniec = new Point(poczatek.x, poczatek.y + dlugosc);
					}
					czyRozmieszczony = true;
					czyPunktWstaw = false;
					iloscStatkow--;

				} catch (Exception e) {
					// pole wychodzi poza tablicę dwuwymiarową
					czyRozmieszczony = false;
				}

				// sprawdzenie czy nie ma w tym miejscu statku
				petla: for (int m = poczatek.x; m <= koniec.x; m++){
							for (int n = poczatek.y; n <= koniec.y; n++){
								if (board[m][n] != BoardState.PUSTE_POLE) {
									czyRozmieszczony = false;
									break petla;
								}
							}
						}

				for (int d = 1; d < dlugosc; d++){
					if (kierunek == Kierunek.PRAWO) {
						board[(poczatek.x + d)][(poczatek.y)] = BoardState.STATEK;
					} else if(kierunek == Kierunek.LEWO){
						board[(poczatek.x - d)][(poczatek.y)] = BoardState.STATEK;
					} else if (kierunek == Kierunek.GORA){
						board[(poczatek.x)][(poczatek.y - d)] = BoardState.STATEK;
					}else if(kierunek == Kierunek.DOL){
						board[(poczatek.x)][(poczatek.y + d)] = BoardState.STATEK;
					}
				}
				kierunek = null;	
			}
		}
	}
}
