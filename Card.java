import java.util.List;
import java.util.ArrayList;

public class Card {
	private int budget;
	private List<Role> onCardRoles;
	private String description;

	private boolean hasPlayerOnCard;

	//For GUI version
	private String sceneName;
	private String img;
	private int sceneNumber;

	public Card(int budget, String description, String sceneName, String img, int sceneNumber) {
		this.hasPlayerOnCard = false;
		this.budget = budget;
		this.description = description;
		this.sceneName = sceneName;
		this.img = img;
		this.sceneNumber = sceneNumber;

		this.onCardRoles = new ArrayList<Role>();
	}

	public int getBudget() {
		return budget;
	}

	public boolean getHasPlayer() {
		return hasPlayerOnCard;
	}
	
	public List<Role> getRoles() {
		return onCardRoles;
	}

	public int getNumberOfRoles() {
		return onCardRoles.size();
	}
	
	public String getDescription() {
		return description;
	}

	public void addRoleToCard(Role role) {
		onCardRoles.add(role);
	}

	public String getImgName() {
		return img;
	}

}
