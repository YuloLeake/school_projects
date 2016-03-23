/**
 *  Written by Yulo Leake.
 *  Winter 2015, CSCI 345 (Object Oriented Design)
 *
 *  This Class manages all main View logic of Deadwood.
 */

package view;

import model.player.Player;
import model.role.ExtraRole;
import model.role.Role;
import model.role.StarringRole;
import model.room.*;
import model.util.Area;
import model.util.Board;
import model.util.Take;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardView extends JLayeredPane 
                       implements Player.PlayerObserver, Set.SetObserver, Board.BoardObserver, Scene.SceneObserver, Role.RoleObserver{

    private List<BoardViewObserver> observers = null;

    private static final int RANK_PADDING = 10;

    private JLabel turnLabel    = null;
    private JLabel boardLabel   = null;
    private JLabel endTurnLabel = null;

    private JLabel officeLabel   = null;
    private JLabel trailersLabel = null;

    private JLabel[] dolUpgrdLabels = new JLabel[5];
    private JLabel[] crdUpgrdLabels = new JLabel[5];

    private Map<String, Area> roleAreaMap   = null;
    private Map<String, Area> playerAreaMap = null;

    private Map<String, JLabel> role2LabelMap = null;       // used only to store Mouse Click Events

    private Map<String, JLabel> takeLableMap   = null;
    private Map<String, JLabel> sceneLableMap  = null;

    private Map<String, JLabel> playerLableMap = null;
    private Map<String, JLabel> infoRankLableMap   = null;
    private Map<String, JLabel> infoRehPtLabelMap  = null;
    private Map<String, JLabel> infoCreditLabelMap = null;
    private Map<String, JLabel> infoDollarLabelMap = null;

    private Map<String, JLabel> playerInfoLabelMap = null;

    private List<Color> colorList = null;

    private Resources r = Resources.getInstance();

    private ImageIcon boardImg = r.getBoardImg();

    public BoardView(){
        init();

        int width  = boardImg.getIconWidth();
        int height = boardImg.getIconHeight();
        setSize(width, height);
        setDoubleBuffered(true);

        boardLabel = new JLabel(boardImg);
        add(boardLabel, DEFAULT_LAYER);
        boardLabel.setBounds(0, 0, width, height);

        setVisible(true);
        setFocusable(true);
        atNewDay();
    }

    private void init(){
        roleAreaMap   = new HashMap<>();
        playerAreaMap = new HashMap<>();

        role2LabelMap = new HashMap<>();

        takeLableMap   = new HashMap<>();
        sceneLableMap  = new HashMap<>();
        playerLableMap = new HashMap<>();

        infoRankLableMap   = new HashMap<>();
        playerInfoLabelMap = new HashMap<>();

        infoRehPtLabelMap  = new HashMap<>();
        infoCreditLabelMap = new HashMap<>();
        infoDollarLabelMap = new HashMap<>();


        colorList = new ArrayList<>();
        colorList.add(Color.blue);
        colorList.add(Color.cyan);
        colorList.add(Color.green);
        colorList.add(Color.orange);
        colorList.add(Color.pink);
        colorList.add(Color.red);
        colorList.add(Color.magenta);
        colorList.add(Color.yellow);

        initInfoLabels();
        initTrailersAndOffice();
        initUpgradeLabels();
    }

    private void initInfoLabels(){
        int width  = boardImg.getIconWidth();

        ImageIcon img = r.getInfoBoardImg();

        // Labels for player infos on the right
        JLabel infoLabel = null;
        int i;
        for(i = 0; i < 8; i++){
            infoLabel = new JLabel(img);
            add(infoLabel, DEFAULT_LAYER);
            infoLabel.setBackground(Color.darkGray);
            infoLabel.setOpaque(true);
            infoLabel.setBounds(width, i * img.getIconHeight(),
                    img.getIconWidth(), img.getIconHeight());
        }

        // Label for end of turn on bottom right
        img = r.getEndTurnImg();
        endTurnLabel = new JLabel(img);
        endTurnLabel.setBounds(width, i * img.getIconHeight(),
                img.getIconWidth(), img.getIconHeight());
        add(endTurnLabel, DEFAULT_LAYER);
        endTurnLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                notifyEndClicked();
            }
        });

        // Label to indicate who's turn it is
        turnLabel = new JLabel(r.getTurnBoardImg());
        add(turnLabel, PALETTE_LAYER);
    }

    private void initTrailersAndOffice(){
        // Label for Trailers and Casting Office
        final CastingOffice office = CastingOffice.getInstance();
        Area a = office.getArea();
        System.out.println(a.toString());
        officeLabel = new JLabel();
        add(officeLabel, PALETTE_LAYER);
        officeLabel.setBounds(a.getX(), a.getY(), a.getW(), a.getH());
        officeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                notifyCardClicked(office.getName());
            }
        });

        final Trailers trailers = Trailers.getInstance();
        a = trailers.getArea();
        System.out.println(a.toString());
        trailersLabel = new JLabel();
        add(trailersLabel, PALETTE_LAYER);
        trailersLabel.setBounds(a.getX(), a.getY(), a.getW(), a.getH());
        trailersLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                notifyCardClicked(trailers.getName());
            }
        });

    }

    /**
     * init Upgrade Labels
     * Hard code in upgrade labels (since too lazy to parse XML).
     */
    private void initUpgradeLabels(){
        int x   = 98;
        int y[] = new int[]{542, 564, 587, 609, 631};
        int w   = 19;
        int h   = 19;
        final int r[] = new int[]{2, 3, 4, 5, 6};

        JLabel label = null;
        for(int i = 0; i < y.length; i++){
            final int k = i;
            label = new JLabel();
            label.setBounds(x, y[i], w, h);
            dolUpgrdLabels[i] = label;
            add(label, MODAL_LAYER);
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    notifyUpgradeClicked(CastingOffice.CURRENCY_DOLLARS, r[k]);
                }
            });
        }

        x = 147;
        for(int i = 0; i < y.length; i++){
            final int k = i;
            label = new JLabel();
            label.setBounds(x, y[i], w, h);
            crdUpgrdLabels[i] = label;
            add(label, MODAL_LAYER);
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    notifyUpgradeClicked(CastingOffice.CURRENCY_CREDIT, r[k]);
                }
            });
        }


    }

    private String formatCreditInfo(int credit){
        return String.format("cr: %d", credit);
    }

    private String formatDollarInfo(int dollar){
        return String.format(" $: %d", dollar);
    }

    private String formatRehearsePtInfo(int point){
        return String.format("rp: %d", point);
    }

    private String getTakeLabelKey(String name, int take){
        return (String.format("%s-%d", name, take));
    }

    private void atNewDay(){
        // reset each scene with card's behind image
        ImageIcon cardButt = r.getCardButtImg();
        for(Map.Entry<String, JLabel> e : sceneLableMap.entrySet()){
            e.getValue().setIcon(cardButt);
        }

        // "remove" all clapper icon from take labels
        for(Map.Entry<String, JLabel> e : takeLableMap.entrySet()){
            e.getValue().setIcon(null);
        }
    }

    /**
     * Board View Observer
     * Observer/Listener interface that listens for mouse click and notifies the Observer
     *  with appropriate methods.
     */
    public interface BoardViewObserver{
        public void onCardClicked(String room);
        public void onRoleClicked(String role);
        public void onEndClicked(BoardView v);
        public void onTakeClicked(BoardView v);
        public void onUpgradeClicked(int type, int rank);
    }

    public void addObserver(BoardViewObserver o){
        if(observers == null){
            observers = new ArrayList<>();
        }
        observers.add(o);
    }

    private void notifyCardClicked(String room){
        for(BoardViewObserver o : observers){
            o.onCardClicked(room);
        }
    }

    private void notifyRoleClicked(String role){
        for(BoardViewObserver o : observers){
            o.onRoleClicked(role);
        }
    }

    private void notifyEndClicked(){
        for(BoardViewObserver o : observers){
            o.onEndClicked(this);
        }
    }

    private void notifyTakeClicked(){
        for(BoardViewObserver o : observers){
            o.onTakeClicked(this);
        }
    }

    private void notifyUpgradeClicked(int type, int rank){
        for(BoardViewObserver o : observers){
            o.onUpgradeClicked(type, rank);
        }
    }

    /* Observer implementations */

    /* PlayerObserver*/

    /**
     * on Attach
     * Called when this is added to p's observer list.
     * Add p to p to label map and populate info panel for this player.
     * @param p     Player to listen to.
     */
    @Override
    public void onAttach(Player p) {
        JLabel label = new JLabel();
        add(label, POPUP_LAYER);
        label.setIcon(r.getDiceImage(p.getName(), p.getRank()));
        playerLableMap.put(p.getName(), label);

        // Label to set up the player's info board
        int width  = boardImg.getIconWidth();
        ImageIcon infoBoard = r.getInfoBoardImg();
        JLabel infoLabel = new JLabel(infoBoard);
        add(infoLabel, PALETTE_LAYER);
        infoLabel.setBackground(colorList.get(p.getPNum()));
        infoLabel.setOpaque(true);
        infoLabel.setBounds(width, p.getPNum() * infoBoard.getIconHeight(),
                infoBoard.getIconWidth(), infoBoard.getIconHeight());

        playerInfoLabelMap.put(p.getName(), infoLabel);

        // Label to show player current rank
        ImageIcon rankImg = r.getDiceImage(p.getName(), p.getRank());
        JLabel rankLabel = new JLabel(rankImg);
        infoLabel.add(rankLabel);
        rankLabel.setBounds(RANK_PADDING, RANK_PADDING,
                            rankImg.getIconWidth(), rankImg.getIconHeight());
        infoRankLableMap.put(p.getName(), rankLabel);

        // Label to show player current money
        JLabel dollarLabel = new JLabel();
        dollarLabel.setText(formatDollarInfo(p.getMoney()));
        infoLabel.add(dollarLabel);
        dollarLabel.setBounds(2*RANK_PADDING + rankImg.getIconWidth(), RANK_PADDING, 80, 2*RANK_PADDING);
        infoDollarLabelMap.put(p.getName(), dollarLabel);

        Font font = new Font(dollarLabel.getFont().getName(), Font.BOLD, 18);
        dollarLabel.setFont(font);

        // Label to show player current credit
        JLabel creditLabel = new JLabel();
        creditLabel.setFont(font);
        creditLabel.setText(formatCreditInfo(p.getCredit()));
        infoLabel.add(creditLabel);
        creditLabel.setBounds(2*RANK_PADDING + rankImg.getIconWidth(), RANK_PADDING * 3, 80, 2*RANK_PADDING);
        infoCreditLabelMap.put(p.getName(), creditLabel);

        // Label to show player's rehearsal point
        JLabel rehLabel = new JLabel();
        rehLabel.setFont(font);
        rehLabel.setText(formatRehearsePtInfo(p.getCredit()));
        infoLabel.add(rehLabel);
        rehLabel.setBounds(2*RANK_PADDING + rankImg.getIconWidth(), RANK_PADDING * 5, 80, 2*RANK_PADDING);
        infoRehPtLabelMap.put(p.getName(), rehLabel);
    }

    @Override
    public void onRoleTaken(Player p) {
        JLabel label = playerLableMap.get(p.getName());

        String id = p.getRoleIdentifier();
        Area a = roleAreaMap.get(id);
        label.setBounds(a.getX(), a.getY(), a.getW(), a.getH());
    }

    /**
     * on Moved
     * Changes the bounds of player's label to match appropriate area.
     * @param p     Player to change label of
     */
    @Override
    public void onMoved(Player p, Area a) {
        String id = p.getPlayerRoomId();
        Area pa = playerAreaMap.get(id);

        // room area for player is not present, so add it.
        if(pa == null){
            ImageIcon icon = this.r.getDiceImage(p.getName(), 1);
            int h = icon.getIconHeight();
            int w = icon.getIconWidth();
            int x = a.getX() + (w/2 * p.getPNum());
            int y = a.getY();
            if(!p.getRoomName().equals(Trailers.T_NAME) && !p.getRoomName().equals(CastingOffice.CO_NAME)){
                y += a.getH();
            }
            pa = new Area(x, y, h, w);

            playerAreaMap.put(id, pa);
        }

        JLabel label = playerLableMap.get(p.getName());
        label.setBounds(pa.getX(), pa.getY(), pa.getH(), pa.getW());
    }

    @Override
    public void onRankChange(Player p) {
        ImageIcon icon = r.getDiceImage(p.getName(), p.getRank());

        JLabel label = playerLableMap.get(p.getName());
        label.setIcon(icon);

        label = infoRankLableMap.get(p.getName());
        label.setIcon(icon);
    }

    @Override
    public void onCurrencyChange(Player p) {
        JLabel dLabel = infoDollarLabelMap.get(p.getName());
        JLabel cLabel = infoCreditLabelMap.get(p.getName());

        dLabel.setText(formatDollarInfo(p.getMoney()));
        cLabel.setText(formatCreditInfo(p.getCredit()));
    }

    @Override
    public void onWrap(Player p) {
        String id = p.getPlayerRoomId();
        Area pa = playerAreaMap.get(id);

        JLabel label = playerLableMap.get(p.getName());
        label.setBounds(pa.getX(), pa.getY(), pa.getH(), pa.getW());
    }

    /* SetObserver*/

    /**
     * on Attach
     * Called when this is added to s's observer list.
     * Initialize set's card area, takes area, and area of extra roles.
     * Create mouse clicked event for each role area using label.
     * @param s     Set to listen to
     */
    @Override
    public void onAttach(final Set s) {
        Area a = s.getArea();
        JLabel label = new JLabel();
        add(label, PALETTE_LAYER);
        label.setBounds(a.getX(), a.getY(), a.getW(), a.getH());
        sceneLableMap.put(s.getName(), label);
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                notifyCardClicked(s.getName());
            }
        });

        // make all take labels for this set
        List<Take> tList = s.getTakes();
        for(Take t : tList){
            a = t.getArea();
            label = new JLabel();
            add(label, PALETTE_LAYER);
            label.setBounds(a.getX(), a.getY(), a.getW(), a.getH());
            takeLableMap.put(getTakeLabelKey(s.getName(), t.getTake()), label);
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    notifyTakeClicked();
                }
            });
        }

        // store area of all extra roles for this set
        // store label with each area of role for mouse click event
        List<ExtraRole> roles = s.getAvailableRoles();
        for(final ExtraRole r : roles){
            a = r.getArea();
            roleAreaMap.put(r.getIdentifier(), a);

            label = new JLabel();
            add(label, MODAL_LAYER);
            label.setBounds(a.getX(), a.getY(), a.getW(), a.getH());
            role2LabelMap.put(r.getIdentifier(), label);
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    notifyRoleClicked(r.getName());
                }
            });
        }
    }

    @Override
    public void onTakeSuccess(Set s) {
        System.out.println(getTakeLabelKey(s.getName(), s.getCurrentTakeNum()));
        JLabel label = takeLableMap.get(getTakeLabelKey(s.getName(), s.getCurrentTakeNum()));
        label.setIcon(r.getClapperImg());
    }

    @Override
    public void onWrap(Set s) {
        JLabel label = sceneLableMap.get(s.getName());
        label.setIcon(null);
    }

    /* BoardObserver */

    @Override
    public void onNewDay(Board b) {
       atNewDay();
    }

    @Override
    public void onNextTurn(Board b) {
        int turn = b.getCurrentPlayerCount();
        Icon icn = turnLabel.getIcon();
        turnLabel.setBounds(boardImg.getIconWidth(), turn * icn.getIconHeight(),
                icn.getIconWidth(), icn.getIconHeight());
    }

    /* SceneObserver */

    /**
     * on Set Assigned
     * Store all starring roles under s.
     * This is done here since the area of roles is dynamic; it changes each game.
     * @param s     Scene to listen to
     */
    @Override
    public void onSetAssigned(Scene s) {
        Area ra = null;
        Area sa = s.getSetArea();

        int x = 0;
        int y = 0;
        int h = 0;
        int w = 0;

        List<StarringRole> roles = s.getRoles();
        for(final StarringRole r : roles){
            ra = r.getArea();
            x = ra.getX() + sa.getX();
            y = ra.getY() + sa.getY();
            h = ra.getH();
            w = ra.getW();
            roleAreaMap.put(r.getIdentifier(), new Area(x, y, h, w));
        }
    }

    /**
     * on Reveal
     * Reveal (flip) scene card and add mouse click listener to
     *  starring role areas.
     * @param s     Scene to listen to
     */
    @Override
    public void onReveal(Scene s) {
        ImageIcon cardImg = Resources.getInstance().getCardImage(s.getImgAddr());

        JLabel label = sceneLableMap.get(s.getSetName());
        label.setIcon(cardImg);

        Area ra = null;
        Area sa = s.getSetArea();

        int x = 0;
        int y = 0;
        int h = 0;
        int w = 0;

        List<StarringRole> roles = s.getRoles();
        for(final StarringRole r : roles){
            ra = r.getArea();
            x = ra.getX() + sa.getX();
            y = ra.getY() + sa.getY();
            h = ra.getH();
            w = ra.getW();

            label = new JLabel();
            add(label, MODAL_LAYER);
            label.setBounds(x, y, w, h);
            role2LabelMap.put(r.getIdentifier(), label);
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    notifyRoleClicked(r.getName());
                }
            });
        }
    }

    @Override
    public void onWrap(Scene s) {
        List<StarringRole> roles = s.getRoles();
        JLabel label = null;
        for(StarringRole r : roles){
            roleAreaMap.remove(r.getIdentifier());
            label = role2LabelMap.remove(r.getIdentifier());
            if(label != null){
                remove(label);
            }
        }
    }

    /* RoleObserver */
    @Override
    public void onRehPtChange(Role r) {
        JLabel rLabel = infoRehPtLabelMap.get(r.getActorName());
        rLabel.setText(formatRehearsePtInfo(r.getRehearsePoint()));
    }
}
