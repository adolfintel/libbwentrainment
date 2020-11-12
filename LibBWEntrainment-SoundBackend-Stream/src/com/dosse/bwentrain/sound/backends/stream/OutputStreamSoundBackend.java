/*
 * Copyright (C) 2014 Federico Dossena.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.dosse.bwentrain.sound.backends.stream;

import com.dosse.bwentrain.sound.ISoundDevice;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Federico
 */
public class OutputStreamSoundBackend implements ISoundDevice {

    private OutputStream out;
    private boolean opened = false, closed = false;
    private float sampleRate, volume = 1;
    private int nChannels;

    /**
     * output to OutputStream as 16 bit, little endian, signed
     *
     * @param out stream
     * @param sampleRate sample rate &gt;0
     * @param nChannels number of channels &gt;=1
     */
    public OutputStreamSoundBackend(OutputStream out, float sampleRate, int nChannels) {
        if (nChannels < 1) {
            throw new IllegalArgumentException("nChannels must be >=1");
        }
        if (sampleRate <= 0) {
            throw new IllegalArgumentException("sampleRate must be >0");
        }
        this.out = out;
        this.sampleRate = sampleRate;
        this.nChannels = nChannels;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        if (!opened || closed) {
            return;
        }
        try {
            out.close();
            closed = true;
        } catch (IOException ex) {
        }
    }

    @Override
    public void open() {
        if (opened || closed) {
            return;
        }
        opened = true;
    }

    @Override
    public int getChannelCount() {
        return nChannels;
    }

    @Override
    public int getBitsPerSample() {
        return 16;
    }

    @Override
    public float getSampleRate() {
        return sampleRate;
    }

    private byte[] tempBuffer = null; //used to avoid reallocating memory every time write is called, as long as write is always called with an array of the same size

    @Override
    public void write(float[] data) {
        if (closed || !opened) {
            return;
        }
        if (tempBuffer == null || tempBuffer.length != data.length * 2) { //we must reallocate the tempBuffer
            tempBuffer = new byte[data.length * 2];
        }
        //convert floats to 16 bit samples
        short temp;
        for (int i = 0; i < data.length; i++) {
            //data[i] = data[i] < -1 ? -1 : data[i] > 1 ? 1 : data[i]; //code for clipping instead of overflow, commented out to improve performance
            temp = (short) (data[i] * volume * Short.MAX_VALUE);
            tempBuffer[2 * i] = (byte) temp;
            tempBuffer[2 * i + 1] = (byte) (temp >> 8);
        }
        try {
            out.write(tempBuffer, 0, tempBuffer.length);
        } catch (IOException ex) {
        }
    }

    @Override
    public void setVolume(float vol) {
        volume = vol < 0 ? 0 : vol > 1 ? 1 : vol;
    }

    @Override
    public float getVolume() {
        return volume;
    }

}
