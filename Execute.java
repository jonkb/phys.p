import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
public class Execute{
    static Screen screen;
    public static long t0;
    //0: just a few messages 1:a few each frame 2: lots
    public static int debugging = 2;
    
    //Execution Options:
    public static boolean 
        fileMode = false,
        keepPre = false;
    private static int 
        endF = 1000000,
        simNum = 24;
    private static double
        precision = .1,//.00000005;
        zoom = 2;
    private static String 
        SIM = "",//"SandFall",
        saveFile = "Tests/"+SIM+"/"+simNum+".";
    
    
    public static void main(String[] args){
        if(args.length > 0)
            debugging = Integer.parseInt(args[0]);
        t0 = System.currentTimeMillis();
        System.gc();
        
        if( SIM.equals(""))
            saveFile = "Tests/"+simNum+".";
        
        screen = new Screen(endF, precision, saveFile, SIM, zoom, true);
        if(!fileMode){
            JFrame frame = new JFrame("Particles");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            frame.getContentPane().add(screen);
            frame.pack();
            frame.setVisible(true);
        }
    }
    /**
     * Loads data from a file and resumes the simulation
     */
    public static void load(String phys){
        File f = new File(phys);
        //If it is a .phys file
        if(f.getName().substring(f.getName().lastIndexOf(".")).equals(".phys"))
        {
            try
            {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                Data d = (Data) ois.readObject();
                ois.close();
                System.out.println("Loading "+f.getName());
                String[] pieces = phys.split("\\.");
                assert pieces.length > 2: phys;
                int F= Integer.parseInt(pieces[pieces.length-2]);
                loadData(d, F);
            }
            catch(Exception e)
            {
                System.out.println("E: "+e);
            }
        }
        else
            System.out.println("Error- Not proper .phys file");
    }
    /**
     * Loads data from a Data object and resumes the simulation
     */
    private static void loadData(Data d, int F){
        assert d!= null: d;
        if(keepPre){
            precision = d.precision;
        }
        zoom = d.zoom;
        saveFile = d.saveFile;
        SIM = "";
        
        assert d.particles != null: d.particles;
        System.out.println("loading "+d.particles.length+ " particles");
        //+" starting with "+d.particles[0]+": a "+d.particles[0].type
        //+" @ x= "+d.particles[0].X);
        screen = new Screen(endF, precision, saveFile, SIM, zoom, false);
        screen.frameCount = F;
        for(Data.pData pd: d.particles) {
            assert pd != null;
            assert pd.type != null;
            Particle p = null;
            switch(pd.type) {
                case FIXED:
                    p = new Fixed();
                    break;
                case SAND:
                    p = new Sand();
                    break;
                case WATER:
                    p = new Water();
                    break;
                case BOMB:
                    p = new Bomb();
                    break;
                case BRICK:
                    p = new Brick();
                    break;
                case RUBBER:
                    p = new Rubber();
                    break;
                default:
                    p = new Sand();
                    break;
            }
            p.world = screen.world;
            assert p != null;
            //System.out.println(pd.X);
            p.setLocation(pd.X, pd.Y);
            p.velocity = pd.velocity;
            assert screen.world != null;
            screen.world.addPart(p);
        }
        
        if(!fileMode)
        {
            JFrame frame = new JFrame("Particles");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            frame.getContentPane().add(screen);
            frame.pack();
            frame.setVisible(true);
        }
        screen.start();
    }
    public static void preview(String phys){
        File f = new File(phys);
        //If it is a .phys file
        if(f.getName().substring(f.getName().lastIndexOf(".")).equals(".phys")){
            try{
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                Data d = (Data) ois.readObject();
                ois.close();
                System.out.println("Loading "+f.getName());
                previewData(d);
            }
            catch(Exception e){
                System.out.println("E: "+e);
            }
        }
        else
            System.out.println("Error- Not proper .phys file");
    }
    /**
     * Loads a table with the info stored in a phys data object
     */
    private static void previewData(Data d){
        assert d!= null: d;
        assert d.particles != null: d.particles;
        System.out.println("loading "+d.particles.length+ " particles");
        
        for(Data.pData pd: d.particles) {
            assert pd != null;
            assert pd.type != null;
            //if(pd.type != Types.FIXED)
            System.out.println(pd.type+" @ "+"\t("+pd.X+",\t"+pd.Y+")"+
                "\twith V="+pd.velocity.Mag()+"\t@ "+pd.velocity.Dir());
        }
    }
    
    public static Data exportData(BiList<Being> beings){
        Data d = new Data(precision, zoom, saveFile, beings);
        return d;
    }
    
    
    
