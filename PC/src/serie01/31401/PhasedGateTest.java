package serie01;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PhasedGateTest {
	PhasedGate p;

	@BeforeMethod()
	public void beforeTest()
	{
		p = new PhasedGate(5);
	}
  @Test(threadPoolSize = 50, invocationCount = 10,  timeOut = 10000)
  public void Wait() {
    System.out.println("Testing PhasedGate Wait");
    p.Wait();
    p.RemoveParticipant();
  }

}
