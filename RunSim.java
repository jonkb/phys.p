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
        debugShout("Simulation Ended at: "+System.currentTimeMillis(), 0);
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
                 * Number of subframes = MaxV / maxD
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
                 * Also new, put a couple beings on each thread
                 */
                //act
                mamma.world.act();
                
                int actors_per_thread = 1;
                if(Execute.max_threads > 0) // 0 means unlimited
                    actors_per_thread = (int) Math.ceil( mamma.world.beings.sizeA() / (double) Execute.max_threads);
                Being[] next = new Being[actors_per_thread];
                int next_size = 0;
                
                for(BiList.Node n = mamma.world.beings.a1; n != null; n = n.getNextA()){
                    Being being = (Being) n.getVal();
                    next[next_size] = being;
                    next_size++;
                    
                    if(next_size >= actors_per_thread){
                        ActPhys a = new ActPhys(next, 0);
                        Thread t = new Thread(a);
                        acting.add(t);
                        t.start();
                        //This should work
                        next = new Being[actors_per_thread];
                        next_size = 0;
                    }
                }
                reportMem("B");
                //poll() retrieves & removes
                for(Thread t = acting.poll(); t!= null; t = acting.poll()){
                    try{
                        t.join();//"Waits for this thread to die"
                    }catch(Exception e){System.out.println(e);}
                }
                //updateXY
                
                next = new Being[actors_per_thread];
                next_size = 0;
                
                for(BiList.Node n = mamma.world.beings.a1; n != null; n = n.getNextA()){
                    Being being = (Being) n.getVal();
                    next[next_size] = being;
                    next_size++;
                    
                    if(next_size >= actors_per_thread){
                        ActPhys a = new ActPhys(next, 1);
                        Thread t = new Thread(a);
                        acting.add(t);
                        t.start();
                        //This should work - hopefully
                        next = new Being[actors_per_thread];
                        next_size = 0;
                    }
                }
                for(Thread t = acting.poll(); t!= null; t = acting.poll()){
                    try{
                        t.join();
                    }catch(Exception e){System.out.println(e);}
                }
                reportMem("C");
                //Impatience message. Prints out the current subframe and how many to go every 1e4 subframes
                int sf_depth = (mamma.subFrameCount % 1e4)==0 ? 1: 2;
                int totalFrames = (int)Math.ceil(1/mamma.world.time);
                int percent = 100* mamma.subFrameCount/totalFrames;
                debugShout("Subframe: "+mamma.subFrameCount+" of "+totalFrames+" ("+percent+"%)", sf_depth);
                if(Execute.debugging > 0 && (mamma.subFrameCount % 1e4)==0)
                    mamma.saveSim("temp");
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
                    if(mamma.frameCount >= mamma.endFrame)
                        end();
                }
            }
        }
    }
    
    public static void reportMem(String tag){
        debugShout("Heap memory at "+tag+"(f/t/m):"+(Runtime.getRuntime().freeMemory() / 1000)+"k/"
            +(Runtime.getRuntime().totalMemory() / 1000)+"k/"+(Runtime.getRuntime().maxMemory() / 1000)+"k", 3);
    }
    public static void debugShout(String message){
        Screen.debugShout(message);
    }
    public static void debugShout(String message, int p){
        Screen.debugShout(message, p);
    }
}