    /**
     * The rest is for interfacing with the simulator through CLI
     */
    public static void textInterface(){
        System.out.println("Running Simulation:");
        //String res = prompt("Screen Dimensions? (w,h)");
        int w = 768;
        int h = 576;
        Lab world = new Lab(w, h);
        addThings(world);
        runSome(world);
        System.out.println(world);
    }
    public static void addThings(Lab world){
        String res = prompt("Load Sim (1), Print (2), or Add Particle (3)");
        switch(res){
            case "1": 
                res = prompt("Sim Name? (Caps)");
                if(!world.sim(res))
                    System.out.println("Not Found.");
            break;
            case "2": 
                String type = prompt("type? {FIXED, SAND, WATER, BOMB, BRICK, RUBBER}");
                int density = Integer.parseInt(prompt("Density?"));
                boolean iso = promptB("hexagonal?");
                res = prompt("x0,y0,x1,y1");
                int x0 = Integer.parseInt(res.split(",")[0]);
                int y0 = Integer.parseInt(res.split(",")[1]);
                int x1 = Integer.parseInt(res.split(",")[2]);
                int y1 = Integer.parseInt(res.split(",")[3]);
                double h = Math.sqrt(3)/2/density;
                Particle particle = new Sand();
                if(iso){
                    for(double a = x0; a <= x1; a+= 1/density){
                        int row = 0;
                        for(double b = y0; b <= y1; b+= h){
                            switch(type){
                                case "FIXED":
                                    particle = new Fixed();
                                    break;
                                case "SAND":
                                    particle = new Sand();
                                    break;
                                case "WATER":
                                    particle = new Water();
                                    break;
                                case "BOMB":
                                    particle = new Bomb();
                                    break;
                                case "BRICK":
                                    particle = new Brick();
                                    break;
                                case "RUBBER":
                                    particle = new Rubber();
                                    break;
                            }
                            double offSet = 0;
                            if((row & 1) == 1)//odd row
                                offSet = .5/density;
                            if(a+offSet > 0 && a+offSet < world.getWidth() && b > 0 && b < world.getHeight())
                                world.addPhys(particle, a+offSet, b);/*HERE*/
                            
                            row++;
                        }
                    }
                }
                else{
                    for(double a = x0; a <= x1; a+= 1/density){
                        for(double b = y0; b <= y1; b+= 1/density){
                            switch(type){
                                case "FIXED":
                                    particle = new Fixed();
                                    break;
                                case "SAND":
                                    particle = new Sand();
                                    break;
                                case "WATER":
                                    particle = new Water();
                                    break;
                                case "BOMB":
                                    particle = new Bomb();
                                    break;
                                case "BRICK":
                                    particle = new Brick();
                                    break;
                                case "RUBBER":
                                    particle = new Rubber();
                                    break;
                            }
                            if(a > 0 && a < world.getWidth() && b > 0 && b < world.getHeight())
                                world.addPhys(particle, a, b);/*HERE*/
                            
                        }
                    }
                    
                }
            break;
            case "3": 
                type = prompt("Type? {FIXED, SAND, WATER, BOMB, BRICK, RUBBER}");
                double x = Double.parseDouble(prompt("x?"));
                double y = Double.parseDouble(prompt("y?"));
                particle = new Sand();
                switch(type){
                    case "FIXED":
                        particle = new Fixed();
                        break;
                    case "SAND":
                        particle = new Sand();
                        break;
                    case "WATER":
                        particle = new Water();
                        break;
                    case "BOMB":
                        particle = new Bomb();
                        break;
                    case "BRICK":
                        particle = new Brick();
                        break;
                    case "RUBBER":
                        particle = new Rubber();
                        break;
                }
                world.addPhys(particle, x, y);
            break;
        }
        if(promptB("Add More?"))
            addThings(world);
    }
    public static void runSome(Lab world){
        System.out.println(world);
        int endF = Integer.parseInt(prompt("How many frames?"));
        double maxD = Double.parseDouble(prompt("With what precision (maxD)?"));
        for(int f = 0; f<endF; f++){
            int subFrameCount = 0;
            while(subFrameCount <= Math.ceil(1/world.time)){//<
                subFrameCount++;
                /*
                 * This next section adjusts the number of steps per frame
                 * Movement is resolved many times per frame so that 
                 *   particles do not pass through each other
                 * Number of subframes = MaxV*10
                 */
                //Have at least .001/maxD subframes. prevents a jumpy first frame
                double maxV = .001;
                //Find the biggest V
                for(BiList.Node n = world.beings.o1; n!= null; n = n.getNext()){
                    Being being = (Being) n.getVal();
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
                
                
                for(BiList.Node n = world.beings.o1; n!= null; n = n.getNext()){
                    Being being = (Being) n.getVal();
                    being.act();
                }
                for(BiList.Node n = world.beings.o1; n!= null; n = n.getNext()){
                    Being being = (Being) n.getVal();
                    being.updateXY();//move
                }
                world.dumpBin();//Replaces world.act()
            }
        }
        if(promptB("Run More?"))
            runSome(world);
    }
    
    public static String prompt(String prompt){
        System.out.println(prompt);
        Scanner scan = new Scanner(System.in);
        return scan.nextLine();
    }
    public static boolean promptB(String prompt){
        prompt += " (y/n)";
        System.out.println(prompt);
        Scanner scan = new Scanner(System.in);
        String res = scan.nextLine();
        if(res.equals("y") || res.equals("Y"))
            return true;
        if(res.equals("n") || res.equals("N"))
            return false;
        System.out.println("Pardon? Please use 'y' or 'n'");
        return promptB(prompt);
    }
}