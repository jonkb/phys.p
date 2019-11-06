import java.awt.image.*;
import java.awt.*;
import java.awt.event.*;//KeyListener, ActionListener, KeyEvent
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import java.util.ArrayList;
import java.util.Date;
//import java.lang.System;

public class Screen extends JPanel implements KeyListener, 
MouseWheelListener, MouseListener, MouseMotionListener {
    Lab world; 
    RunSim run;
    
    //(int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().width*1/2;
    static final int width = 768;
    //(int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().height*1/2;
    static final int height = 576;
    
    public static double maxD, zRatio;//10^- //For LinkedParticle.0001 - 00000001;
    private BufferedImage back;
    public static String stem;
    public static int endFrame;
    
    private int scrollamount = 0;
    public int frameCount = 1;
    public int subFrameCount = 0;
    public boolean a = false; 
    public byte paused = 0; 
    
    public boolean onScreen;
    public static boolean recording = false;
    
    public int Width() {
        return (int)(width/zRatio);
    }
    public int Height() {
        return (int)(height/zRatio);
    }
    
    //Start of all Listeners' commands
    //keyListener commands
    public void keyReleased(KeyEvent e)
    {
        if(e.getKeyCode() == e.VK_SHIFT)
            world.shiftDown = false;
        if(e.getKeyCode() == e.VK_CONTROL)
            world.ctrlDown = false;
    }
    public void keyTyped(KeyEvent e){}
    public void keyPressed(KeyEvent e) {
        int c = e.getKeyCode();
        
        switch(c)
        {
            case KeyEvent.VK_SHIFT: 
                world.shiftDown = true;
                break;
            case KeyEvent.VK_CONTROL: 
                world.ctrlDown = true;
                break;
            case KeyEvent.VK_RIGHT:
                /**
                 * trace 1: 1%10 +1 = 2
                 * trace 9: 9%10 +1 = 10
                 * trace 10: 10%10 +1 = 1
                 */
                world.density = (byte) (world.density%10 + 1);//1 to 10
                break;
            case KeyEvent.VK_LEFT:
                /**
                 * trace 1: (1-2)%10+1 = 10
                 * trace 2: (2-2)%10+1 = 1
                 * trace 10: (10-2)%10+1 = 9
                 */
                world.density = (byte) ((world.density - 1)%10);//1 to 10
                break;
            case KeyEvent.VK_UP:
                world.curserShape = (byte) ((world.curserShape + 1)%4);//0 to 3
                break;
            case KeyEvent.VK_DOWN:
                /**
                 * trace 0: (0-1)%4 = 3
                 * trace 1: (1-1)%4 = 0
                 * trace 3: (3-1)%4 = 2
                 */
                world.curserShape = (byte) ((world.curserShape-1)%4);//0 to 3
                break;
            case KeyEvent.VK_0:
                world.curserType = Types.FIXED;
                break;
            case KeyEvent.VK_1:
                world.curserType = Types.SAND;
                break;
            case KeyEvent.VK_2:
                world.curserType = Types.WATER;
                break;
            case KeyEvent.VK_3:
                world.curserType = Types.BOMB;
                break;
            case KeyEvent.VK_4:
                world.curserType = Types.BRICK;
                break;
            case KeyEvent.VK_5:
                world.curserType = Types.RUBBER;
                break;
            case KeyEvent.VK_6:
                world.curserType = Types.CRYSTAL;
                break;
            case KeyEvent.VK_SPACE:
                run.togglePause();
                //paused = (byte) ((paused + 1) & 1);// 0 to 1
                break;
            case KeyEvent.VK_ESCAPE:
                run.end();
                break;
            case KeyEvent.VK_R:
                if(recording)
                    recording = false;
                else{
                    frameCount = 0;
                    recording = true;
                }
                break;
        }
    }
    //MouseWheelListener command
    public void mouseWheelMoved(MouseWheelEvent e){
        scrollamount += e.getWheelRotation();  
        e.consume();
    }
    
    //A method useful for a MouseWheelListener to have
    public int getScroll(){
        int t = scrollamount;
        scrollamount = 0;
        if(t < 6)
            return t*t*t;
        else if(t < 11)
            return 75*t-250;
        else
            return 500;
    }
    
    //MouseListener commands
    public void mouseClicked(MouseEvent e){
        if(e.getButton() == MouseEvent.BUTTON1)
        {
            //System.out.println("print");
            world.curser.setLocation(e.getX()/zRatio, e.getY()/zRatio);
            world.print();
        }
        if(e.getButton() == MouseEvent.BUTTON3)
        {
            //System.out.println("erase");
            world.curser.setLocation(e.getX()/zRatio, e.getY()/zRatio);
            world.erase();
        }
    }
    public void mouseEntered(MouseEvent e){onScreen = true;}
    public void mouseExited(MouseEvent e){onScreen = false;}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    
    //MouseMotionListener commands
    public void mouseMoved(MouseEvent e){
        world.curser.setLocation(e.getX()/zRatio, e.getY()/zRatio);}
    public void mouseDragged(MouseEvent e) {}
    
    /**Constructor
     * All the arguments are replaced with the defaults
     */
    public Screen(){
        System.out.print("Running Simulation: Precision= "+maxD+
            ": Save file= "+stem);
        if(Execute.fileMode)
        {
            System.out.println(": FILE ONLY MODE");
            recording = true;
        }
        else
        {
            System.out.println(": Interactive Mode");
            addKeyListener(this);
            addMouseWheelListener(this);
            addMouseListener(this);
            addMouseMotionListener(this);
        }
        
        back = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        setPreferredSize(new Dimension(width, height));
        
        a = false;
        world = new Lab(this);
        
        if(!Execute.fileMode)
            repaint();//Adds a frame zero
        if(recording)
            snap();
        //Start loop
    }
    
    /**Constructor
     * endF: Frame at which to end the simulation
     * precision: dt for Euler approximation
     * saveFile: place to save images and .phys data
     * SIM: name of a simulation to load automatically
     * zoom: zoom factor
     * start: whether or not to start automatically. 
     *      If this is false, start() should be called externally
     */
    public Screen(int endF, double precision, String saveFile, String SIM, double zoom, boolean start){
        endFrame = endF;
        maxD = precision;
        stem = saveFile;
        zRatio = zoom;
        
        System.out.print("Running Simulation: Precision= "+maxD+
            ": Save file= "+stem);
        if(Execute.fileMode){
            recording = true;
            System.out.println(": FILE ONLY MODE");
        }
        else{
            recording = true;//TEMP
            System.out.println(": Interactive Mode");
            addKeyListener(this);
            addMouseWheelListener(this);
            addMouseListener(this);
            addMouseMotionListener(this);
        }
        
        back = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        setPreferredSize(new Dimension(width, height));
        
        a = false;
        world = new Lab(this);
        
        world.sim(SIM);
        
        if(start){
            start();
        }
    }
    
    
    public void lose(){
        a = true;
        repaint();
        
        stop();
    }
    /**
     * Start the Timer. Also snap a frame zero
     */
    public void start(){
        run = new RunSim(this);
        rps();
        //start loop
        Thread loop = new Thread(run);
        loop.start();
    }
    /** 
     * Stop the Timer
     */
    public void stop(){
        //end loop
        run.end();
    }
    //Repaint, snap
    public synchronized void rps(){
        if(!Execute.fileMode)
            repaint();
        if(recording)
            snap();
    }
    public void paint(Graphics g){
        //System.out.println("painting");
        g.setFont(new Font("font", Font.BOLD, 11));
        
        if(!Execute.fileMode && run.ended()){
            g.setColor(Color.white);
            g.drawString("Simulation Complete", 10, height/ 2);
        }
        else{
            //Draw the black background
            g.setColor(Color.black);
            g.fillRect(0, 0, width, height);
            /**
             * Make the top left corner display
             */
            g.setColor(Color.white);
            //g.drawString("fp's': "+20/world.time , 5, 15);
            g.drawString("simulation time: "+ 
                milTimeToStr(System.currentTimeMillis()-Execute.t0), 5, 15);
            g.drawString("frame count: "+ frameCount, 5, 26);
            g.drawString("subframes: "+ subFrameCount, 5, 37);
            g.drawString("print density: "+ world.density, 5, 48);
            g.drawString("precision: "+ maxD, 5, 59);            
            g.drawString("zoom: "+ zRatio, 5, 70); 
            if(recording)
                g.drawString("recording  |  save destination: "+stem, 5, 81);
            else
                g.drawString("not recording", 5, 81);
            if(!Execute.fileMode){
                if(run.paused())
                    g.drawString("PAUSED", 5, 92);
                else
                    g.drawString("RUNNING", 5, 92);
            }
            /**
             * Print the image of every being
             */
            //The offset centers images (of particles at least) around their location
            int offset = 0;
            if(zRatio >= 4)
                offset = (int)Math.ceil(zRatio/4.0);
            for(BiList.Node n = world.beings.o1; n != null; n = n.getNext()){
                Being being = (Being) n.getVal();
                g.drawImage(being.getImage(), (int)Math.round(being.getX()*zRatio)-offset, 
                (int)Math.round(being.getY()*zRatio)-offset, null);
            }
        }
    }
    /**
     * Given absolute coordinates of a particle, draw an image to be placed over the point
     * Should work better than drawCircle or fillCircle
     *
    private BufferedImage genCircle(double X, double Y, Color color){
        //absolute radius is always 1/4
        double radius = zRatio / 4; //cells
        int cx = (int) Math.round(X*zRatio); //cells
        int cy = (int) Math.round(Y*zRatio); //cells
        int w = 2*Math.ceil(radius) + 1; //+1 for center dot
        int h = w;
        double px, py;
        BufferedImage circle = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        for(int px = 0; px < w; px++){
            for(int py = 0; py < h; py++){
                //absolute coordinates of the current pixel in the image
                pX = ( cx - Math.ceil(radius) + px )/zRatio;
                pY = ( cy - Math.ceil(radius) + py )/zRatio;
                //p[delta](X/Y)
                pdX = pX - X;
                pdY = pY - Y;
                //pdx^2+pdy^2 < (1/4)^2
                if( pdX*pdX + pdY*pdY < 1/16)
                    circle.setRGB(px,py,color.getRGB());
            }
        }
        return circle;
    }*/
    /**
     * Saves the screen to file
     */
    public void snap(){
        /* System.out.println("Frame "+frameCount+": "+
         *   milTimeToStr(System.currentTimeMillis()-Execute.t0));
         *   This is duplicated in RunSim, so there were two messages
        */  
        try{
            paint(back.getGraphics());
            File saveFile = new File(stem+frameCount+".png");
            ImageIO.write(back, "png", saveFile);
        }
        catch(IOException e){}
        saveSim();
    }
    public void snap(String tag, boolean savePhys){
        /* System.out.println("Frame "+frameCount+": "+
         *   milTimeToStr(System.currentTimeMillis()-Execute.t0));
         *   This is duplicated in RunSim, so there were two messages
        */  
        try{
            paint(back.getGraphics());
            File saveFile = new File(stem+tag+".png");
            ImageIO.write(back, "png", saveFile);
        }
        catch(IOException e){}
        if(savePhys)
            saveSim(tag);
    }
    public void saveSim(){
        try{
            File saveFile = new File(stem+frameCount+".phys");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile));
            oos.writeObject(Execute.exportData(world.beings, frameCount, subFrameCount));
        }
        catch(IOException e){}
    }
    public void saveSim(String tag){
        try{
            File saveFile = new File(stem+tag+".phys");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile));
            oos.writeObject(Execute.exportData(world.beings, frameCount, subFrameCount));
        }
        catch(IOException e){}
    }
    
    /**
     * Convert a time in milliseconds to a String
     * with the format "hh:mm:ss"
     */
    public static String milTimeToStr(long millis){
        long sec = Math.round(millis/1000);
        long ss = sec % 60;
        long mm = (sec / 60) % 60;
        long hh = sec / 3600;
        return String.format("%02d:%02d:%02d", hh, mm, ss);
    }
    
    public static void debugShout(String message, int depth){
        /* Generally: 0 = Once a sim
         * 1= once a frame
         * 2= multiple per frame
        */
        // How much to show - High depth messages only are printed if debugging is high
        if(Execute.debugging >= depth){
            System.out.println(message+ " at "+milTimeToStr(System.currentTimeMillis() - Execute.t0));
        }
    }
    public static void debugShout(String message){
        /* Default: 1*/
        if(Execute.debugging >= 1){
            System.out.println(message+ " at "+milTimeToStr(System.currentTimeMillis() - Execute.t0));
        }
    }
}

//class BeingList extends ArrayList<Being>{}