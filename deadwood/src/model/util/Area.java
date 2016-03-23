/**
 *  Written by Yulo Leake.
 *  Winter 2015, CSCI 345 (Object Oriented Design)
 *
 *  Stores the area where the images go.
 */

package model.util;

import org.w3c.dom.Node;

public class Area {

    private int x, y;   // Origin in cartesian graph
    private int h, w;   // Width and height of area

    public Area(int x, int y, int h, int w){
        this.x = x;
        this.y = y;
        this.h = h;
        this.w = w;
    }

    /**
     * build
     * Given a XML node, parse out parameters for making an Area.
     * @param aNode     XML node to parse out from
     * @return          A new Area object with info from aNode
     */
    public static Area build(Node aNode) {
        int x = Integer.parseInt(aNode.getAttributes().getNamedItem("x").getNodeValue());
        int y = Integer.parseInt(aNode.getAttributes().getNamedItem("y").getNodeValue());
        int h = Integer.parseInt(aNode.getAttributes().getNamedItem("h").getNodeValue());
        int w = Integer.parseInt(aNode.getAttributes().getNamedItem("w").getNodeValue());
        return new Area(x, y, h, w);
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getH(){
        return h;
    }
    public int getW(){
        return w;
    }

    public String toString(){
        return String.format("(%d, %d), (%d, %d)", x, y, h, w);
    }
}
