import com.badlogic.gdx.jnigen.BuildExecutor;

public class GdxPdNativeCompiler {

	public static void main(String[] args) {
//		if(!BuildExecutor.executeAnt("jni/build-linux64.xml", "-v -Dhas-compiler=true clean postcompile"))
//			System.exit(1);
		
		// sudo apt-get install libc6-dev-i386
		//BuildExecutor.executeAnt("jni/build-linux32.xml", "-v -Dhas-compiler=true clean postcompile");
		
//		if(!BuildExecutor.executeAnt("jni/build-android32.xml", "-v -Dhas-compiler=true clean postcompile"))
//			System.exit(1);
		if(!BuildExecutor.executeAnt("jni/build-macosx64.xml", "-v -Dhas-compiler=true clean postcompile"))
			System.exit(1);
		
		BuildExecutor.executeAnt("jni/build.xml", "-v pack-natives");
	}
}
