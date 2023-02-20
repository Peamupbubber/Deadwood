public class Role {
	private int minRank;
	private boolean isOnCard;

	private Card cardOnSet;
	private Player currentPlayer;

	private String name;

	//For GUI version
	private String line;
	private int[] area;

	public Role(int minRank, boolean isOnCard, String name, String line, int[] area) {
		this.minRank = minRank;
		this.isOnCard = isOnCard;
		this.currentPlayer = null;
		this.name = name;
		this.line = line;
		this.area = area;
	}

	public int getMinRank() {
		return minRank;
	}

	public String getName(){
		return this.name;
	}
	
	public boolean isOnCard() {
		return isOnCard;
	}

	public boolean isTaken() {
		return currentPlayer != null;
	}

	public Card getCardOnSet() {
		return cardOnSet;
	}

	public void setCardOnSet(Card cardOnSet)
	{
		this.cardOnSet = cardOnSet;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public void flipIsOnCard(){
		this.isOnCard = !this.isOnCard;
	}

	public int[] getArea() {
		return area;
	}
}
