import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.util.*;

public class FilmSet extends Room{
    private int maxShotCounters;
    private int currentShotCounters;
    private Card cardOnSet;

    private List<Role> offCardRoles;

    //For GUI version
    private Map<Integer, int[]> shotCounterArea;

    public FilmSet(String roomName) {
        super(roomName);

        this.cardOnSet = null;
        this.offCardRoles = new ArrayList<Role>();
        this.shotCounterArea = new HashMap<Integer, int[]>();
    }

    public FilmSet(int maxShotCounters, String roomName) {
        super(roomName);
        this.maxShotCounters = maxShotCounters;
        this.currentShotCounters = maxShotCounters;

        this.offCardRoles = new ArrayList<Role>();
        this.shotCounterArea = new HashMap<Integer, int[]>();
    }

    public Role getRoleByName(String roleName) {
        if(cardOnSet == null)
            return null;
        for(Role r : offCardRoles) {
            if(r.getName().equals(roleName))
                return r;
        }

        for(Role r : cardOnSet.getRoles()) {
            if(r.getName().equals(roleName))
                return r;
        }

        return null;
    }
    
    public void addRoleToSet(Role role){
        this.offCardRoles.add(role);
    }
    public String getFilmSetName() {
        return super.getRoomName();
    }

    public int getShotCounters() {
        return currentShotCounters;
    }

    public List<Role> getOffCardRoles() {
        return offCardRoles;
    }
    
    public void addShotCounterArea(int number, int[] shotCounterArea) {
        this.shotCounterArea.put(number, shotCounterArea);
    }

    public int[] getShotCounterArea()
    {
        if(currentShotCounters >= 0)
        {
            return shotCounterArea.get(maxShotCounters - currentShotCounters);
        }
        else
            return null;
    }
      public boolean checkShotCounters(int mouseX, int mouseY) {
    	for (Map.Entry<Integer, int[]> entry : shotCounterArea.entrySet()) {
            int[] value = entry.getValue();
            if(detectMouseCollision(mouseX, mouseY, value))
            	return true;
        }
    	return false;
    }

    public boolean checkRehearse(int mouseX, int mouseY) {
    	return detectMouseCollision(mouseX, mouseY, super.getArea());
    }
	
    private boolean detectMouseCollision(int mouseX, int mouseY,int[] area){
        int wStart = area[0];
        int wEnd = area[0] + area[3];
        int hStart = area[1];
        int hEnd = area[1] + area[2];
        return (mouseX >= wStart && mouseX <= wEnd && mouseY > hStart && mouseY < hEnd);
    }

    public void setMaxShotCounters(int maxShotCounters) {
        this.maxShotCounters = maxShotCounters;
    }

    public void removeShotCounter() {
        currentShotCounters--;
    }

    public void resetShotCounters() {
        currentShotCounters = maxShotCounters;
    }

    public void addNeighbor(String neighbor) {
        super.addNeighbor(neighbor);
    }

    public void setCardOnSet(Card card) {
        cardOnSet = card;
        for(Role r : offCardRoles)
            r.setCardOnSet(card);
    }

    public void setArea(int[] area) {
        super.setArea(area);
    }

    public Card getCardOnSet() {
        return cardOnSet;
    }
}
