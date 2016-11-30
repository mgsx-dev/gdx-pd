package net.mgsx.pd.midi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

import org.puredata.core.PdBase;

class PdMidiReceiver implements Receiver
{
	private PdMidiMusic music;
	
	public PdMidiReceiver(PdMidiMusic music) {
		super();
		this.music = music;
	}

	@Override
	public void send(MidiMessage message, long timeStamp) {
		if(message instanceof ShortMessage){
			ShortMessage msg = ((ShortMessage) message);
			switch(msg.getCommand() >> 4){
			case 0x8:
				PdBase.sendNoteOn(msg.getChannel(), msg.getData1(), 0); // emulate note off with a note on at zero velocity
				break;
			case 0x9:
				// apply velocity scaling effect
				if(music.velocityScale != 1)
					PdBase.sendNoteOn(msg.getChannel(), msg.getData1(), 
						Math.round(((float)msg.getData2() / 127.f) * music.velocityScale * 127));
				else
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
			// TODO PdBase.sendSysex(port, value)
			// TODO PdBase.sendRealtime ...
			System.out.println("unsupported sysex message");
		}
	}
	
	@Override
	public void close() {
		
	}
}