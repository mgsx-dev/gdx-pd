package net.mgsx.pd;

public class PdConfiguration 
{
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
	 * Default is 256 frames per buffer.
	 * Can be decreased to reduce latency or increased to avoid glitches.
	 */
	public int bufferSize = 256;
	
	/**
	 * Buffer count is number of pre-filled buffers.
	 * Overall latency in sample is bufferSize * bufferCount.
	 * Overall latency in miliseconds is 1000 * bufferSize * bufferCount / sampleRate.
	 * Default latency is about 52 ms.
	 */
	public int bufferCount = 9;
}
