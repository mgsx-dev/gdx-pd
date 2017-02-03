import org.junit.Assert;
import org.junit.Test;

import com.leff.midi.util.VariableLengthInt;

public class VariableLengthIntTest {

	@Test
	public void testOne(){
		
		Assert.assertArrayEquals(new byte[]{0x7F}, new VariableLengthInt(0x7F).getBytes());
	}
	@Test
	public void testTwo(){
		
		Assert.assertArrayEquals(new byte[]{(byte)0x81, 0x7F}, new VariableLengthInt(0xFF).getBytes());
	}
	@Test
	public void testThree(){
		
		Assert.assertArrayEquals(new byte[]{(byte)0x82, (byte)0x80, 0x00}, new VariableLengthInt(0x8000).getBytes());
	}
	@Test
	public void testFour(){
		
		Assert.assertArrayEquals(new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0x7F}, new VariableLengthInt(0xFFFFFFF).getBytes());
	}

}
