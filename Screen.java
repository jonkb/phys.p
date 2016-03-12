import java.awt.image.*;
import java.awt.*;
import java.awt.event.*;//KeyListener, ActionListener, KeyEvent
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import java.util.ArrayList;
import java.util.Date;
//import java.lang.System;

public class Screen extends JPanel
implements KeyListener, ActionListener, MouseWheelListener, 
MouseListener, MouseMotionListener {
    Lab world; 
    
    //(int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().width*1/2;
    static final int width = 768;
    //(int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().height*1/2;
    static final int height = 576;
    
    //Delay between timer triggers
    private static int speed = 0;//10fps t = 100 ms //100fps (MAX)
    public static double maxD, zRatio;//10^- //For LinkedParticle.0001 - 00000001;
    private Timer timer;
    private BufferedImage back;
    public static String stem;
    public static int endFrame;
    private long startTime;
    
    private int scrollamount = 0;
    public boolean a = false; 
    public byte paused = 0; 
    public int frameCount = 1;
    private int subFrameCount = 0;
    
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
                world.density = (byte) ((world.density - 2)%10 + 1);//1 to 10
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
            case KeyEvent.VK_SPACE:
                paused = (byte) ((paused + 1) & 1);// 0 to 1
                break;
            case KeyEvent.VK_ESCAPE:
                a = true;
                break;
            case KeyEvent.VK_R:
                if(recording)
                    recording = false;
                else
                {
                    frameCount = 0;
                    recording = true;
                }
                break;
            default:
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
            world.curser.setLocation((int)(e.getX()/zRatio), (int)(e.getY()/zRatio));
            world.print();
        }
        if(e.getButton() == MouseEvent.BUTTON3)
        {
            //System.out.println("erase");
            world.curser.setLocation((int)(e.getX()/zRatio), (int)(e.getY()/zRatio));
            world.erase();
        }
    }
    public void mouseEntered(MouseEvent e){onScreen = true;}
    public void mouseExited(MouseEvent e){onScreen = false;}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    
    //MouseMotionListener commands
    public void mouseMoved(MouseEvent e){
        world.curser.setLocation((int)(e.getX()/zRatio), (int)(e.getY()/zRatio));}
    public void mouseDragged(MouseEvent e) {}

    //ActionListener command for timer - One Frame
    public void actionPerformed(ActionEvent e){
        subFrameCount++;
        if(paused == 0){
            if (!onScreen)
                requestFocusInWindow();
            
            /**
             * This next section adjusts the number of steps per frame
             * Movement is resolved many times per frame so that 
             *   particles do not pass through each other
             * Number of subframes = MaxV*10
             */
            //Have at least .001/maxD subframes. prevents a jumpy first frame
            double maxV = .001;
            //Find the biggest V
            for(Being being: world.beings){
                if(being instanceof Physical){
                    Physical phys = (Physical) being;
                    if(Math.abs(phys.velocity.Mag()) > maxV)
                        maxV = Math.abs(phys.velocity.Mag());
                }
            }
            
            if(maxV/maxD > 1)//more than one subframe required
                world.time = maxD/maxV;
            else
                world.time = 1;
            assert world.time > 0;
            
            for(Being being: world.beings){
                being.act();
            }
            for(Being being: world.beings){
                being.updateXY();//move
            }
            world.act();
        }
        //paused
        else{
            world.act();
            world.curser.act();
            if(subFrameCount > 20)
            {
                if(!Execute.fileMode)
                    repaint();
                subFrameCount = 0;
            }
        }
        //This makes every frame happen 10 times per real frame
        //if(subFrameCount % Math.ceil(.1/world.time) == 0)
        //New phrasing stops the glitch where two frames happen 
        //      in a row because world.time decreases slightly
        if(subFrameCount > Math.ceil(.1/world.time))
        {
            frameCount++;
            if(!Execute.fileMode)
                repaint();
            if(recording)
                snap();
            subFrameCount = 0;
        }
        
        /**
         * For ridiculous simulations and impatient people*/
        if(subFrameCount % 10000 == 0){
            Date now = new Date();
            System.out.println(subFrameCount+" of "+Math.ceil(.1/world.time)+" needed. "+"time: "+now.toString());
        }
        
        if(a || frameCount == endFrame)
            stop();
    }
    
    public Screen()
    {
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
        timer = new Timer(speed, this);
        timer.setInitialDelay(1000);
        world = new Lab(this);
        
        startTime = System.currentTimeMillis();
        if(!Execute.fileMode)
            repaint();//Adds a frame zero
        if(recording)
            snap();
        timer.start();
    }
    
    /**
     * Constructor
     */
    public Screen(int endF, double precision, String saveFile, String SIM, double zoom, boolean start)
    {
        endFrame = endF;
        maxD = precision;
        stem = saveFile;
        zRatio = zoom;
        
        System.out.print("Running Simulation: Precision= "+maxD+
            ": Save file= "+stem);
        if(Execute.fileMode)
        {
            recording = true;
            System.out.println(": FILE ONLY MODE");
        }
        else
        {
            recording = false;
            System.out.println(": Interactive Mode");
            addKeyListener(this);
            addMouseWheelListener(this);
            addMouseListener(this);
            addMouseMotionListener(this);
        }
        
        back = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        setPreferredSize(new Dimension(width, height));
        
        a = false;
        timer = new Timer(speed, this);
        timer.setInitialDelay(1000);
        world = new Lab(this);
        
        world.sim(SIM);
        
        if(start){
            start();
        }
    }
    
    
    public void lose()
    {
        a = true;
        repaint();
        
        stop();
    }
    /**
     * Start the Timer. Also snap a frame zero
     */
    public void start(){
        startTime = System.currentTimeMillis();
        if(!Execute.fileMode)
            repaint();//Adds a frame zero (though frameCount = 1 for file format)
        if(recording)
            snap();
        timer.start();
    }
    /** 
     * Stop the Timer
     */
    public void stop()
    {
        timer.stop();
        System.out.println("Simulation Complete");
    }
    public void paint(Graphics g)
    {
        //System.out.println("painting");
        g.setFont(new Font("font", Font.BOLD, 11));
            
        if(a)
        {
            g.setColor(Color.white);
            g.drawString("Simulation Complete", width/ 2, height/ 2);
        }
        else
        {
            //Draw the black background
            g.setColor(Color.black);
            g.fillRect(0, 0, width, height);
            /**
             * Make the top left corner display
             */
            g.setColor(Color.white);
            //g.drawString("fp's': "+20/world.time , 5, 15);
            g.drawString("simulation time: "+ 
                milTimeToStr(System.currentTimeMillis()-startTime), 5, 15);
            g.drawString("frame count: "+ frameCount, 5, 26);
            g.drawString("subframes: "+ subFrameCount, 5, 37);
            g.drawString("print density: "+ world.density, 5, 48);
            g.drawString("precision: "+ maxD, 5, 59);            
            g.drawString("zoom: "+ zRatio, 5, 70); 
            if(recording)
                g.drawString("recording  |  save destination: "+stem, 5, 81);
            else
                g.drawString("not recording", 5, 81);
            if(paused == 0)
                g.drawString("RUNNING", 5, 92);
            else
                g.drawString("PAUSED", 5, 92);
            /**
             * Print the image of every being
             */
            for(Being being: world.beings)
                g.drawImage(being.getImage(), (int)(being.getDbX()*zRatio), (int)(being.getDbY()*zRatio), null);
        }
    }
    public void snap(){
        System.out.println("Frame "+frameCount+": "+
            milTimeToStr(System.currentTimeMillis()-startTime));
        try{
            paint(back.getGraphics());
            File saveFile = new File(stem+frameCount+".png");
            ImageIO.write(back, "png", saveFile);
        }
        catch(IOException e){}
        saveSim();
    }
    public void saveSim(){
        try{
            File saveFile = new File(stem+frameCount+".phys");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile));
            oos.writeObject(Execute.exportData(world.beings));
        }
        catch(IOException e){}
    }
    
    /**
     * Convert a time in milliseconds to a String
     * with the format "hh:mm:ss"
     */
    public static String milTimeToStr(long millis)
    {
        long sec = Math.round(millis/1000);
        long ss = sec % 60;
        long mm = (sec / 60) % 60;
        long hh = sec / 3600;
        return String.format("%02d:%02d:%02d", hh, mm, ss);
    }
}

//class BeingList extends ArrayList<Being>{}