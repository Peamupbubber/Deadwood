import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;  
import java.awt.event.*;

public class Deadwood {
    private static Board board;
    private static Player[] players;
    private static int playerCount;
    private static Bank bank;
    private static View view;
    private static String[] playerNames = {"b", "c", "g", "o", "p", "r", "v", "y"};;

    public static void main(String args[]) {
        String boardFileName = "board.xml";
        String cardFileName = "cards.xml";
        board = new Board(boardFileName, cardFileName);
        bank = new Bank();
        view = new View();
        
        view.numPlayer();
        view.makeVisible();
        while(view.getNumPlayers() == 0)
        {
            System.out.print("");
        }
        
        //initialize players and check for count related cases
        playerCount = view.getNumPlayers();
        view.unload();

        int initialRank = playerCount < 7 ? 1 : 2;
        int initialCredits = playerCount == 5 ? 2 : (playerCount == 6 ? 4 : 0); 
        int daysToPlay = playerCount > 3 ? 4 : 3;

        players = new Player[playerCount];

        for(int i = 0; i < playerCount; i++) {
            players[i] = new Player(initialRank, playerNames[i], initialCredits, playerNames[i], board);
        }
        
        /* MAIN CONTROL LOOP FOR DEADWOOD */
        while(daysToPlay > 0) {
            startNewDay();
            while(board.dayIsActive()) {
                for(int p = 0; p < playerCount; p++) {
                    playerTurn(players[p]);
                    if(!board.dayIsActive())
                        break;
                }
            }
            daysToPlay--;
        }
        endGame();
    }

    //Wait for input from a mouse click, then do action based on that click
    private static void playerTurn(Player player) {
        player.resetTakenTurn();
        player.resetMoved();
        view.printOpeningSentence(player.getName());

        view.initBoard();
        view.makeVisible();
        boolean error = true;
        updateBoard();
        view.displayPlayerTurn("It is " + player.getName() + "'s Turn");

        while(!view.isTurnEnded(player.getName()) && !player.getTurnTaken()) {
            view.printBeginningOfRound();
            int[] mouseLocations = view.isMouseClicked();

            //GET A MOUSE CLICK
            while(mouseLocations == null){
                mouseLocations = view.isMouseClicked();
                System.out.print("");
            }

            //CHECK IF CLICKED ON A ROOM
            System.out.println("X: " + mouseLocations[0] + " Y: " + mouseLocations[1]);
            String roomName = board.checkRooms(mouseLocations[0],mouseLocations[1], player.getRoom());
            if(roomName != null){
                move(player,roomName);
                error = false;
                view.displayOutput("Move to " + player.getRoom().getRoomName() + " Succesfull");
            }

            //CHECK IF CLICKED ON A ROLE
            String roleName = board.checkRoles(mouseLocations[0],mouseLocations[1], player.getRoom().getRoomName());
            System.out.println(roleName);
            if(roleName != null && player.getCurrentRole() == null){
                takeRole(player,roleName);
                error = false;
                view.displayOutput("Role taken Succesfully");
            } 

            //CHECK IF ACTING OR REHEARSING OR UPGRADING
            roomName = player.getRoom().getRoomName();
            if(!roomName.equals("trailer") && !roomName.equals("office")) {
                if(((FilmSet)player.getRoom()).checkShotCounters(mouseLocations[0], mouseLocations[1])){
                    act(player);
                    error = false;
                    view.displayOutput("Act Succesfull");
                }
                else if(((FilmSet)player.getRoom()).checkRehearse(mouseLocations[0], mouseLocations[1]) && player.getCurrentRole() != null){
                    rehearse(player);
                    error = false;
                    view.displayOutput("Rehearsal Succesfull");
                }
            }
            else if(roomName.equals("office")) {
                int[] upgradeInfo = board.checkUpgrade(mouseLocations[0], mouseLocations[1]);
                if(upgradeInfo != null) {
                    upgrade(player, upgradeInfo[0], upgradeInfo[1] == 1 ? true : false);
                    error = false;
                }
            }

            //LOG NO INPUT GIVEN
            if(error){
                System.out.println("ERROR");
                view.displayOutput("No input detected!");
            }
            
            //IF PLAYER HAS TO WORK, DON'T LET THEM END THE TURN
            if(!player.getTurnTaken() && player.getCurrentRole() != null) {
                view.displayOutput("You have to work!");
                playerTurn(player);
            }
        }
    }

    //initiate final payouts then display winner!
    private static void endGame() {
        int[] finalScores = new int[playerCount];
        int indexOfMax = 0;
        String winnerName = "";

        for(int i = 0; i < playerCount; i++) {
            finalScores[i] = players[i].getCredits() + players[i].getDollars() + players[i].getRank() * 5;
        }

        for(int i = 0; i < playerCount; i++) {
            indexOfMax = finalScores[i] == Math.max(finalScores[indexOfMax], finalScores[i]) ? i : indexOfMax;
        }
        winnerName = players[indexOfMax].getName();

        for(int i = 0; i < playerCount; i++) {
            view.printFinalScore(players[i].getName(), finalScores[i]);
        }
        view.printWinner(winnerName, finalScores[indexOfMax]);
        view.displayOutput(winnerName + " has Won");
        
        view.closeScanner();
    }

    private static void updateBoard() {
        String [] colNames = {"Player", "Dollars", "Credits", "Practice Chips", "Score"};

        Object[][] data= new Object[players.length+1][5];
        data[0] = colNames;
        for(int i = 0; i < players.length; i++) {
            data[i+1][0] = players[i].getName();
            data[i+1][1] = players[i].getDollars();
            data[i+1][2] = players[i].getCredits();
            data[i+1][3] = players[i].getPracticeChips();
            data[i+1][4] = players[i].getCredits() + players[i].getDollars() + players[i].getRank() * 5;
        }

        JTable table = new JTable(data, colNames);
        view.displayTable(table);
    }

