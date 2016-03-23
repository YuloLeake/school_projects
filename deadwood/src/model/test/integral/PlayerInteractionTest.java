package model.test.integral;

import model.player.Player;
import model.role.Role;
import model.room.CastingOffice;
import model.room.Room;
import model.util.Board;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Created by Yulo Leake on 2/22/15.
 * Integration tests between Player and Room, Scene, and Role.
 *  The purpose of these tests is to determine that the interaction between Player and basic
 * board components are met.
 */
public class PlayerInteractionTest {

    private Player p = null;
    private Player q = null;
    private Player r = null;
    private Board  b = Board.getInstance();

    @Before
    public void setUpGame(){

    }

    /**
     * game Finish Test
     * Tests whether or the game finishes
     */
    @Test
    public void gameFinishTest(){
        System.out.println("\nGame Finish Test\n");
        System.out.println("Testing 3 days");
        b.setUpBoard(2, new Random());
        Assert.assertTrue(b.startDay());
        Assert.assertTrue(b.startDay());
        Assert.assertTrue(b.startDay());
        Assert.assertFalse(b.startDay());

        System.out.println("Testing 4 days");
        b.setUpBoard(5, new Random());
        Assert.assertTrue(b.startDay());
        Assert.assertTrue(b.startDay());
        Assert.assertTrue(b.startDay());
        Assert.assertTrue(b.startDay());
        Assert.assertFalse(b.startDay());
    }

