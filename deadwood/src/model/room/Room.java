/**
 *  Written by Yulo Leake.
 *  Winter 2015, CSCI 345 (Object Oriented Design)
 *
 *  This is an abstract Class for various Rooms in the game.
 */

package model.room;

import java.util.ArrayList;
import java.util.List;

import model.player.Player;
import model.role.ExtraRole;
import model.role.Role;
import model.util.Area;
import model.util.Board;
import model.util.Take;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class Room {

    protected String name = "";
    protected Area area   = null;
    protected List<Take> takes = null;

    public Room(String name, Area area, List<Take> takes){
        this.name  = name;
        this.area  = area;
        this.takes = takes;
    }

    public static Room build(Node nNode, Class<? extends Room> c){
        Room ret = null;

        if(c.equals(Set.class)){
            String name = nNode.getAttributes().getNamedItem("name").getNodeValue();

            // get child attributes (area, takes, etc).
            NodeList cList = nNode.getChildNodes();
            Area area = Area.build(cList.item(1));

            ArrayList<Take> takes = new ArrayList<>(3);
            NodeList tList = cList.item(3).getChildNodes();
            for(int t = 0; t < tList.getLength(); t++){
                Node tNode = tList.item(t);
                if(tList.item(t).getNodeType() == Node.ELEMENT_NODE){
                    takes.add(Take.build(tNode));
                }
            }

            List<ExtraRole> parts = new ArrayList<>(4);
            NodeList pList = cList.item(5).getChildNodes();
            for(int p = 0; p < pList.getLength(); p++){
                Node pNode = pList.item(p);
                if(pList.item(p).getNodeType() == Node.ELEMENT_NODE){
                    parts.add(Role.build(pNode, ExtraRole.class));
                }
            }
            ret = new Set(name, area, takes, parts);
        } else if(c.equals(Trailers.class)){
            ret = Trailers.getInstance();
        } else if(c.equals(CastingOffice.class)){
            ret = CastingOffice.getInstance();
        }
        return ret;
    }

    public abstract void act(Role r) throws IllegalRoomActionException,
                                            Player.IllegalPlayerRequestException;
    public abstract void rehearse(Role r) throws IllegalRoomActionException,
                                            Role.IllegalPracticeRequestException;
    public abstract void upgrade(Player p, int cr, int level) throws IllegalRoomActionException,
                                            CastingOffice.IllegalCurrencyException,
                                            IllegalRoomRequestException,
                                            CastingOffice.InsufficientUpgradeFundException,
                                            Player.IllegalPlayerRequestException;
    public abstract Role takeRole(String part) throws IllegalRoomActionException,
                                            IllegalRoomRequestException;
    public abstract List<ExtraRole> getRoles()         throws IllegalRoomActionException;
    public abstract List<Role> getAllAvailableRoles()  throws IllegalRoomActionException;

    public void moveInto(){

    }

    public String getName(){
        return name;
    }

    public Area getArea(){
        return area;
    }

    public Room getAdjacentRoom(String rm) throws IllegalRoomRequestException {
        Room ret = Board.getInstance().getAdjacentRoom(this.name, rm);
        if(ret == null){
            throw new IllegalRoomRequestException(
                    String.format("%s is not adjacent to your current room(%s).", rm, this.name));
        }
        return ret;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        return sb.toString();
    }



    /**
     * Illegal Room Action Exception
     * An Exception for illegal action for that room.
     * e.g. trying to act in Trailers
     */
    public static class IllegalRoomActionException extends Exception{
        public IllegalRoomActionException(String s){
            super(s);
        }
    }

    /**
     * Illegal Room Request Exception
     * An Exception for legal action, but a illegal request for that room.
     * e.g. trying to move to non-adjacent room
     */
    public static class IllegalRoomRequestException extends Exception{

        public IllegalRoomRequestException(String s) {
            super(s);
        }
    }
}
