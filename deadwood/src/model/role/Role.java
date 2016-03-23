/**
 *  Written by Yulo Leake.
 *  Winter 2015, CSCI 345 (Object Oriented Design)
 *
 *  This is an abstract Class for various Roles (Starring and Extra) in the game.
 */

package model.role;

import model.player.Player;
import model.util.Area;
import model.util.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public abstract class Role {

    /* View stuff */
    private List<RoleObserver> observers;
    public interface RoleObserver{
        public void onRehPtChange(Role r);
    }

    public void addObserver(RoleObserver o){
        if(observers == null){
            observers = new ArrayList<>();
        }
        observers.add(o);
    }

    private void notifyOnRehPtChange(){
        for(RoleObserver o: observers){
            o.onRehPtChange(this);
        }
    }


    protected Player actor = null;
    protected Area   area  = null;
    protected String name  = "";
    protected String desc  = "";
    protected int    rank  = 0;
    protected int rehearsePoint = 0;

    public Role(String name, String desc, int rank, Area area){
        this.name = name;
        this.desc = desc;
        this.rank = rank;
        this.area = area;
    }

    /**
     * build
     * Given a XML node, parse out parameters for making a role.
     * Return specific Role based on what is given.
     * @param pNode     xML Node to parse out from
     * @param t         Class object to return
     * @param <T>       Some generic Class that extends the Role Class
     * @return          A proper Role with info from pNode.
     */
    public static <T extends Role> T build(Node pNode, Class<T> t){
        String name = pNode.getAttributes().getNamedItem("name").getNodeValue();
        String desc = ((Element) pNode).getElementsByTagName("line").item(0).getTextContent();
        int level   = Integer.parseInt(pNode.getAttributes().getNamedItem("level").getNodeValue());
        Area area   = Area.build(pNode.getChildNodes().item(1));

        // Beware! Black magic below.
        if(t.equals(StarringRole.class)){
            return t.cast(new StarringRole(name, desc, level, area));
        } else if(t.equals(ExtraRole.class)){
            return t.cast(new ExtraRole(name, desc, level, area));
        } else {
            return null;
        }
    }

    public abstract void reward(boolean success) throws Player.IllegalPlayerRequestException;
    public abstract void bonus(int b) throws Player.IllegalPlayerRequestException;
    public abstract String getIdentifier();

    public String getName(){
        return name;
    }

    public String getActorName(){
        return actor.getName();
    }

    public Area getArea(){
        return area;
    }

    public boolean isTaken(){
        return actor != null;
    }

    public int getRehearsePoint(){
        return rehearsePoint;
    }

    /**
     * rehearse
     * Increase {@link #rehearsePoint} by one.
     * @param budget        budget of the scene
     * @throws IllegalPracticeRequestException  thrown if actor can succeed with next dice roll
     */
    public void rehearse(int budget) throws IllegalPracticeRequestException {
        if(rehearsePoint + 1 < budget){
            rehearsePoint++;
            notifyOnRehPtChange();
        } else {
            throw new IllegalPracticeRequestException("You have rehearsed enough. Act now!");
        }
    }

    /**
     * assign Role
     * Assigns this Role to given Player.
     * @param actor     Player to assign this Role to
     * @throws IllegalRoleAssignmentException   Throw if actor is too low in its rank
     */
    public void assignRole(Player actor) throws IllegalRoleAssignmentException {

        if(this.rank > actor.getRank()){
            throw new IllegalRoleAssignmentException(
                    String.format("\"Hey %s! You can't handle the \'%s!\'\"" +
                            "\n(You have insufficient rank to take on this role " +
                            "(b: %d vs p: %d)", actor.getName(), name, rank, actor.getRank()));
        }
        this.actor = actor;
        actor.setRole(this);

        Logger.p(String.format("%s is now playing \"%s\"", getActorName(), getName()));
    }

    /**
     * free Role
     * Frees up this role and reset the {@link #rehearsePoint}.
     */
    public void freeRole(){
        rehearsePoint = 0;
        notifyOnRehPtChange();
        actor.sceneWrap();  // tell player that the scene has wrapped
        actor.setRole(null);
        actor = null;
    }

    public String toString(){
        return String.format("%s, \"%s\"", name, desc);
    }

    public String listString(){
        return String.format("%s, \"%s\", rank: %d", name, desc, rank);
    }

    public String debugString(){
        return String.format("%s, \"%s\", r = %d, a = %s", name, desc, rank, area);
    }


    /**
     * Illegal Role Assignment Exception
     * An Exception for illegal assignment of role to Player
     * e.g. Player with lower rank than required
     */
    public static class IllegalRoleAssignmentException extends Exception{

        public IllegalRoleAssignmentException(String s) {
            super(s);
        }
    }

    /**
     * Illegal Practice Request Exception
     * An Exception for illegal request to rehearse
     * e.g. Player has no need to rehearse (i.e. getting 1 on dice roll will still succeed)
     */
    public static class IllegalPracticeRequestException extends Exception{
        public IllegalPracticeRequestException(String s){
            super(s);
        }
    }
}
