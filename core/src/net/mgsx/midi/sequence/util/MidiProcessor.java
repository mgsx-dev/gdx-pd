//////////////////////////////////////////////////////////////////////////////
//	Copyright 2011 Alex Leffelman
//	
//	Licensed under the Apache License, Version 2.0 (the "License");
//	you may not use this file except in compliance with the License.
//	You may obtain a copy of the License at
//	
//	http://www.apache.org/licenses/LICENSE-2.0
//	
//	Unless required by applicable law or agreed to in writing, software
//	distributed under the License is distributed on an "AS IS" BASIS,
//	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//	See the License for the specific language governing permissions and
//	limitations under the License.
//////////////////////////////////////////////////////////////////////////////

package net.mgsx.midi.sequence.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.mgsx.midi.playback.Sequencer;
import net.mgsx.midi.sequence.MidiSequence;
import net.mgsx.midi.sequence.MidiTrack;
import net.mgsx.midi.sequence.event.Controller;
import net.mgsx.midi.sequence.event.MidiEvent;
import net.mgsx.midi.sequence.event.meta.Tempo;

public class MidiProcessor implements Sequencer
{
    private static final int PROCESS_RATE_MS = 8;

    private HashMap<Class<? extends MidiEvent>, ArrayList<MidiEventListener>> mEventsToListeners;
    private HashMap<MidiEventListener, ArrayList<Class<? extends MidiEvent>>> mListenersToEvents;

    private MidiSequence mMidiFile;
    private boolean mRunning;
    private double mTicksElapsed;
    private long mMsElapsed;

    private int mMPQN;
    private int mPPQ;

    private MidiTrackEventQueue[] mEventQueues;

    public MidiProcessor(MidiSequence input)
    {

        mMidiFile = input;

        mMPQN = Tempo.DEFAULT_MPQN;
        mPPQ = mMidiFile.getResolution();

        mEventsToListeners = new HashMap<Class<? extends MidiEvent>, ArrayList<MidiEventListener>>();
        mListenersToEvents = new HashMap<MidiEventListener, ArrayList<Class<? extends MidiEvent>>>();

        this.reset();
    }

    public synchronized void play()
    {
        if(mRunning)
            return;

        mRunning = true;
        new Thread(new Runnable()
        {
            public void run()
            {
                process();
            }
        }).start();
    }

    public void stop()
    {
        mRunning = false;
    }

    public void reset()
    {

        mRunning = false;
        mTicksElapsed = 0;
        mMsElapsed = 0;

        ArrayList<MidiTrack> tracks = mMidiFile.getTracks();

        if(mEventQueues == null)
        {
            mEventQueues = new MidiTrackEventQueue[tracks.size()];
        }

        for(int i = 0; i < tracks.size(); i++)
        {
            mEventQueues[i] = new MidiTrackEventQueue(tracks.get(i));
        }
    }

    public boolean isStarted()
    {
        return mTicksElapsed > 0;
    }

    public boolean isRunning()
    {
        return mRunning;
    }

    protected void onStart(boolean fromBeginning)
    {

        Iterator<MidiEventListener> it = mListenersToEvents.keySet().iterator();

        while(it.hasNext())
        {

            MidiEventListener mel = it.next();
            mel.onStart(fromBeginning);
        }
    }

    protected void onStop(boolean finished)
    {

        Iterator<MidiEventListener> it = mListenersToEvents.keySet().iterator();

        while(it.hasNext())
        {

            MidiEventListener mel = it.next();
            mel.onStop(finished);
        }
        
    }

    public void registerEventListener(MidiEventListener mel, Class<? extends MidiEvent> event)
    {

        ArrayList<MidiEventListener> listeners = mEventsToListeners.get(event);
        if(listeners == null)
        {

            listeners = new ArrayList<MidiEventListener>();
            listeners.add(mel);
            mEventsToListeners.put(event, listeners);
        }
        else
        {
            listeners.add(mel);
        }

        ArrayList<Class<? extends MidiEvent>> events = mListenersToEvents.get(mel);
        if(events == null)
        {

            events = new ArrayList<Class<? extends MidiEvent>>();
            events.add(event);
            mListenersToEvents.put(mel, events);
        }
        else
        {
            events.add(event);
        }
    }

    public void unregisterEventListener(MidiEventListener mel)
    {

        ArrayList<Class<? extends MidiEvent>> events = mListenersToEvents.get(mel);
        if(events == null)
        {
            return;
        }

        for(Class<? extends MidiEvent> event : events)
        {

            ArrayList<MidiEventListener> listeners = mEventsToListeners.get(event);
            listeners.remove(mel);
        }

        mListenersToEvents.remove(mel);
    }

