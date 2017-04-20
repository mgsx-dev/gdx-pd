package net.mgsx.pd;

public class PdConfiguration 
{
	/**
	 * Whether to completely disable Pd (for profiling purpose)
	 */
	public static boolean disabled = false;
	
	/**
	 * Whether to enable Pd in remote mode.
	 * Used with gdx-pd-network.pd patch to enable live patching (see gdx-pd live patching guide).
	 */
	public static boolean remoteEnabled = false;
	
	/**
	 * Host to use in remote mode. Can be host name ("localhost") or IP address ("192.160.0.13")
	 * or broadcast address ("225.0.0.37").
	 * see {@link PdAudioRemote}
	 */
	public static String remoteHost = "localhost";
	
	/**
	 * Port to use in remote mode to send messages to Pd.
	 */
	public static int remoteSendPort = 3000;
	
	/**
	 * Port to use in remote mode to send midi stream to Pd.
	 */
	public static int remoteMidiPort = 3001;
	
	/**
	 * Port to use in remote mode to receive messages from Pd.
	 */
	public static int remoteRecvPort = 3002;

	/**
	 * Whether to check all pd errors. 
	 * When true, any send methods will throw {@link net.mgsx.pd.utils.PdRuntimeException} if receiver
	 * doesn't exists.
	 * Default is true.
	 */
	public static boolean safe = true;
	
	
	/**
	 * input channels. Supported values are : 
	 * 0 (no input), 1 (mono microphone), 2 (stereo microphone).
	 * Default is 0 (no inputs) since microphone usage is not so usual and consume CPU.
	 */
	public int inputChannels = 0;
	
	/**
	 * output channels. Supported values are :
	 * 1 (mono playback), 2 (stereo playback).
	 * Default is 2 (stereo playback) for typical usage.
	 */
	public int outputChannels = 2;
	
	/**
	 * Sample rate in Hertz.
	 * Default is 44100 Hz (typical hardware)
	 */
	public int sampleRate = 44100;
	
	/**
	 * Buffer size in frames (stereo means 2 samples per frames, mono means 1 sample per frame).
	 * Buffer size is a tradeoff between synchronization accuracy and performances : MIDI music
	 * require events processing at high frequency.
	 * Minimum is 64 (default PD block size) and should be power of 2.
	 * Default is 512 which is well suited for MIDI music.
	 * Greater value use less CPU but may lead to synchronization issues (MIDI events at high BPM).
	 * Smaller value use more CPU and could bring audio glitches.
	 */
	public int bufferSize = 512;
	
	/**
	 * Only used by desktop (OpenAL) implementation.
	 * Buffer count is the number of pre-filled buffers.
	 * Buffer count is a tradeoff between latency and performances.
	 * Default bufferCount is 8 to ensure most hardware compatibility.
	 * Latency can be computed as follow : bufferSize * bufferCount / sampleRate.
	 * With 4096 samples (512x8), default latency is about 93 ms.
	 * This value can be decreased to reduce latency.
	 * This value can be increased to avoid glitches or free CPU usage.
	 */
	public int bufferCount = 8;
	
}
