package net.mgsx.gdx.pd;

import java.io.File;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;

import org.puredata.core.PdBase;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;

public class MidiPlayerPOC {

	// apt-get install timidity \
	// timidity-interfaces-extra (optional)
	//
	// in Pd open ALSA midi
	// => Pd log this : Opened Alsa Client 129 in:1 out:0
	//
	// run alsa player : aplaymidi -p 129:0  yourmidifile.mid
	//
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		
		Pd.audio = new PdAudioDesktop();
		Pd.audio.create(new PdConfiguration());
		Pd.audio.open(new FileHandle(new File("pd/master.pd")));
		Pd.audio.sendFloat("volume", 0.2f);
	//	Pd.audio.addListener(source, listener);
		
		Sequencer seq = MidiSystem.getSequencer(false);
		Sequencer seq2 = MidiSystem.getSequencer();
//		Synthesizer synth = MidiSystem.getSynthesizer();
//		seq2.getTransmitter().setReceiver(synth.getReceiver());
		seq.getTransmitter().setReceiver(new Receiver() {
			
			// https://www.midi.org/specifications/item/table-1-summary-of-midi-message
			// https://www.nyu.edu/classes/bello/FMT_files/9_MIDI_code.pdf
			@Override
			public void send(MidiMessage message, long timeStamp) {
				if(message instanceof ShortMessage){
					ShortMessage msg = ((ShortMessage) message);
					switch(msg.getCommand() >> 4){
					case 0x8:
						PdBase.sendNoteOn(msg.getChannel(), msg.getData1(), 0); // note off
						break;
					case 0x9:
						PdBase.sendNoteOn(msg.getChannel(), msg.getData1(), msg.getData2());
						break;
					case 0xA:
						PdBase.sendPolyAftertouch(msg.getChannel(), msg.getData1(), msg.getData2());
						break;
					case 0xB:
						PdBase.sendControlChange(msg.getChannel(), msg.getData1(), msg.getData2());
						break;
					case 0xC:
						PdBase.sendProgramChange(msg.getChannel(), msg.getData1());
						break;
					case 0xD:
						PdBase.sendAftertouch(msg.getChannel(), msg.getData1());
						break;
					case 0xE:
						PdBase.sendPitchBend(msg.getChannel(), (msg.getData1() | (msg.getData2() << 7)) - 0x2000);
						break;
					}
					
				}else if(message instanceof SysexMessage){
					SysexMessage msg = (SysexMessage)message;
					// System.out.println(String.format("%x", msg.getData()[0]));
					// TODO PdBase.sendSysex(port, value)
					// TODO PdBase.sendRealtime ...
					System.out.println("unsupported sysex message");
				}
			}
			
			@Override
			public void close() {
				// TODO Auto-generated method stub
				
			}
		});
		
		Sequence sequence = MidiSystem.getSequence(new File("pd/macross.mid"));
		
		// http://www.ccarh.org/courses/253/handout/smf/
		for(Track track : sequence.getTracks()){
			for(int i=0 ; i<track.size() ; i++){
				MidiEvent event = track.get(i);
				MidiMessage message = event.getMessage();
				if(message instanceof MetaMessage){
					MetaMessage meta = ((MetaMessage) message);
					switch(meta.getType()){
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
					case 6: 
						System.out.print("text : " + new String(meta.getData()));
						break;
					case 0x2f:
						break;
					case 0x51: // Tempo
						System.out.print("Tempo");
						break;
					case 0x54: 
						break;
					case 0x58: 
						break;
					case 0x59: 
						break;
					case 0x00: 
						break;
					default:
						System.out.println("unknow : " + meta.getType() + " " + String.format("%x", meta.getType()));
					}
				}
			}
		}
		
		
		Sequencer s = seq;
		s.setSequence(sequence);
		s.open();
		s.start();
		
		System.in.read();
		
		s.stop();
		s.close();
		
		System.exit(0);
	}
}
