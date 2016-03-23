/**
 *  Written by Yulo Leake.
 *  Winter 2015, CSCI 345 (Object Oriented Design)
 *
 *  This class consolidates all types of external resources used in this game.
 *  That includes xml and img resources.
 */

package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Resources {

    public static Resources instance = null;

    // image resources
    private ImageIcon boardImg = null;
    private ImageIcon cardButt = null;  //front of a card is the "face," so what is the back called?
    private ImageIcon clpprImg = null;
    private ImageIcon infoBImg = null;
    private ImageIcon endTnImg = null;
    private ImageIcon turnBImg = null;

    private HashMap<String, ImageIcon> diceMap = null;
    private HashMap<String, ImageIcon> cardMap = null;

    // xml resources

    private final String pathPrefix = "../";

    private final String cardsPath  = pathPrefix + "resources/cards.xml";
    private final String boardsPath = pathPrefix + "resources/board.xml";

    public static Resources getInstance(){
        if(instance == null){
            instance = new Resources();
        }
        return instance;
    }

    private Resources(){
        try {
            boardImg = new ImageIcon(ImageIO.read(
                    new File(pathPrefix + "resources/board.jpg")));
            cardButt = new ImageIcon(ImageIO.read(
                    new File(pathPrefix + "resources/behind.png")));
            clpprImg = new ImageIcon(ImageIO.read(
                    new File(pathPrefix + "resources/shot.png")));
            infoBImg = new ImageIcon(ImageIO.read(
                    new File(pathPrefix + "resources/infoboard.png")));
            endTnImg = new ImageIcon(ImageIO.read(
                    new File(pathPrefix + "resources/endturn.png")));
            turnBImg = new ImageIcon(ImageIO.read(
                    new File(pathPrefix + "resources/turnboard.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        diceMap = new HashMap<>();
        cardMap = new HashMap<>(40);
    }


    public ImageIcon getBoardImg(){
        return boardImg;
    }

    public ImageIcon getCardButtImg(){
        return cardButt;
    }

    public ImageIcon getClapperImg(){
        return clpprImg;
    }

    public ImageIcon getInfoBoardImg(){
        return infoBImg;
    }

    public ImageIcon getEndTurnImg(){
        return endTnImg;
    }

    public ImageIcon getTurnBoardImg(){
        return turnBImg;
    }

    public ImageIcon getDiceImage(String color, int rank){
        String key = color.charAt(0)+""+rank;
        ImageIcon icn = diceMap.get(key);
        if(icn == null){
            // no icon was found. Load it in from res
            try {
                icn = new ImageIcon(ImageIO.read(
                        new File(String.format("%sresources/dice/%s.png", pathPrefix, key))));
                diceMap.put(key, icn);
                System.out.println(String.format("Loaded %s into diceMap", key));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return icn;
    }

    public ImageIcon getCardImage(String img){
        ImageIcon icn = cardMap.get(img);
        if(icn == null){
            // no icon was found. Load it in from res
            try {
                icn = new ImageIcon(ImageIO.read(
                        new File(String.format("%sresources/cards/%s", pathPrefix, img))));
                cardMap.put(img, icn);
                System.out.println(String.format("Loaded %s into cardMap", img));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return icn;
    }

    public String getBoardsPath(){
        return boardsPath;
    }

    public String getCardsPath(){
        return cardsPath;
    }

}
