import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Particle extends Physical{
    protected double coOfDiffusion = 0;
    protected double em_k = 1;
    protected double em_range = 10;
    protected double r_em_max = 3;
    protected double r_diff_max = 10;
    private Color color;
    
    public Particle(double m, boolean fixed, Color colour){
        super(m, fixed);
        
        color = colour;
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
        image.setRGB(0, 0, colour.getRGB());
        setImage(image);
        em_k = m;//Scale to mass so the normal distance of 1 cancels gravity
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
    public void act(){//I Moved the move function to the second phase so it all happens at once 
        if(this != null){
            applyFriction();
            applyGravity();
            applyEM();
            interAct();
            //System.out.println(this+"is Acting");
        }
    }
    public void applyEM(){
        ArrayList<Particle> particles = getParticlesInRange(em_range);
        for(Particle near: particles){
            double d = Math.sqrt((near.X - X)*(near.X - X)+(near.Y - Y)*(near.Y - Y));
            double th = Math.atan2(Y-near.Y, X-near.X);//angle from you to me
            double F_sum = 0;
            if(near.type() == this.type())
                F_sum = F_em(d)+F_diff(d);
            else
                F_sum = F_em(d);
            applyForceAtCenter(F_sum, th);
            near.applyForceAtCenter(F_sum, th+Math.PI);
        }
    }
    //Diffusion
    protected double F_diff(double r){
        if(r < r_diff_max)
            return coOfDiffusion/r/r;
        else
            return 0;
    }
    //EM for collisions
    protected double F_em(double r){
        if(r < r_em_max)
            return em_k/r/r;
        else
            return 0;
    }
    public void interAct(){}
    public String toString(){
        return this.getClass().getSimpleName() + ": ("+X+", "+Y+")";
    }
    public Types type(){
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
        if(this instanceof Crystal)
            return Types.CRYSTAL;
        return null;
    }
}
