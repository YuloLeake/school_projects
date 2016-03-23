/**
 *  Written by Yulo Leake.
 *  Winter 2015, CSCI 345 (Object Oriented Design)
 *
 *  Stores location where the Take tokens are located.
 */

package model.util;

import org.w3c.dom.Node;

public class Take {

    private Area area = null;
    private int  take = 0;

    public Take(Area area, int take){
        this.area = area;
        this.take = take;
    }

    /**
     * build
     * Given a XML node, parse out parameters for making a Take.
     * @param tNode     xML Node to parse out from
     * @return          A new Take object with info from tNode
     */
    public static Take build(Node tNode){
        int num = Integer.parseInt(tNode.getAttributes().getNamedItem("number").getNodeValue());
        Area area = Area.build(tNode.getChildNodes().item(0));
        return new Take(area, num);
    }

    public Area getArea() {
        return area;
    }

    public int getTake() {
        return take;
    }

    public String toString(){
        return String.format("Number: %d, Area: %s", take, area);
    }

}
