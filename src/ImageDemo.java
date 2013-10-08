    import java.awt.image.BufferedImage;  
    import java.io.File;  
    import java.io.IOException;  
     
    import javax.imageio.ImageIO;  
     
    public class ImageDemo {  
     
     public void binaryImage() throws IOException{  
        File file = new File("D:\\builder\\020b.jpg");  
        BufferedImage image = ImageIO.read(file);  
          
        int width = image.getWidth();  
        int height = image.getHeight();  
          
        //二值化
        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);  
        for(int i= 0 ; i < width ; i++){  
            for(int j = 0 ; j < height; j++){  
            int rgb = image.getRGB(i, j);  
            grayImage.setRGB(i, j, rgb);  
            }  
        }  
        
        
        File newFile = new File("D:\\builder\\020f.jpg");  
        ImageIO.write(grayImage, "jpg", newFile);  
        }  
    
     //灰度化
    public void grayImage() throws IOException{  
        File file = new File("D:\\builder\\000.jpg");  
        BufferedImage image = ImageIO.read(file);  
          
        int width = image.getWidth();  
        int height = image.getHeight();  
          
        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);  
        for(int i= 0 ; i < width ; i++){  
            for(int j = 0 ; j < height; j++){  
            int rgb = image.getRGB(i, j);  
            grayImage.setRGB(i, j, rgb);  
            }  
        }  
          
        File newFile = new File("D:\\builder\\000g.jpg");  
        ImageIO.write(grayImage, "jpg", newFile);  
        }  
          
    public static void main(String[] args) throws IOException {  
        ImageDemo demo = new ImageDemo();  
        demo.binaryImage();  
      //  demo.grayImage();  
        }  
     
    } 
