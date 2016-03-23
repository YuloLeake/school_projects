/**
 *  Written by Yulo Leake.
 *  Winter 2015, CSCI 345 (Object Oriented Design)
 *
 *  This Class represents the Player of the game.
 *  Uses the State Pattern to dictate what action a player can make.
 */

package model.player;

import model.role.Role;
import model.room.CastingOffice;
import model.room.Room;
import model.room.Trailers;
import model.util.Area;

import java.util.ArrayList;
import java.util.List;

public class Player {

    /*   View stuff   */
    private List<PlayerObserver> observers;

    public interface PlayerObserver{
        /* do multiple notifies */
        public void onAttach(Player p);
        public void onRoleTaken(Player p);
        public void onMoved(Player p, Area a);
        public void onRankChange(Player p);
        public void onCurrencyChange(Player p);
        public void onWrap(Player p);
    }

    public void addObserver(PlayerObserver o){
        if(observers == null){
            observers = new ArrayList<>();
        }
        observers.add(o);
        o.onAttach(this);
    }

    private void notifyRoleTaken(){
        for(PlayerObserver p: observers){
            p.onRoleTaken(this);
        }
    }

    private void notifyMoved(){
        for(PlayerObserver p: observers){
            p.onMoved(this, room.getArea());
        }
    }

    private void notifyRankChange(){
        for(PlayerObserver p: observers){
            p.onRankChange(this);
        }
    }

    private void notifyCurrencyChange(){
        for(PlayerObserver p: observers){
            p.onCurrencyChange(this);
        }
    }

    private void notifyWrap(){
        for(PlayerObserver p: observers){
            p.onWrap(this);
        }
    }



    public static String getPlayerRoomId(Player p, Room r){
        return String.format("%s-%s", p.getName(), r.getName());
    }

    public String getPlayerRoomId(){
        return getPlayerRoomId(this, this.room);
    }

    private PlayerState state = null;
    private String name   = "";
    private int    pNum   = 0;
    private int    money  = 0;
    private int    credit = 0;
    private int    rank   = 0;
    private Role   role   = null;
    private Room   room   = null;

    public Player(String name, int credit, int rank) {
        this(name, 0, credit, rank);
    }
    public Player(String name, int pNum, int credit, int rank){
        this.name   = name;
        this.pNum   = pNum;
        this.money  = 0;
        this.credit = credit;
        this.rank   = rank;
        this.role   = null;
        this.room   = null;
        setUpState();
    }

    public static void brandNewDay(List<Player> list){
        for(Player p: list){
            p.room = Trailers.getInstance();
            if(p.role != null){
                p.role.freeRole();
            }
            p.notifyMoved();
        }
    }

    public String getName(){
        return name;
    }

    public int getPNum(){
        return this.pNum;
    }

    public void setRole(Role role){
        this.role = role;
    }

    public boolean hasRole(){
        return role != null;
    }

    public void changeMoney(int dm) throws IllegalPlayerRequestException {
        int temp = money + dm;
        if(temp < 0) {
            throw new IllegalPlayerRequestException("You cannot go into debt with money!");
        }
        this.money = temp;
    }
    public int getMoney(){
        return money;
    }

    public void changeCredit(int dc) throws IllegalPlayerRequestException {
        int temp = credit + dc;
        if(temp < 0) {
            throw new IllegalPlayerRequestException("You cannot go into debt with credit!");
        }
        this.credit = temp;
    }
    public int getCredit(){
        return credit;
    }

    public int getRank(){
        return rank;
    }
    public void rankUp(int to) throws IllegalPlayerRequestException {
        if(this.rank >= to){
            throw new IllegalPlayerRequestException("You cannot downgrade a player!");
        }
        this.rank = to;
    }

    /**
     * set Up State
     * This method needs to be called at the start of player's turn.
     */
    public void setUpState(){
        if(role == null){
            this.state = new MovingState();
        } else {
            this.state = new ActingState();
        }
    }

    public void sceneWrap(){
        notifyCurrencyChange();
        notifyWrap();
    }


    public String getRoomName(){
        return room.getName();
    }

    public String getRoomSceneName(){
        String sceneName = "";
        if(room.getClass().getName().equals(Trailers.class.getName())){
            sceneName = "Trailers (Has no Scene)";
        } else if (room.getClass().getName().equals(CastingOffice.class.getName())){
            sceneName = "Casting Office (Has no Scene)";
        } else {
            sceneName = room.toString();
        }
        return sceneName;
    }

    public String getRoleIdentifier(){
        return role.getIdentifier();
    }

    /**
     * make New
     * Makes a deep clone of this Player with different name.
     * Used for initiation purpose.
     * @param name      new Name of cloned Player
     * @return          new Player with new Name
     */
    public Player makeNew(String name, int pNum){
        return new Player(name, pNum, this.credit, this.rank);
    }


