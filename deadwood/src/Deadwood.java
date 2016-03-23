/**
 *  Written by Yulo Leake.
 *  Winter 2015, CSCI 345 (Object Oriented Design)
 *
 *  This Class is the main Class to run the application from.
 */

import controller.BoardController;
import model.player.Player;
import model.role.Role;
import model.room.CastingOffice;
import model.room.Room;
import model.util.Board;
import model.util.Logger;
import view.BoardView;
import view.Resources;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.util.Scanner;

class YuloDeadwood extends Deadwood {


    // Swing components
    private JFrame mainFrame;
    private BoardView bView;

    private boolean isGameOver = false;

    private int playerCount = 0;

    private Board b = null;
    private Resources r = null;

    private static final int GAME_WIDTH  = 1450;
    private static final int GAME_HEIGHT = 900;

    public YuloDeadwood(int playerCount){
        r = Resources.getInstance();
        this.playerCount = playerCount;
        b = Board.getInstance();
        b.setUpBoard(playerCount, new Random());

        initSwingComps();

        b.addObserver(bView);
        b.setPlayerObserver(bView);
        b.setSetObserver(bView);
        b.setSceneObserver(bView);
        b.setRoleObserver(bView);
        b.startDay();

        printHelp();
        end();
    }

    private void initSwingComps(){
        mainFrame = new JFrame();
        mainFrame.setTitle("Deadwood");

        bView = new BoardView();
        bView.addObserver(new BoardController(b));

        mainFrame.setSize(GAME_WIDTH, GAME_HEIGHT);
        mainFrame.getContentPane().add(bView);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }});

        mainFrame.setVisible(true);
        mainFrame.setResizable(false);
        bView.requestFocus();
    }

    @Override
    public boolean gameOver() {
        if(b != null){
            return b.isGameOver();
        }
        return true;
    }

    @Override
    public void who() {
       Logger.p(b.getCurrentPlayer());
    }

    @Override
    public void where() {
        Logger.p(b.getCurrentPlayer().getRoomSceneName());
    }

    @Override
    public void move(String to) {
        b.move(to);
    }

    @Override
    public void work(String part) {
        b.work(part);
    }

    @Override
    public void upgradeDollars(int level) {
        b.upgradeDollars(level);
    }

    @Override
    public void upgradeCrdit(int level) {
        b.upgradeCredit(level);
    }

    @Override
    public void rehearse() {
         b.rehearse();
    }

    @Override
    public void act() {
        b.act();
    }

    @Override
    public void end() {
        b.end();
    }

    @Override
    public void list() {
        b.printList(b.getCurrentPlayer().getRoomName());
    }

    @Override
    public void listAll() {
        b.printListAll();
    }

    @Override
    public void printAdj() {
        b.printAdjacentRooms(b.getCurrentPlayer().getRoomName());
    }
}


/**
 * Codes below are provided by Aran Clauson (instructor).
 * Modified areas are noted by a comment.
 */
public abstract class Deadwood {

    // These methods will be implemented by a derived class and will do the
    // correct thing, whatever that might be.
    public abstract boolean gameOver();
    public abstract void who();
    public abstract void where();
    public abstract void move(String to);
    public abstract void work(String part);
    public abstract void upgradeDollars(int level);
    public abstract void upgradeCrdit(int level);
    public abstract void rehearse();
    public abstract void act();
    public abstract void end();
    public abstract void list();
    public abstract void listAll();
    public abstract void printAdj();

    public static void printHelp(){
        System.out.println("List of available commands...");
        System.out.println("\twho            | Print who you are.");
        System.out.println("\twhere          | Print where you are at.");
        System.out.println("\tmove room      | Move to given room.");
        System.out.println("\twork role      | Take given role in the room.");
        System.out.println("\tupgrade $  lvl | Upgrade to given lvl using $.");
        System.out.println("\tupgrade cr lvl | Upgrade to given lvl using cr.");
        System.out.println("\trehearse       | Rehearse current role.");
        System.out.println("\tact            | Act current role.");
        System.out.println("\tend            | End this turn.");
        System.out.println("\tlist           | List available roles of current room.");
        System.out.println("\tlistall        | List all available roles.");
        System.out.println("\thelp           | Display help.");
        System.out.println("\tadj            | Display adjacent rooms.");
    }

