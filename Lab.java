import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.lang.System;

public class Lab {
    Screen screen;
    private final int wWidth, wHeight;
    public ArrayList<Being> beings, bin;
    //public ArrayList<Being> actors;// = beings minus fixed
    //public ArrayList<Being> bin;//to be deleated
    
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
    
    int CurserX;
    
    public void addBeing(Being being, int x, int y)
    {
        being.world = this;
        being.setLocation(x, y);
        beings.add(being);
        being.addedToWorld();
    }
    public void addPhys(Physical phys, double x, double y)
    {
        phys.world = this;
        phys.setLocation(x, y);
        beings.add(phys);
        //phys.addedToWorld(); This line is ommitted because it forces the phys back onto the grid
    }
    public void addPart(Particle part)
    {
        beings.add(part);
    }
    public void removeBeing(Being being)
    {
        bin.add(being);
    }
    //To be called every frame
    public void dumpBin(){
        if(bin.size() > 0){
            for(Being being: bin)
                beings.remove(being);
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
    public Being getBeingAt(int x, int y)//!!!!! Not Efficient Or Useful !!!!!
    {
        Being here = null;
        assert y < wHeight && y >= 0 && x < wWidth && x >= 0;
        for(Being being: beings)
        {
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
        
        beings = new ArrayList<Being>(500);
        bin = new ArrayList<Being>();
        
        curser = new Curser();
        if(!Execute.fileMode)
            addBeing(curser, wWidth/2, wHeight/2);
        curserType = Types.SAND;
        
        System.out.println("new world: w="+wWidth+"h="+wHeight);
    }
    
    /**
     * To be called from textInterface()
     */
    public Lab(int w, int h){
        wWidth = w;
        wHeight = h;
        
        beings = new ArrayList<Being>(500);
        bin = new ArrayList<Being>();
        
        System.out.println("new world: w="+wWidth+"h="+wHeight);
    }
    
    /**
     * Loads a pre-created simulation
     */
    public boolean sim(String sim)
    {
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
                            addPhys(new Fixed(), a, b);
                    }
                    else
                    {
                        addPhys(new Fixed(), a, 298);
                        addPhys(new Fixed(), a, 299);
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
                    addPhys(new Brick(), a, 100);
                    addPhys(new Brick(), a+.5, 100 + Math.sqrt(3)/2);
                    addPhys(new Brick(), a, 100 + Math.sqrt(3));
                    addPhys(new Brick(), a+.5, 100 + 3*Math.sqrt(3)/2);
                    //Add two Bases
                    if(a < 106 || a > 123)//6 by 2
                    {
                        addPhys(new Fixed(), a, 105);
                        addPhys(new Fixed(), a+.5, 105 + Math.sqrt(3)/2);
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
                            addPhys(new Rubber(), a, b);
                        else if(a==40 || a==40+1.2*8 || b==46){
                            addPhys(new Fixed(), a, b);
                            addPhys(new Fixed(), a+.3, b+.3);
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
                    addPhys(new Brick(), a, 300);
                    addPhys(new Brick(), a+.5, 300 + Math.sqrt(3)/2);
                    addPhys(new Brick(), a, 300 + Math.sqrt(3));
                    addPhys(new Brick(), a+.5, 300 + 3*Math.sqrt(3)/2);
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
                        addPhys(new Brick(), 320.5, a-29-Math.sqrt(3)/2); 
                    else
                        addPhys(new Brick(), 339.5, a-59-Math.sqrt(3)/2);
                }
                /**
                 * Add Anchors
                 */
                Brick Anchor = new Brick();
                Anchor.fixed = true;
                addPhys(Anchor, 320.5, 270-Math.sqrt(3)/2);
                Brick Anchor2 = new Brick();
                Anchor2.fixed = true;
                addPhys(Anchor2, 339.5, 270-Math.sqrt(3)/2);
                break;
            case "Brick":
                /**
                 * Build a ledge
                 */
                for(int a = 100; a < 110; a++)
                {
                    addPhys(new Fixed(), a, 100);
                    addPhys(new Fixed(), a, 101);
                    addPhys(new Fixed(), a, 102);
                    
                    addPhys(new Fixed(), a+.5, 100.5);
                    addPhys(new Fixed(), a+.5, 101.5);
                    addPhys(new Fixed(), a+.5, 102.5);
                }
                /**
                 * Build a Brick
                 */
                for(int a = 108; a < 118; a++)
                {
                    addPhys(new Brick(), a, 95);
                    addPhys(new Brick(), a+.5, 95+Math.sqrt(3)/2);
                    addPhys(new Brick(), a, 95+Math.sqrt(3));
                    addPhys(new Brick(), a+.5, 95+3*Math.sqrt(3)/2);
                }
                break;
            case "SandFall":
                for(int a = 200; a < 210; a++)
                    for(int b = 200; b < 210; b++)
                    {
                        //Large Square
                        addPhys(new Sand(), a, b);
                        //Small Dense square underneith
                        addPhys(new Fixed(), 203 + (a-200)/3, 213 + (b-200)/3);//303-306
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
                        addPhys(new Brick(), a, 10);
                    addPhys(new Brick(), a+.5, 10 + Math.sqrt(3)/2);
                    //Add two Bases
                    if((a>10 && a < 13) || a > 17)//2 by 1
                    {
                        addPhys(new Fixed(), a, 13);
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
    
    public void print()
    {
        System.out.println("Print: ("+curser.getX()+","+curser.getY()+")");
        Particle particle = new Sand();
        /**
         * 0: Rectangle
         * 1: Hexagon
         * 2: Isometric Rectangle
         * 3: Hollow Circle
         */
        switch(curserShape)
        {
            case 0: //Rectangle
            //These lines drastically decrese the time spent resizing the array
            int newParts = curserW*curserH*density*density;
            beings.ensureCapacity(newParts+10);
            
            double x;
            double y;
            for(int a = 0; a <= (curserW-1) * density ; a++)
            {
                for(int b = 0; b <= (curserH-1) * density ; b++)
                {
                    switch(curserType)
                    {
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
                    x = curser.getX() + a / (double) density;
                    y = curser.getY() + b / (double) density;
                    
                    if(x > 0 && x < wWidth
                    && y > 0 && y < wHeight)
                        addPhys(particle, x, y);
                }
            }
            break;
            
            case 1:  //Hexagon
            //Predict number based on density and size and ensure capacity
            //which hexagonal number (number of rings
            int n = density*curserW + 1;
            int nParts = 3*n*(n-1) + 1;//source: wiki centered hexagonal number
            beings.ensureCapacity(nParts+10);
            
            // NEVERMIND -Too much trig - Prints all particles spiraling outward from the center 
            //INSTEAD - go Row by Row
            for(int a = 0; a < 2*(n+1); a++)
            {
                y = curser.getY() + a*Math.sqrt(3)/2.0/density; //a*1/2*sqrt(3)
                //number of particles in this row
                int rowLen = 2*n-1-Math.abs(n-a-1);
                //distance in rows from center row
                int offSet = Math.abs(n-a-1);
                for(int b = 0; b < rowLen; b++)
                {
                    x = curser.getX() + (offSet/2.0 + b)/density;
                    
                    switch(curserType)
                    {
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
                    if(x > 0 && x < wWidth && y > 0 && y < wHeight)
                        addPhys(particle, x, y);
                }
            }
            break;
            
            case 2:  //Isometric Rectangle
            double h = Math.sqrt(3)/2/density;//Height of one row
            double lastCol = curser.getX()+curserW;
            double lastRow = curser.getY()+curserH;
            for(double a = curser.getX(); a <= lastCol; a+= 1/density)
            {
                int row = 0;
                for(double b = curser.getY(); b <= lastRow; b += h)
                {
                    switch(curserType)
                    {
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
                    if(a+offSet > 0 && a+offSet < wWidth && b > 0 && b < wHeight)
                        addPhys(particle, a+offSet, b);
                    
                    row++;//int row = (int) Math.round((b-curser.getY())/h)
                }
            }
            break;
            
            case 3:  //Hollow Circle
            int r = curserW; //inner radius
            int rings = curserH; // # of rings progressing outwards
            
            for(int a = 0; a < rings; a++)
            {
                //Path Length (circumference) = 2*pi*r
                int cir = (int) Math.floor(2*Math.PI*(r+a));
                for(int b = 0; b < cir; b++)
                {
                    switch(curserType)
                    {
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
                    x = curser.getX() + (r + a)*Math.cos(th);
                    y = curser.getY() + (r + a)*Math.sin(th);
                    
                    if(x > 0 && x < wWidth && y > 0 && y < wHeight)
                        addPhys(particle, x, y);
                }
            }
            break;
            
            default:
            break;
        }
    }
    
    public void erase()
    {
        for(Being being: beings)
            if(being.getX() >= curser.getX() && being.getX() < curser.getX()+curserW
            && being.getY() >= curser.getY() && being.getY() < curser.getY()+curserH
            && being != curser)
                removeBeing(being);    
        beings.trimToSize();//Memory optimization
    }
    
    public String toString(){
        String res = "Lab:\n";
        for(Being being: beings){
            res+= being + "\n";
        }
        return res;
    }
}