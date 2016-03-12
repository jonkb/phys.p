import java.util.ArrayList;

/**
 * Write a description of class Physical here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Physical extends Being
{
    public vector velocity = new vector();
    public double X;
    public double Y;
    public double mass = 1;
    public boolean fixed = false;
    
    public Physical(){}
    public Physical(double M)
    {
        super();
        mass = M;
    }
    public Physical(double M, boolean F)
    {
        super();
        mass = M;
        fixed = F;
    }
    
    public double getDbX()
    {return X;}
    public double getDbY()
    {return Y;}
    public void addedToWorld()
    {
        X = getX();
        Y = getY();
    }
    
    public void setLocation(double xx, double yy)
    {
        super.setLocation(xx, yy);
        X = xx;
        Y = yy;
    }
    
    
    public ArrayList<Particle> getParticlesInRange(double r)
    {
        ArrayList<Particle> inRange = new ArrayList<Particle>();
        for(Being being: world.beings)
        {
            if(being != null && being instanceof Particle && being != this)
            {
                Particle part = (Particle) being;
                double dx = part.X - X;
                double dy = part.Y - Y;
                if(Math.sqrt(dx*dx+dy*dy) <= r)
                    inRange.add(part);
            }
        }
        return inRange;
    }
    public ArrayList<Particle> getAllOtherParticles()
    {
        ArrayList<Particle> parts = new ArrayList<Particle>();
        for(Being being: world.beings)
        {
            if(being != null && being instanceof Particle && being != this)
            {
                Particle part = (Particle) being;
                parts.add(part);
            }
        }
        return parts;
    }
    
    
    public void accelerate(vector a)
    {
        if(!fixed)
        {
            assert world.time != 0;
            assert vector.scalMult(a, world.time).Mag() < 999999999: world.time;
            velocity.addOn(vector.scalMult(a, world.time));
        }
    }
    public void accelerate(double magnitude, double direction)
    {
        if(!fixed)
        {
            assert world.time != 0;
            vector v = new vector(magnitude*world.time, direction);
            assert v.Mag() < 1000000000.: v.Mag();
            velocity.addOn(v);//a=dv/t dv = at
        }
    }
    public void applyForceAtCenter(double magnitude, double direction)
    {   //F=ma a=F/m
        accelerate(magnitude / mass, direction);
    }
    
    protected void applyFriction(){
        //simulating air friction. the faster you go, the closer you get to canceling out
        //gravity completely. 
        //6c/f = terminal velocity
        //friction(at 6 c/f) = 1c/f^2 : gravity
        //a.f=1/6vel
        
        if(world.getAir() == true){
            if(Math.abs(velocity.Mag()) > .005)
                accelerate(velocity.Mag()/6, velocity.Dir()+ Math.PI);
        }
    }
    protected void applyGravity()
    {accelerate(world.getGravityV());}
    protected void move()
    {
        //System.out.println(this+"X="+X+";   Y="+Y+";   V="+velocity.Mag()+" @"+180*velocity.Dir()/Math.PI+"deg");
        assert world.time != 0;//v=x/t x = vt
        X += world.time*velocity.Mag()*Math.cos(velocity.Dir()); //System.out.println(this+"dx="+world.time*velocity.Mag()*Math.cos(velocity.Dir()));
        Y += world.time*velocity.Mag()*Math.sin(velocity.Dir());
        if(X<0 || X >= world.getWidth() - 0.5
        || Y<0 || Y >= world.getHeight() - 0.5)
            world.removeBeing(this);
        else
        {
            assert X>=0 && X<= world.getWidth(): "X out of bounds";
            assert Y>=0 && Y<= world.getHeight(): "Y out of bounds";
            setLocation(X,Y);
        }
    }
    public void act(){
        if(this != null && !this.fixed){
            applyFriction();
            applyGravity();
        }
    }
    public void updateXY(){
        if(!fixed)
            move();
        /*
        x = (int) Math.round(X);
        y = (int) Math.round(Y);*/
    }
    public void print()
    {
        setLocation(X, Y);
    }
}