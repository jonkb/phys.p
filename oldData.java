public class oldData implements java.io.Serializable{
    double precision;
    double zoom;
    String saveFile;
    pData[] particles;
    public oldData(double pre, double z, String saveF, BiList<Being> b){
        precision = pre;
        zoom = z;
        saveFile = saveF;
        particles = new pData[b.size()];
        int i = 0;
        for(BiList.Node n = b.o1; n != null; n = n.getNext()){
            Being be = (Being) n.getVal();
            if(be instanceof Particle){
                particles[i] = new pData((Particle) be);
                i++;
            }
        }
    }
    public class pData implements java.io.Serializable{
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
