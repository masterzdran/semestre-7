
public class Aula1 {
/*
	public static void main(String[] args) {
		for (int i=0; i<10; ++i){
			new MyThread(i).start();
		}
	}
*/
	public static void main(String[] args) {
		for (int i=0; i<10; ++i){
			final int a=i;
			new Thread(new Runnable(){
				public void run(){
					for (int j=0; j<20; ++j)
						System.out.println("Thread"+a+":"+j);
				}
			}).start();
		}	
	}
}
