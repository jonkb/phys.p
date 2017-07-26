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
        double c1 = .832;
        double c2 = -6.09;
        double c3 = 10.4;
        return c1/r + c2*Math.exp(-r) + c3*Math.exp(-2*r);
    }
}
 