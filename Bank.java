public class Bank{
	/* perform the payout for the stars of the roles */
	/* this is a complex algorithm so the choices are thouroughly explained view comments */
	public void payStarBonus(Card card) {
		//roll dice equal to budget of card
		int budget = card.getBudget();
		int[] vals = new int[budget];
		for(int i = 0; i < budget; i++)
		{
			Dice dice = new Dice();
			vals[i] = dice.getTopValue();
		}

		//sort the values
		vals = insertionSort(vals);

		int wrap = card.getNumberOfRoles() - 1;
		int wrap_temp = wrap;
		budget--;

		//Assuming the roles are sorted by minRank (they should come out sorted when parsing xml files)
		while(budget >= 0) {
			//pay the player on the role the amount given by the sorted values
			//there might not be a player on that role so it must check for that
			if(card.getRoles().get(wrap).getCurrentPlayer() != null) {
				card.getRoles().get(wrap).getCurrentPlayer().addDollars(vals[budget]);
				card.getRoles().get(wrap).getCurrentPlayer().resetPracticeChips();
			}
			//either tick wrap down by one, or wrap it back to the next player
			wrap += wrap <= 0 ? wrap_temp : -1;

			//tick budget down to look at the next smallest value rolled
			budget--;
		}
	}

	public void payExtraBonus(Role role) {
			role.getCurrentPlayer().addDollars(role.getMinRank());
			role.getCurrentPlayer().resetPracticeChips();
	}

	public int getUpgradePrice(int rank, boolean payedWithDollars) {
		int price = -1;
		switch(rank) {
			case 2:
				price = payedWithDollars ? 4 : 5;
				break;
			case 3:
				price = payedWithDollars ? 10 : 10;
				break;
			case 4:
				price = payedWithDollars ? 18 : 15;
				break;
			case 5:
				price = payedWithDollars ? 28 : 20;
				break;
			case 6:
				price = payedWithDollars ? 40 : 25;
				break;
		}
		
		return price;
	}

	/* basic insertion sort method to sort the dice values array */
	private int[] insertionSort(int[] array) {
        for (int i = 1; i < array.length; i++) {
            for (int j = i; j > 0; j--) {
                if(array[j] < array[j - 1]) {
					int temp = array[j];
        			array[j] = array[j - 1];
        			array[j - 1] = temp;
                }
            }
        }

        return array;
    }
}
