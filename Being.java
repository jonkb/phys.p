import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.ArrayList;
/**
 * Write a description of class Being here.
 * 
 * @author (JonathanKnightBlack) 
 * @version (1.0)
 */
public class Being
{
    protected double x, y;
    Lab world;
    private BufferedImage classImage;
    private BufferedImage image;
    //private int direction = 1;
    //public int actPriority;
    
    public Being(){}
    public Being(String img)
    {
        try {
            classImage = ImageIO.read(new File(img));
        } catch (IOException e) {}
        setImage(classImage);
    }
    public void act(){}
    //public void act1(){}
    public void print(){}
    public void updateXY(){}
    public void addedToWorld(){}
    public double getX()
    {return x;}
    public double getY()
    {return y;}
    public BufferedImage getImage()
    {return image;}
    public BufferedImage getClassImage()
    {return classImage;}
    public void setLocation(int X, int Y)
    {
        assert X>=0 && X<= world.getWidth(): "X out of bounds";
        assert Y>=0 && Y<= world.getHeight(): "Y out of bounds";
        x = X;
        y = Y;
    } 
    public void setLocation(double X, double Y)
    {
        //world.grid[x][y] = null;
        assert X>=0 && X<= world.getWidth(): "X out of bounds";
        assert Y>=0 && Y<= world.getHeight(): "Y out of bounds";
        x = X;
        y = Y;
    } 
    public void setImage(BufferedImage newImage)
    {image = newImage;}
}
