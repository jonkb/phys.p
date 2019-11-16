import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
public class Execute{
    static Screen screen;
    public static long t0;
    //0: just a few messages 1:a few each frame 2: lots 3:stupid lots
    public static int debugging = 1;
    //How much multithreading? 0: unlimited ; more: max that many
    public static int max_threads = 1;
    
    //Execution Options:
    public static boolean 
        fileMode = true,
        loadPre = false, // When loading a file, load the precision too
        stepWise = false; // Pause & ask for confirmation after each frame & 100 000 subframes
    private static int 
        endF = 100000,
        filemode_max_frames = 5000,
        simNum = 3;
    private static double 
        precision = 5e-6,//1e-5,//.00000005;
        zoom = 12,
        gravity = 1;
    private static String 
        SIM = "Pulley_2",
        saveFile = "Tests/"+SIM+"/"+simNum+".";
    
    /**
     * h - help
     * hsim - list sims
     * l[] - load
     * v[] - preView
     * d# - debugging 
     * z# - zoom 
     * p# - precision 
     * pl - load precision (use with l[])
     * t# - max_threads 
     * n# - simNum 
     * e# - endF
     * s[] - SIM
     * g# - set gravity magnitude
     * i - interactive mode
     * istep - stepwise mode
     * Example: java Execute d0 z2 t512 sSandFall
     */
    public static void main(String[] args){
        ArrayList<ArrayList<String>> parsed_args = parse_args(args);
        for(ArrayList<String> option: parsed_args){
            String tag = option.get(0);
            switch(tag){
                case "h":
                case "-help":
                    //Help
                    help(option);
                    return;
                case "l":
                case "-load":
                    if(option.size() < 2){
                        System.out.println("Must supply a filename to use the load option");
                        return;
                    }
                    load(option.get(1));
                    return;
                case "v":
                    if(option.size() < 2){
                        System.out.println("Must supply a filename to use the view option");
                        return;
                    }
                    preview(option.get(1));
                    return;
                case "d":
                    if(option.size() < 2){
                        System.out.println("Must supply a debugging level");
                        return;
                    }
                    debugging = Integer.parseInt(option.get(1));
                    break;
                case "z":
                    if(option.size() < 2){
                        System.out.println("Must supply a zoom level");
                        return;
                    }
                    zoom = Integer.parseInt(option.get(1));
                    break;
                case "p":
                    if(option.size() < 2){
                        System.out.println("Must supply a precision level");
                        return;
                    }
                    if(option.get(1).equals("l"))
                        loadPre = true;
                    else
                        precision = Double.parseDouble(option.get(1));
                    break;
                case "i":
                    if(option.size() > 1){
                        if(option.get(1).equals("step") || option.get(1).equals("s")){
                            stepWise = true;
                        }
                    }
                    else{
                        fileMode = false;
                    }
                    break;
                case "t":
                    if(option.size() < 2){
                        System.out.println("Must supply a max thread number");
                        return;
                    }
                    max_threads = Integer.parseInt(option.get(1));
                    break;
                case "n":
                    if(option.size() < 2){
                        System.out.println("Must supply the sim number");
                        return;
                    }
                    simNum = Integer.parseInt(option.get(1));
                    break;
                case "e":
                    if(option.size() < 2){
                        System.out.println("Must supply the end frame number");
                        return;
                    }
                    endF = Integer.parseInt(option.get(1));
                    break;
                case "s":
                    if(option.size() < 2){
                        System.out.println("Must supply the sim name");
                        return;
                    }
                    SIM = option.get(1);
                    break;
                case "g":
                    if(option.size() < 2){
                        System.out.println("Must supply the gravity level");
                        return;
                    }
                    gravity = Double.parseDouble(option.get(1));
                    break;
                default:
                    System.out.println("Unknown CLI argument");
                    return;
            }
        }
        
        saveFile = "Tests/"+SIM+"/"+simNum+".";
        t0 = System.currentTimeMillis();
        System.gc();
        screen = new Screen(endF, precision, saveFile, SIM, zoom, true);
        if(gravity != 1)
            screen.world.setGravity(gravity);
        
        saveFile = "Tests/"+SIM+"/"+simNum+".";
        if( SIM.equals(""))
            saveFile = "Tests/"+simNum+".";
        
        //Prevent terrible accidents that bloat the computer with 200,000 png files
        if(fileMode && endF > filemode_max_frames)
            endF = filemode_max_frames;
        if(!fileMode){
            JFrame frame = new JFrame("Particles");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(screen);
            frame.pack();
            frame.setVisible(true);
        }
    }
    //Parse the cli args to a list of lists
    private static ArrayList<ArrayList<String>> parse_args(String[] args){
        //Usually there will probably be less than 4 options at a time
        ArrayList<ArrayList<String>> parsed_args = new ArrayList<ArrayList<String>>(4);
        int option_n = -1; //index of current option
        for(String s: args){
            if(s.substring(0,1).equals("-")){
                ArrayList<String> option = new ArrayList<String>(4);
                option.add(s.substring(1));
                parsed_args.add(option);
                option_n++;
            }
            //Ignores an argument before the first hyphen
            //Should I change that?
            else if(option_n > -1){
                parsed_args.get(option_n).add(s);
            }
        }
        return parsed_args;
    }
    //Print a help message for the CLI
    private static void help(ArrayList<String> args){
        assert args.get(0).equals("h") || args.get(0).equals("-help"): "help called not by a help argument";
        //support multiple word help requests
        String body = "";
        for(int i = 1; i<args.size(); i++){
            body = body.concat(args.get(i));
        }
        System.out.print("Printing help message");
        if(body.length() > 0)
            System.out.print(" for "+body+":\n");
        else
            System.out.print(":\n");
        switch(body){
            case "":
                System.out.println("Each option for phys.p is specified with a one character tag "
                    +"that may be followed by one modifier or argument.");//For now
                System.out.println("Options:");
                System.out.println("\t-d #      \tdebugging");
                System.out.println("\t-z #      \tzoom");
                System.out.println("\t-p #      \tprecision");
                System.out.println("\t-p l      \tload precision (use with -l)");
                System.out.println("\t-i        \tinteractive mode");
                System.out.println("\t-i step   \tstepwise mode");
                System.out.println("\t-t #      \tnumber of threads");
                System.out.println("\t-g #      \tgravity (default 1)");
                System.out.println("\t-n #      \tsim number. Shows up in file names.");
                System.out.println("\t-e #      \tend frame");
                System.out.println("\t-s [string]\tSIM name for loading an automatic sim");
                System.out.println("Other Commands:");
                System.out.println("\t-h        \thelp");
                System.out.println("\t--help    \thelp");
                System.out.println("\t-h [option]\thelp for a particular option");
                System.out.println("\t-l [file] \tload and continue .phys");
                System.out.println("\t-v [file] \tload and preview .phys");
                break;
            case "s":
            case "sim":
            case "sims":
                System.out.println("SIMS:");
                System.out.println("Beaker, Bridge, noGrav, Rubber, SuBridge, Brick, SandFall, "
                    +"TinyBridge, Pulley");
                System.out.println("Recommended Zoom:");
                System.out.println("\tTinyBridge: 20 (16-30)");
                System.out.println("\tSuBridge: 8 (5-9)");
                System.out.println("\tSandFall: 3");
                System.out.println("\tPulley: 8");
                break;
            case "d"://Help with the "d" option
            case "d#":
            case "d #":
                System.out.println("This option specifies the depth of debugging, "+
                    "A.K.A. how many messages appear during the simulation.");
                System.out.println("\t0: just a few messages");
                System.out.println("\t1: a few each frame");
                System.out.println("\t2: lots");
                System.out.println("\t3: stupid lots");
                break;
            case "p":
                System.out.println("Set the precision of the simulation. This number is the max"+
                    " distance that the fastest particle can travel without re-calculating forces.");
                System.out.println("A precision of .1, for example, means that the maximum distance "+
                    "that a particle can travel inbetween subframes is .1 cells.");
                System.out.println("If the fastest particle is traveling at 10 cells / frame, "+
                    "then with p=.1, 100 subframes will be calculated.");
                System.out.println("Smaller p means more subframes and more precision. "+
                    "1e-4 is often enough, even for linked particles to stay linked.");
                break;
            case "pl":
            case "p l":
                System.out.println("If '-p l' is specified, the precision will automatically "+
                    "be set the same as it was in the file that is being loaded.");
                System.out.println("Only works when used together with the -l [file] command");
                break;
            case "v":
            case "v[file]":
            case "v [file]":
                System.out.println("Loads the specified .phys file, but not to continue the simulation, "+
                    "rather this command serves to save a .png snapshot of the given .phys file.");
                break;
            case "t":
            case "t#":
            case "t #":
                System.out.println("Sets the max number of threads for multithreading.");
                System.out.println("The default is 1 (no multithreading)");
                System.out.println("If t=0, that allows for unlimited multithreading, "+
                    "which creates one thread per particle. CAUTION");
        }
    }
    
