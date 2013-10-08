/**
 * Volatile 作用测试类
 * @author TMSer
 * @2012-11-05
 */
public class VolatileTest extends Thread{
	
	public VolatileTest(A a){
		this.a  =  a;
	}
	private A a;
	public void run(){
		for(int i=0;i<10;i++){
			a.setA(i);
			try {
				sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(a.getA());

		}
	}
	
	public static void main(String[] args) {
		Thread[] t = new VolatileTest[15];
		A a = new VolatileTest.A();
		for(int i=0;i<15;i++){
			t[i] = new VolatileTest(a);
		}
		
		for(int i=0;i<15;i++){
			t[i].start();
		}
		
		for(int i=0;i<15;i++){
			try {
				t[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
 public static class A{
		private volatile int a = 0; //分别输出加volatile 和不加 volatile 的结果
		public void setA(int a){
			this.a = a;
		}
		
		public int getA(){
			return a;
		}
	}
}
