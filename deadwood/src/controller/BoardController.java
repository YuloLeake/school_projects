/**
 *  Written by Yulo Leake.
 *  Winter 2015, CSCI 345 (Object Oriented Design)
 *
 *  Main controller to drive the game.
 */

package controller;

import model.player.Player;
import model.room.CastingOffice;
import model.util.Board;
import view.BoardView;

public class BoardController implements BoardView.BoardViewObserver{

    private Board board = null;

    public BoardController(Board board){
        this.board = board;
    }

    @Override
    public void onCardClicked(String room) {
        Player p = board.getCurrentPlayer();
        if(p != null && p.hasRole()){
            board.rehearse();
        } else {
            board.move(room);
        }
    }

    @Override
    public void onRoleClicked(String role) {
        board.work(role);
    }

    @Override
    public void onEndClicked(BoardView v) {
        board.end();
    }

    @Override
    public void onTakeClicked(BoardView v) {
        board.act();
    }

    @Override
    public void onUpgradeClicked(int type, int rank) {
        switch (type){
            case CastingOffice.CURRENCY_DOLLARS:
                board.upgradeDollars(rank);
                break;
            case CastingOffice.CURRENCY_CREDIT:
                board.upgradeCredit(rank);
                break;
            default:
                break;
        }
    }
}
