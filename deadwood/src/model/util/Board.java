/**
 *  Written by Yulo Leake.
 *  Winter 2015, CSCI 345 (Object Oriented Design)
 *
 *  This Class is the Game Manager, keeps track of mostly everything.
 *  It is a singleton Class.
 */

package model.util;

import model.player.Player;
import model.role.ExtraRole;
import model.role.Role;
import model.role.StarringRole;
import model.room.*;
import model.room.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import view.BoardView;
import view.Resources;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Board {

    /*   View stuff   */
    private List<BoardObserver> observers;

    public interface BoardObserver{
        public void onNewDay(Board b);
        public void onNextTurn(Board b);
    }

    public void addObserver(BoardObserver o){
        if(observers == null){
            observers = new ArrayList<>();
        }
        observers.add(o);
    }

    private void notifyNewDay(){
        for(BoardObserver o : observers){
            o.onNewDay(this);
        }
    }

    private void notifyNextTurn(){
        for(BoardObserver o : observers){
            o.onNextTurn(this);
        }
    }

    public void setPlayerObserver(Player.PlayerObserver o){
        Player p = null;
        for(int i = 0; i < playerList.size(); i++){
            p = playerList.get(i);
            p.addObserver(o);
        }
    }

    public void setRoleObserver(Role.RoleObserver o){
        List<Role> roles = new ArrayList<>();
        for(Set s : setList){
            roles.addAll(s.getRoles());
        }
        for(Scene s : sceneDeck){
            roles.addAll(s.getRoles());
        }
        for(Role r : roles){
            r.addObserver(o);
        }
    }

    public void setSetObserver(Set.SetObserver o){
        for(Set s : setList){
            s.addObserver(o);
        }
    }

    public void setSceneObserver(Scene.SceneObserver o){
        for(Scene s : sceneDeck){
            s.addObserver(o);
        }
    }

    private static Board mInstance = new Board();

    private Map<String, Integer> roomToInt = null;
    private Map<String, Room>      roomMap = null;
    private Room[]               intToRoom = null;
    private List<Set>              setList = null;
    private boolean[][]  roomAdjacencyMap  = null;

    private Stack<Scene> sceneDeck = null;

    private Player player = null;

    private List<Player> playerList = null;
    private int currentPlayer = 0;
    private String[] playerColors = new String[]{"blue", "cyan", "green", "orange",
                                                 "pink", "red", "violet", "yellow"};

    private int wrapUps = 0;

    private boolean gameOver = false;

    public static Board getInstance() {
        return mInstance;
    }

    private Board() {

    }

    /**
     * get Adjacent Room
     * See if dst room is adjacent to src room, and return dst room if it is. Else, return null.
     * @param src   Source room name to move from
     * @param dst   Destination room name to move to
     * @return      Actual instance of the destination room, if it is adjacent to source room.
     *              If not adjacent, returns null
     */
    public Room getAdjacentRoom(String src, String dst) throws Room.IllegalRoomRequestException {
        Integer s = roomToInt.get(src);
        Integer d = roomToInt.get(dst);

        if(d == null){
            throw new Room.IllegalRoomRequestException("You cannot move to imaginary room.");
        }

        if(roomAdjacencyMap[s][d]){
            return roomMap.get(dst);
        } else {
            return null;
        }
    }

    /**
     * set Up Board
     * Public interface to set up the game at start.
     * Needs to be called in order for the game to take place.
     * @param num   Number of players participating in the game
     * @param rnd   Mainly for testing purpose with seed
     */
    public void setUpBoard(int num, Random rnd){
        if(rnd == null){
            rnd = new Random();
        }
        Logger.p(String.format("Getting the board ready for %d players...", num));
        setUpRoomMap();
        setUpAdjacencyMap();
        setUpScenes(rnd);
        setUpPlayer(num);
    }

    /**
     * set Up Player
     * Makes number of players given that follows the set up rule of the game.
     * @param num   Number of players participating in the game
     */
    private void setUpPlayer(int num){
        Player proto = new Player("", -1, 0 , 1);
        playerList = new ArrayList<>(num);
        switch (num){
            case 2:
            case 3:
                Logger.p("\tSpecial Rule for less than four players.\n\t\tOnly 3 days to play.");
                for(int i = 0; i < 10; i++){
                    sceneDeck.pop();
                }
                break;
            case 5:
                Logger.p("\tSpecial Rule for five players.\n\t\tEvery player gets 2cr at start.");
                proto = new Player("", -1,  2, 1);
                break;
            case 6:
                Logger.p("\tSpecial Rule for size players.\n\t\tEvery player gets 4cr at start.");
                proto = new Player("", -1,  4, 1);
                break;
            case 7:
            case 8:
                Logger.p("\tSpecial Rule for more than six players.\n\t\tStart at rank 2.");
                proto = new Player("", -1, 0, 2);
                break;
            default:
                Logger.p("\tNo Special Rule this game.");
                break;
        }

        for(int i = 0; i < num; i++){
            playerList.add(proto.makeNew(playerColors[i], i));
        }
    }

    /**
     * set Up Room Map
     * Creates 12 Rooms from board.xml and sets up two Maps - roomMap and roomToInt.
     *  roomMap maps room name to actual instance of that room
     *  roomToInt maps room name to index of room for adjacency map.
     */
    private void setUpRoomMap(){
        List<Room> sets = new ArrayList<>(12);
        setList = new ArrayList<>(10);
        // First, parse through board.xml file and create a list of sets
        try {
            File f = new File(Resources.getInstance().getBoardsPath());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(f);

            NodeList nList = document.getElementsByTagName("set");

            // iterate through sets
            for(int i = 0; i < nList.getLength(); i++){
                Node nNode = nList.item(i);
                if(nNode.getNodeType() == Node.ELEMENT_NODE){
                    // because I want it specifically as Set
                    Set s = Set.class.cast(Room.build(nNode, Set.class));
                    sets.add(s);
                    setList.add(s);
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // add Trailers and Casting Office to the set list
        sets.add(Trailers.getInstance());
        sets.add(CastingOffice.getInstance());
        roomMap   = new HashMap<>(sets.size());
        roomToInt = new HashMap<>(sets.size());
        intToRoom = new Room[sets.size()];
        int i = 0;
        for(Room r: sets){
            roomToInt.put(r.getName(), i);
            roomMap.put(r.getName(), r);
            intToRoom[i] = r;
            i++;
        }
    }

    /**
     * set Up Adjacency Map
     * Maps rooms that are adjacent to each other.
     * Hard codes (cause it wasn't provided in the XML :{ ) set adjacency.
     * TODO: make XML/JSON for adjacency if time allows
     */
    private void setUpAdjacencyMap(){
        roomAdjacencyMap = new boolean[roomMap.size()][roomMap.size()];

        int i;
        i = roomToInt.get("Train Station");
        ArrayList<Integer> list = new ArrayList<>(3);
        list.add(roomToInt.get("Jail"));
        list.add(roomToInt.get("General Store"));
        list.add(roomToInt.get(CastingOffice.CO_NAME));
        for(Integer j: list){
            roomAdjacencyMap[i][j] = true;
            roomAdjacencyMap[j][i] = true;
        }

        i = roomToInt.get("Jail");
        list.clear();
        list.add(roomToInt.get("General Store"));
        list.add(roomToInt.get("Main Street"));
        for(Integer j: list){
            roomAdjacencyMap[i][j] = true;
            roomAdjacencyMap[j][i] = true;
        }

        i = roomToInt.get("Main Street");
        list.clear();
        list.add(roomToInt.get(Trailers.T_NAME));
        list.add(roomToInt.get("Saloon"));
        for(Integer j: list){
            roomAdjacencyMap[i][j] = true;
            roomAdjacencyMap[j][i] = true;
        }

        i = roomToInt.get("General Store");
        list.clear();
        list.add(roomToInt.get("Ranch"));
        list.add(roomToInt.get("Saloon"));
        for(Integer j: list){
            roomAdjacencyMap[i][j] = true;
            roomAdjacencyMap[j][i] = true;
        }

        i = roomToInt.get("Saloon");
        list.clear();
        list.add(roomToInt.get("Bank"));
        list.add(roomToInt.get(Trailers.T_NAME));
        for(Integer j: list){
            roomAdjacencyMap[i][j] = true;
            roomAdjacencyMap[j][i] = true;
        }

        i = roomToInt.get(Trailers.T_NAME);
        list.clear();
        list.add(roomToInt.get("Hotel"));
        for(Integer j: list){
            roomAdjacencyMap[i][j] = true;
            roomAdjacencyMap[j][i] = true;
        }

        i = roomToInt.get(CastingOffice.CO_NAME);
        list.clear();
        list.add(roomToInt.get("Ranch"));
        list.add(roomToInt.get("Secret Hideout"));
        for(Integer j: list){
            roomAdjacencyMap[i][j] = true;
            roomAdjacencyMap[j][i] = true;
        }

        i = roomToInt.get("Ranch");
        list.clear();
        list.add(roomToInt.get("Bank"));
        list.add(roomToInt.get("Secret Hideout"));
        for(Integer j: list){
            roomAdjacencyMap[i][j] = true;
            roomAdjacencyMap[j][i] = true;
        }

        i = roomToInt.get("Bank");
        list.clear();
        list.add(roomToInt.get("Hotel"));
        list.add(roomToInt.get("Church"));
        for(Integer j: list){
            roomAdjacencyMap[i][j] = true;
            roomAdjacencyMap[j][i] = true;
        }

        i = roomToInt.get("Secret Hideout");
        list.clear();
        list.add(roomToInt.get("Church"));
        for(Integer j: list){
            roomAdjacencyMap[i][j] = true;
            roomAdjacencyMap[j][i] = true;
        }

        i = roomToInt.get("Church");
        list.clear();
        list.add(roomToInt.get("Hotel"));
        for(Integer j: list){
            roomAdjacencyMap[i][j] = true;
            roomAdjacencyMap[j][i] = true;
        }
    }

    /**
     * set Up Scenes
     * Creates 40 Scenes from cards.xml and initializes {@link #sceneDeck}
     */
    private void setUpScenes(Random rnd){
        List<Scene> scenes = new ArrayList<>(40);

        // First, parse through cards.xml file and create a list of sets
        try {
            File f = new File(Resources.getInstance().getCardsPath());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(f);

            NodeList nList = document.getElementsByTagName("card");

            // iterate through sets
            for(int i = 0; i < nList.getLength(); i++){
                Node nNode = nList.item(i);

                if(nNode.getNodeType() == Node.ELEMENT_NODE) {
                    String name = nNode.getAttributes().getNamedItem("name").getNodeValue();
                    String img  = nNode.getAttributes().getNamedItem("img").getNodeValue();
                    int    budg = Integer.parseInt(nNode.getAttributes().getNamedItem("budget").getNodeValue());

                    NodeList cList = nNode.getChildNodes();

                    Node cNode  = cList.item(1);
                    int    num  = Integer.parseInt(cNode.getAttributes().getNamedItem("number").getNodeValue());
                    String desc = cNode.getTextContent().trim();

                    List<StarringRole> parts = new ArrayList<>(4);
                    for(int c = 2; c < cList.getLength(); c++){
                        cNode = cList.item(c);
                        if(cNode.getNodeType() == Node.ELEMENT_NODE){
                            parts.add(Role.build(cNode, StarringRole.class));
                        }
                    }
                    scenes.add(new Scene(name, desc, img, num, budg, parts));
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // put them into stack to simulate a deck of cards
        sceneDeck = shuffleSceneCards(scenes, rnd);
    }

    /**
     * shuffle Scene Cards
     * Helper method for {@link #setUpScenes(Random rnd)}.
     * Shuffles given list, then puts them into a stack one by one.
     * @param cards     List of Scenes
     * @return          Stack of Scenes
     */
    private Stack<Scene> shuffleSceneCards(List<Scene> cards, Random rnd){
        if(cards == null || cards.isEmpty()){
            return null;
        }

        Stack<Scene> stack = new Stack<Scene>();

        Collections.shuffle(cards, rnd);

        for(Scene s: cards){
            stack.add(s);
        }

        return stack;
    }

    /**
     * next Turn
     * Return Player object who's turn it is to go.
     * @return Player who's turn it is
     */
    public Player nextTurn(){
        notifyNextTurn();
        player = playerList.get(currentPlayer);
        currentPlayer++;
        if(currentPlayer >= playerList.size()){
            currentPlayer -= playerList.size();
        }
        return player;
    }

    public int getCurrentPlayerCount(){
        return currentPlayer;
    }

    /**
     * print List
     * Prints list of available roles (or upgrade matrix) for given room name
     * @param rm    name of the room to get the list
     */
    public void printList(String rm){
        switch (rm){
            case CastingOffice.CO_NAME:
                printUpgradeScheme();
                break;
            case Trailers.T_NAME:
                System.out.println("There's nothing to list in the Trailers");
                break;
            default:
                try {
                    printAvailableRoles(rm);
                } catch (Room.IllegalRoomRequestException e) {
                    System.out.println(e.getMessage());
                }
                break;
        }
    }

    /**
     * print List All
     * Prints all roles available in the setList.
     */
    public void printListAll(){
        for(Set s: setList){
            try {
                printAvailableRoles(s.getName());
            } catch (Room.IllegalRoomRequestException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * print Available Roles
     * Prints available roles in given room (represented by room name).
     * @param rm    name of the room to print available roles
     * @throws Room.IllegalRoomRequestException     thrown when room requested does not exist
     */
    private void printAvailableRoles(String rm) throws Room.IllegalRoomRequestException {
        Room room = roomMap.get(rm);

        if(room == null){
            throw new Room.IllegalRoomRequestException(String.format("%s is not a valid room", rm));
        }

        Logger.p("Available roles for " + room.toString());
        List<Role> roles = null;
        try {
            roles = room.getAllAvailableRoles();
            if(roles == null || roles.isEmpty()){
                Logger.p("There are currently no available roles. Come back tomorrow.");
            } else {
                for(Role r: roles){
                    Logger.p(String.format("\t%s", r.listString()));
                }
            }
        } catch (Room.IllegalRoomActionException e) {
            Logger.p(String.format("\t%s", e.getMessage()));
        }
    }

    /**
     * print Upgrade Scheme
     * Prints upgrade matrix.
     */
    private void printUpgradeScheme(){
        int[] du = CastingOffice.DR_UPGRADE;
        int[] cu = CastingOffice.CR_UPGRADE;
        Logger.p("Casting Office Upgrade Fees:");
        Logger.p("\tR |  $ | cr");
        for(int i = 0; i < 5; i++){
            Logger.p(String.format("\t%d | %2d | %2d", i + 2, du[i], cu[i]));
        }

    }

    /**
     * print Adjacent Rooms
     * Prints list of rooms adjacent to given room (represented by name).
     * @param rm    name of the room in question
     */
    public void printAdjacentRooms(String rm){
        int r = roomToInt.get(rm);
        boolean[] adjList = roomAdjacencyMap[r];

        System.out.println(String.format("Printing adjacent rooms to %s", rm));
        for(int i = 0; i < adjList.length; i++){
            if(adjList[i]){
                System.out.println(String.format("\t%s", intToRoom[i]));
            }
        }
    }

    /**
     * wrap Scene
     * Decrement wrap counter ({@link #wrapUps} by 1).
     * If the counter is less than 1, it is the end of the day, call {@link #startDay()}.
     */
    public void wrapScene(){
        wrapUps--;
        if(wrapUps < 1){
            startDay();
            if(gameOver){
                System.out.println("Game is over!");
            }
        }
    }

    /**
     * start Day
     * Check if there are any more scenes in {@link #sceneDeck}.
     *  If the deck is empty, the game is over.
     * Call {@link Player#brandNewDay(List<Player>)}, which moves all players to Trailers.
     * Call {@link Set#brandNewDay(List<Set>)}, which resets the shotCount and removes scene.
     * Then assign new scenes to all sets.
     * @return  whether or not it can start a new day
     */
    public boolean startDay(){
        if(sceneDeck == null || sceneDeck.isEmpty()){
            // end of game. Game over
            gameOver = true;
            return false;
        }

        Player.brandNewDay(playerList);
        Set.brandNewDay(setList);

        Logger.p("Dawn of new day.");
        Logger.p("Everybody starts at the Trailers.");
        Logger.p("New Scenes has been set on each Set");
        Logger.p("");

        Scene scene = null;
        for(Set s : setList){
            // new day. Assign new scenes to each set.
            scene = sceneDeck.pop();
            s.assignScene(scene);
            scene.assignSet(s);
            wrapUps++;
        }
        wrapUps--;
        gameOver = false;

        notifyNewDay();

        return true;
    }

    /**
     * is Game Over
     * Returns whether or not game is over.
     * @return      whether or not game is over
     */
    public boolean isGameOver(){
        return gameOver;
    }

    /* public interface that moves the game */

    public void move(String to){
        if(gameOver){
            return;
        }

        try {
            player.move(to);
        } catch (Player.IllegalPlayerActionException e) {
            System.out.println(e.getMessage());
        } catch (Room.IllegalRoomRequestException e) {
            System.out.println(e.getMessage());
        }
    }

    public void work(String part){
        if(gameOver){
            return;
        }

        try {
            player.takeRole(part);
        } catch (Player.IllegalPlayerActionException e) {
            System.out.println(e.getMessage());
        } catch (Role.IllegalRoleAssignmentException e) {
            System.out.println(e.getMessage());
        } catch (Room.IllegalRoomActionException e) {
            System.out.println(e.getMessage());
        } catch (Room.IllegalRoomRequestException e) {
            System.out.println(e.getMessage());
        }
    }

    public void upgradeDollars(int level){
        if(gameOver){
            return;
        }

        try {
            player.upgrade(CastingOffice.CURRENCY_DOLLARS, level);
        } catch (Player.IllegalPlayerActionException e) {
            System.out.println(e.getMessage());
        } catch (Room.IllegalRoomActionException e) {
            System.out.println(e.getMessage());
        } catch (CastingOffice.IllegalCurrencyException e) {
            System.out.println(e.getMessage());
        } catch (Room.IllegalRoomRequestException e) {
            System.out.println(e.getMessage());
        } catch (CastingOffice.InsufficientUpgradeFundException e) {
            System.out.println(e.getMessage());
        } catch (Player.IllegalPlayerRequestException e) {
            System.out.println(e.getMessage());
        }
    }

    public void upgradeCredit(int level){
        if(gameOver){
            return;
        }

        try {
            player.upgrade(CastingOffice.CURRENCY_CREDIT, level);
        } catch (Player.IllegalPlayerActionException e) {
            System.out.println(e.getMessage());
        } catch (Room.IllegalRoomActionException e) {
            System.out.println(e.getMessage());
        } catch (CastingOffice.IllegalCurrencyException e) {
            System.out.println(e.getMessage());
        } catch (Room.IllegalRoomRequestException e) {
            System.out.println(e.getMessage());
        } catch (CastingOffice.InsufficientUpgradeFundException e) {
            System.out.println(e.getMessage());
        } catch (Player.IllegalPlayerRequestException e) {
            System.out.println(e.getMessage());
        }
    }

    public void rehearse(){
        if(gameOver){
            return;
        }

        try {
            player.rehearse();
        } catch (Player.IllegalPlayerActionException e) {
            System.out.println(e.getMessage());
        } catch (Room.IllegalRoomActionException e) {
            System.out.println(e.getMessage());
        } catch (Role.IllegalPracticeRequestException e) {
            System.out.println(e.getMessage());
        }
    }

    public void act(){
        if(gameOver){
            return;
        }

        try {
            player.act();
        } catch (Player.IllegalPlayerActionException e) {
            System.out.println(e.getMessage());
        } catch (Room.IllegalRoomActionException e) {
            System.out.println(e.getMessage());
        } catch (Player.IllegalPlayerRequestException e) {
            System.out.println(e.getMessage());
        }
    }

    public void end() {
        if(gameOver){
            return;
        }

        nextTurn();
        player.setUpState();
    }

    public Player getCurrentPlayer(){
        return player;
    }

}
