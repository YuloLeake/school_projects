/**
 *  Written by Yulo Leake.
 *  Winter 2015, CSCI 345 (Object Oriented Design)
 *
 *  This Class represents the Casting Office in the game.
 *  It exists only to allow players to upgrade.
 *  It is a singleton class.
 */

package model.room;

import model.player.Player;
import model.role.ExtraRole;
import model.role.Role;
import model.util.Area;

import java.util.List;

public class CastingOffice extends Room {
    private static CastingOffice mInstance = null;

    public static final int CURRENCY_DOLLARS = 0;
    public static final int CURRENCY_CREDIT  = 1;

    public static final int[] DR_UPGRADE = new int[]{4, 10, 18, 28, 40};
    public static final int[] CR_UPGRADE = new int[]{5, 10, 15, 20, 25};

    public static final String CO_NAME = "Casting Office";


    public static CastingOffice getInstance() {
        if(mInstance == null){
            mInstance = new CastingOffice(CO_NAME, new Area(9, 459, 208, 209));
        }
        return mInstance;
    }

    private CastingOffice(String name, Area area) {
        super(name, area, null);
    }

    /*   Implementation of abstract methods. Everything by upgrade will throw an exception   */

    @Override
    public void act(Role r) throws IllegalRoomActionException {
        throw new IllegalRoomActionException(
                "\"Shh. Please be quiet in the Casting Office.\"\n(You can't act here)");
    }

    @Override
    public void rehearse(Role r) throws IllegalRoomActionException {
        throw new IllegalRoomActionException(
                "\"Shh. Please be quiet in the Casting Office.\"\n(You can't rehearse here)");
    }

    /**
     * upgrade
     * Upgrades p to rank level, using currency cr.
     * First test to see if level is in legal bound [2,6].
     * Second, test to see if it not a downgrade.
     * Third, test to see if it is a legal currency ($$$ and credits).
     * Fourth, test to see if p has sufficient currency to buy upgrade.
     * @param p         Player that wishes to upgrade
     * @param cr        Currency to use to upgrade
     * @param level     Rank to upgrade to
     * @throws IllegalRoomActionException               shouldn't be thrown
     * @throws IllegalCurrencyException                 thrown if fail test 3
     * @throws IllegalRoomRequestException              thrown if fail test 1 and 2
     * @throws InsufficientUpgradeFundException         thrown if fail test 4
     * @throws Player.IllegalPlayerRequestException     shouldn't be thrown
     */
    @Override
    public void upgrade(Player p, int cr, int level) throws IllegalRoomActionException,
                                                            IllegalCurrencyException,
                                                            IllegalRoomRequestException,
                                                            InsufficientUpgradeFundException,
                                                            Player.IllegalPlayerRequestException {
        if(level < 2 || level > 6){
            throw new IllegalRoomRequestException(
                    String.format(
                            "\"I'm afraind we can't let you do that, %s\"" +
                                    "\n(Level request %d out of bound [%d, %d])",
                            p.getName(), level, 2, 6));
        }
        if(p.getRank() >= level){
            throw new IllegalRoomRequestException("\"There's only one way, and that's up.\"" +
                    "\n(You cannot downgrade!)");
        }
        switch (cr){
            case CURRENCY_DOLLARS:
                if(p.getMoney() >= DR_UPGRADE[level-2]){
                    p.rankUp(level);
                    p.changeMoney(-DR_UPGRADE[level-2]);
                } else {
                    throw new InsufficientUpgradeFundException(
                            "\"You get non of that free lunch here.\"" +
                                    "\n(You do not have enough money to upgrade)");
                }
                break;
            case CURRENCY_CREDIT:
                if(p.getCredit() >= CR_UPGRADE[level-2]){
                    p.rankUp(level);
                    p.changeCredit(-CR_UPGRADE[level-2]);
                } else {
                    throw new InsufficientUpgradeFundException(
                            "\"You come back once you found a real job.\"" +
                                    "\n(You do not have enough credit to upgrade)");
                }
                break;
            default:
                throw new IllegalCurrencyException(
                        "\"We don't take that kind of thing here.\"" +
                                "\n(That currency is not supported here.)");
        }
    }

    @Override
    public Role takeRole(String part) throws IllegalRoomActionException {
        throw new IllegalRoomActionException(
                "\"I can't let you take that.\"\n(There's no role for you here)");
    }

    @Override
    public List<ExtraRole> getRoles() throws IllegalRoomActionException {
        throw new IllegalRoomActionException("\"You want what now?\"" +
                "(There are no roles in Casting Office.)");
    }

    @Override
    public List<Role> getAllAvailableRoles() throws IllegalRoomActionException {
        throw new IllegalRoomActionException("\"You want what now?\"" +
                "(There are no roles in Casting Office.)");
    }

    /**
     * Insufficient Upgrade Fund Exception
     * An Exception when player doesn't have enough fund to upgrade.
     */
    public static class InsufficientUpgradeFundException extends Exception{
        public InsufficientUpgradeFundException(String s){
            super(s);
        }

    }

    /**
     * Illegal Currency Exception
     * An Exception when player tries to use not supported currency.
     */
    public static class IllegalCurrencyException extends Exception{
        public IllegalCurrencyException(String s){
            super(s);
        }

    }
}
