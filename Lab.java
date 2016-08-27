import java.awt.event.*;
import javax.swing.*;
import java.util.*;//queue, arraylist, linkedlist
import java.lang.System;
public class Lab{
    Screen screen;
    private final int wWidth, wHeight;
    public BiList<Being> beings;//A list is Actors; B list includes fixed
    public Queue<Being> bin;//To Do: Phase this into the being list (TriList)
    
    //world.time is the dt for the simulation. Usually it is a very small number measured in frames.
    static double time = 1;
    
    private final boolean walls = true;
    private int gravity = 1;
    private vector gravityV = new vector(1.0, Math.PI/2);
    public final boolean air = true;
    
    public int curserW = 20;
    public int curserH = 10;
    public byte curserShape = 0;
    public boolean shiftDown, ctrlDown;
    public byte density = 1;
    public Types curserType;
    public Curser curser;
    //JPanel panel = WorldHandler.getInstance().getWorldCanvas();
    
    public void addBeing(Being being, int x, int y, boolean actor){
        being.world = this;
        being.setLocation(x, y);
        if(actor)
            beings.addA(being);
        else
            beings.add(being);
        being.addedToWorld();
    }
    public void addPhys(Physical phys, double x, double y, boolean actor){
        phys.world = this;
        phys.setLocation(x, y);
        if(actor)
            beings.addA(phys);
        else
            beings.add(phys);
        //phys.addedToWorld(); This line is ommitted because it forces the phys back onto the grid
    }
    public void addPhys(Physical phys, double x, double y){
        phys.world = this;
        phys.setLocation(x, y);
        if(phys.fixed)
            beings.add(phys);
        else
            beings.addA(phys);
    }
    public void addPart(Particle p){
        if(p instanceof Fixed)
            beings.add(p);
        else
            beings.addA(p);
    }
    public void removeBeing(Being being){
        synchronized(this){
            bin.add(being);
        }
    }
    //To be called every frame
    public void dumpBin(){
        if(bin.size() > 0){
            synchronized(this){
                for(Being b = bin.poll(); b!= null; b = bin.poll())
                    beings.remove(b);
                System.gc();
            }
        }
    }
    public double getZoom()
    {return screen.zRatio;}
    public int getWidth()
    {return wWidth;}
    public int getHeight()
    {return wHeight;}
    public double getGravity()
    {return gravity;}
    public vector getGravityV()
    {return gravityV;}
    public boolean getAir()
    {return air;}
    //!!!!! Not Efficient Or Useful !!!!!
    public Being getBeingAt(int x, int y){
        Being here = null;
        assert y < wHeight && y >= 0 && x < wWidth && x >= 0;
        for(BiList.Node n = beings.o1; n!= null; n = n.getNext()){
            Being being = (Being) n.getVal();
            if(Math.round(being.getX()) == x && Math.round(being.getY()) == y)
                here = being;
        }
        if(here != null)
            return here;
        else 
            return null;
    }
    
    public Lab(Screen s){
        screen = s;
        wWidth = s.Width();
        wHeight = s.Height();
        beings = new BiList<Being>();
        bin = new LinkedList<Being>();
        
        curser = new Curser();
        if(!Execute.fileMode)
            addBeing(curser, wWidth/2, wHeight/2, false);
        curserType = Types.SAND;
        
        System.out.println("new world: w="+wWidth+"h="+wHeight);
    }
    
    /**
     * To be called from textInterface()
     */
    public Lab(int w, int h){
        wWidth = w;
        wHeight = h;
        
        beings = new BiList<Being>();
        bin = new LinkedList<Being>();
        
        System.out.println("new world: w="+wWidth+"h="+wHeight);
    }
    
