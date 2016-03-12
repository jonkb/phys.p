import java.awt.Color;
import java.util.ArrayList;

/**
 * Write a description of class Sand here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Spring extends LinkedParticle
{
    Spring partner;
    boolean seed = true;
    int okDist = 5;
    int k = 2;
    public Spring()
    {
        super(1, 2, Color.GREEN);
    }
    public void addedToWorld()
    {
        super.addedToWorld();//X=x;Y=y
        if(seed)
        {
            partner = new Spring();
            partner.partner = this;
            partner.seed = false;
            world.addBeing(partner, (int) Math.round(X), (int) Math.round(Y + okDist));
            //Neighbors.add(partner);
            //partner.Neighbors.add(this);
        }
    }
    public void applyEM()
    {
        double d = Math.sqrt((partner.X - X)*(partner.X - X)+(partner.Y - Y)*(partner.Y - Y));
        double Force = F(d);
        applyForceAtCenter(Force, Math.atan2(Y-partner.Y, X-partner.X));
        partner.applyForceAtCenter(Force, Math.atan2(partner.Y-Y, partner.X-X));
        
        
        ArrayList<Particle> particles = getParticlesInRange(okDist*2);//3);
        //ArrayList<Particle> particles = getAllOtherParticles();
        for(int a = 0; a < particles.size(); a++)
        {
            Particle near = particles.get(a);
            boolean LP = near instanceof LinkedParticle;
            
            d = Math.sqrt((near.X - X)*(near.X - X)+(near.Y - Y)*(near.Y - Y));
            assert d > 0: "Zero d - Me:"+this+"It:"+near;
            if(!LP)
            {
                if(!fixed)
                    applyForceAtCenter(coOfDiffusion/(d*d), Math.atan2(Y-near.Y, X-near.X));
                near.applyForceAtCenter(coOfDiffusion/(d*d), Math.atan2(near.Y-Y, near.X-X));
            }
        }
    }
}
