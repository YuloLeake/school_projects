/**
 *  Written by Yulo Leake.
 *  Winter 2015, CSCI 345 (Object Oriented Design)
 *
 *  This Class represents the Starring Roles (On-Card Roles) of the game.
 */

package model.role;

import model.player.Player;
import model.util.Area;
import model.util.Logger;

public class StarringRole extends Role {

    public StarringRole(String name, String desc, int rank, Area area){
        super(name, desc, rank, area);
    }

    /**
     * reward
     * Reward actor for their acting.
     * In case of Starring Role, give 2cr if a success.
     * @param success   whether or not the roll was success
     * @throws Player.IllegalPlayerRequestException     shouldn't be thrown
     */
    @Override
    public void reward(boolean success) throws Player.IllegalPlayerRequestException {
        if(success){
            actor.changeCredit(2);
            Logger.p(String.format("%s was rewarded %dcr", getActorName(), 2));
        }

    }

    /**
     * bonus
     * Give bonus to player.
     * @param b     bonus to give the actor
     * @throws Player.IllegalPlayerRequestException     shouldn't be thrown
     */
    @Override
    public void bonus(int b) throws Player.IllegalPlayerRequestException {
        actor.changeMoney(b);
        Logger.p(String.format("\tBonus! %s was rewarded $%d", getActorName(), b));
    }

    @Override
    public String getIdentifier() {
        return "Star-" + name;
    }

    public String listString(){
        return String.format("Starring: %s", super.listString());
    }

    public String debugString(){
        return "Star: " + super.debugString();
    }
}