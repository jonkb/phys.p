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
    /* Let gravity = 10m/s^2
     * Let 1s = 20f
     * let 1m = n*c
     * 10 m  | n*c | 1s ^ 2     = n/40 c/f^2 --> n = 40
     * 1 s^2 | 1m   | 20f ^ 2
     *    assuming g = 10m/s^2
     *    &fps = 20, 40c = 1m
     */
    private double gravity = 1;
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
    //Specify gravity magnitude (positive number). 1 is default.
    public void setGravity(double grav){
        gravity = grav;
        gravityV = new vector(grav, Math.PI/2);
    }
    
    public Lab(Screen s){
        screen = s;
        wWidth = s.Width();
        wHeight = s.Height();
        beings = new BiList<Being>();
        bin = new LinkedList<Being>();
        
        curser = new Curser();
        if(!Execute.fileMode){
            addBeing(curser, wWidth/2, wHeight/2, false);
            curserType = Types.SAND;
        }
        
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
        switch(sim){
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
                int x0 = 20;
                int y0 = 40;
                int length = 60;
                int rope_length = 12;
                for(int a = x0; a < x0+length; a++){
                    //Make four-layer plank from 300 to 359
                    addPhys(new Brick(), a, y0, true);
                    addPhys(new Brick(), a+.5, y0 + Math.sqrt(3)/2, true);
                    addPhys(new Brick(), a, y0 + Math.sqrt(3), true);
                    addPhys(new Brick(), a+.5, y0 + 3*Math.sqrt(3)/2, true);
                    //Make two triple-layer, double density platforms
                    if(a < (x0+10) || a > (x0+length-11)){
                        addPhys(new Fixed(), a-.25, y0+4.8);
                        addPhys(new Fixed(), a-.25, y0+5.3);
                        addPhys(new Fixed(), a-.25, y0+5.8);
                        addPhys(new Fixed(), a+.25, y0+4.8);
                        addPhys(new Fixed(), a+.25, y0+5.3);
                        addPhys(new Fixed(), a+.25, y0+5.8);
                    }
                    //Make two vertical ropes
                    //y0 -Math.sqrt(3)/2 
                    //y0 -Math.sqrt(3)/2   a=x0 + length - rope_length
                    if(a < x0 + rope_length)
                        addPhys(new Brick(), x0+20.5, y0+x0-a-Math.sqrt(3)/2, true); 
                    else if(a > x0 + length - rope_length - 1)
                        addPhys(new Brick(), x0+39.5, y0+x0-a+(length-rope_length)-Math.sqrt(3)/2, true);
                }
                /**
                 * Add Anchors
                 */
                Brick Anchor = new Brick();
                Anchor.fixed = true;
                addPhys(Anchor, x0+20.5, y0-rope_length-Math.sqrt(3)/2, false);
                Brick Anchor2 = new Brick();
                Anchor2.fixed = true;
                addPhys(Anchor2, x0+39.5, y0-rope_length-Math.sqrt(3)/2, false);
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
                for(int a = 10; a < 20; a++){
                    for(int b = 4; b < 14; b++){
                        //Large Square
                        addPhys(new Sand(), a, b, true);
                        //Small Dense square underneath
                        addPhys(new Fixed(), 13 + (a-10)/3, 17 + (b-4)/3, false);//13-16
                    }
                }
                break;
            case "TinyBridge":
                /**
                 * Build the Bridge
                 */
                for(int a = 10; a < 20; a++){
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
            case "Pulley":
                x0 = 30;
                y0 = 10;
                double r0 = 4;
                double r1 = 6;
                double endY = 0;//Temp
                length = 6;
                //S = 1 = r*Dth Dth=1/r
                //Make Pulley ring
                for(double r = r0; r<r1; r++){
                    for(double th = Math.PI; th< 2*Math.PI+1/r; th += 1/r){
                        addPhys(new Fixed(), x0 + r*Math.cos(th), y0 + r*Math.sin(th), false);
                    }
                }
                //Make Rope semiring
                for(double th = Math.PI; th< 2*Math.PI+1/r1; th += 1/r1){
                    endY = y0 + r1*Math.sin(th);//Keep track of the last bit
                    addPhys(new Brick(), x0 + r1*Math.cos(th), endY, true);
                }
                //Make vertical part of rope
                for(double y = y0+1; y< y0+length; y++){
                    addPhys(new Brick(), x0 - r1, y, true);
                    //Shift so it lines up with the curved bit
                    addPhys(new Brick(), x0 + r1, y - y0 + endY, true);
                }
                //Make weight
                for(double y = y0+length; y < y0+length+5; y++){
                    addPhys(new Brick(), x0 - r1, y, true);
                    addPhys(new Brick(), x0 - r1-Math.sqrt(3)/2, y+ .5, true);
                    addPhys(new Brick(), x0 - r1+Math.sqrt(3)/2, y+ .5, true);
                }
                break;
            case "Pulley_2":
                x0 = 18;//Center of the left pulley wheel
                y0 = 12;
                length = 7;
                r0 = 2;
                r1 = 4;
                double topY = 0;//Will be changed later
                double endX = 0;
                double fixed_step = .3;
                //Make left pulley quarter ring
                for(double r = r1-1; r>r0; r-=fixed_step){
                    for(double th = Math.PI; th< 1.5*Math.PI+fixed_step/r; th += fixed_step/r){
                        addPhys(new Fixed(), x0 + r*Math.cos(th), y0 + r*Math.sin(th), false);
                    }
                }
                //Make Rope quarter rings
                for(double th = Math.PI; th< 1.5*Math.PI+1/r1; th += 1/r1){
                    topY = y0 + r1*Math.sin(th);//10.06056...
                    endX = x0 + r1*Math.cos(th);
                    addPhys(new Brick(), endX, topY, true);
                }
                Screen.debugShout("topY="+topY+"; y0-r1="+(y0-r1));
                Screen.debugShout("endX="+endX+"; x0="+x0);
                double arm_length = 20+2*(endX-x0);
                //Make right pulley quarter ring
                for(double r = r1-1; r>r0; r-=fixed_step){
                    for(double th = 2*Math.PI; th> 1.5*Math.PI-fixed_step/r; th -= fixed_step/r){
                        addPhys(new Fixed(), x0 + arm_length + r*Math.cos(th), y0 + r*Math.sin(th), false);
                    }
                }
                for(double th = 2*Math.PI; th> 1.5*Math.PI-1/r1; th -= 1/r1){
                    addPhys(new Brick(), x0 + arm_length + r1*Math.cos(th), y0 + r1*Math.sin(th), true);
                }
                //Make horizontal part of rope
                for(double x = endX+1; x< endX-2+arm_length; x++){
                    addPhys(new Brick(), x, topY, true);// switch for topY
                }
                //Make vertical part of rope
                for(double y = y0+1; y< y0+length; y++){
                    addPhys(new Brick(), x0 - r1, y, true);
                    //Shift so it lines up with the curved bit
                    addPhys(new Brick(), x0 + r1 + arm_length, y, true);
                }
                //Make weight
                for(double y = y0+length; y < y0+length+5; y++){
                    addPhys(new Brick(), x0 - r1, y, true);
                    addPhys(new Brick(), x0 - r1-Math.sqrt(3)/2, y+ .5, true);
                    addPhys(new Brick(), x0 - r1+Math.sqrt(3)/2, y+ .5, true);
                }
                //Make second, heavier weight
                for(double y = y0+length; y < y0+length+6; y++){
                    addPhys(new Brick(), x0 + arm_length + r1, y, true);
                    addPhys(new Brick(), x0 + arm_length + r1-Math.sqrt(3)/2, y+ .5, true);
                    addPhys(new Brick(), x0 + arm_length + r1+Math.sqrt(3)/2, y+ .5, true);
                    addPhys(new Brick(), x0 + arm_length + r1-Math.sqrt(3), y, true);
                    addPhys(new Brick(), x0 + arm_length + r1+Math.sqrt(3), y, true);
                }
                
                break;
            case "Liquid":
                //Make globe for gas
                double r = 10;
                x0 = 12;
                y0 = 12;
                double spacing = .75;
                for(double th = 0; th < 2*Math.PI; th += spacing/r){
                    addPhys(new Fixed(), x0+r*Math.cos(th), y0+r*Math.sin(th), false);
                }
                
                break;
            case "Floor":
                for(double x = wWidth/4.0; x <= 3*wWidth/4.0; x++){
                    addPhys(new Fixed(), x, 7*wHeight/8.0, false);
                }
                addPhys(new Fixed(), wWidth/4.0 - Math.sqrt(3)/2, 7*wHeight/8.0 - .5, false);
                addPhys(new Fixed(), 3*wWidth/4.0 + Math.sqrt(3)/2, 7*wHeight/8.0 - .5, false);
                
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
        assert time > 0;
        if(!Execute.fileMode){
            //This way, when curser is the only thing, life is easier
            curser.act();
            
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
        }
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
            //1/4 is the centering offset from Screen.print()
            double cleft = curser.getX() - 1.0/4;
            double ctop = curser.getY() - 1.0/4;
            if(being.getX() >= cleft && being.getX() < cleft+curserW
            && being.getY() >= ctop && being.getY() < ctop+curserH
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
        double cx,cy;
        int cw,ch;
        
        public PrintRequest(double x, double y, int w, int h){
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
                            case CRYSTAL: 
                            particle = new Crystal();
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
                int n = density*cw;
                int nParts = 3*n*(n-1) + 1;//source: wiki centered hexagonal number
    
                // NEVERMIND -Too much trig - Prints all particles spiraling outward from the center 
                //INSTEAD - go Row by Row
                for(int a = 0; a < 2*n-1; a++){
                    y = cy + a*Math.sqrt(3)/2.0/density; //a*1/2*sqrt(3)
                    //distance in rows from center row
                    int offset = Math.abs(n-a-1);
                    //number of particles in this row
                    int rowLen = 2*n-1-offset;
                    for(int b = 0; b < rowLen; b++){
                        x = cx + (offset/2.0 + b)/density;
    
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
                            case CRYSTAL: 
                            particle = new Crystal();
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
                for(x = cx; x < lastCol; x+= 1.0/density){
                    int row = 0;
                    for(y = cy; y < lastRow; y += h){
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
                            case CRYSTAL: 
                            particle = new Crystal();
                            break;
                            default: particle = new Sand();
                        }
                        //Offset every other row by .5
                        double offset = 0;
                        if((row & 1) == 1)//odd row
                            offset = .5/density;
                        if(x+offset > 0 && x+offset < wWidth 
                            && y > 0 && y < wHeight){
                            addPhys(particle, x+offset, y, (curserType != Types.FIXED));
                        }
    
                        row++;//int row = (int) Math.round((b-cy)/h)
                    }
                }
                break;
    
                case 3:  //Hollow Circle (annulus)
                int r = cw; //inner radius
                int rings = ch*density; // # of rings progressing outwards
    
                for(int a = 0; a < rings; a++){
                    //radius of current ring
                    double R = r+a*1.0/density;
                    //Path Length (circumference) = 2*pi*r
                    double cir = 2*Math.PI*R;
                    //b = path length traveled
                    for(double b = 0; b < cir; b+= 1.0/density){
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
                            case CRYSTAL: 
                            particle = new Crystal();
                            break;
                            default: particle = new Sand();
                        }
                        //angle = 2pi * b / cir
                        double th = 2*b*Math.PI / cir;
                        x = cx + R*Math.cos(th);
                        y = cy + R*Math.sin(th);
    
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