    /**
     * movement Test
     * Tests whether or not player can move in different situations.
     */
    @Test
    public void movementTest(){
        System.out.println("\nMovement Test\n");

        // Initialization
        p = new Player("Red", 0, 1);
        b.setUpBoard(5, new Random());
        if(!b.startDay()){
            Assert.fail("Board should be up and ready to start!");
        }

        // Trying to move to non-adjacent room
        // Should throw IllegalRoomRequestException
        // Check to see if p still in MovingState
        try {
            p.move("Casting Office");
            Assert.fail("Should not move to Casting Office");
        } catch (Player.IllegalPlayerActionException e) {
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.out.println(e.getMessage());
        }
        Assert.assertTrue(p.getStateTag().equals(Player.MovingState.TAG));

        // Trying to take a role in Trailers (though not strictly movement, need to test
        //      if p will NOT go to MovedState
        // Should throw IllegalRoomActionException
        // Check to see if p still in MovingState
        try {
            p.takeRole("Role");
            Assert.fail("Should not be able to take a role in Trailers.");
        } catch (Player.IllegalPlayerActionException e) {
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Role.IllegalRoleAssignmentException e) {
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.out.println(e.getMessage());
        } catch (Room.IllegalRoomRequestException e) {
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        Assert.assertTrue(p.getStateTag().equals(Player.MovingState.TAG));

        // Trying to move to adjacent room
        // Should throw nothing
        // Check to see p has moved to MovedState
        // Check to see p has moved to correct Room
        try {
            p.move("Main Street");
        } catch (Player.IllegalPlayerActionException e) {
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        Assert.assertTrue(p.getStateTag().equals(Player.MovedState.TAG));
        System.out.println(p.getRoomName());

        // Trying to move to adjacent room after moving
        // Should throw IllegalPlayerActionException
        // Check to see if p still in MovedState
        // Check to see if p still in Main Street
        try {
            p.move("Train Station");
            Assert.fail("Should not be able to move after moving.");
        } catch (Player.IllegalPlayerActionException e) {
            System.out.println(e.getMessage());
        } catch (Room.IllegalRoomRequestException e) {
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        Assert.assertTrue(p.getStateTag().equals(Player.MovedState.TAG));
        System.out.println(p.getRoomName());

        // Trying to move to non-adjacent room after moving
        // Should throw IllegalPlayerActionException
        // Check to see if p still in MovedState
        // Check to see if p still in Main Street
        try {
            p.move("Saloon");
            Assert.fail("Should not be able to move after moving (even if it's adjacent).");
        } catch (Player.IllegalPlayerActionException e) {
            System.out.println(e.getMessage());
        } catch (Room.IllegalRoomRequestException e) {
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }

        // Emulate next turn (p.setUpState())
        // Check to see if p is in MovingState
        // Trying to move to imaginary room
        // Should throw IllegalRoomRequestException
        // Check to see if p is still in MovingState
        p.setUpState();
        Assert.assertTrue(p.getStateTag().equals(Player.MovingState.TAG));
        try {
            p.move("Washington");
            Assert.fail("Should not be able to move to non-existing room.");
        } catch (Player.IllegalPlayerActionException e) {
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.out.println(e.getMessage());
        }
        Assert.assertTrue(p.getStateTag().equals(Player.MovingState.TAG));

        // Trying to take a Role (not movement, but need to check ActingState
        // Should throw nothing
        // Check to see if p in TerminalState
        try {
            p.takeRole("Railroad Worker");
        } catch (Player.IllegalPlayerActionException e) {
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Role.IllegalRoleAssignmentException e) {
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        Assert.assertTrue(p.getStateTag().equals(Player.TerminalState.TAG));

        // Trying to move to adjacent room while in TerminalState
        // Should throw IllegalPlayerActionException
        // Check to see if p in TerminalState
        try {
            p.move("Jail");
        } catch (Player.IllegalPlayerActionException e) {
            System.out.println(e.getMessage());
        } catch (Room.IllegalRoomRequestException e) {
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        Assert.assertTrue(p.getStateTag().equals(Player.TerminalState.TAG));

        // Emulate next turn
        // Check to see if p now in ActingState
        // Try to move to adjacent room while in ActingState
        // Should throw IllegalPlayerActionException
        // Check to see if p still in ActingState
        p.setUpState();
        Assert.assertTrue(p.getStateTag().equals(Player.ActingState.TAG));
        try {
            p.move("Jail");
        } catch (Player.IllegalPlayerActionException e) {
            System.out.println(e.getMessage());
        } catch (Room.IllegalRoomRequestException e) {
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        Assert.assertTrue(p.getStateTag().equals(Player.ActingState.TAG));

        // Trying to act the role (not movement, but need to check if it will go to TerminalState)
        // Should throw nothing
        // Check to if in TerminalState
        try {
            p.act();
        } catch (Player.IllegalPlayerActionException e) {
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Player.IllegalPlayerRequestException e) {
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        Assert.assertTrue(p.getStateTag().equals(Player.TerminalState.TAG));


    }

    /**
     * extra Role Test
     * Tests interaction between multiple players (2 in this case) concerning extra roles in same room.
     */
    @Test
    public void extraRoleTest(){
        System.out.println("\nExtraRole Test\n");
        // Initialization
        p = new Player("Red", 0, 5);
        q = new Player("Pink", 0, 2);
        b.setUpBoard(5, new Random(100));
        if(!b.startDay()){
            Assert.fail("Board should be up and ready to start!");
        }

        // Move p to Hotel
        // Should throw nothing
        p.setUpState();
        try {
            p.move("Hotel");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }

        // Try to take a role as q
        // Should throw IllegalRoomActionException (you can't take a role in Trailers)
        q.setUpState();
        try {
            q.takeRole("Sleeping Drunkard");
            Assert.fail("There are no roles in Trailers");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Role.IllegalRoleAssignmentException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.out.println(e.getMessage());
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }

        // Move q to Hotel
        // Should throw nothing
        try {
            q.move("Hotel");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }

        // Try to take a role as p
        // Should throw nothing
        p.setUpState();
        try {
            p.takeRole("Sleeping Drunkard");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Role.IllegalRoleAssignmentException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }

        // Try to take a role as q (role taken by p)
        // Should throw IllegalRoomRequestException
        q.setUpState();
        try {
            q.takeRole("Sleeping Drunkard");
            Assert.fail("Should not be able to take this role (it is taken by p).");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Role.IllegalRoleAssignmentException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.out.println(e.getMessage());
        }

        // Try to take a role as q (immaginary role)
        // Should throw IllegalRoomRequestException
        try {
            q.takeRole("Yulo Leake");
            Assert.fail("Should not be able to take this role (It's an imaginary role).");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Role.IllegalRoleAssignmentException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.out.println(e.getMessage());
        }

        // Try to take a role as q (role outside of this room)
        // Should throw IllegalRoomRequestException
        try {
            q.takeRole("Railroad Worker");
            Assert.fail("Should not be able to take this role (Not in this room).");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Role.IllegalRoleAssignmentException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.out.println(e.getMessage());
        }

        // Try to take a role as q (role too high rank for q)
        // Should throw IllegalRoleAssignmentException
        try {
            q.takeRole("Australian Bartender");
            Assert.fail("Should not be able to take this role (Insufficient rank).");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Role.IllegalRoleAssignmentException e) {
            System.out.println(e.getMessage());
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        // Try to take a role as q
        // Should throw nothing
        try {
            q.takeRole("Faro Player");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Role.IllegalRoleAssignmentException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }

        // Try to take another role as q (in TerminalState)
        // Should throw IllegalPlayerActionException
        try {
            q.takeRole("Falls from Balcony");
            Assert.fail("Should not be able to take this role (In TerminalState).");
        } catch (Player.IllegalPlayerActionException e) {
            System.out.println(e.getMessage());
        } catch (Role.IllegalRoleAssignmentException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }

        // Try to take another role as q (in ActingState)
        // Should throw IllegalPlayerActionException
        q.setUpState();
        try {
            q.takeRole("Falls from Balcony");
            Assert.fail("Should not be able to take this role (In ActingState).");
        } catch (Player.IllegalPlayerActionException e) {
            System.out.println(e.getMessage());
        } catch (Role.IllegalRoleAssignmentException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }

        // Try to rehearse until it cannot as p
        // Should throw IllegalPracticeRequestException at end of loop
        int b = 1;
        while(true){
            try {
                p.setUpState();
                p.rehearse();
                b++;
            } catch (Player.IllegalPlayerActionException e) {
                System.err.println(e.getMessage());
                Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
            } catch (Room.IllegalRoomActionException e) {
                System.err.println(e.getMessage());
                Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
            } catch (Role.IllegalPracticeRequestException e) {
                System.out.println(e.getMessage());
                break;
            }
        }

        System.out.println(p);
        System.out.println(q);
        System.out.println(q.getRoomSceneName());
        System.out.println(String.format("budget = %d", b));

        // Try acting as p twice (will succeed twice since p has max rehearsal)
        // Should throw nothing
        // Assert if p has appropriate rewards ($2 and 2cr)
        try {
            p.setUpState();
            p.act();
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Player.IllegalPlayerRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        try {
            p.setUpState();
            p.act();
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Player.IllegalPlayerRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        Assert.assertTrue(p.getMoney()  == 2);
        Assert.assertTrue(p.getCredit() == 2);

        System.out.println("#");
        // Try acting as q until it is wrap
        // Should throw IllegalPlayerActionException when the scene has wrapped
        // Assert if q has appropriate money and credits ($f and 1cr)
        int f = 0;
        while(true){
            try {
                q.setUpState();
                q.act();
                f++;
            } catch (Player.IllegalPlayerActionException e) {
                System.out.println(e.getMessage());
                break;
            } catch (Room.IllegalRoomActionException e) {
                System.err.println(e.getMessage());
                Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
            } catch (Player.IllegalPlayerRequestException e) {
                System.err.println(e.getMessage());
                Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
            }
        }
        Assert.assertTrue(q.getMoney()  == f);
        Assert.assertTrue(q.getCredit() == 1);

        // Assert that p is in MovingState
        // Try moving to next room (Church) and take a role as Dead Man
        p.setUpState();
        Assert.assertTrue(p.getStateTag().equals(Player.MovingState.TAG));
        try {
            p.move("Church");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        try {
            p.takeRole("Dead Man");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Role.IllegalRoleAssignmentException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }

        // Assert that q is in MovingState
        // First, try to take a role on wrapped room
        // Should return IllegalRoomRequest
        // Try moving to next room (Bank) and take a role as Suspicious Gentleman
        q.setUpState();
        Assert.assertTrue(q.getStateTag().equals(Player.MovingState.TAG));
        try {
            q.takeRole("Falls from Balcony");
            Assert.fail(String.format("You cannot take a role on wrapped scene"));
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Role.IllegalRoleAssignmentException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.out.println(e.getMessage());
        }
        try {
            q.move("Bank");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        try {
            q.takeRole("Suspicious Gentleman");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Role.IllegalRoleAssignmentException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }


    }

    /**
     * starring Role Test
     * Tests interaction between multiple players (3 in this case)
     * Test Staring Roles (2 of the 3) and their interaction with extra roles (1 of 3) in terms of bonus
     */
    @Test
    public void starringRoleTest(){
        System.out.println("\nStarring Role Test\n");

        // Initialization
        p = new Player("Red", 0, 5);
        q = new Player("Pink", 0, 2);
        r = new Player("Blue", 0, 4);
        b.setUpBoard(5, new Random(100));
        if(!b.startDay()){
            Assert.fail("Board should be up and ready to start!");
        }

        // Move all players to "Saloon"
        p.setUpState();
        try {
            p.move("Saloon");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        q.setUpState();
        try {
            q.move("Saloon");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        b.printList(r.getRoomName());
        r.setUpState();
        try {
            r.move("Saloon");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        b.printList(r.getRoomName());

        // All player takes a role
        // r tries to take p's role to make sure IllegalRoomRequestException is thrown
        // Assert everybody is in TerminalState
        p.setUpState();
        try {
            p.takeRole("Josie");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Role.IllegalRoleAssignmentException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        q.setUpState();
        try {
            q.takeRole("Woman in Red Dress");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Role.IllegalRoleAssignmentException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        r.setUpState();
        try {
            r.takeRole("Josie");
            Assert.fail("Should have triggered IllegalRoomRequestException");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Role.IllegalRoleAssignmentException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.out.println(e.getMessage());
        }
        try {
            r.takeRole("St. Clement of Alexandria");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Role.IllegalRoleAssignmentException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        Assert.assertTrue(p.getStateTag().equals(Player.TerminalState.TAG));
        Assert.assertTrue(q.getStateTag().equals(Player.TerminalState.TAG));
        Assert.assertTrue(r.getStateTag().equals(Player.TerminalState.TAG));

        b.printList(r.getRoomName());
        System.out.println(p);
        System.out.println(q);
        System.out.println(r);

        r.setUpState();
        try {
            r.takeRole("Cow");
            Assert.fail("Expected IllegalPlayerActionException");
        } catch (Player.IllegalPlayerActionException e) {
            System.out.println(e.getMessage());
        } catch (Role.IllegalRoleAssignmentException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }

        // Act in order until the scene is wrapped
        //  It is hard to test out what exactly comes out (since Random number is produced),
        // so check manually :(
        while(true){
            p.setUpState();
            try {
                p.act();
            } catch (Player.IllegalPlayerActionException e) {
                System.out.println(e.getMessage());
                break;
            } catch (Room.IllegalRoomActionException e) {
                System.err.println(e.getMessage());
                Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
            } catch (Player.IllegalPlayerRequestException e) {
                System.err.println(e.getMessage());
                Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
            }

            q.setUpState();
            try {
                q.act();
            } catch (Player.IllegalPlayerActionException e) {
                System.out.println(e.getMessage());
                break;
            } catch (Room.IllegalRoomActionException e) {
                System.err.println(e.getMessage());
                Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
            } catch (Player.IllegalPlayerRequestException e) {
                System.err.println(e.getMessage());
                Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
            }

            r.setUpState();
            try {
                r.act();
            } catch (Player.IllegalPlayerActionException e) {
                System.out.println(e.getMessage());
                break;
            } catch (Room.IllegalRoomActionException e) {
                System.err.println(e.getMessage());
                Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
            } catch (Player.IllegalPlayerRequestException e) {
                System.err.println(e.getMessage());
                Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
            }
        }
        System.out.println(p);
        System.out.println(q);
        System.out.println(r);
    }

    /**
     * upgrade Test
     * Test interaction between players and Casting Office
     * Test for out of bound, insufficient fund, illegal currency, downgrade
     */
    @Test
    public void upgradeTest(){
        System.out.println("\nUpgrade Test\n");

        // Initialization
        p = new Player("Red", 0, 6);
        q = new Player("Pink", 0, 1);
        b.setUpBoard(5, new Random(100));
        if(!b.startDay()){
            Assert.fail("Board should be up and ready to start!");
        }

        p.setUpState();
        try {
            p.act();
            Assert.fail("Expected IllegalPlayerActionException");
        } catch (Player.IllegalPlayerActionException e) {
            System.out.println(e.getMessage());
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Player.IllegalPlayerRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        try {
            p.rehearse();
            Assert.fail("Expected IllegalPlayerActionException");
        } catch (Player.IllegalPlayerActionException e) {
            System.out.println(e.getMessage());
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Role.IllegalPracticeRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }

        // Move both p and q to Casting Office one room at a time
        // Should not throw any exceptions
        p.setUpState();
        try {
            p.move("Saloon");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        p.setUpState();
        try {
            p.move("Bank");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        p.setUpState();
        try {
            p.move("Ranch");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        p.setUpState();
        try {
            p.move("Casting Office");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        q.setUpState();
        try {
            q.move("Saloon");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        q.setUpState();
        try {
            q.move("Bank");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        q.setUpState();
        try {
            q.move("Ranch");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        q.setUpState();
        try {
            q.move("Casting Office");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        b.printList(q.getRoomName());
        System.out.println(p);
        System.out.println(q);
        System.out.println(q.getRoomSceneName());

        // Test illegal upgrades (out of bound, equal, and under levels).
        // Should throw IllegalRoomRequestException
        try {
            p.upgrade(CastingOffice.CURRENCY_DOLLARS, 7);
            Assert.fail("Should have thrown IllegalRoomRequestException");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (CastingOffice.IllegalCurrencyException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.out.println(e.getMessage());
        } catch (CastingOffice.InsufficientUpgradeFundException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Player.IllegalPlayerRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        try {
            p.upgrade(CastingOffice.CURRENCY_DOLLARS, 6);
            Assert.fail("Should have thrown IllegalRoomRequestException");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (CastingOffice.IllegalCurrencyException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.out.println(e.getMessage());
        } catch (CastingOffice.InsufficientUpgradeFundException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Player.IllegalPlayerRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        try {
            p.upgrade(CastingOffice.CURRENCY_DOLLARS, 5);
            Assert.fail("Should have thrown IllegalRoomRequestException");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (CastingOffice.IllegalCurrencyException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.out.println(e.getMessage());
        } catch (CastingOffice.InsufficientUpgradeFundException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Player.IllegalPlayerRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        try {
            p.upgrade(CastingOffice.CURRENCY_DOLLARS, 1);
            Assert.fail("Should have thrown IllegalRoomRequestException");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (CastingOffice.IllegalCurrencyException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.out.println(e.getMessage());
        } catch (CastingOffice.InsufficientUpgradeFundException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Player.IllegalPlayerRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }


        // Give q money and credit for testing purpose
        // Should throw nothing
        System.out.println(q);
        try {
            q.changeMoney(4);
            q.changeCredit(15);
        } catch (Player.IllegalPlayerRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        System.out.println(q);

        // Upgrade q to rank 2 using $$$
        // Should throw nothing
        try {
            q.upgrade(CastingOffice.CURRENCY_DOLLARS, 2);
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (CastingOffice.IllegalCurrencyException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (CastingOffice.InsufficientUpgradeFundException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Player.IllegalPlayerRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        System.out.println(q);
        // Upgrade q to rank 4 using cr
        // Should throw nothing
        try {
            q.upgrade(CastingOffice.CURRENCY_CREDIT, 4);
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (CastingOffice.IllegalCurrencyException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (CastingOffice.InsufficientUpgradeFundException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Player.IllegalPlayerRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        System.out.println(q);

        // Give more credits to q
        // Should throw nothing
        try {
            q.changeCredit(25);
        } catch (Player.IllegalPlayerRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }

        // Downgrade q to rank 3 using cr
        // Should throw IllegalRoomRequestException
        try {
            q.upgrade(CastingOffice.CURRENCY_CREDIT, 3);
            Assert.fail("Should throw IllegalRoomRequestException");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (CastingOffice.IllegalCurrencyException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.out.println(e.getMessage());
        } catch (CastingOffice.InsufficientUpgradeFundException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Player.IllegalPlayerRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        System.out.println(q);

        // Try to upgrade q to rank 6 using dollars
        // Should throw InsufficientUpgradeFundException
        try {
            q.upgrade(CastingOffice.CURRENCY_DOLLARS, 6);
            Assert.fail("Should throw InsufficientUpgradeFundException");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (CastingOffice.IllegalCurrencyException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (CastingOffice.InsufficientUpgradeFundException e) {
            System.out.println(e.getMessage());
        } catch (Player.IllegalPlayerRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        System.out.println(q);

        // Try to upgrade q to rank 6 using illegal currency
        // Should throw IllegalCurrencyException
        try {
            q.upgrade(100, 6);
            Assert.fail("Should have thrown IllegalCurrencyException");
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (CastingOffice.IllegalCurrencyException e) {
            System.out.println(e.getMessage());
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (CastingOffice.InsufficientUpgradeFundException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Player.IllegalPlayerRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        System.out.println(q);

        // Try to upgrade q to rank 6
        // Should throw nothing
        try {
            q.upgrade(CastingOffice.CURRENCY_CREDIT, 6);
        } catch (Player.IllegalPlayerActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomActionException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (CastingOffice.IllegalCurrencyException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Room.IllegalRoomRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (CastingOffice.InsufficientUpgradeFundException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        } catch (Player.IllegalPlayerRequestException e) {
            System.err.println(e.getMessage());
            Assert.fail(String.format("Did not expect \"%s\"", e.getClass().getName()));
        }
        System.out.println(q);
        System.out.println(p);
    }
}
