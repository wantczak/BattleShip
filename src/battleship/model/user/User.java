package battleship.model.user;

/**
 * Klasa przechowujaca imie gracza
 * 
 * @author Wojciech Antczak
 *
 */
public class User {
	private String userName;
	
	public User(String userName){
		this.userName = userName;
	}
	
	public String getUserName(){
		return userName;
	}
}
