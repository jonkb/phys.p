import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Polygon;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Write a description of class Curser here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Curser extends Being
{
    public void act() 
    {
        /**
        if(world.screen.onScreen)
            setLocation(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
        else
            setLocation(Screen.width / 2, Screen.height / 2);    
            */
        makeImage();
    }
    public void makeImage()
    {
        Color color;
        switch(world.curserType){
            case SAND:
                color = Color.YELLOW;
                break;
            case WATER:
                color = Color.BLUE;
                break;
            case BOMB:
                color = Color.RED;
                break;
            case BRICK:
                color = Color.ORANGE;
                break;
            case RUBBER:
                color = Color.GREEN;
                break;
            case FIXED:
                color = Color.GRAY;
                break;
            case CRYSTAL:
                color = Color.CYAN;
                break;
            default:
                color = Color.WHITE;
        }
        
        //Picture dimensions
        int pW = (int)(world.curserW*world.getZoom());
        int pH = (int)(world.curserH*world.getZoom());
        
        switch(world.curserShape)
        {
            case 0: case 2://Rectangle or Iso-Rectangle
            BufferedImage image = new BufferedImage(pW, pH, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D graph = image.createGraphics();
            graph.setColor(color);
            
            graph.draw(new Rectangle(0, 0, pW - 1, pH - 1));
            setImage(image);
            break;
            
            case 1: // Hexagon
            int r = pW - (int)world.getZoom();
            if(r < 1){
                //From above
                image = new BufferedImage(pW, pH, BufferedImage.TYPE_4BYTE_ABGR);
                graph = image.createGraphics();
                graph.setColor(color);
                graph.draw(new Rectangle(0, 0, pW - 1, pH - 1));
                setImage(image);
                break;
            }
            double h = r * Math.sqrt(3);
            int[] x =
               {r+r,
                (int) Math.round(1.5*r),
                (int) Math.round(.5*r),
                0,
                (int) Math.round(.5*r),
                (int) Math.round(1.5*r)};
            int[] y = 
               {(int) Math.round(h/2),
                0,
                0,
                (int) Math.round(h/2), 
                (int) Math.round(h),
                (int) Math.round(h)};
            Polygon hex = new Polygon(x, y, 6);
            
            image = new BufferedImage(2*pW, (int) Math.ceil(h), BufferedImage.TYPE_4BYTE_ABGR);
            graph = image.createGraphics();
            graph.setColor(color);
            
            graph.draw(hex);
            setImage(image);
            break;
            
            case 3: //annulus
            r = pW;
            int rings = pH;
            //Temporary graphic
            
            Rectangle lin = new Rectangle(r-1,0,rings-1,0);
            
            image = new BufferedImage(r+rings+1, 1, BufferedImage.TYPE_4BYTE_ABGR);
            graph = image.createGraphics();
            graph.setColor(color);
            
            graph.draw(lin);
            setImage(image);
            break;
            
            default:
            break;
        }
    }
    /*
    public void setLocation(int X, int Y)
    {
        x = X;
        y = Y;
        if(x < 0)
            x = world.getWidth() + x;
        if(x > world.getWidth() - 1)
            x = x - world.getWidth();
        if(y < 0)
            y = world.getHeight() + y;
        if(y > world.getHeight() - 1)
            y = y - world.getHeight();
    } */
}
