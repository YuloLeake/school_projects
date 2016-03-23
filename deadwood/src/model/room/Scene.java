/**
 *  Written by Yulo Leake.
 *  Winter 2015, CSCI 345 (Object Oriented Design)
 *
 *  This Class represents the Card with scenes in the game.
 */

package model.room;

import model.role.Role;
import model.role.StarringRole;
import model.util.Area;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    /* View stuff */

    private List<SceneObserver> observers;

    public interface SceneObserver{
        public void onSetAssigned(Scene s);
        public void onReveal(Scene s);
        public void onWrap(Scene s);
    }

    public void addObserver(SceneObserver o){
        if(observers == null){
            observers = new ArrayList<>();
        }
        observers.add(o);
    }

    private void notifySetAssignment(){
        for(SceneObserver o : observers){
            o.onSetAssigned(this);
        }
    }

    private void notifyRevealed(){
        for(SceneObserver o : observers){
            o.onReveal(this);
        }
    }

    private void notifyWrap(){
        for(SceneObserver o : observers){
            o.onWrap(this);
        }
    }


    private String  name = "";
    private String  desc = "";
    private String  imgr = "";
    private int     numb = 0;
    private int     budg = 0;
    private List<StarringRole> roles = null;
    private boolean revealed = false;

    private Set set = null;

    public Scene(String name, String desc, String imgr, int numb, int budg, List<StarringRole> roles){
        this.name  = name;
        this.desc  = desc;
        this.imgr  = imgr;
        this.numb  = numb;
        this.budg  = budg;
        this.roles = roles;
        this.revealed = false;
    }

    public int getBudget() {
        return budg;
    }

    public List<StarringRole> getRoles() {
        return roles;
    }

    public List<StarringRole> getAvailableRoles(){
        List<StarringRole> r = new ArrayList<>();
        for(StarringRole s : roles){
            if(!s.isTaken()){
                r.add(s);
            }
        }
        return r;
    }

    public void assignSet(Set set){
        this.set = set;
        notifySetAssignment();
    }

    public String getImgAddr(){
        return imgr;
    }

    public boolean hasActorActing(){
        boolean actor = false;
        for(Role r: roles){
            if(r.isTaken()){
                actor = true;
                break;
            }
        }
        return actor;
    }

    public void atMoveInto(){
        if(!revealed){
            this.revealed = true;
            notifyRevealed();
        }
    }

    public String getSetName(){
        return set.getName();
    }

    public Area getSetArea(){
        return set.getArea();
    }

    public void wrapScene(){
        notifyWrap();
        set.assignScene(null);
    }


    public String toString(){
        return String.format("%s scene %d", name, numb);
    }

}
