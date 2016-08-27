import javax.swing.*;
import java.util.*;//stack, arraylist
public class RunSim implements Runnable{
    private Screen mamma;
    public long startTime, prevTime;
    public int rate = 10;
    private static boolean end = false;
    private static boolean paused = false; 
    //How many are left to act
    //public static ArrayList<ActPhys> acting = new ArrayList<ActPhys>();
    public Queue<Thread> acting = new LinkedList<Thread>();
    public RunSim(Screen mom){
        mamma = mom;
        startTime = System.currentTimeMillis();
    }
    public void togglePause(){
        if(paused){
            paused = false;
            prevTime = System.currentTimeMillis();
            debugShout("Unpausing.", 1);
        }
        else{
            paused = true;
            debugShout("Pausing.", 1);
        }
    }
    public void pause(){
        paused = true;
        debugShout("Pausing.", 1);
    }
    public void unpause(){
        paused = false;
        prevTime = System.currentTimeMillis();
        debugShout("Unpausing.", 1);
    }
    public boolean paused(){
        return paused;
    }
    public void end(){
        end = true;
    }
    public boolean ended(){
        return end;
    }
    public void run(){
        debugShout("Simulation Started at: "+System.currentTimeMillis(), 0);
        prevTime = System.currentTimeMillis();
        debugShout("Wait a second.");
        try{
            Thread.sleep(rate);
        }catch(Exception e){System.out.println(e);}
        
        debugShout("Starting the main loop now");
        try{
            loop();
        }catch(Exception e){
            System.out.println("Exception at "+System.currentTimeMillis()+": "+e);
            throw e;
        }
        System.out.println("Simulation Ended at: "+System.currentTimeMillis());
    }
    private void loop(){
        while(!end){
            //Needed for keyboard input
            if (!mamma.onScreen)
                mamma.requestFocusInWindow();
            
            if(paused){
                mamma.world.act();
                mamma.rps();
                try{
                    Thread.sleep(rate);
                }catch(Exception e){System.out.println(e);}
            }
            else{
                mamma.subFrameCount++;
                /**
                 * This next section adjusts the number of steps per frame
                 * Movement is resolved many times per frame so that 
                 *   particles do not pass through each other
                 * Number of subframes = MaxV*10
                 */
                //Have at least .001/maxD subframes. prevents a jumpy first frame
                double maxV = .001;
                //Find the biggest V
                for(BiList.Node n = mamma.world.beings.a1; n != null; n = n.getNextA()){
                    Being being = (Being) n.getVal();
                    if(being instanceof Physical){
                        Physical phys = (Physical) being;
                        if(Math.abs(phys.velocity.Mag()) > maxV)
                            maxV = Math.abs(phys.velocity.Mag());
                    }
                }
                if(maxV/mamma.maxD > 1)//more than one subframe required
                    mamma.world.time = mamma.maxD/maxV;
                else
                    mamma.world.time = 1;
                assert mamma.world.time > 0;
                
                reportMem("A");
                /* New version implementing massive multithreading
                 */
                //act
                mamma.world.act();
                for(BiList.Node n = mamma.world.beings.a1; n != null; n = n.getNextA()){
                    Being being = (Being) n.getVal();
                    ActPhys a = new ActPhys(being, 0);
                    Thread t = new Thread(a);
                    acting.add(t);
                    t.start();
                }
                reportMem("B");
                for(Thread t = acting.poll(); t!= null; t = acting.poll()){
                    try{
                        t.join();
                    }catch(Exception e){System.out.println(e);}
                }
                //updateXY
                for(BiList.Node n = mamma.world.beings.a1; n != null; n = n.getNextA()){
                    Being being = (Being) n.getVal();
                    ActPhys a = new ActPhys(being, 1);
                    Thread t = new Thread(a);
                    acting.add(t);
                    t.start();
                }
                for(Thread t = acting.poll(); t!= null; t = acting.poll()){
                    try{
                        t.join();
                    }catch(Exception e){System.out.println(e);}
                }
                reportMem("C");
                debugShout("Subframe: "+mamma.subFrameCount+" of "+Math.ceil(1/mamma.world.time));
                //Frame
                if(mamma.subFrameCount >= 1/mamma.world.time){
                    mamma.frameCount++;
                    String f = "Frame: "+mamma.frameCount;
                    if(paused)
                        debugShout(f+": paused", 3);
                    else
                        debugShout(f);
                    mamma.rps();
                    try{
                        Thread.sleep(rate);
                    }catch(Exception e){System.out.println(e);}
                    mamma.subFrameCount = 0;
                }
            }
            
            /*
            // By invoking this later, it actually works. 
            // Repaint() must be called from EDT
            // Try switching it to a "synchronous" method of screen
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    //Everything needs to be in place before it is drawn on the canvas
                    for(Thread t = acting.poll(); t!= null; t = acting.poll()){
                        try{
                            t.join();
                        }catch(Exception e){System.out.println(e);}
                    }
                    mamma.repaint();
                    if(mamma.recording)
                        mamma.snap();
                }
            });*/
        }
    }
    
    public static void reportMem(String tag){
        debugShout("Heap memory at "+tag+"(f/t/m):"+(Runtime.getRuntime().freeMemory() / 1000)+"k/"+(Runtime.getRuntime().totalMemory() / 1000)+"k/"+(Runtime.getRuntime().maxMemory() / 1000)+"k", 3);
    }
    public static void debugShout(String message){
        Screen.debugShout(message);
    }
    public static void debugShout(String message, int p){
        Screen.debugShout(message, p);
    }
}