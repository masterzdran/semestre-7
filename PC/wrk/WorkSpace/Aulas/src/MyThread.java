
public class MyThread extends Thread{

	private final int id;
	
	public MyThread(int index)
	{
		id = index;
	}
	public void run()	
	{
		for(int i=0; i<20; ++i){
			System.out.println("Thread "+id+":"+i);
		}
	}
}
