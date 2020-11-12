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
package com.dosse.bwentrain.sound.backends.pc;

import com.dosse.bwentrain.sound.ISoundDevice;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author dosse
 */
public class PCSoundBackend implements ISoundDevice {

    private SourceDataLine speaker; //outputs to java sound
    private float vol = 1;
    private float sampleRate;
    private int nChannels;
    private static final int SOUND_CARD_BUFFER = 8192; //sound card buffer size. lower values decrease latency but may cause stuttering on crap computers
    private boolean closed = false, opened = false;

    /**
     * opens an audio channel (16bit)
     *
     * @param sampleRate sample rate
     * @param nChannels number of channels (1=mono, 2=stereo, ...)
     * @throws Exception if something goes wrong
     */
    public PCSoundBackend(float sampleRate, int nChannels) throws Exception {
        try {
            this.sampleRate = sampleRate;
            this.nChannels = nChannels;
            AudioFormat af = new AudioFormat(sampleRate, 16, nChannels, true, false); //16 bit, little endian, signed
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
            speaker = (SourceDataLine) AudioSystem.getLine(info);
            speaker.open(af, SOUND_CARD_BUFFER);
        } catch (Throwable e) {
            throw new Exception("Sound Device error");
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        if (opened) {
            speaker.flush();
        }
        speaker.close();
        closed = true;
    }

    @Override
    public void open() {
        if (closed || opened) {
            return;
        }
        speaker.start();
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
            temp = (short) (data[i] * vol * Short.MAX_VALUE);
            tempBuffer[2 * i] = (byte) temp;
            tempBuffer[2 * i + 1] = (byte) (temp >> 8);
        }
        speaker.write(tempBuffer, 0, tempBuffer.length);
    }

    @Override
    public float getSampleRate() {
        return sampleRate;
    }

    @Override
    public void setVolume(float vol) {
        this.vol = vol < 0 ? 0 : vol > 1 ? 1 : vol;
    }

    @Override
    public float getVolume() {
        return vol;
    }
}