    private static void createBoardGUI() {
        view.initBoard();
        setBackCards();
        setPlayers();
        view.setPane();
        view.makeVisible();
    }

    private static void setPlayers() {
        int[] area = {941,148,194,201};
        for(int i = 0; i < playerCount; i++)
        {
            if(i%2==0)
            {
                area[1] = area[1] + 40;
                area[0] = 991;
            }
            view.setPlayer(players[i].getColor(), players[i].getRank(), area);
            area[0] = area[0] + 45;
        }
    }

    private static void setBackCards() {
        for(FilmSet f : board.getFilmSets()) {
            view.addBackCard(f.getArea());
        }
    }

    private static void flipCard(Player p) {
        view.flipCard(((FilmSet)p.getRoom()).getCardOnSet().getImgName(), p.getRoom().getArea());
        view.setPane();
        view.makeVisible();
    }

    /////////
    // methods for enacting player actions

    private static void move(Player p, String location) {
        if(p.move(location)) {
            view.moveValid(p.getName(), location);
            setPlayerInView(p);
            if(!location.equals("trailer") && !location.equals("office") && (((FilmSet)p.getRoom()).getCardOnSet() != null)) {
                flipCard(p);
            }
            view.setPane();
            view.makeVisible();
        }
        else if(p.getMoved()) {
            view.printAlreadyMoved(p.getName());
            view.displayOutput(p.getName() + " has already moved");
        }
        else {
            view.moveInvalid(location);
            view.displayOutput("Move is invalid");
        }
    }

    private static void takeRole(Player p, String role) {
        if(p.takeRole(role)) {
            if(p.roleOnCard()) {
                view.movePlayer(p.getColor(), p.getRank(), p.getOnCardRoleLocation());
            }
            else {
                view.movePlayer(p.getColor(), p.getRank(), p.getOffCardRoleLocation());
            }
            view.printRole(p.getName());
        }
        else
            view.printFailedRole(p.getName());
    }

    private static void act(Player p) {
        if(p.getTurnTaken() == true) {
            view.printDoneAction(p.getName());
            return;
        }

        String payout = p.act();

        if(payout != null && !payout.equals("1 dollar")) {
            view.printActing(p.getName(), payout);
            view.printShotCounter(p.getRoom().getRoomName(), ((FilmSet)p.getRoom()).getShotCounters());
            view.setShotCounter(((FilmSet)p.getRoom()).getShotCounterArea());
            if(((FilmSet)p.getRoom()).getShotCounters() == 0) {
                sceneCompleted((FilmSet)p.getRoom());
                view.printSceneComplete(p.getRoom().getRoomName());
            }
        }
        else {
            view.displayOutput("Failed Acting");
            view.printFailedActing(p.getName(), payout);
        }
    }

    private static void rehearse(Player p) {
        if(p.hasRole()) {
            if(p.rehearse()) {
                view.printRehearse(p.getName());
            }
            else {
                view.displayOutput("Can't rehearse");
                view.printFailedRehearse(p.getName());
            }
        }
        else {
            view.displayOutput("Failed rehearse");
            view.printFailedRehearseNoRole(p.getName());
        }
    }

    private static void upgrade(Player p, int rank, boolean payedWithDollars) {
        int payment = bank.getUpgradePrice(rank, payedWithDollars);
        if(p.upgradable(rank, payment)) {
            if(p.upgrade(rank, payment, payedWithDollars)) {
                view.printUpgrade(p.getName(), rank);
                setPlayerInView(p);
            }
            else {
                view.printFailedUpgrade(p.getName());
                view.displayOutput("failed upgrade");
            }
        }
        else {
            view.printUnupgradable(p.getName());
            view.displayOutput("You cannot upgrade");
        }
    }
    /////////

    public static int numPlayersInRoom(Room room) {
        int counter = 0;
        for(Player p : players) {
            if(p.getRoom().equals(room)) {
                counter++;
            }
        }
        System.out.println(counter);
        return counter;
    }
    
    public static void setPlayerInView(Player p) {
        int[] area = {p.getRoom().getArea()[0], p.getRoom().getArea()[1], p.getRoom().getArea()[2], p.getRoom().getArea()[3]};
        area[0] = area[0]+40*(numPlayersInRoom(p.getRoom())-1)-80;
        area[1] = area[1]+80;
        System.out.println(p.getRoom().getArea()[0]+","+p.getRoom().getArea()[1]+","+p.getRoom().getArea()[2]+","+p.getRoom().getArea()[3]);
        view.movePlayer(p.getColor(), p.getRank(), area);
    }

    //Reset the board and reset each player
    private static void startNewDay() {
        board.resetBoard();
        view.printNewDay();
        view.clearMap();
        for(int i = 0; i < playerCount; i++) {
            players[i].resetPlayer();
        }
        createBoardGUI();
    }

    public static void sceneCompleted(FilmSet filmSet) {
        bank.payStarBonus(filmSet.getCardOnSet());
        for(Role r : filmSet.getCardOnSet().getRoles()) {
            if(r.getCurrentPlayer() != null)
                r.getCurrentPlayer().removeRole();
        }

        if(filmSet.getCardOnSet() != null) {
            for(Role r : filmSet.getOffCardRoles()) {
                if(r.getCurrentPlayer() != null) {
                    bank.payExtraBonus(r);
                    r.getCurrentPlayer().removeRole();
                }
            }
        }

        view.removeCard(filmSet.getArea());
        filmSet.setCardOnSet(null);
    }
}
