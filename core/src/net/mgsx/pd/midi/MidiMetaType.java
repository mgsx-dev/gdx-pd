package net.mgsx.pd.midi;

class MidiMetaType
{
	// http://www.ccarh.org/courses/253/handout/smf/
	public static final int SequenceNumber = 0x00;
	public static final int TextEvent = 0x01;
	public static final int CopyrightNotice = 0x02;
	public static final int SequenceTrackName = 0x03;
	public static final int InstrumentName = 0x04;
	public static final int LyricsText = 0x05;
	public static final int MarkerText = 0x06;
	public static final int CuePoint = 0x07;
	public static final int midiChannelPrefixAssignement = 0x20;
	public static final int EndOfTrack = 0x2f;
	public static final int Tempo = 0x51;
	public static final int SMPTE = 0x54;
	public static final int TimeSignature = 0x58;
	public static final int keySignature = 0x59;
	public static final int miscEvent = 0x7f;
	
}