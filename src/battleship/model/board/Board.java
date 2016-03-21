package battleship.model.board;

import java.awt.Point;
import java.io.IOException;
import battleship.gui.game.GameServerViewController;
import battleship.model.server.ServerProcedure.Procedure;
import battleship.model.user.Player;

/*====== Model planszy przechowującej stany poszczegulnych pól gry=========
 * 
 * @author Paweł Czernek
 * 
 */

public class Board {
	private BoardState[][] board = new BoardState[11][11];
	private Player boardOwner = null;
	private int iloscStatkow = 8;
	private GameServerViewController serverViewController;
	
	public Board() {
	}
	
	public void setViewControllerReference(GameServerViewController controller){
		this.serverViewController = controller;
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

	private boolean czyPunktWstaw = false; // parametr okreslający czy w obecnej
											// iteracji metody wstawiany punkt
											// początkowy czy punkt wskazujący
											// kierunek
	private Point poczatek; // początek statku

	// Metoda rozmieszczająca statki na planszy
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
				

				if (!czyPunktWstaw) {
					poczatek = new Point(x, y);
					if(!czyJestStatekWsasiedztwie(poczatek)){
						board[poczatek.x][poczatek.y] = BoardState.STATEK;
						czyPunktWstaw = true;
						serverViewController.setTextAreaLogi("Wskaz kierunek.");
					}else{
						serverViewController.setTextAreaLogi("Pozycja za blisko istniejacego statku! Wstaw jeszcze raz.");
						return;
					}
				} else {
						punktKierunku = new Point(x, y);
						board[poczatek.x][poczatek.y] = BoardState.PUSTE_POLE;
						
						if(poczatek.x == punktKierunku.x){
							if(punktKierunku.y-poczatek.y>0){
								koniec = new Point(poczatek.x, poczatek.y+dlugosc-1);
								if(koniec.y>10){
									serverViewController.setTextAreaLogi("Statek wychodzi poza plansze, rozmiesc jeszcze raz");
									//board[poczatek.x][poczatek.y] = BoardState.PUSTE_POLE;
									czyPunktWstaw = false;
									koniec = null;
									return;
								}
								for(int d=0; d<dlugosc;d++){
									//board[poczatek.x][poczatek.y] = BoardState.PUSTE_POLE;
									if(czyJestStatekWsasiedztwie(new Point(poczatek.x, poczatek.y+d))){
										serverViewController.setTextAreaLogi("Za blisko innego statku, rozmiesc jeszcze raz");
										czyPunktWstaw = false;
										koniec = null;
										return;
									}
								}
								for(int d=0; d<dlugosc;d++){
									board[poczatek.x][poczatek.y+d] = BoardState.STATEK;
								}
							} else {
								koniec = new Point(poczatek.x, poczatek.y-dlugosc+1);
								if(koniec.y<0){
									serverViewController.setTextAreaLogi("Statek wychodzi poza plansze, rozmiesc jeszcze raz");
									czyPunktWstaw = false;
									koniec = null;
									return;
								}
								for(int d=0; d<dlugosc;d++){
									//board[poczatek.x][poczatek.y] = BoardState.PUSTE_POLE;
									if(czyJestStatekWsasiedztwie(new Point(poczatek.x, poczatek.y-d))){
										serverViewController.setTextAreaLogi("Za blisko innego statku, rozmiesc jeszcze raz");
										czyPunktWstaw = false;
										koniec = null;
										return;
									}
								}
								for(int d=0; d<dlugosc;d++){
									board[poczatek.x][poczatek.y-d] = BoardState.STATEK;
								}
							}
							czyPunktWstaw = false;
							
						} else if (poczatek.y == punktKierunku.y){
							if(punktKierunku.x-poczatek.x>0){
								koniec = new Point(poczatek.x+dlugosc-1, poczatek.y);
								if(koniec.x>10){
									serverViewController.setTextAreaLogi("Statek wychodzi poza plansze, rozmiesc jeszcze raz");
									czyPunktWstaw = false;
									koniec = null;
									return;
								}
								for(int d=0; d<dlugosc;d++){
									//board[poczatek.x][poczatek.y] = BoardState.PUSTE_POLE;
									if(czyJestStatekWsasiedztwie(new Point(poczatek.x+d, poczatek.y))){
										serverViewController.setTextAreaLogi("Za blisko innego statku, rozmiesc jeszcze raz");
										czyPunktWstaw = false;
										koniec = null;
										return;
									}
								}
								for(int d=0; d<dlugosc;d++){
									board[poczatek.x+d][poczatek.y] = BoardState.STATEK;
								}
							} else {
								koniec = new Point(poczatek.x-dlugosc+1, poczatek.y);
								if(koniec.x<0){
									serverViewController.setTextAreaLogi("Statek wychodzi poza plansze, rozmiesc jeszcze raz");
									czyPunktWstaw = false;
									koniec = null;
									return;
								}
								for(int d=0; d<dlugosc;d++){
									//board[poczatek.x][poczatek.y] = BoardState.PUSTE_POLE;
									if(czyJestStatekWsasiedztwie(new Point(poczatek.x-d, poczatek.y))){
										serverViewController.setTextAreaLogi("Za blisko innego statku, rozmiesc jeszcze raz");
										czyPunktWstaw = false;
										koniec = null;
										return;
									}
								}
								for(int d=0; d<dlugosc;d++){
									board[poczatek.x-d][poczatek.y] = BoardState.STATEK;
								}
							}
						}
						czyPunktWstaw = false;
						iloscStatkow--;
						if (iloscStatkow > 7) dlugosc = 5;
						else if (iloscStatkow > 6) dlugosc = 4;
						else if (iloscStatkow > 4) dlugosc = 3;
						else dlugosc = 2;
						if(iloscStatkow>0){
							serverViewController.setTextAreaLogi("Wskaz punkt poczatkowy "+dlugosc+" masztowca.");
						} else{
							serverViewController.setTextAreaLogi("Rozmieszczanie statkow zakonczone.");
							serverViewController.setProcedure(Procedure.READY_TO_START);
						}
						
				}
			}
		}
	
	//funkcja sprawdzająca  kolizje przy rozkładzie statków
	private boolean czyJestStatekWsasiedztwie(Point point) {
		Point p = new Point(point.x, point.y);
		for(int i=-1; i<2;i++){
			for(int j=-1; j<2; j++){
				if(board[round(p.x+i)][round(p.y+j)] == BoardState.STATEK || board[round(p.x+i)][round(p.y)] == BoardState.STATEK_TRAFIONY) return true;
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

