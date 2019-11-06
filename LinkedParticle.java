import java.awt.Color;
import java.util.ArrayList;

/**
 * Write a description of class LinkedParticle here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LinkedParticle extends Particle{
    /* 
     * 
     */
    protected double k = .1;
    protected double r0 = 1;
    protected double r_stretch = .1*r0;// Must be < 1/4 *r0. r0/4 = Crystal break point
    //boolean a = true;
    //protected ArrayList<Particle> Neighbors = new ArrayList<Particle>();
    public LinkedParticle(double m, Color colour){
        //super(m, fix, CoD, colour);
        super(m, false, colour);
        //r0 = 1.0/world.density;
        //r_stretch = .3*r0;
    }
    public void applyEM(){
        ArrayList<Particle> particles = getParticlesInRange(em_range);
        for(Particle near: particles){
            double d = Math.sqrt((near.X - X)*(near.X - X)+(near.Y - Y)*(near.Y - Y));
            assert d > 0: "Zero d - me:"+this+"it:"+near;//Could crash here
            double th = Math.atan2(Y-near.Y, X-near.X);//angle from you to me
            double F_sum = 0;
            if(near.type() == this.type()){
                if(d < r0*1.732-r_stretch){
                    //Apply the bonding force here
                    F_sum = F(d);
                    //System.out.println("me"+this+"; near"+near+"; F="+F_sum+"; d="+d);
                    //System.out.println("d:"+d+"  F:"+Force);
                }
                else{
                    F_sum = F_em(d)+F_diff(d);
                }
            }
            else{
                //Avoid collisions regularly
                F_sum = F_em(d);
            }
            applyForceAtCenter(F_sum, th);//pushing me away
            near.applyForceAtCenter(F_sum, th + Math.PI);
        }
    }
    /* F(r) = Repulsive force at a given distance
     * This version makes a brick crystallize
     */
    protected double F(double r) {
        if(r > r0-r_stretch && r < r0+r_stretch){
            //Vertical assymptotes at the max stretch distance
            double div1 = r-r0+r_stretch;
            double div2 = r-r0-r_stretch;
            return k/div1/div1 - k/div2/div2;//2nd order
        }
        else{
            //F that causes a displacement of r_stretch/4
            double F_4th = F(r0-.25*r_stretch);
            return F_4th/3/r/r;
        }
    }
    //Old versions of F(r)
    protected double F4(double r) {
            /* These values work at p=5e-7 : k=50; r_stretch=.02
             * k = slope at okD
             * k=100 means a step of .01 out of line produces a force slightly more than 1 (g=1 for reference)
             */
         //   protected double k = 100;
         //   protected double r0 = 1;
         //   protected double r_stretch = .1*r0;// Must be < 1/4 *r0. r0/4 = Crystal break point
        //Simply numerator
        double num = k*r_stretch*r_stretch/2.0;
        if(r < r0-r_stretch)
        //With infintessimal step size, this would never happen
            return k/r; 
        else if(r > r0-r_stretch && r < r0+r_stretch)
        //Vertical assymptotes at the extents of the range
            return num/(r-r0+r_stretch) + num/(r-r0-r_stretch);
        else 
        /* outside of bonding range. 
         * This should make sponaneous bonding when bricks collide unlikely, but possible.
         */
            return k * Math.pow(r/(r0+r_stretch), -4);//coOfDiffusion/(r*r);   
    }
    protected double F1(double r) { 
        double x = r/r0;
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
        double x = r/r0;
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
        double x = r/r0;
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
        double x = r/r0;
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
                double r_stretch = Math.atan2(Y - hitY, X - hitX);
                double force = k*Math.abs(dist-r0);
                if(dist > r0)
                {
                    applyForceAtCenter(force, Math.PI + r_stretch);
                    Neighbors.get(b).applyForceAtCenter(force, r_stretch);
                }
                if(dist < r0)
                {
                    applyForceAtCenter(force, r_stretch);
                    Neighbors.get(b).applyForceAtCenter(force, r_stretch + Math.PI);
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
