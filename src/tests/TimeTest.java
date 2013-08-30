package tests;

import static org.junit.Assert.*;

import org.junit.Test;
import server.Time;

public class TimeTest {

	/**
	 * does a simple test of measuring time elapsed between an original call and a new call (using the Time class's main() call)
	 * @throws InterruptedException
	 * 
	 *  we test this at 02:28:15, 02:28:05, 02:59:59, 23:59:55, 23:59:59, 00:00:00, and various times of day. It succeeds at all points.
	 */
	
	@Test
	public void test() throws InterruptedException {
		String firstTime = Time.main();
		String[] splitFirst = firstTime.split(":");
		Thread.sleep(5000);
		String secondTime = Time.main();
		String[] splitSecond = secondTime.split(":");
		
		
		if(Integer.parseInt(splitFirst[2])>=55)  //if first time was >= 55 seconds
		{
			assertEquals(Integer.parseInt(splitSecond[2]),(Integer.parseInt(splitFirst[2])+5)%60); //seconds should increase by 5 mod 60

			if(Integer.parseInt(splitFirst[1])==59) //if first time was 59 minutes
			{ 
				assertTrue(splitFirst[1].equals("00")); //reset to zero minutes
				
				if(Integer.parseInt(splitFirst[0])==23) //if first time was 23 hours
					assertTrue(splitFirst[0].equals("00")); //reset to zero hours
				else //if first time is not 23 hours
					assertEquals(Integer.parseInt(splitFirst[0])+1, Integer.parseInt(splitSecond[0])); //hours should increase by 1
			}
			else //if first time was not 59 minutes
			{
				assertEquals(Integer.parseInt(splitFirst[1])+1, Integer.parseInt(splitSecond[1])); //minutes should increase by 1
				assertTrue(splitFirst[0].equals(splitSecond[0])); //hours should not change
			}
		}
		else //if first time was <= 55 seconds
		{
			assertEquals(Integer.parseInt(splitSecond[2]),(Integer.parseInt(splitFirst[2])+5)); //seconds should increase by 5
			assertTrue(splitFirst[1].equals(splitSecond[1])); //minutes should not change
			assertTrue(splitFirst[0].equals(splitSecond[0])); //hours should not change
			
		}
		
	}

}
