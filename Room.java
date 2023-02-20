import java.util.List;
import java.util.ArrayList;

public class Room {
    private String roomName;
    private List<String> neighbors;
    private int[] area;

    public Room(String roomName) {
        this.roomName = roomName;
        neighbors = new ArrayList<String>();
    }

    public void addNeighbor(String room) {
        this.neighbors.add(room);
    }

    public void setArea(int[] area) {
        this.area = area;
    }

    public List<String> getRoom() {
        return this.neighbors;
    }

    public String getRoomName() {
        return roomName;
    }

    public List<String> getNeighbors() {
        return neighbors;
    }

    public boolean isAdjacentTo(String roomName) {
        for(String s: neighbors) {
            if(s.equals(roomName))
                return true;
        }
        return false;
    }

    public int[] getArea() {
        return area;
    }
}