import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
public class Execute
{
    static Screen screen;
    
    //Execution Options:
    public static boolean 
        fileMode = false,
        samePre = false;
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
        if(samePre){
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
    
    public static Data exportData(ArrayList<Being> beings)
    {
        Data d = new Data(precision, zoom, saveFile, beings);
        return d;
    }
}