import java.util.Random;

public class Dice {
	private int topValue;
	
	public Dice() {
		roll(0);
	}
	
	public Dice(int topValue) {
		this.topValue = topValue;
	}
	
	public void setTopValue(int topValue) {
		this.topValue = topValue;
	}

	public int getTopValue() {
		return topValue;
	}

	public void roll(int praciticeChips) {
		Random rand = new Random();
		this.topValue = rand.nextInt(6) + 1 + praciticeChips;
	}
}
