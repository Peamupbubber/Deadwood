public class Player {
	private int dollars;
	private int credits;
	private int practiceChips;
	private int rank;
	private String name;
	private String color;
	
	private Dice playerDice;
	private Role currentRole;

	private Board gameBoard;
	private Room currentRoom;

	private boolean turnTaken;
	private boolean hasMoved;

	public Player(int initialRank, String color, int initialCredits, String name, Board gameBoard) {
		this.dollars = 0;
		this.practiceChips = 0;

		this.rank = initialRank;
		this.color = color;
		this.credits = initialCredits;

		this.playerDice = new Dice(rank);
		this.currentRole = null;

		this.gameBoard = gameBoard;
		this.currentRoom = this.gameBoard.getRoomByName("trailer");

		this.name = name;
		turnTaken = false;
		hasMoved = false;
	}

	public boolean move(String roomName) {
		if(currentRoom.isAdjacentTo(roomName) && currentRole == null && getMoved() == false) {
			currentRoom = gameBoard.getRoomByName(roomName);
			hasMoved();
			return true;
		}
		else
			return false;
	}
	
	/* if role is not taken, change the current role as well as other necessary variables*/
	//might need to change Role to string based on input
	public boolean takeRole(String roleName) {
		if(currentRoom.getRoomName().equals("trailer") || currentRoom.getRoomName().equals("office"))
			return false;

		Role role = ((FilmSet)currentRoom).getRoleByName(roleName);

		if(role == null) {
			return false;
		}

		if(role.isTaken() == false && role.getMinRank() <= rank) {
			currentRole = role;
			currentRole.setCurrentPlayer(this);
			hasTakenTurn();
			return true;
		}
		else
			return false;
	}
	
	//return null on failure, return payment on success
	public String act() {
		if(currentRole == null)
			return null;
		String payment = null;

		Dice die = new Dice();
		die.roll(practiceChips);

		if(die.getTopValue() >= currentRole.getCardOnSet().getBudget()) {
			((FilmSet)currentRoom).removeShotCounter();
			if(currentRole.isOnCard()) {
				addCredits(2);
				payment = "2 credits";
			}
			else {
				addCredits(1);
				addDollars(1);
				payment = "1 credit and 1 dollar";
			}
		}
		else if(!currentRole.isOnCard()) {
			addDollars(1);
			payment = "1 dollar";
		}
		hasTakenTurn();
		return payment;
	}

	/* return true and update practice chips if rehearsal is permitted*/
	public boolean rehearse() {
		if(practiceChips + 1 < currentRole.getCardOnSet().getBudget()) {
			practiceChips++;
			hasTakenTurn();
			return true;
		}
		else
			return false;
	}
	
	/* upgrade if possible */
	public boolean upgrade(int newRank, int payment, boolean payedWithDollars) {
		if(payedWithDollars) {
			if(payDollars(payment)) {
				rank = newRank;
				playerDice.setTopValue(newRank);
				return true;
			}
			else
				return false;
		}
		else {
			if(payCredits(payment)) {
				rank = newRank;
				playerDice.setTopValue(newRank);
				return true;
			}
			else
				return false;
		}
	}

	public boolean upgradable(int newRank, int payment) {
		if(!currentRoom.getRoomName().equals("office") || payment < 0 || newRank <= rank)
			return false;
		else
			return true;
	}

	public void addDollars(int dollarsToAdd) {
		dollars += dollarsToAdd;
	}

	public void addCredits(int creditsToAdd) {
		credits += creditsToAdd;
	}

	/* payment methods: return false if there are insufficient
	 * funds, return true and deduct payment if funds are
	 * sufficinet
	*/ 
	public boolean payDollars(int payment) {
		if(payment > dollars)
			return false;
		else {
			dollars -= payment;
			return true;
		}
	}

	public boolean payCredits(int payment) {
		if(payment > credits)
			return false;
		else {
			credits -= payment;
			return true;
		}
	}

	//Called at the end of each day
	public void resetPlayer() {
		currentRoom = gameBoard.getRoomByName("trailer");
		currentRole = null;
		practiceChips = 0;
	}

	public void removeRole() {
		currentRole = null;
	}

	public int getPracticeChips() {
		return practiceChips;
	}

	public void resetPracticeChips(){
		practiceChips = 0;
	}

	public String getName() {
		return name;
	}

	public int getDollars() {
		return dollars;
	}

	public int getCredits() {
		return credits;
	}

	public int getRank() {
		return rank;
	}

	public Role getCurrentRole() {
		return currentRole;
	}

	public Room getRoom() {
		return currentRoom;
	}

	public Board getBoard() {
		return gameBoard;
	}

	private void hasTakenTurn() {
		turnTaken = true;
	}

	public void resetTakenTurn() {
		turnTaken = false;
	}

	public boolean getTurnTaken() {
		return turnTaken;
	}

	private void hasMoved() {
		hasMoved = true;
	}

	public void resetMoved() {
		hasMoved = false;
	}

	public boolean getMoved() {
		return hasMoved;
	}

	public boolean hasRole() {
		return currentRole != null;
	}

	public String getColor() {
		return color;
	}

	public int[] getOnCardRoleLocation() {
		int[] area = {currentRole.getArea()[0] + currentRoom.getArea()[0], currentRole.getArea()[1] + currentRoom.getArea()[1],currentRole.getArea()[2], currentRole.getArea()[3]};
		return area;
	}
	
	public int[] getOffCardRoleLocation() {
		return currentRole.getArea();
	}

	public boolean roleOnCard() {
		if(((FilmSet)currentRoom).getOffCardRoles().contains(currentRole) == false) {
			return true;
		}
		else {
			return false;
		}
	}
}
