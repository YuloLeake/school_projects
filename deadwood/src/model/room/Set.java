/**
 *  Written by Yulo Leake.
 *  Winter 2015, CSCI 345 (Object Oriented Design)
 *
 *  This Class represents most of the Rooms in the game.
 *  Ties in Player action with Room, Scene, and Role.
 */

package model.room;

import model.player.Player;
import model.role.ExtraRole;
import model.role.Role;
import model.util.Area;
import model.util.Board;
import model.util.Logger;
import model.util.Take;

import java.util.*;

public class Set extends Room {

    /*   View stuff   */
    private List<SetObserver> observers;

    public interface SetObserver{
        public void onAttach(Set s);
        public void onTakeSuccess(Set s);
        public void onWrap(Set s);
    }

    public void addObserver(SetObserver o){
        if(observers == null){
            observers = new ArrayList<>();
        }
        observers.add(o);
        o.onAttach(this);
    }

    private void notifyTakeSuccess(){
        for(SetObserver p: observers){
            p.onTakeSuccess(this);
        }
    }

    private void notifyWrap(){
        for(SetObserver p: observers){
            p.onWrap(this);
        }
    }

    private int shotCount = 0;
    private List<Take> takes = null;
    private List<ExtraRole> xRoles = null;
    private Scene scene = null;
    private static final Random dice = new Random();

    public Set(String name, Area area, List<Take> takes, List<ExtraRole> xRoles) {
        super(name, area, takes);
        this.xRoles = xRoles;
        this.takes = takes;
    }

    public static void brandNewDay(List<Set> sets){
        for(Set s: sets){
            if(s.scene != null){
                s.scene.wrapScene();
                s.scene = null;
            }
            s.shotCount = s.takes.size();
        }
    }

    @Override
    public void act(Role r) throws IllegalRoomActionException, Player.IllegalPlayerRequestException {
        int budget = scene.getBudget();
        int role   = dice.nextInt(6) + 1; // adding one since nextInt is exclusive range
        int bonus  = r.getRehearsePoint();

        if(role + bonus >= budget){
            // if success, decrement shotCount and reward the role who succeeded
            shotCount--;
            Logger.p(String.format("Success! You rolled %d (+ %d) against %d", role, bonus, budget));
            r.reward(true);

            notifyTakeSuccess();

            if(shotCount <= 0){
                // if no more shots to take, distribute bonuses
                distributeBonus();
            }
        } else {
            Logger.p(String.format("Failure! You rolled %d (+ %d) against %d", role, bonus, budget));
            r.reward(false);
        }
    }

    @Override
    public void rehearse(Role r) throws IllegalRoomActionException, Role.IllegalPracticeRequestException {
        int budget = scene.getBudget();
        r.rehearse(budget);
    }

    @Override
    public void upgrade(Player p, int cr, int level) throws IllegalRoomActionException, CastingOffice.IllegalCurrencyException {
        throw new IllegalRoomActionException(
                "\"Hey you! Take your fancy spreadsheet to the Casting Office!\"\n(You can't upgrade here)");
    }

    @Override
    public Role takeRole(String part) throws IllegalRoomActionException, IllegalRoomRequestException {
        if(scene == null){
            throw new IllegalRoomRequestException("The scene has wrapped - there are no roles to take.");
        }
        Role ret = null;

        List<Role> roles = new ArrayList<>();
        roles.addAll(scene.getRoles());
        roles.addAll(getRoles());

        for(Role r: roles){
            if(r.getName().equals(part)){
                if(r.isTaken()){
                    throw new IllegalRoomRequestException(
                            String.format("%s is already taken by %s.", part, r.getActorName()));
                } else {
                    ret = r;
                }
                break;
            }
        }
        if(ret == null){
            throw new IllegalRoomRequestException(
                    String.format("%s does not exist in this room.", part));
        }

        return ret;
    }

    @Override
    public List<ExtraRole> getRoles() {
        return this.xRoles;
    }

    @Override
    public List<Role> getAllAvailableRoles() throws IllegalRoomActionException {
        if(scene == null){
            throw new IllegalRoomActionException("The scene has wrapped - there are no roles.");
        }
        List<Role> roles = new ArrayList<>();
        roles.addAll(scene.getAvailableRoles());
        roles.addAll(getAvailableRoles());

        return roles;
    }

    @Override
    public void moveInto(){
        if(scene != null){
            scene.atMoveInto();
        }
    }

    public List<Take> getTakes(){
        return takes;
    }

    public int getCurrentTakeNum(){
        return takes.size() - shotCount;
    }

    private List<Role> getAllRoles(){
        List<Role> roles = new ArrayList<>();
        roles.addAll(scene.getRoles());
        roles.addAll(xRoles);
        return roles;
    }

    /**
     * distribute Bonus
     * Helper method to distribute bonus to actors.
     * For star bonus, uses PriorityQueue to hold bonus from highest to lowest.
     * Wrap the scene at the end.
     * @throws Player.IllegalPlayerRequestException    thrown if r.bonus results in neg number
     * @throws IllegalRoomActionException              thrown if room has no role (shouldn't happen)
     */
    private void distributeBonus() throws Player.IllegalPlayerRequestException, IllegalRoomActionException {
        Logger.p(String.format("That's a wrap for %s", scene.toString()));
        if(scene.hasActorActing()){
            Logger.p("Distributing bonus for the scene to the Stars...");
            int rolls = scene.getBudget();
            PriorityQueue<Integer> maxQ = new PriorityQueue<>(rolls, Collections.reverseOrder());
            for(int i = 0; i < rolls; i++){
                int roll = dice.nextInt(6) + 1; // add one since nextInt is exclusive upper range
                maxQ.add(roll);
            }

            List<? extends Role> roles = scene.getRoles();
            Collections.reverse(roles); // b/c roles is in ascending order of rank, need descending

            // add up bonuses to give to the stars
            int[] bonuses = new int[roles.size()];
            int i = 0;
            while(!maxQ.isEmpty()){
                bonuses[i%roles.size()]+= maxQ.poll();
                i++;
            }
            for(i = 0; i < bonuses.length; i++){
                Role r = roles.get(i);
                if(r.isTaken()){
                    r.bonus(bonuses[i]);
                }
            }

            Logger.p("Distributing bonus for the scene to the Extras...");
            roles = getRoles();
            for(Role r: roles){
                if(r.isTaken()){
                    r.bonus(0);
                }
            }
        } else {
            Logger.p("No bonus is rewarded for this scene (no Starring player)");
        }
        wrapScene();
    }

    private void wrapScene(){
        List<Role> roles = getAllRoles();
        for(Role r: roles){
            if(r.isTaken()){
                r.freeRole();
            }
        }
        scene.wrapScene();
        notifyWrap();
        Board.getInstance().wrapScene();    // tell Board it has wrapped a scene
    }

    /**
     * assign Scene
     * Assign new scene for this set and reset shotCount.
     * @param scene     new scene
     */
    public void assignScene(Scene scene){
        this.scene = scene;
        shotCount  = takes.size();
    }

    public List<ExtraRole> getAvailableRoles(){
        List<ExtraRole> r = new ArrayList<>();
        for(ExtraRole s : xRoles){
            if(!s.isTaken()){
                r.add(s);
            }
        }
        return r;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append(getName());
        if(scene != null){
            sb.append(String.format(" shooting %s, budget of $%d million (take %d of %d)",
                    scene, scene.getBudget(), takes.size() - shotCount + 1, takes.size()));
        }

        return sb.toString();
    }

}
