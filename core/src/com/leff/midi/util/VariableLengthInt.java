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

package com.leff.midi.util;

import java.io.IOException;
import java.io.InputStream;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class VariableLengthInt
{
    private int mValue;
    private byte[] mBytes;
    private int mSizeInBytes;

    public VariableLengthInt(int value)
    {
        setValue(value);
    }

    public VariableLengthInt(InputStream in) throws IOException
    {
        parseBytes(in);
    }

    public void setValue(int value)
    {
        mValue = value;
        buildBytes();
    }

    public int getValue()
    {
        return mValue;
    }

    public int getByteCount()
    {
        return mSizeInBytes;
    }

    public byte[] getBytes()
    {
        return mBytes;
    }

    private void parseBytes(InputStream in) throws IOException
    {
    	mValue = 0;
    	for(mSizeInBytes = 0 ; mSizeInBytes<4 ; ){
    		int b = in.read();
    		mValue = (mValue << 7) | (b & 0x7F);
    		mSizeInBytes++;
    		if((b & 0x80) == 0) break;
    		if(mSizeInBytes == 4) throw new GdxRuntimeException("bad variable length overflow !");
    	}

    	createBytes();
    }

    private void buildBytes()
    {
    	mSizeInBytes = 0;
    	int val = mValue;
    	for(mSizeInBytes = 0; mSizeInBytes<4 ; ){
    		val >>= 7;
    		mSizeInBytes++;
    		if(val == 0) break;
    		if(mSizeInBytes == 4) throw new GdxRuntimeException("bad variable length overflow !");
    	};
    	
    	createBytes();
    	
    }
    
    private void createBytes(){
    	mBytes = new byte[mSizeInBytes];
        int val = mValue;
        for(int i = 0; i < mSizeInBytes; i++)
        {
            mBytes[mSizeInBytes-1-i] = i==0 ? (byte)( val & 0x7F) : (byte)( val & 0x7F | 0x80);
            val >>= 7;
        }
    }

    @Override
    public String toString()
    {
        return MidiUtil.bytesToHex(mBytes) + " (" + mValue + ")";
    }
}
