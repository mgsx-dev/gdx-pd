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

package net.mgsx.midi.sequence.event;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.midi.sequence.event.meta.MetaEvent;
import net.mgsx.midi.sequence.util.VariableLengthInt;

public abstract class MidiEvent implements Comparable<MidiEvent>
{
    protected long mTick;
    protected VariableLengthInt mDelta;
    
    /** insert index on track (used for deterministic order) */
    public int mIndex;

    public MidiEvent(long tick, long delta)
    {
        mTick = tick;
        mDelta = new VariableLengthInt((int) delta);
    }

    public long getTick()
    {
        return mTick;
    }

    public long getDelta()
    {
        return mDelta.getValue();
    }

    public void setDelta(long d)
    {
        mDelta.setValue((int) d);
    }

    protected abstract int getEventSize();

    public int getSize()
    {
        return getEventSize() + mDelta.getByteCount();
    }

    public void writeToFile(OutputStream out) throws IOException
    {
        out.write(mDelta.getBytes());
    }
    
    @Override
    public int compareTo(MidiEvent other)
    {
    	// first compare by time position
        if(this.mTick < other.mTick)
        {
            return -1;
        }
        if(this.mTick > other.mTick)
        {
            return 1;
        }

        // then compare by insert order (to keep read/write deterministic)
        return Integer.compare(mIndex, other.mIndex);
    }


    private static int sId = -1;
    private static int sType = -1;
    private static int sChannel = -1;

    public static final MidiEvent parseEvent(long tick, long delta, InputStream in) throws IOException
    {
        in.mark(1);

        
        int id = in.read();
        System.out.println(String.format("%d +%d 0x%x", tick, delta, id));
        if(!verifyIdentifier(id))
        {
            in.reset();
        }

        if(sType >= 0x8 && sType <= 0xE)
        {
            return ChannelEvent.parseChannelEvent(tick, delta, sType, sChannel, in);
        }
        else if(sId == 0xFF)
        {
            return MetaEvent.parseMetaEvent(tick, delta, in);
        }
        else if(sId == 0xF0 || sId == 0xF7)
        {
            VariableLengthInt size = new VariableLengthInt(in);
            byte[] data = new byte[size.getValue()];
            in.read(data);
            return new SystemExclusiveEvent(sId, tick, delta, data);
        }
        else
        {
        	throw new GdxRuntimeException("Unable to handle status byte, skipping: " + sId);
        }

    }

    private static boolean verifyIdentifier(int id)
    {
        sId = id;

        int type = id >> 4;
        int channel = id & 0x0F;

        if(type >= 0x8 && type <= 0xE)
        {
            sId = id;
            sType = type;
            sChannel = channel;
        }
        else if(id == 0xFF)
        {
            sId = id;
            sType = -1;
            sChannel = -1;
        }
        else if(type == 0xF)
        {
            sId = id;
            sType = type;
            sChannel = -1;
        }
        else
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "" + mTick + " (" + mDelta.getValue() + "): " + this.getClass().getSimpleName();
    }
}
