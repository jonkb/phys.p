import java.awt.Color;

/**
 * Write a description of class Water here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Water extends LinkedParticle
{
    public Water()
    {
        super(1, 8, Color.BLUE);
    }
    protected double F(double r)
    {
        if(r < 2)
            return 6/r - 6/(r-3) - 9; //Zeros=1,2 Min=1.5
        else
            return 0;
    }
    /*
    protected double F(double r)
    {
        return .1*(4/Math.PI*(Math.atan(4*r-7.6)-Math.atan(12*r-14.4))+Math.pow(Math.E, 1.2-4*r));
    }*/
}
