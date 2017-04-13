import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
public class test
{
    /**
     * Conclusion: top left of bufferedimages is (0,0)
     */
    static void go(){
        System.out.println("test");
        
        if(true){
            screen s = new screen();
            
            JFrame frame = new JFrame("test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(s);
            frame.pack();
            frame.setVisible(true);
        }
    }
    
    static class screen extends JPanel{
        int width = 100;
        int height = 100;
        Color c = new Color(255,255,255);
        BufferedImage back;
        BufferedImage pixel;
        BufferedImage circle;
        screen(){
            setPreferredSize(new Dimension(Math.max(width, 200), height));
            back = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            pixel = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
            pixel.setRGB(0,0,c.getRGB());
            repaint();
        }
        public void paint(Graphics g){
            //Draw the background
            g.setColor(Color.white);
            g.fillRect(0, 0, width, height);
            
            //g.setColor(Color.blue);
            //g.fillOval(12,  12,  6, 6);
            /*
            int diam = 2;
            circle = new BufferedImage(diam, diam, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics cg = circle.getGraphics();
            cg.setColor(Color.blue);
            cg.fillOval(0,0,diam,diam);
            g.drawImage(circle, 0, 0, null);*/
            
            //
            double z = 8;
            double X = 1.2;
            double Y = 1.3;
            g.drawImage(getImage(), (int)Math.round(z*X), (int)Math.round(z*Y), null);
        }
        public BufferedImage getImage(){
            double z = 8;
            double X = 1.2;
            double Y = 1.3;
            if(z < 4){
                return null;
            }
            else{
                Color color = Color.blue;
                /*
                int diam = (int) Math.round(z/2);
                image = new BufferedImage(diam, diam, BufferedImage.TYPE_4BYTE_ABGR);
                Graphics ig = image.getGraphics();
                ig.setColor(Color.blue);
                ig.fillOval(0,0,diam,diam);*/
                
                //absolute radius is always 1/4
                double radius = z / 4.0; //cells
                int cx = (int) Math.round(X*z); //cells
                int cy = (int) Math.round(Y*z); //cells
                int w = (int) (2*Math.ceil(radius) + 1); //+1 for center dot
                int h = w;
                //capitals mean absolute units
                double pX, pY, pdX, pdY;
                BufferedImage circle = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
                for(int px = 0; px < w; px++){
                    for(int py = 0; py < h; py++){
                        //absolute coordinates of the current pixel in the image
                        pX = ( cx - Math.ceil(radius) + px )/z;
                        pY = ( cy - Math.ceil(radius) + py )/z;
                        //p[delta](X/Y)
                        pdX = pX - X;
                        pdY = pY - Y;
                        double pRad = Math.sqrt(pdX*pdX + pdY*pdY);
                        System.out.println(pRad);
                        //pdx^2+pdy^2 < (1/4)^2
                        if( pdX*pdX + pdY*pdY < 1.0/16)
                            circle.setRGB(px,py,color.getRGB());
                    }
                }
                return circle;
            }
        }
    }
}
