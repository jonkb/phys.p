import java.awt.Color;
public class Fixed extends Particle
{
    public final double velocityX = 0;
    public final double velocityY = 0;
    
    
    public Fixed()
    {
        super(100.0, true, 1.0, Color.GRAY);
        //(mass, fixed, coOfDiff, colour)
    }
    public void print(){}
    public void act(){}
    public static void create(Lab world, int x,int y,int w,int h)
    {   //Just a quick print void
        for(int a = 0; a < w ; a++)
        {
            for(int b = 0; b < h ; b++)
            {
                Particle particle = new Fixed();
                world.addBeing(particle, x + a, y + b);
            }
        }
    }
}
