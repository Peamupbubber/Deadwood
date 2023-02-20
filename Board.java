import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Board {
    private Room trailers;
    private Room castingOffice;
    private List<FilmSet> filmSets;
    private List<Card> deckOfCards;

    //Board creation will be called by the controller, board will store all info about rooms
    public Board(String boardFileName, String cardFileName) {
        filmSets = new ArrayList<FilmSet>();
        deckOfCards = new ArrayList<Card>();

        buildBoard(boardFileName);
        buildDeck(cardFileName);
    }
    
    //Parse board.xml and build the rooms for the game
    private void buildBoard(String fileName) {
        initializeTrailers();
        initializeCastingOffice();

        XMLBoardParser parser = new XMLBoardParser(fileName);
        filmSets = parser.getSetList();
    }

    //Parse cards.xml and build the deck of cards for the game, then shuffle the deck
    private void buildDeck(String filename) {
        XMLCardParser parser = new XMLCardParser(filename);
        deckOfCards = parser.getCardList();
        shuffleDeck();
    }

    private void initializeTrailers() {
        trailers = new Room("trailer");
        trailers.addNeighbor("Main Street");
        trailers.addNeighbor("Saloon");
        trailers.addNeighbor("Hotel");

        int[] area = {991, 248, 194, 201};
        trailers.setArea(area);
    }

    private void initializeCastingOffice() {
        castingOffice = new Room("office");
        castingOffice.addNeighbor("Train Station");
        castingOffice.addNeighbor("Ranch");
        castingOffice.addNeighbor("Secret Hideout");

        int[] area = {9, 459, 208, 209};
        castingOffice.setArea(area);
    }

    private void shuffleDeck() {
        Random rand = new Random();
        int n = deckOfCards.size();
        for(int i = 0; i < n; i++) {
            Card tempCard = deckOfCards.get(i);
            int randomIndex = rand.nextInt(n - i) + i;

            deckOfCards.set(i, deckOfCards.get(randomIndex));
            deckOfCards.set(randomIndex, tempCard);
        }
    }

    /* shot counters remaining on a filmset indicate the scene is active, meaning if all shot counters are
     * gone on a given set, that scene is inactive, and if more than one set is active, the day is still
     * going
    */
    public boolean dayIsActive() {
        int activeSets = 0;
        for(int i = 0; i < filmSets.size(); i++) {
            activeSets += filmSets.get(i).getShotCounters() > 0 ? 1 : 0;
        }

        return activeSets > 1;
    }

    public Room getRoomByName(String roomName) {
        if(roomName.equals("trailer"))
            return trailers;
        else if(roomName.equals("office"))
            return castingOffice;

        for(FilmSet f : filmSets) {
            if(f.getFilmSetName().equals(roomName))
                return f;
        }

        return null;
    }

    public List<Role> getOffCardRolesForRoom(String roomName) {
        for(FilmSet f : filmSets) {
            if(f.getFilmSetName().equals(roomName)) {
                return f.getOffCardRoles();
            }
        }
        return null;
    }

    public List<Role> getOnCardRolesForRoom(String roomName) {
        for(FilmSet f : filmSets) {
            if(f.getFilmSetName().equals(roomName)) {
                if(f.getCardOnSet() != null)
                    return f.getCardOnSet().getRoles();
            }
        }
        return null;
    }

    //deal out cards and reset shot counters
    public void resetBoard() {
        for(FilmSet f : filmSets) {
            for(Role r : f.getOffCardRoles()) {
                r.setCardOnSet(deckOfCards.get(0));
                r.setCurrentPlayer(null);
            }
            f.setCardOnSet(deckOfCards.get(0));
            deckOfCards.remove(0);

            f.resetShotCounters();
        }
    }

    public List<FilmSet> getFilmSets() {
        return filmSets;
    }

    ////////
    // Methods for checking mouse intersections

	public String checkRooms(int mouseX, int mouseY, Room currentRoom) {
        List<String> neighborNames = currentRoom.getNeighbors();
        List<Room> neighbors = new ArrayList<Room>();
        for(String n : neighborNames) {
            neighbors.add(getRoomByName(n));
        }

        for(Room room : neighbors) {
            if(room.getRoomName().equals("trailer")) {
                int[] trailerArea = this.trailers.getArea();
                if(detectMouseCollision(mouseX, mouseY, trailerArea))
                    return trailers.getRoomName();
            }
            else if(room.getRoomName().equals("office")) {
                int[] castingArea = this.castingOffice.getArea();
                if(detectMouseCollision(mouseX, mouseY, castingArea))
                    return castingOffice.getRoomName();
            }
            else {
                FilmSet f = (FilmSet)room;
                int [] area = f.getArea();
                boolean clickedInRole = false;
                if(detectMouseCollision(mouseX, mouseY, area)){
                    if(f.getCardOnSet() != null) {
                        for(Role r : f.getCardOnSet().getRoles()) {
                            int[] roleArea = r.getArea();
                            int[] roleAreaTemp = new int[4];
                            roleAreaTemp[0] = roleArea[0] + area[0];
                            roleAreaTemp[1] = roleArea[1] + area[1];
                            roleAreaTemp[2] = roleArea[2];
                            roleAreaTemp[3] = roleArea[3];
                            if(detectMouseCollision(mouseX, mouseY, roleAreaTemp))
                                clickedInRole = true;
                        }
                    }
                    if(!clickedInRole)
                        return f.getRoomName();
                }
            }
        }
        
        return null;
    }
	
	public String checkRoles(int mouseX, int mouseY, String roomName) {
        if(roomName.equals("trailer") || roomName.equals("office"))
            return null;

        FilmSet f = (FilmSet)getRoomByName(roomName);
        int[] area = f.getArea();
        for(Role r : f.getOffCardRoles()) {
            if(detectMouseCollision(mouseX, mouseY, r.getArea()))
                return r.getName();
        }
        if(f.getCardOnSet() != null) {
            for(Role r : f.getCardOnSet().getRoles()) {
                int[] roleArea = r.getArea();
                int[] roleAreaTemp = new int[4];
                roleAreaTemp[0] = roleArea[0] + area[0];
                roleAreaTemp[1] = roleArea[1] + area[1];
                roleAreaTemp[2] = roleArea[2];
                roleAreaTemp[3] = roleArea[3];
                if(detectMouseCollision(mouseX, mouseY, roleAreaTemp))
                    return r.getName();
            }
        }
        return null;
    }

    //Returns {rank to upgrade, (1 if paying with dollars, 0 if paying with credits)}
    public int[] checkUpgrade(int mouseX, int mouseY) {
        int[] returnInfo = new int[2];

        boolean clickedUpgrade = false;

        int[][] upgradeAreas_Dollars = {
                {98, 542, 19, 19},
                {98, 564, 19, 19},
                {98, 587, 19, 19},
                {98, 609, 19, 19},
                {98, 631, 19, 19}
        };

        int[][] upgradeAreas_Credits = {
                {147, 542, 19, 19},
                {147, 564, 19, 19},
                {147, 587, 19, 19},
                {147, 609, 19, 19},
                {147, 631, 19, 19}
        };

        for(int i = 0; i < 5; i++) {
            if(detectMouseCollision(mouseX, mouseY, upgradeAreas_Dollars[i])) {
                returnInfo[0] = i + 1;
                returnInfo[1] = 1;
                clickedUpgrade = true;
            }
            if(detectMouseCollision(mouseX, mouseY, upgradeAreas_Credits[i])) {
                returnInfo[0] = i + 1;
                returnInfo[1] = 0;
                clickedUpgrade = true;
            }
        }
        return clickedUpgrade ? returnInfo : null;
    }
	
    private boolean detectMouseCollision(int mouseX, int mouseY,int[] area){
        int wStart = area[0];

        int wEnd = area[0] + area[3];
        int hStart = area[1];
        int hEnd = area[1] + area[2];
        return (mouseX >= wStart && mouseX <= wEnd && mouseY > hStart && mouseY < hEnd);
    }
    ////////
}