import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.badlogic.gdx.jnigen.AntScriptGenerator;
import com.badlogic.gdx.jnigen.BuildConfig;
import com.badlogic.gdx.jnigen.BuildTarget;

public class GdxPdNativeGenerator {

	
	private static String[] ext(String[] array, String...args){
		
		int size = array.length + args.length;
		
		String [] narray = new String[size];
		int i=0;
		for( ; i<array.length ; i++){
			narray[i] = array[i];
		}
		for(String arg : args) narray[i++] = arg;
		
		return narray;
	}
	
	public static void copySource() 
	{
		
		String [] pdfiles = {
				
				"pure-data/src/d_arithmetic.c", "pure-data/src/d_array.c", "pure-data/src/d_ctl.c",
				"pure-data/src/d_dac.c", "pure-data/src/d_delay.c", "pure-data/src/d_fft.c",
				"pure-data/src/d_fft_fftsg.c",
				"pure-data/src/d_filter.c", "pure-data/src/d_global.c", "pure-data/src/d_math.c",
				"pure-data/src/d_misc.c", "pure-data/src/d_osc.c", "pure-data/src/d_resample.c",
				"pure-data/src/d_soundfile.c", "pure-data/src/d_ugen.c",
				"pure-data/src/g_all_guis.c", "pure-data/src/g_array.c", "pure-data/src/g_bang.c",
				"pure-data/src/g_canvas.c", "pure-data/src/g_clone.c", "pure-data/src/g_editor.c",
				"pure-data/src/g_graph.c", "pure-data/src/g_guiconnect.c", "pure-data/src/g_hdial.c",
				"pure-data/src/g_hslider.c", "pure-data/src/g_io.c", "pure-data/src/g_mycanvas.c",
				"pure-data/src/g_numbox.c", "pure-data/src/g_readwrite.c",
				"pure-data/src/g_rtext.c", "pure-data/src/g_scalar.c", "pure-data/src/g_template.c",
				"pure-data/src/g_text.c", "pure-data/src/g_toggle.c", "pure-data/src/g_traversal.c",
				"pure-data/src/g_vdial.c", "pure-data/src/g_vslider.c", "pure-data/src/g_vumeter.c",
				"pure-data/src/m_atom.c", "pure-data/src/m_binbuf.c", "pure-data/src/m_class.c",
				"pure-data/src/m_conf.c", "pure-data/src/m_glob.c", "pure-data/src/m_memory.c",
				"pure-data/src/m_obj.c", "pure-data/src/m_pd.c", "pure-data/src/m_sched.c",
				"pure-data/src/s_audio.c", "pure-data/src/s_audio_dummy.c",
				
				"libpd_wrapper/util/ringbuffer.c",
				"libpd_wrapper/util/z_print_util.c",
				"libpd_wrapper/util/z_queued.c",
				"jni/z_jni_plain.c",
				
				"pure-data/src/s_file.c", 
				
				
				"pure-data/src/s_inter.c",
				"pure-data/src/s_loader.c", "pure-data/src/s_main.c", "pure-data/src/s_path.c",
				"pure-data/src/s_print.c", "pure-data/src/s_utf8.c", "pure-data/src/x_acoustics.c",
				"pure-data/src/x_arithmetic.c", "pure-data/src/x_array.c", "pure-data/src/x_connective.c",
				"pure-data/src/x_gui.c", "pure-data/src/x_interface.c", "pure-data/src/x_list.c",
				"pure-data/src/x_midi.c", "pure-data/src/x_misc.c", "pure-data/src/x_net.c",
				"pure-data/src/x_scalar.c", "pure-data/src/x_text.c", "pure-data/src/x_time.c",
				
				
				
				"pure-data/src/x_vexp.c", 
				"pure-data/src/x_vexp_if.c", 
				"pure-data/src/x_vexp_fun.c",
				
				
				
				"libpd_wrapper/s_libpdmidi.c", 
				"libpd_wrapper/x_libpdreceive.c",
				"libpd_wrapper/z_hooks.c", 
				"libpd_wrapper/z_libpd.c",
				
				
				"pure-data/extra/bob~/bob~.c", 
				"pure-data/extra/bonk~/bonk~.c",
				"pure-data/extra/choice/choice.c",
				"pure-data/extra/fiddle~/fiddle~.c", "pure-data/extra/loop~/loop~.c",
				"pure-data/extra/lrshift~/lrshift~.c", "pure-data/extra/pique/pique.c",
				"pure-data/extra/sigmund~/sigmund~.c", "pure-data/extra/stdout/stdout.c"
				
				
		};
		
		for(String pdfile : pdfiles){
			try {
				Files.createDirectories(new File("jni/pd", pdfile).getParentFile().toPath());
				if(pdfile.startsWith("jni/"))
					Files.copy(new File("../libpd", pdfile).toPath(), new File("jni/pd", pdfile.replaceAll("^jni/", "")).toPath(), StandardCopyOption.REPLACE_EXISTING);
				else
					Files.copy(new File("../libpd", pdfile).toPath(), new File("jni/pd", pdfile).toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private static void setup(BuildTarget target){
		target.libraries += " -lm -ldl -lpthread";
		target.cFlags += " -DPD -DHAVE_UNISTD_H -DUSEAPI_DUMMY -DHAVE_LIBDL -Wno-int-to-pointer-cast -Wno-pointer-to-int-cast -fPIC";
		target.headerDirs =	ext(target.headerDirs, 
				"../../libpd/pure-data/src", 
				"../../libpd/libpd_wrapper",
				"../../libpd/jni",
				"../../libpd/libpd_wrapper/util");
	}
	private static void setupAndroid(BuildTarget target){
		target.libraries += " -ldl";
		target.cFlags += " -DPD -DHAVE_UNISTD_H -DHAVE_LIBDL -DUSEAPI_DUMMY -w -Wno-int-to-pointer-cast -Wno-pointer-to-int-cast";
		target.headerDirs =	ext(target.headerDirs, 
				"../../libpd/pure-data/src", 
				"../../libpd/libpd_wrapper",
				"../../libpd/jni",
				"../../libpd/libpd_wrapper/util");
	}
	
	public static void main(String[] args) 
	{
		copySource();
		
		BuildTarget linux64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Linux, true);
		setup(linux64);
		// linux64.libName = "org/puredata/core/natives/linux/x86_64/libpdnative.so";
		
		BuildTarget linux32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Linux, false);
		setup(linux32);
//		linux64.libName = "org/puredata/core/natives/linux/x86_64/libpdnative.so";
		
		BuildTarget android64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Android, true); // arm7
		setupAndroid(android64);
//		linux64.libName = "org/puredata/core/natives/linux/x86_64/libpdnative.so";
		
		BuildTarget mac64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.MacOsX, true);
		mac64.libName = "org/puredata/libpd-macosx.so";
		setup(mac64);
		
		
		BuildConfig config = new BuildConfig("gdx-pd");
		
		new AntScriptGenerator().generate(config, linux64, linux32, android64, mac64); // , linux32, windows32, windows64, macosx, android, ios)
	}

}
