import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class NIOTest {

	public static void main(String[] args) throws Exception {
		
		File oldf = new File("e:\\test.txt");
		File newf = new File("e:\\newtest.txt");
		
		long start = System.currentTimeMillis();
		FileInputStream in = new FileInputStream(oldf);
		FileOutputStream out = new FileOutputStream(newf);
		
		FileChannel fin = in.getChannel();
		FileChannel fout = out.getChannel();
		
		ByteBuffer buffer =  ByteBuffer.allocate(1024);
		while(fin.read(buffer) != -1){
			buffer.flip();
			fout.write(buffer);
			buffer.clear();
		}
		
/*		byte[] buffer = new byte[1024];
		BufferedInputStream fin = new BufferedInputStream(in);
	    while((fin.read(buffer))!=-1){
	    	out.write(buffer);        
	     }*/
	    System.out.println("Used time: " + (System.currentTimeMillis() - start));
	    in.close();
	    out.close();

	}
}