    public void unregisterEventListener(MidiEventListener mel, Class<? extends MidiEvent> event)
    {

        ArrayList<MidiEventListener> listeners = mEventsToListeners.get(event);
        if(listeners != null)
        {
            listeners.remove(mel);
        }

        ArrayList<Class<? extends MidiEvent>> events = mListenersToEvents.get(mel);
        if(events != null)
        {
            events.remove(event);
        }
    }

    public void unregisterAllEventListeners()
    {
        mEventsToListeners.clear();
        mListenersToEvents.clear();
    }

    protected void dispatch(MidiEvent event)
    {

        // Tempo and Time Signature events are always needed by the processor
        if(event.getClass().equals(Tempo.class))
        {
            mMPQN = ((Tempo) event).getMpqn();
        }

        this.sendOnEventForClass(event, event.getClass());
        this.sendOnEventForClass(event, MidiEvent.class);
    }

    private void sendOnEventForClass(MidiEvent event, Class<? extends MidiEvent> eventClass)
    {

        ArrayList<MidiEventListener> listeners = mEventsToListeners.get(eventClass);

        if(listeners == null)
        {
            return;
        }

        for(MidiEventListener mel : listeners)
        {
            mel.onEvent(event, mMsElapsed);
        }
    }

    private void process()
    {

        onStart(mTicksElapsed < 1);

        long lastMs = System.currentTimeMillis();

        boolean finished = false;

        while(mRunning)
        {

            long now = System.currentTimeMillis();
            long msElapsed = now - lastMs;

            if(msElapsed < PROCESS_RATE_MS)
            {
                try
                {
                    Thread.sleep(PROCESS_RATE_MS - msElapsed);
                }
                catch(Exception e)
                {
                }
                continue;
            }

            double ticksElapsed = MidiUtil.msToTicks(msElapsed, mMPQN, mPPQ);

            if(ticksElapsed < 1)
            {
                continue;
            }

            lastMs = now;
            mMsElapsed += msElapsed;
            mTicksElapsed += ticksElapsed;

            boolean more = false;
            for(int i = 0; i < mEventQueues.length; i++)
            {

                MidiTrackEventQueue queue = mEventQueues[i];
                if(!queue.hasMoreEvents())
                {
                    continue;
                }

                ArrayList<MidiEvent> events = queue.getNextEventsUpToTick(mTicksElapsed);
                for(MidiEvent event : events)
                {
                    this.dispatch(event);
                }

                if(queue.hasMoreEvents())
                {
                    more = true;
                }
            }

            if(!more)
            {
                finished = true;
                break;
            }
        }
        
        for(int i=0 ; i<16 ; i++){
			Controller e = new Controller(0, i, 123, 0); // All notes off
			this.dispatch(e);
		}

        mRunning = false;
        onStop(finished);
    }

    private class MidiTrackEventQueue
    {

        private MidiTrack mTrack;
        private Iterator<MidiEvent> mIterator;
        private ArrayList<MidiEvent> mEventsToDispatch;
        private MidiEvent mNext;

        public MidiTrackEventQueue(MidiTrack track)
        {

            mTrack = track;

            mIterator = mTrack.getEvents().iterator();
            mEventsToDispatch = new ArrayList<MidiEvent>();

            if(mIterator.hasNext())
            {
                mNext = mIterator.next();
            }
        }

        public ArrayList<MidiEvent> getNextEventsUpToTick(double tick)
        {

            mEventsToDispatch.clear();

            while(mNext != null)
            {

                if(mNext.getTick() <= tick)
                {
                    mEventsToDispatch.add(mNext);

                    if(mIterator.hasNext())
                    {
                        mNext = mIterator.next();
                    }
                    else
                    {
                        mNext = null;
                    }
                }
                else
                {
                    break;
                }
            }

            return mEventsToDispatch;
        }

        public boolean hasMoreEvents()
        {
            return mNext != null;
        }
    }

	@Override
	public void setBPM(float bpm) {
		mMPQN = (int) (60000000 / bpm);
	}

	@Override
	public float getBPM() {
		return mMPQN / 60000000f;
	}

	@Override
	public long getPositionInTicks() {
		return (long)mTicksElapsed;
	}

	@Override
	public void setPositionInTicks(long ticks) {
		// not supported
	}
}
