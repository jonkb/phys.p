import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Particle extends Physical
{
    public double coOfDiffusion;
    private Color color;
    public Particle(double m, boolean fixed, double coOfDiffusion, Color colour){
        super(m, fixed);
        
        color = colour;
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
        image.setRGB(0, 0, colour.getRGB());
        setImage(image);
        
        this.coOfDiffusion = coOfDiffusion;
    }
    
    public BufferedImage getImage(){
        double z = world.getZoom();
        if(z < 4){
            return super.getImage();
        }
        else{
            /*
            int diam = (int) Math.round(z/2);
            image = new BufferedImage(diam, diam, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics ig = image.getGraphics();
            ig.setColor(Color.blue);
            ig.fillOval(0,0,diam,diam);*/
            
            //absolute radius is always 1/4
            double radius = z / 4.0; //cells
            int cx = (int) Math.round(X*z); //cells
            int cy = (int) Math.round(Y*z); //cells
            int w = (int) (2*Math.ceil(radius) + 1); //+1 for center dot
            int h = w;
            //capitals mean absolute units
            double pX, pY, pdX, pdY;
            BufferedImage circle = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
            for(int px = 0; px < w; px++){
                for(int py = 0; py < h; py++){
                    //absolute coordinates of the current pixel in the image
                    pX = ( cx - Math.ceil(radius) + px )/z;
                    pY = ( cy - Math.ceil(radius) + py )/z;
                    //p[delta](X/Y)
                    pdX = pX - X;
                    pdY = pY - Y;
                    //pdx^2+pdy^2 < (1/4)^2
                    if( pdX*pdX + pdY*pdY < 1.0/16)
                        circle.setRGB(px,py,color.getRGB());
                }
            }
            return circle;
        }
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
            for(Particle near: particles){
                double d = Math.sqrt((near.X - X)*(near.X - X)+(near.Y - Y)*(near.Y - Y));
                assert d > 0: d;
                double th = Math.atan2(Y-near.Y, X-near.X);//angle from you to me
                applyForceAtCenter(F(d), th);
                near.applyForceAtCenter(F(d), th+Math.PI);
            }
        }
    }
    protected double F(double r)
    {
        return coOfDiffusion/r/r;
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
