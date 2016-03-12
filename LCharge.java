import java.awt.Color;
/**
 * Write a description of class Sand here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LCharge extends LinkedParticle
{
    public LCharge()
    {
        super(10, 2, Color.RED);
        k = 40;
        okDist = 10;
    }
}