    /**
     * Loads a pre-created simulation
     */
    public boolean sim(String sim){
        switch(sim)
        {
            case "Beaker":
                /**
                 * Build a beaker
                 */
                for(int a = 300; a < 360; a++)
                {
                    if(a < 302 || a > 357)
                    {
                        for(int b = 200; b < 300; b++)
                            addPhys(new Fixed(), a, b, false);
                    }
                    else
                    {
                        addPhys(new Fixed(), a, 298, false);
                        addPhys(new Fixed(), a, 299, false);
                    }
                }
                break;
            case "Bridge":
                /**
                 * Build the Bridge
                 */
                for(int a = 100; a < 130; a++)
                {
                    //Make three(four)-layer plank from 300 to 349
                    addPhys(new Brick(), a, 100, true);
                    addPhys(new Brick(), a+.5, 100 + Math.sqrt(3)/2, true);
                    addPhys(new Brick(), a, 100 + Math.sqrt(3), true);
                    addPhys(new Brick(), a+.5, 100 + 3*Math.sqrt(3)/2, true);
                    //Add two Bases
                    if(a < 106 || a > 123)//6 by 2
                    {
                        addPhys(new Fixed(), a, 105, false);
                        addPhys(new Fixed(), a+.5, 105 + Math.sqrt(3)/2, false);
                    }
                }
                break;
            case "noGrav":
                gravity = 0;
                gravityV = new vector();
                break;
            /**
             * Makes a block of rubber which should contract and oscilate
             */
            case "Rubber":
                for(int a = 40; a <= 40+1.2*8; a+= 1.2){
                    for(int b = 40; b<= 46; b+= 1.2){
                        if( a>41.2 && a < 48.4 && b < 44.8)
                            addPhys(new Rubber(), a, b, true);
                        else if(a==40 || a==40+1.2*8 || b==46){
                            addPhys(new Fixed(), a, b, false);
                            addPhys(new Fixed(), a+.3, b+.3, false);
                        }
                    }
                }
                break;
            case "SuBridge":
                /**
                 * Build a suspension Bridge
                 */
                for(int a = 300; a < 360; a++)
                {
                    //Make four-layer plank from 300 to 359
                    addPhys(new Brick(), a, 300, true);
                    addPhys(new Brick(), a+.5, 300 + Math.sqrt(3)/2, true);
                    addPhys(new Brick(), a, 300 + Math.sqrt(3), true);
                    addPhys(new Brick(), a+.5, 300 + 3*Math.sqrt(3)/2, true);
                    //Make two triple-layer, double density platforms
                    /*
                    if(a < 310 || a > 349)
                    {
                        addPhys(new Fixed(), a-.25, 304.8);
                        addPhys(new Fixed(), a-.25, 305.3);
                        addPhys(new Fixed(), a-.25, 305.8);
                        addPhys(new Fixed(), a+.25, 304.8);
                        addPhys(new Fixed(), a+.25, 305.3);
                        addPhys(new Fixed(), a+.25, 305.8);
                    }*/
                    //Make two vertical ropes
                    if(a < 330)
                        addPhys(new Brick(), 320.5, a-29-Math.sqrt(3)/2, true); 
                    else
                        addPhys(new Brick(), 339.5, a-59-Math.sqrt(3)/2, true);
                }
                /**
                 * Add Anchors
                 */
                Brick Anchor = new Brick();
                Anchor.fixed = true;
                addPhys(Anchor, 320.5, 270-Math.sqrt(3)/2, false);
                Brick Anchor2 = new Brick();
                Anchor2.fixed = true;
                addPhys(Anchor2, 339.5, 270-Math.sqrt(3)/2, false);
                break;
            case "Brick":
                /**
                 * Build a ledge
                 */
                for(int a = 100; a < 110; a++){
                    addPhys(new Fixed(), a, 100, false);
                    addPhys(new Fixed(), a, 101, false);
                    addPhys(new Fixed(), a, 102, false);
                    
                    addPhys(new Fixed(), a+.5, 100.5, false);
                    addPhys(new Fixed(), a+.5, 101.5, false);
                    addPhys(new Fixed(), a+.5, 102.5, false);
                }
                /**
                 * Build a Brick
                 */
                for(int a = 108; a < 118; a++){
                    addPhys(new Brick(), a, 95, true);
                    addPhys(new Brick(), a+.5, 95+Math.sqrt(3)/2, true);
                    addPhys(new Brick(), a, 95+Math.sqrt(3), true);
                    addPhys(new Brick(), a+.5, 95+3*Math.sqrt(3)/2, true);
                }
                break;
            case "SandFall":
                for(int a = 200; a < 210; a++)
                    for(int b = 200; b < 210; b++)
                    {
                        //Large Square
                        addPhys(new Sand(), a, b, true);
                        //Small Dense square underneith
                        addPhys(new Fixed(), 203 + (a-200)/3, 213 + (b-200)/3, false);//303-306
                    }
                break;
            case "TinyBridge":
                /**
                 * Build the Bridge
                 */
                for(int a = 10; a < 20; a++)
                {
                    //Make three(four)-layer plank from 70 to 79
                    if(a != 10)
                        addPhys(new Brick(), a, 10, true);
                    addPhys(new Brick(), a+.5, 10 + Math.sqrt(3)/2, true);
                    //Add two Bases//2 by 1
                    if((a>10 && a < 13) || a > 17){
                        addPhys(new Fixed(), a, 13, false);
                    }
                }
                break;
                
            default:
                return false;
        }
        return true;
    }
    
    
    /**
     * Update the size of the curser. Dump Bin.
     */
    public void act()
    {
        //This way, when curser is the only thing, life is easier
        curser.act();
        
        assert time > 0;
        int d = screen.getScroll();
        if(!shiftDown)
            curserH -= d;
        if(!ctrlDown)
            curserW -= d;
        if(shiftDown && ctrlDown)
        {
            curserW -= d;
            curserH = curserW;
        }
        /**
         * Ensure that neither height nor width is out of bounds
         */
        if(curserH < 1)
            curserH = 1;
        else if(curserH > wHeight)
            curserH = wHeight;
        if(curserW < 1)
            curserW = 1;
        else if(curserW > wWidth)
            curserW = wWidth;
        
        dumpBin();
    }
    
