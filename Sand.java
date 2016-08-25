import java.awt.Color;
/**
 * Write a description of class Sand here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Sand extends Particle
{
    public Sand()
    {
        super(8, false, 10, Color.YELLOW);//m, fixed, cOfD, colour
    }
    /*
    public void act()
    {
        super.act();
        System.out.println("Sand Y:"+Y+";  Vy:"+velocity.Mag()*Math.sin(velocity.Dir()));
        //System.out.println(this+"X="+X+";   Y="+Y+";   V="+velocity.Mag()+" @"+180*velocity.Dir()/Math.PI+"deg");
    }*/
}
