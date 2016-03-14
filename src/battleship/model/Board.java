package battleship.model;


import java.awt.Point;
import java.io.IOException;
import battleship.application.MainAppFactory;
import battleship.gui.game.GameServerViewController;



/*====== Model planszy przechowującej stany poszczegulnych pól gry=========
 * 
 * @author Paweł Czernek
 * 
 */
public class Board {
	
	private BoardState[][] board = new BoardState[10][10];
	private Player boardOwner = null;
	private int iloscStatkow = 9;
	private GameServerViewController gameServerViewController;
	private MainAppFactory factory;
	
	
	public Board(){
	}
	
	public void setMainFactory(MainAppFactory factory){
		this.factory = factory;
	}
	
	// Metoda resetująca planszę do stanu przed rozmieszczeniem statków 
	public void resetBoard(){
		for(int i=0; i<board.length; i++){
			for(int j=0; j<board[i].length; j++){
				board[i][j] = BoardState.PUSTE_POLE;
			}
		}
	}
	
	enum Kierunek{
		POZIOMO,
		PIONOWO
	}
	
	// Metoda rozmieszczająca statki na planszy
	public void locateShips(){
		
		boolean ok;	//parametr określający czy statki zostały prawidłowo rozmieszczone 
		Point poczatek; //początek statku
		Point koniec; //koniec statku
		int x, y;
		Kierunek kierunek = null;  //w którym kierunku ma być umieszczony statek
		
		gameServerViewController = factory.getGameServerViewController();
		
		for(int i=0; i<iloscStatkow; i++){
			int dlugosc = 5;
			if(i>0) dlugosc = 4;
			if(i>1) dlugosc = 3;
			if(i>3) dlugosc = 2;
			if(i>5) dlugosc = 1;
			
			do {
				System.out.println("Podaj punkt początkowy "+dlugosc+" masztowca.");
				x = (int) gameServerViewController.button01.getInsets().getLeft();
				y = (int) gameServerViewController.button01.getInsets().getTop();
				poczatek = new Point(x, y);
					
				System.out.println("Podaj kierunek ustawienia statku");
				x = (int) gameServerViewController.button01.getInsets().getLeft();
				y = (int) gameServerViewController.button01.getInsets().getTop();
				koniec = new Point(x, y);
				
				do{
					if(poczatek.getX() == koniec.getX()){
						kierunek = Kierunek.POZIOMO;
					}else if(poczatek.getY() == koniec.getY()){
						kierunek = Kierunek.PIONOWO;
					}else kierunek = null;
				}while(kierunek == null);
				
					if(kierunek == Kierunek.POZIOMO){
						koniec = new Point(x + dlugosc, y);
					}else{
						koniec = new Point(x, y + dlugosc);
					}
				ok = true;
				
				try{
					
				}catch(Exception e){
					// pole wychodzi poza tablicę dwuwymiarową
					ok = false;
				}
				
				//sprawdzenie czy nie ma w tym miejscu statku
				petla: for (int m = poczatek.x; m <= koniec.x; m++)
					for (int n = poczatek.y; n <= koniec.y; n++)
						if (board[m][n] != BoardState.PUSTE_POLE) {
							ok = false;
							break petla;
						}
			} while (!ok);

			for (int d = 0; d < dlugosc; d++)
				if (kierunek == Kierunek.POZIOMO) {
					board[x + d][y] = BoardState.STATEK;
				} else {
					board[x][y + d] = BoardState.STATEK;
			}
		}
	}
}


