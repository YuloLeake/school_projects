/**
 *  Written by Yulo Leake.
 *  Winter 2015, CSCI 345 (Object Oriented Design)
 *
 *  This Class represents the Trailers in the game.
 *  It exists for no purpose but to house the Players at start of each day.
 *  It is a singleton class.
 */

package model.room;

import model.player.Player;
import model.role.ExtraRole;
import model.role.Role;
import model.util.Area;

import java.util.List;

public class Trailers extends Room {
    private static Trailers mInstance = null;

    public static final String T_NAME = "Trailers";

    public static Trailers getInstance() {
        if(mInstance == null){
            mInstance = new Trailers(T_NAME, new Area(991, 248, 194, 201));
        }
        return mInstance;
    }

    private Trailers(String name, Area area) {
        super(name, area, null);
    }

    @Override
    public void act(Role r) throws IllegalRoomActionException {
        throw new IllegalRoomActionException(
                "\"Hey! Are you blind? No acting in the Trailers\"\n(You cannot act in Trailers.)");
    }

    @Override
    public void rehearse(Role r) throws IllegalRoomActionException {
        throw new IllegalRoomActionException(
                "\"Take your Shakespeare out of here!\"\n(You cannot act in Trailers.)");
    }

    @Override
    public void upgrade(Player p, int cr, int level) throws IllegalRoomActionException {
        throw new IllegalRoomActionException(
                "\"Why don't you learn to read a map? Casting Office is at opposite end\"" +
                        "\n(You cannot upgrade in Trailers.)");
    }

    @Override
    public Role takeRole(String part) throws IllegalRoomActionException {
        throw new IllegalRoomActionException(
                "\"Who are you looking for?\"\n(You cannot take a role in Trailers.)");
    }

    @Override
    public List<ExtraRole> getRoles() throws IllegalRoomActionException {
        throw new IllegalRoomActionException("There are no roles in Trailers.");
    }

    @Override
    public List<Role> getAllAvailableRoles() throws IllegalRoomActionException {
        throw new IllegalRoomActionException("There are no roles in Trailers.");
    }
}
