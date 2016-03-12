import java.util.ArrayList;
public class Data implements java.io.Serializable
{
    double precision;
    double zoom;
    String saveFile;
    pData[] particles;
    public Data(double pre, double z, String saveF, ArrayList<Being> b)
    {
        precision = pre;
        zoom = z;
        saveFile = saveF;
        particles = new pData[b.size()];
        int i = 0;
        for(Being be: b)
        {
            if(be instanceof Particle)
            {
                particles[i] = new pData((Particle) be);
                i++;
            }
        }
    }
    public class pData implements java.io.Serializable
    {
        double X;
        double Y;
        vector velocity;
        Types type;
        public pData(Particle p)
        {
            X = p.X;
            Y = p.Y;
            velocity = p.velocity;
            type = p.type();
        }
        public pData(double x, double y, vector vel, Types t)
        {
            X = x;
            Y = y;
            velocity = vel;
            type = t;
        }
    }
}
