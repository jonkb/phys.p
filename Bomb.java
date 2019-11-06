import java.awt.Color;
import java.util.ArrayList;
/**
 * Write a description of class Sand here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Bomb extends Particle{
    int charges = 1;
    public Bomb(){
        super(1, false, Color.RED);
    }
    public void interAct(){
        ArrayList<Particle> Neighbors = getParticlesInRange(1);
        for(int a = 0; a < Neighbors.size(); a++)
        {
            Being near = Neighbors.get(a);
            if(near != null && near.getClass() != Bomb.class)
            {
                world.removeBeing(near);
                charges--;
                if(charges == 0)
                    world.removeBeing(this);
            }
        }
    }
}
