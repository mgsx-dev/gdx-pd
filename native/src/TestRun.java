import java.io.IOException;

import org.puredata.core.PdBase;

public class TestRun {

	public static void main(String[] args) throws IOException {
		
		// in = PdBase.class.getResourceAsStream("/libgdx-pd64.so");
		
		// System.out.println(PdBase.exists("toto"));
		
		System.out.println(PdBase.openAudio(0, 2, 44100));
//		File tmp = new File("/tmp/libgdx-pd64" + System.currentTimeMillis() + ".so");
//		Files.copy(
//				TestRun.class.getResourceAsStream("/libgdx-pd64.so"), 
//				tmp.toPath());
//		
//		System.load(tmp.getAbsolutePath());
//		
//		initialize();
	}
	
	

}
