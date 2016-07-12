package net.diderot.scijava.provider;

import org.junit.Test;

import net.diderot.scijava.api.Scijava;

/*
 * Example JUNit test case
 * 
 */

public class ScijavaImplTest {

	/*
	 * Example test method
	 */

	@Test
	public void simple() {
		Scijava impl = new ScijavaImpl();
		
		impl.say("Hello World");
	}

}
