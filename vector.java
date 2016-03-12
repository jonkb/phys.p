public class vector implements java.io.Serializable{
    private double magnitude;
    private double direction;
    public vector(){}
    public vector(double mag, double dir)
    //dir is in degrees clockwise from east
    {
        magnitude = mag;
        direction = dir%(2*Math.PI);
    }
    public double Mag()
    {return magnitude;}
    public double Dir()
    {return direction;}
    public void setValues(double mag, double dir)
    {
        magnitude = mag;
        direction = dir;
    }
    public void addOn(vector v)
    {
        assert v.Mag() < 99999999: "added vector is too large";
        double d1 = direction;
        double d2 = v.Dir();
        double Vx = (magnitude*Math.cos(d1)+v.Mag()*Math.cos(d2));
        double Vy = (magnitude*Math.sin(d1)+v.Mag()*Math.sin(d2));
        double Vr = Math.sqrt(Vx*Vx+Vy*Vy);
        double Vtheta = Math.atan2(Vy , Vx);
        assert Vr < 99999999: "Resultant vector is too large";
        setValues(Vr, Vtheta);
    }
    public static vector add(vector v1, vector v2)
    {
        double d1 = v1.Dir();
        double d2 = v2.Dir();
        double Vx = (v1.Mag()*Math.cos(d1)+v2.Mag()*Math.cos(d2));
        double Vy = (v1.Mag()*Math.sin(d1)+v2.Mag()*Math.sin(d2));
        double Vr = Math.sqrt(Vx*Vx+Vy*Vy);
        double Vtheta = Math.atan2(Vy , Vx);
        assert Vr < 99999999: "Resultant vector is too large";
        return new vector(Vr, Vtheta);
    }
    public static vector scalDiv(vector v, double s)
    {
        assert s != 0;
        return new vector(v.Mag()/s, v.Dir());
    }
    public static vector scalMult(vector v, double s)
    {
        assert s < 99999999;
        return new vector(v.Mag()*s, v.Dir());
    }
    public static vector compToVect(double dx, double dy)
    {
        double mag = Math.sqrt(dx*dx+dy*dy);
        double dir = Math.atan2(dy,dx);
        return new vector(mag, dir);
    }
    public String toString(){
        return magnitude + " @ " + direction;
    }
}