    // The is the factory function that builds the correct type.
    public static Deadwood build (int players) {
        // Modified by Yulo to run my version of Deadwood
//        return new DummyDeadwood();
        return new YuloDeadwood(players);
    }

    // Very simple main program that parses the console game's language.
    // You could probably do this better with regular expressions, but since it is
    // only for testing, this is good enough.
    public static void main(String[] args)
    {
        if(args.length < 1){
            System.err.println("Error: Not enough argument.\nUsage:\njava Deadwood playerCount");
            System.exit(1);
        }

        Integer p = 0;
        try{
            p = new Integer(args[0]);
        } catch(NumberFormatException e){
            System.err.println("Error: That's not an integer. Please pass in an integer (2-8)");
            System.exit(1);
        }
        if(2 > p || p > 8){
            System.err.println("Error: Player size has to be in range of [2,8].");
            System.exit(1);
        }

        Deadwood dw = build(p);
        Scanner in = new Scanner(System.in);

        while(!dw.gameOver() && in.hasNext()) {

            String cmd = in.next();
            switch(cmd) {
                case "who":
                    dw.who();
                    break;

                case "where":
                    dw.where();
                    break;

                case "move":
                    dw.move(in.nextLine().trim());
                    break;

                case "work":
                    dw.work(in.nextLine().trim());
                    break;

                case "upgrade":
                    String currency = in.next().trim();
                    int level = in.nextInt();
                    switch(currency) {
                        case "$":
                            dw.upgradeDollars(level);
                            break;
                        case "cr":
                            dw.upgradeCrdit(level);
                            break;
                        default:
                            System.out.println("Unknown currency \"" + currency +"\"");
                            break;
                    }
                    break;

                case "rehearse":
                    dw.rehearse();
                    break;

                case "act":
                    dw.act();
                    break;

                case "end":
                    dw.end();
                    break;

                case "list":
                    dw.list();
                    break;

                case "listall":
                    dw.listAll();
                    break;

                case "help":
                    printHelp();
                    break;

                case "adj":
                    dw.printAdj();
                    break;

                default:
                    System.out.println("Unknown command \""+cmd+"\". " +
                            "\nType \"help\" to see all commands.");
                    break;
            }
        }
    }
}

// This is a dummy implementation of deadwood.  This is just to show you how you
// might extended the deadwood base class.  Can you figure out the quote for the
// end command?  
class DummyDeadwood extends Deadwood {
    private boolean isOver = false;

    public boolean gameOver() {
        return isOver;
    }

    public void who() {
        System.out.println("Who...are...you?\n ---The Caterpillar");
    }


    public void where() {
        System.out.println("If you don't know where you are going,");
        System.out.println("you might wind up someplace else.     ");
        System.out.println("                        ---Yogi Berra");
    }

    public void move(String to) {
        System.out.println("Move to the \""+to+"\"");
    }

    public void work(String part) {
        System.out.println("Work the \""+part+"\" part");
    }

    public void upgradeDollars(int level) {
        System.out.format("Upgrade to %d with dollars\n", level);
    }

    public void upgradeCrdit(int level){
        System.out.format("Upgrade to %d with credits\n", level);
    }

    public void rehearse() {
        System.out.println("Life is not a dress rehearsal.");
        System.out.println("              ---Rose Tremain");

    }

    @Override
    public void act() {
        System.out.println("Acting deals with very delicate emotions.");
        System.out.println("It is not putting up a mask.");
        System.out.println("Each time an actor acts he does not hide; he exposes himself.");
        System.out.println("                                        ---Rodney Dangerfield");
    }

    public void end() {
        System.out.println("But all the magic I have known");
        System.out.println("I've had to make myself.");
        System.out.println("           ---Shel Silverstein");
        isOver = true;
    }

    @Override
    public void list() {

    }

    @Override
    public void listAll() {

    }

    @Override
    public void printAdj() {

    }
}