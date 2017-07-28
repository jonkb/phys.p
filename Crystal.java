import java.awt.Color;
/**
 * Try to make a cool web-like pattern
 */
public class Crystal extends LinkedParticle
{
    public Crystal(){
        super(10, 6, Color.CYAN);
        okDist = 80;
    }
    protected double F(double r){
        /*
         * double c1 = .9259;
         * double c2 = -6.148;
         * double c3 = 3.074;
         * double c4 = 5.839;
         * return c1*Math.pow(r,-2) + 2*c2*Math.pow(r,-3) 
            + 3*Math.pow(r,-4) + c4*Math.exp(-r);
        double c1 = 4.732;
        double c2 = -6.464;
        double c3 = 2.732;
        return c1*Math.pow(r,-2.0) + 2*c2*Math.pow(r,-3.0) 
            + 3*c3*Math.pow(r,-4.0);*/
        //I think I'll give up on trying to make it continuous
        double sq3 = Math.sqrt(3);
        if(r < sq3)
            return 4*(r-1)*(r-sq3);
        else
            return .05*Math.exp(sq3-r);
    }
}
 