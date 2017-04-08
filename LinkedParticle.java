import java.awt.Color;
import java.util.ArrayList;

/**
 * Write a description of class LinkedParticle here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LinkedParticle extends Particle {
    /* These values work at p=5e-7 : k=50; r_max=.02
     * k = slope at okD
     * k=100 means a step of .01 out of line produces a force slightly more than 1 (g=1 for reference)
     */
    protected double k = 100;
    protected double okDist = 1;
    protected double r_max = .1;//.02;// < (sqrt(2) / 2) (.7)
    //boolean a = true;
    //protected ArrayList<Particle> Neighbors = new ArrayList<Particle>();
    public LinkedParticle(double m, double CoD, Color colour){
        //super(m, fix, CoD, colour);
        super(m, false, CoD, colour);
        //okDist = 1.0/world.density;
        //r_max = .3*okDist;
    }
    public void applyEM() {
        ArrayList<Particle> particles = getParticlesInRange(okDist*4);//*1.4);//3);  1.4 would be less than sqrt(2) for a block
        //ArrayList<Particle> particles = getAllOtherParticles();
        for(int a = 0; a < particles.size(); a++)
        {
            Particle near = particles.get(a);
            double d = Math.sqrt((near.X - X)*(near.X - X)+(near.Y - Y)*(near.Y - Y));
            assert d > 0: "Zero d - me:"+this+"it:"+near;
            if(near instanceof LinkedParticle)
            {
                if(d < okDist*1.4)
                {
                    double Force = F(d);
                    double th = Math.atan2(Y-near.Y, X-near.X);//you to me
                    applyForceAtCenter(Force, th);//pushing me away
                    near.applyForceAtCenter(Force, th + Math.PI);
                    //System.out.println(this+"X="+X+"F="+Force+"d="+d+"th="+th);
                    //System.out.println("d:"+d+"  F:"+Force);
                }
            }
            else
            {
                applyForceAtCenter(coOfDiffusion/(d*d*d*d), Math.atan2(Y-near.Y, X-near.X));
                near.applyForceAtCenter(coOfDiffusion/(d*d*d*d), Math.atan2(near.Y-Y, near.X-X));
            }
        }
    }
    //Repulsive force at a given distance
    protected double F(double r) {
        //Simply, numerator
        double num = k*r_max*r_max/2.0;
        if(r < okDist-r_max)
        //With infintessimal step size, this would never happen
            return k/r; 
        else if(r > okDist-r_max && r < okDist+r_max)
        //Vertical assymptotes at the extents of the range
            return num/(r-okDist+r_max) + num/(r-okDist-r_max);
        else 
        /* outside of bonding range. 
         * This should make sponaneous bonding when bricks collide unlikely, but possible.
         */
            return k * Math.pow(r/(okDist+r_max), -4);//coOfDiffusion/(r*r);   
    }
    //Old versions of F(r)
    protected double F1(double r) { 
        double x = r/okDist;
        if(x < .4)
            return .4*k/x;
        else if(x < 1)
            return k;
        else if(x < 1.4)
            return -k;
        else if(x < 4)
            return -.1*k;
        else
            return 0;
    }
    protected double F0(double r) {   
        double x = r/okDist;
        return k*(.1*(4/Math.PI*(Math.atan(4*x-7.6)-Math.atan(12*x-14.4))+Math.pow(Math.E, 1.2-4*x)));
        /**OLD:
         * 4*(x-1)*Math.pow(2,-.4*(x-1)*(x-1))
         * -.4/((0.6+x)*(0.6+x));
         * 22.1807*(x-1)*Math.pow(2, -8*(x-1)*(x-1)) - 1/((0.4+x)*(0.4+x));
         * 22.1807*(x-1)*2^(-8*(x-1)*(x-1)) - 1/((0.4+x)*(0.4+x))
         */
        //F=4/pi[atan(4(x-1.9))-atan(12(x-1.2))]+e^(-4(x-.3))      
        // =4/pi[atan(4x-7.6)-atan(12x-14.4)]+e^(1.2-4x)
        // =4/Math.PI*(Math.atan(4*x-7.6)-Math.atan(12*x-14.4))+e^(1.2-4*x)
    }
    protected double F2(double r) {
        double x = r/okDist;
        /*
        if(x < 3)//cubic function F(x)= (0,k) (ok,0) (2*ok,-k) (3*ok,-8k)
            return -k*(x-1)*(x-1)*(x-1);*/
        if(x < .6)
            return k/x;
        else if(x < 1.4)//linear function F(x)= (0,k) (ok,0) (2*ok,-k) (3*ok,-2k)
            return -k*(x-1);
        else if(x < 4)
            return -.2*k;
        else
            return 0;
    }
    protected double F3(double r) {
        double x = r/okDist;
        if(x < 1.4)
            return k/x-k;
        else
            return -.2*k;
    }
    
    
    /*OLD
    public void stayLinked()
    {
        for(int b = 0; b < Neighbors.size(); b++)//b < Neighbors.size()
        {
            if(Neighbors.get(b).world != null)
            {
                double hitX = Neighbors.get(b).X;
                double hitY = Neighbors.get(b).Y;
                double dist = Math.sqrt((X-hitX)*(X-hitX) + (Y-hitY)*(Y-hitY));
                double r_max = Math.atan2(Y - hitY, X - hitX);
                double force = k*Math.abs(dist-okDist);
                if(dist > okDist)
                {
                    applyForceAtCenter(force, Math.PI + r_max);
                    Neighbors.get(b).applyForceAtCenter(force, r_max);
                }
                if(dist < okDist)
                {
                    applyForceAtCenter(force, r_max);
                    Neighbors.get(b).applyForceAtCenter(force, r_max + Math.PI);
                }
            }
        }
    }*//*
    public void act()
    {
        //otherwise we get a concurrentModificationException
        //Screen.doLater(this, void addNeighbors(){
        if(a)
        {
            Neighbors = getParticlesInRange(1);
            //for(Particle p: Neighbors)
            //    if(p.getClass() != LinkedParticle.class)
            //        Neighbors.remove(Neighbors.indexOf(p));
            a = false;
        }//});
        
        //stayLinked();
        applyFriction();
        applyGravity();
        applyEM();
        interAct();
        move();
    }*/
}