    /**
     * Loads data from a file and resumes the simulation
     */
    public static void load(String phys){
        t0 = System.currentTimeMillis();
        System.gc();
        screen = new Screen(endF, precision, saveFile, SIM, zoom, false);
        
        File f = new File(phys);
        //If it is a .phys file
        if(f.getName().substring(f.getName().lastIndexOf(".")).equals(".phys")){
            try{
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                Data d = (Data) ois.readObject();
                ois.close();
                System.out.println("Loading "+f.getName());
                loadData(d);
                screen.start();
            }
            catch(Exception e)
            {
                System.out.println("E: "+e);
            }
        }
        else
            System.out.println("Error- Not proper .phys file");
    }
    /*public static void loadOld(String phys){
        File f = new File(phys);
        //If it is a .phys file
        if(f.getName().substring(f.getName().lastIndexOf(".")).equals(".phys")){
            try{
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                oldData od = (oldData) ois.readObject();
                ois.close();
                Data d2 = new Data(999, 100000, od);
                System.out.println("Loading "+f.getName());
                loadData(d2);
                screen.start();
            }
            catch(Exception e){
                System.out.println("E: "+e);
            }
        }
        else
            System.out.println("Error- Not proper .phys file");
    }*/
    /**
     * Loads data from a Data object
     */
    private static void loadData(Data d){
        assert d!= null: d;
        if(loadPre)
            precision = d.precision;
        zoom = d.zoom;
        saveFile = d.saveFile;
        SIM = "";
        screen = new Screen(endF, precision, saveFile, SIM, zoom, false);
        screen.frameCount = d.frame;
        screen.subFrameCount = 0;
        //BROKEN: d.subframe; 
        //At present, it doesn't reset the subframes to 0 until after saving the .phys file.
        //If you try to start on a frame with d.subframe, it'll start out at subframe 1000/1100 for example
        //If you wish to continue in the middle of a frame, uncomment. For now it's a bit broken.
        
        assert d.particles != null: d.particles;
        System.out.println("loading "+d.particles.length+ " particles");
        //+" starting with "+d.particles[0]+": a "+d.particles[0].type
        //+" @ x= "+d.particles[0].X);
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
        
        if(!fileMode){
            JFrame frame = new JFrame("Particles");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            frame.getContentPane().add(screen);
            frame.pack();
            frame.setVisible(true);
        }
    }
    public static void preview(String phys){
        t0 = System.currentTimeMillis();
        System.gc();
        screen = new Screen(endF, precision, saveFile, SIM, zoom, false);
        
        File f = new File(phys);
        //If it is a .phys file
        if(f.getName().substring(f.getName().lastIndexOf(".")).equals(".phys")){
            try{
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                Data d = (Data) ois.readObject();
                ois.close();
                System.out.println("Loading "+f.getName());
                /* ~~~~~~~ */
                //previewData(d);
                previewDataImg(d);
            }
            catch(Exception e){
                System.out.println("E: "+e);
            }
        }
        else
            System.out.println("Error- Not proper .phys file");
    }
    //Specific case right now
    public static void preview_from_output(String out){
        t0 = System.currentTimeMillis();
        System.gc();
        screen = new Screen(endF, precision, saveFile, SIM, zoom, true);
        Data d = FileService.parseOutput_to_data("Tests/Pulley/2.temp.txt");
        System.out.println("Loading "+out);
        previewData(d);
        previewDataImg(d);
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
    private static void previewDataImg(Data d){
        loadData(d);
        //screen.run = new RunSim(screen);
        screen.snap("temp", false);
    }
    //Called by Screen
    public static Data exportData(BiList<Being> beings, int frameCount, int subFrameCount){
        Data d = new Data(frameCount, subFrameCount, precision, zoom, saveFile, beings);
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
        System.out.println(prompt + " (y/n)");
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