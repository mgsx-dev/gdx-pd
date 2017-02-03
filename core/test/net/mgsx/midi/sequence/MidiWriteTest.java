package net.mgsx.midi.sequence;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import net.mgsx.midi.sequence.MidiSequence;
import net.mgsx.midi.sequence.MidiTrack;
import net.mgsx.midi.sequence.event.MidiEvent;

public class MidiWriteTest {

	public static void main(String[] args) throws Exception 
	{
		// test("aaa-Track_1-1");
		test("MuteCity"); // TODO doesn't exists anymore
	}
	
	
	private static void test(String name) throws Exception
	{
		MidiSequence file = new MidiSequence(new File(name + ".mid"));
		debug(file, new File(name + ".txt"));
		Runtime.getRuntime().exec("xxd " + name + ".mid " + name + ".hex");
		
		file.writeToFile(new File(name + "-gen.mid"));
		debug(file, new File(name + "-gen.txt"));
		Runtime.getRuntime().exec("xxd " + name + "-gen.mid " + name + "-gen.hex");
		
		file = new MidiSequence(new File(name + "-gen.mid"));
		debug(file, new File(name + "-reload.txt"));
		file.writeToFile(new File(name + "-reload.mid"));
		Runtime.getRuntime().exec("xxd " + name + "-reload.mid " + name + "-reload.hex");
	}
	
	private static void debug( MidiSequence file, File f) throws FileNotFoundException{
		PrintWriter w = new PrintWriter(f);
		int i=0;
		w.println("file");
		for(MidiTrack track : file.getTracks()){
			w.println("track " + i++ + " : " + track.getEventCount());
			for(MidiEvent event : track.getEvents())
			{
				w.println(event);
			}
		}
		w.close();
	}

}
