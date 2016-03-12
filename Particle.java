import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Particle extends Physical
{
    public double coOfDiffusion;
    public Particle(double m, boolean fixed, double coOfDiffusion, Color colour)
    {
        super(m, fixed);
        
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
        image.setRGB(0, 0, colour.getRGB());
        setImage(image);
        
        this.coOfDiffusion = coOfDiffusion;
    }
    /**
     * Act - do whatever the Particle wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()//I Moved the move function to the second phase so it all happens at once 
    {
        if(this != null)
        {
            applyFriction();
            applyGravity();
            applyEM();
            interAct();
            //System.out.println(this+"is Acting");
        }
    }
    public void applyEM()
    {
        if(coOfDiffusion != 0)
        {
            //ArrayList<Particle> particles = getParticlesInRange3(5);
            //ArrayList<Particle> particles = getAllOtherParticles();
            ArrayList<Particle> particles = getParticlesInRange(20);
            for(int a = 0; a < particles.size(); a++)
            {
                Particle near = particles.get(a);
                double d = Math.sqrt((near.X - X)*(near.X - X)+(near.Y - Y)*(near.Y - Y));
                assert d > 0: d;
                double th = Math.atan2(Y-near.Y, X-near.X);//th you to me
                applyForceAtCenter(F(d), th);
                near.applyForceAtCenter(F(d), th+Math.PI);
            }
        }
    }
    protected double F(double r)
    {
        return coOfDiffusion/r/r/r/r;
    }
    public void interAct(){}
    public String toString()
    {
        return this.getClass().getSimpleName() + ": ("+X+", "+Y+")";
    }
    public Types type()
    {
        if(this instanceof Fixed)
            return Types.FIXED;
        if(this instanceof Sand)
            return Types.SAND;
        if(this instanceof Water)
            return Types.WATER;
        if(this instanceof Bomb)
            return Types.BOMB;
        if(this instanceof Brick)
            return Types.BRICK;
        if(this instanceof Rubber)
            return Types.RUBBER;
        return null;
    }
}