    public void print(){
        Screen.debugShout("Please print: ("+curser.getX()+","+curser.getY()+") @"+System.currentTimeMillis());
        //This is postponed to avoid ConcurrenModification
        SwingUtilities.invokeLater(new PrintRequest(curser.getX(), curser.getY(), curserW, curserH));
    }
    
    public void erase(){
        for(BiList.Node n = beings.o1; n!= null; n = n.getNext()){
            Being being = (Being) n.getVal();
            if(being.getX() >= curser.getX() && being.getX() < curser.getX()+curserW
            && being.getY() >= curser.getY() && being.getY() < curser.getY()+curserH
            && being != curser)
                removeBeing(being);    
        }
    }
    
    public String toString(){
        String res = "Lab:\n";
        for(BiList.Node n = beings.o1; n!= null; n = n.getNext()){
            Being being = (Being) n.getVal();
            res+= being + "\n";
        }
        return res;
    }
    
    class PrintRequest implements Runnable{
        int cx,cy,cw,ch;
        
        public PrintRequest(int x, int y, int w, int h){
            this.cx = x;
            this.cy = y;
            this.cw = w;
            this.ch = h;
        }
        public void run(){
            Screen.debugShout("Printing: ("+cx+","+cy+") @"+System.currentTimeMillis());
            Particle particle = new Sand();
            /**
             * 0: Rectangle
             * 1: Hexagon
             * 2: Isometric Rectangle
             * 3: Hollow Circle
             */
            switch(curserShape){
                case 0: //Rectangle
                
                double x = 0;
                double y = 0;
                for(int a = 0; a <= (cw-1) * density ; a++){
                    for(int b = 0; b <= (ch-1) * density ; b++){
                        switch(curserType){
                            case FIXED: 
                            particle = new Fixed();
                            break;
                            case SAND: 
                            particle = new Sand();
                            break;
                            case WATER: 
                            particle = new Water();
                            break;
                            case BOMB: 
                            particle = new Bomb();
                            break;
                            case BRICK: 
                            particle = new Brick();
                            break;
                            case RUBBER: 
                            particle = new Rubber();
                            break;
                            default: particle = new Sand();
                        }
                        //addBeing(particle, curser.getX() - curserSize / 2 + a, 
                        //        curser.getY() - curserSize / 2 + b);
                        x = cx + a / (double) density;
                        y = cy + b / (double) density;
    
                        if(x > 0 && x < wWidth 
                            && y > 0 && y < wHeight)
                            addPhys(particle, x, y, (curserType != Types.FIXED));
                    }
                }
                break;
    
                case 1:  //Hexagon
                //which hexagonal number (number of rings
                int n = density*cw + 1;
                int nParts = 3*n*(n-1) + 1;//source: wiki centered hexagonal number
    
                // NEVERMIND -Too much trig - Prints all particles spiraling outward from the center 
                //INSTEAD - go Row by Row
                for(int a = 0; a < 2*(n+1); a++){
                    y = cy + a*Math.sqrt(3)/2.0/density; //a*1/2*sqrt(3)
                    //number of particles in this row
                    int rowLen = 2*n-1-Math.abs(n-a-1);
                    //distance in rows from center row
                    int offSet = Math.abs(n-a-1);
                    for(int b = 0; b < rowLen; b++){
                        x = cx + (offSet/2.0 + b)/density;
    
                        switch(curserType){
                            case FIXED: 
                            particle = new Fixed();
                            break;
                            case SAND: 
                            particle = new Sand();
                            break;
                            case WATER: 
                            particle = new Water();
                            break;
                            case BOMB: 
                            particle = new Bomb();
                            break;
                            case BRICK: 
                            particle = new Brick();
                            break;
                            case RUBBER: 
                            particle = new Rubber();
                            break;
                            default: particle = new Sand();
                        }
                        if(x > 0 && x < wWidth 
                            && y > 0 && y < wHeight)
                            addPhys(particle, x, y, (curserType != Types.FIXED));
                    }
                }
                break;
    
                case 2:  //Isometric Rectangle
                double h = Math.sqrt(3)/2/density;//Height of one row
                double lastCol = cx+cw;
                double lastRow = cy+ch;
                for(x = cx; x <= lastCol; x+= 1/density){
                    int row = 0;
                    for(y = cy; y <= lastRow; y += h){
                        switch(curserType){
                            case FIXED: 
                            particle = new Fixed();
                            break;
                            case SAND: 
                            particle = new Sand();
                            break;
                            case WATER: 
                            particle = new Water();
                            break;
                            case BOMB: 
                            particle = new Bomb();
                            break;
                            case BRICK: 
                            particle = new Brick();
                            break;
                            case RUBBER: 
                            particle = new Rubber();
                            break;
                            default: particle = new Sand();
                        }
                        double offSet = 0;
                        if((row & 1) == 1)//odd row
                            offSet = .5/density;
                        if(x+offSet > 0 && x+offSet < wWidth 
                            && y > 0 && y < wHeight){
                            addPhys(particle, x, y, (curserType != Types.FIXED));
                        }
    
                        row++;//int row = (int) Math.round((b-cy)/h)
                    }
                }
                break;
    
                case 3:  //Hollow Circle
                int r = cw; //inner radius
                int rings = ch; // # of rings progressing outwards
    
                for(int a = 0; a < rings; a++){
                    //Path Length (circumference) = 2*pi*r
                    int cir = (int) Math.floor(2*Math.PI*(r+a));
                    for(int b = 0; b < cir; b++){
                        switch(curserType){
                            case FIXED: 
                            particle = new Fixed();
                            break;
                            case SAND: 
                            particle = new Sand();
                            break;
                            case WATER: 
                            particle = new Water();
                            break;
                            case BOMB: 
                            particle = new Bomb();
                            break;
                            case BRICK: 
                            particle = new Brick();
                            break;
                            case RUBBER: 
                            particle = new Rubber();
                            break;
                            default: particle = new Sand();
                        }
                        //angle = 2pi / len * b
                        double th = 2*b*Math.PI / cir;
                        x = cx + (r + a)*Math.cos(th);
                        y = cy + (r + a)*Math.sin(th);
    
                        if(x > 0 && x < wWidth && y > 0 && y < wHeight)
                            addPhys(particle, x, y, (curserType != Types.FIXED));
                    }
                }
                break;
    
                default:
                break;
            }
            Screen.debugShout("\tPrint done at: ("+cx+","+cy+") @"+System.currentTimeMillis());
        }
    }//PrintRequest
}