import java.io.*;
import java.util.Scanner;
/**
 * Write a description of class FileService here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class FileService
{
    static int i0 = 135;
    static int i1 = 1000;
    static String dir = "Tests/TinyBridge/";
    static String stem = "20.";
    static String ex = ".png";
    public static void renumber(){
        for(int i = i0; i <= i1; i++){
            File old = new File(dir+stem+i+ex);
            int newI = i-i0+1;
            File NEW = new File(dir+"04."+newI+ex);
            assert old != null;
            assert NEW != null;
            
            boolean success = old.renameTo(NEW);
            if(!success)
                System.out.println("Error renaming files");
            if((i-i0)%1000 == 0)
                System.out.println((i-i0)+" / "+ (i1-i0));
        }
    }
    public static void delete(){
        String conf_message = "delete files, "+dir+stem+i0+ex+" to "+dir+stem+i1+ex+" ?(y/n): ";
        if(confirm(conf_message)) {
            System.out.println("deleting");
            for(int i = i0; i <= i1; i++) {
                File dooomed = new File(dir+stem+i+ex);
                assert dooomed != null;
                boolean success = dooomed.delete();
                if(!success)
                    System.out.println("Error deleting files");
                if((i-i0)%1000 == 0)
                    System.out.println((i-i0)+" / "+ (i1-i0));
            }
            System.out.println("done");
        }
    }
    public static void parseOutput(String file){
        try{
            Scanner in = new Scanner(new FileReader(file));
            boolean count = false;//Is this token a count token?
            int num = 0;
            double maxV = 0;
            while(in.hasNext() ) {
                String token = in.next();
                if(count){
                    num = Integer.parseInt(token);
                    count = false;
                }
                if(token.equals("loading")){
                    count = true;
                }
                if(token.indexOf("V=") == 0){
                    double v = Double.parseDouble(token.substring(2));
                    if( v > maxV)
                        maxV = v;
                }
            }
            System.out.println("Max V= "+maxV+" of "+num+" particles");
        }catch(Exception e){}
    }
    //Not general case yet
        public static Data parseOutput_to_data(String file){
        Data d = null;
        try{
            Scanner in = new Scanner(new FileReader(file));
            boolean atCount = false;//Is this token a count token?
            boolean atPre = false;//Is this token a count token?
            int count = 0;
            double pre = 0;
            while(in.hasNext() ) {
                String token = in.next();
                System.out.println(token);
                if(atCount){
                    count = Integer.parseInt(token);
                    atCount = false;
                }
                if(atPre){
                    pre = Double.parseDouble(token);
                    atPre = false;
                }
                if(token.equals("loading")){
                    atCount = true;
                }
                if(token.equals("Precision=")){
                    atPre = true;
                }
                if(token.equals("particles")){
                    break;
                }
            }
            BiList<Being> beings = new BiList<Being>();
            while(in.hasNextLine()){
                //System.out.println("103");
                String line = in.nextLine();
                if(line.length() < 1)
                    continue;
                
                String[] parts = line.split("\t");
                Types pType = Types.SAND;
                /**
                 * Note: Incomplete list
                 */
                switch(parts[0]){
                    case "BRICK @ ":
                        pType = Types.BRICK;
                        break;
                    case "FIXED @ ":
                        pType = Types.FIXED;
                        break;
                }
                double X = Double.parseDouble(parts[1].substring(1, parts[1].indexOf(",")) );
                double Y = Double.parseDouble(parts[2].substring(0, parts[2].indexOf(")")) );
                double mag = Double.parseDouble(parts[3].substring(7) );
                String dirS = parts[4].substring(2);
                double dir = Double.parseDouble(parts[4].substring(2) );
                vector v = new vector(mag, dir);
                //System.out.println("124");
                Particle p = null;
                switch(pType) {
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
                //p.world = screen.world;
                assert p != null;
                p.X = X;
                p.Y = Y;
                p.velocity = v;
                
                beings.add(p);
            }
            System.out.println("156");
            d = new Data(9999, 100000, pre, 12, "Tests/Pulley/2.", beings);
        }catch(Exception e){}
        return d;
    }
    public static String input(String prompt){
        System.out.println(prompt);
        Scanner scan = new Scanner(System.in);
        String in = scan.next();
        return in;
    }
    public static boolean confirm(String prompt){
        if(input(prompt).equals("y"))
            return true;
        else
            return false;
    }
    public static void test() throws Exception {
        File saveFile = new File("Tests/Brick/testtesttest.txt");
        FileWriter f = new FileWriter(saveFile);
        f.append("HI");
    }
}