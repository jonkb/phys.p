import java.awt.Color;
/**
 * Write a description of class Rubber here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Rubber extends LinkedParticle
{
    public Rubber()
    {
        super(10, 2, Color.GREEN);
        k = 2;//0;
        okDist = 1;
        rad = .45;
    }
}
