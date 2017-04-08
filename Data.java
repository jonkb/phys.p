public class Data implements java.io.Serializable{
    double precision;
    double zoom;
    int frame;
    int subframe;
    String saveFile;
    pData[] particles;
    public Data(int f, int sf, double pre, double z, String saveF, BiList<Being> b){
        frame = f;
        subframe = sf;
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
    /*
    public nData(int f, int sf, oldData d){
        frame = f;
        subframe = sf;
        precision = d.precision;
        zoom = d.zoom;
        saveFile = d.saveFile;
        particles = new pData[d.particles.length];
        int i = 0;
        for(oldData.pData opd : d.particles){
            particles[i] = new pData(opd);
            i++;
        }
    }*/
    public class pData implements java.io.Serializable{
        double X;
        double Y;
        vector velocity;
        Types type;
        public pData(Particle p){
            X = p.X;
            Y = p.Y;
            velocity = p.velocity;
            type = p.type();
        }
        public pData(double x, double y, vector vel, Types t){
            X = x;
            Y = y;
            velocity = vel;
            type = t;
        }
        /*public pData(oldData.pData opd){
            X = opd.X;
            Y = opd.Y;
            velocity = opd.velocity;
            type = opd.type;
        }*/
    }
}