    /*  All Player actions are driven by PlayerState  */
    public void act() throws IllegalPlayerActionException,
                                Room.IllegalRoomActionException,
                                IllegalPlayerRequestException {
        state = state.act(this);
        notifyCurrencyChange();
    }
    public void rehearse() throws IllegalPlayerActionException,
                                Room.IllegalRoomActionException,
                                Role.IllegalPracticeRequestException {
        state = state.rehearse(this);
    }
    public void move(String room) throws IllegalPlayerActionException,
                                Room.IllegalRoomRequestException {
        state = state.move(this, room);
        notifyMoved();
    }
    public void upgrade(int cr, int rank) throws IllegalPlayerActionException,
                                Room.IllegalRoomActionException,
                                CastingOffice.IllegalCurrencyException,
                                Room.IllegalRoomRequestException,
                                CastingOffice.InsufficientUpgradeFundException,
                                IllegalPlayerRequestException {
        state = state.upgrade(this, cr, rank);
        notifyCurrencyChange();
        notifyRankChange();
    }
    public void takeRole(String role) throws IllegalPlayerActionException,
                                Role.IllegalRoleAssignmentException,
                                Room.IllegalRoomActionException,
                                Room.IllegalRoomRequestException {
        state = state.takeRole(this, role);
        notifyRoleTaken();
    }


    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s ($%d, %dcr, rank %d)", name, money, credit, rank));
        if(role != null){
            sb.append(" working ");
            sb.append(role.toString());
        }
        return sb.toString();
    }

    public String printRoom(){
        return room.toString();
    }

    /**
     * get State Tag
     * Purely for testing purpose.
     * @return  state's toString method (basically, their tag).
     */
    public String getStateTag(){
        return state.toString();
    }

    /**
     * Player State
     * An interface for State Machine that the Player will base its action on.
     * Based on the State the Player is in, it limits the Player action.
     */
    interface PlayerState {

        public PlayerState act(Player p) throws IllegalPlayerActionException, Room.IllegalRoomActionException, IllegalPlayerRequestException;
        public PlayerState rehearse(Player p) throws IllegalPlayerActionException, Room.IllegalRoomActionException, Role.IllegalPracticeRequestException;
        public PlayerState move(Player p, String to) throws IllegalPlayerActionException, Room.IllegalRoomRequestException;
        public PlayerState upgrade(Player p, int cr, int rank) throws IllegalPlayerActionException, Room.IllegalRoomActionException, CastingOffice.IllegalCurrencyException, Room.IllegalRoomRequestException, CastingOffice.InsufficientUpgradeFundException, IllegalPlayerRequestException;
        public PlayerState takeRole(Player p, String role) throws IllegalPlayerActionException,
                Role.IllegalRoleAssignmentException, Room.IllegalRoomActionException, Room.IllegalRoomRequestException;

    }

    /**
     * Acting State
     * State that says the Player is currently acting.
     * Limits Player action to {@link #act(Player p)} and {@link #rehearse(Player p)}
     */
    public class ActingState implements PlayerState {
        public static final String TAG = "In Acting State";

        @Override
        public PlayerState act(Player p) throws Room.IllegalRoomActionException,
                                            IllegalPlayerRequestException {
            p.room.act(role);
            return new TerminalState();
        }

        @Override
        public PlayerState rehearse(Player p) throws Room.IllegalRoomActionException,
                                            Role.IllegalPracticeRequestException {
            p.room.rehearse(role);
            return new TerminalState();
        }

        @Override
        public PlayerState move(Player p, String to) throws IllegalPlayerActionException {
            throw new IllegalPlayerActionException(
                    "\"Hey! Where are you going? The show must go on!\"" +
                    "\n(Cannot move while acting.)");
        }

        @Override
        public PlayerState upgrade(Player p, int cr, int rank) throws IllegalPlayerActionException {
            throw new IllegalPlayerActionException(
                    "\"Hey! Put away your Sudoku book!\"" +
                    "\n(Cannot upgrade while acting.)");
        }

        @Override
        public PlayerState takeRole(Player p, String role) throws IllegalPlayerActionException {
            throw new IllegalPlayerActionException("" +
                    "\"You cannot take the role \'Dr. Evil.\'\"" +
                    "\n(Cannot take a new role while acting.)");
        }

        public String toString(){
            return TAG;
        }
    }

    /**
     * Moving State
     * State that says the Player has an option to move to adjacent room.
     *  Limits Player action to {@link #move(Player p, String to)},
     * {@link #upgrade(Player p, int cr, int rank)}, and {@link #takeRole(Player p, String role)}.
     */
    public class MovingState implements PlayerState {
        public static final String TAG = "In Moving State";

        @Override
        public PlayerState act(Player p) throws IllegalPlayerActionException {
            throw new IllegalPlayerActionException(
                    "\"How can you have any act if you don't have yer role?\"" +
                            "\n(Cannot act without a Role.)");
        }

        @Override
        public PlayerState rehearse(Player p) throws IllegalPlayerActionException {
            throw new IllegalPlayerActionException(
                    "\"If you don't have yer role, you can't have any rehearsing!\"" +
                            "\n(Cannot rehearse without a Role.)");
        }

        @Override
        public PlayerState move(Player p, String to) throws Room.IllegalRoomRequestException {
            Room dst = p.room.getAdjacentRoom(to);
            p.room = dst;
            dst.moveInto();
            return new MovedState();
        }

        @Override
        public PlayerState upgrade(Player p, int cr, int rank) throws IllegalPlayerRequestException,
                                                Room.IllegalRoomActionException,
                                                CastingOffice.IllegalCurrencyException,
                                                Room.IllegalRoomRequestException,
                                                CastingOffice.InsufficientUpgradeFundException {
            room.upgrade(p, cr, rank);
            return this;
        }

        @Override
        public PlayerState takeRole(Player p, String role) throws Role.IllegalRoleAssignmentException,
                                                Room.IllegalRoomActionException,
                                                Room.IllegalRoomRequestException {
            Role r = p.room.takeRole(role);
            r.assignRole(p);
            return new TerminalState();
        }

        public String toString(){
            return TAG;
        }
    }

    /**
     * Moved State
     * State that says the Player has moved but can still upgrade or take a role.
     *  Limits Player action to
     * {@link #upgrade(Player p, int cr, int rank)} and {@link #takeRole(Player p, String role)}.
     */
    public class MovedState implements PlayerState {
        public static final String TAG = "In Moved State";
        @Override
        public PlayerState act(Player p) throws IllegalPlayerActionException {
            throw new IllegalPlayerActionException(
                    "\"How can you have any act if you don't have yer role?\"" +
                            "\n(Cannot act without a Role.)");
        }

        @Override
        public PlayerState rehearse(Player p) throws IllegalPlayerActionException {
            throw new IllegalPlayerActionException(
                    "\"If you don't have yer role, you can't have any rehearsing!\"" +
                            "\n(Cannot rehearse without a Role.)");
        }

        @Override
        public PlayerState move(Player p, String to) throws IllegalPlayerActionException {
            throw new IllegalPlayerActionException(
                    "\"Don't be greedy! Wait your next turn!\"" +
                            "\n(Cannot move after moving this turn.)");
        }

        @Override
        public PlayerState upgrade(Player p, int cr, int rank) throws IllegalPlayerRequestException,
                                                Room.IllegalRoomActionException,
                                                CastingOffice.IllegalCurrencyException,
                                                Room.IllegalRoomRequestException,
                                                CastingOffice.InsufficientUpgradeFundException {
            room.upgrade(p, cr, rank);
            return this;
        }

        @Override
        public PlayerState takeRole(Player p, String role) throws Role.IllegalRoleAssignmentException,
                                                Room.IllegalRoomActionException,
                                                Room.IllegalRoomRequestException {
            Role r = p.room.takeRole(role);
            r.assignRole(p);
            return new TerminalState();
        }

        public String toString(){
            return TAG;
        }
    }

    /**
     * Terminal State
     * State that says the Player has exhausted this turn.
     * Player has no action to choose from.
     */
    public class TerminalState implements PlayerState{
        public static final String TAG = "In Terminal State";

        @Override
        public PlayerState act(Player p) throws IllegalPlayerActionException {
            throw new IllegalPlayerActionException("Your turn is done - you cannot act.");
        }

        @Override
        public PlayerState rehearse(Player p) throws IllegalPlayerActionException {
            throw new IllegalPlayerActionException("Your turn is done - you cannot rehearse.");
        }

        @Override
        public PlayerState move(Player p, String to) throws IllegalPlayerActionException {
            throw new IllegalPlayerActionException("Your turn is done - you cannot move.");
        }

        @Override
        public PlayerState upgrade(Player p, int cr, int rank) throws IllegalPlayerActionException {
            throw new IllegalPlayerActionException("Your turn is done - you cannot upgrade.");
        }

        @Override
        public PlayerState takeRole(Player p, String role) throws IllegalPlayerActionException {
            throw new IllegalPlayerActionException("Your turn is done - you cannot take a role.");
        }
        public String toString(){
            return TAG;
        }
    }

    /**
     * Illegal Player Action Exception
     * An Exception for illegal action for that PlayerState.
     * e.g. trying to act in MovingState
     */
    public static class IllegalPlayerActionException extends Exception{

        public IllegalPlayerActionException(String s) {
            super(s);
        }
    }

    /**
     * Illegal Player Request Exception
     * An Exception for legal action, but a illegal request for player.
     * e.g. trying to put player into debt
     */
    public static class IllegalPlayerRequestException extends Exception{
        public IllegalPlayerRequestException(String s){
            super(s);
        }
    }
}
