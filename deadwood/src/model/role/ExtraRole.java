/**
 *  Written by Yulo Leake.
 *  Winter 2015, CSCI 345 (Object Oriented Design)
 *
 *  This Class represents the Extra Roles (Off-Card Roles) of the game.
 */

package model.role;

import model.player.Player;
import model.util.Area;
import model.util.Logger;

public class ExtraRole extends Role{

    public ExtraRole(String name, String desc, int rank, Area area){
        super(name, desc, rank, area);
    }

    /**
     * reward
     * Reward actor for their acting.
     * In case of Extra Role, give $1 and 1cr if a success, $1 if failure.
     * @param success   whether or not the roll was success
     * @throws Player.IllegalPlayerRequestException     shouldn't be thrown
     */
    @Override
    public void reward(boolean success) throws Player.IllegalPlayerRequestException {
        if(success){
            actor.changeMoney(1);
            actor.changeCredit(1);
            Logger.p(String.format("%s was rewarded $%d and %dcr", getActorName(), 1, 1));
        } else {
            actor.changeMoney(1);
            Logger.p(String.format("%s was rewarded $%d", getActorName(), 1));
        }
    }

    /**
     * bonus
     * Give bonus to player, which is based on role rank.
     * @param b     this means nothing - it is to satisfy the abstract method param
     * @throws Player.IllegalPlayerRequestException
     */
    @Override
    public void bonus(int b) throws Player.IllegalPlayerRequestException {
        actor.changeMoney(rank);
        Logger.p(String.format("\tBonus! %s was rewarded $%d", getActorName(), rank));
    }

    @Override
    public String getIdentifier() {
        return "Extra-" + name;
    }


    public String listString(){
        return String.format("Extra: %s", super.listString());
    }

    public String debugString(){
        return "Extra: " + super.debugString();
    }
}