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
    static int i0 = 1;
    static int i1 = 15;
    static String dir = "Tests/Bridge/";
    static String stem = "05.";
    static String ex = ".png";
    public static void renumber()
    {
        for(int i = i0; i <= i1; i++)
        {
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
    public static void delete()
    {
        boolean yes = confirm("delete files, "+dir+stem+i0+ex+"to "+dir+stem+i1+ex+" ?(y/n): ");
        if(yes)
        {
            System.out.println("deleting");
            for(int i = i0; i <= i1; i++)
            {
                File dooomed = new File(dir+stem+i+ex);
                assert dooomed != null;
                boolean success = dooomed.delete();
                if(!success)
                    System.out.println("Error deleting files");
                if((i-i0)%1000 == 0)
                    System.out.println((i-i0)+" / "+ (i1-i0));
            }
        }
    }
    public static String input(String prompt)
    {
        System.out.println(prompt);
        Scanner scan = new Scanner(System.in);
        String in = scan.next();
        return in;
    }
    public static boolean confirm(String prompt)
    {
        if(input(prompt).equals("y"))
            return true;
        else
            return false;
    }
    public static void test()
        throws Exception 
    {
        File saveFile = new File("Tests/Brick/testtesttest.txt");
        FileWriter f = new FileWriter(saveFile);
        f.append("HI");
    }
}