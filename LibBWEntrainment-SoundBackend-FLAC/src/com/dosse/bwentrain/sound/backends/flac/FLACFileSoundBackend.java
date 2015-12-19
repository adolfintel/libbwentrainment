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
package com.dosse.bwentrain.sound.backends.flac;

import com.dosse.bwentrain.sound.ISoundDevice;

import java.io.IOException;
import javaFlacEncoder.EncodingConfiguration;
import javaFlacEncoder.FLACEncoder;
import javaFlacEncoder.StreamConfiguration;

/**
 * Encodes to FLAC.<br>
 * The encoder is quite messy, so don't mess around with this class. Seriously.
 *
 * @author dosse
 */
public class FLACFileSoundBackend implements ISoundDevice {

    private static final int BLOCK_SIZE = 1000000; //encode every 1M samples

    private RandomAccessFLACFileOutputStream fos;
    private boolean closed = false, opened = false;
    private FLACEncoder enc;
    private float vol = 1;
    private int nChannels;
    private float sampleRate;

    public FLACFileSoundBackend(String path, float sampleRate, int nChannels) throws IOException {
        fos = new RandomAccessFLACFileOutputStream(path);
        enc = new FLACEncoder();
        EncodingConfiguration ec = new EncodingConfiguration();
        ec.setChannelConfig(EncodingConfiguration.ChannelConfig.EXHAUSTIVE);
        enc.setEncodingConfiguration(ec);
        enc.setStreamConfiguration(new StreamConfiguration(nChannels, StreamConfiguration.DEFAULT_MIN_BLOCK_SIZE, StreamConfiguration.DEFAULT_MAX_BLOCK_SIZE, (int) sampleRate, 16));
        this.nChannels = nChannels;
        this.sampleRate = sampleRate;
        enc.setOutputStream(fos);
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
        try {
            if (opened) {
                int[] empty = new int[nChannels];
                enc.addSamples(empty, 1);
                enc.encodeSamples(1, true); //WHY ON EARTH DO I HAVE TO DO THIS TO MAKE IT CLOSE THE STREAM??
                enc.clear();
            }
            fos.close();
        } catch (Throwable ex) {
        }
        closed = true;
    }

    @Override
    public void open() {
        if (closed||opened) {
            return;
        }
        try {
            enc.openFLACStream();
        } catch (Throwable ex) {
        }
        opened=true;
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

    private int[] tempBuffer = null; //used to avoid reallocating memory every time write is called, as long as write is always called with an array of the same size

    private int samplesToEncode = 0; //number of samples that's been sent to the encoder, but hasn't been encoded yet, because a BLOCK_SIZE wasn't reached yet.

    @Override
    public void write(float[] data) {
        if (closed||!opened) {
            return;
        }
        if (tempBuffer == null || tempBuffer.length != data.length) { //we must reallocate the tempBuffer
            tempBuffer = new int[data.length];
        }
        //convert floats to 16 bit samples
        for (int i = 0; i < data.length; i++) {
            tempBuffer[i] = (int) (data[i] * vol * Short.MAX_VALUE);
        }
        int nSamples = data.length / nChannels;
        enc.addSamples(tempBuffer, nSamples); //send data to encoder. encoder wants number of samples divided by nChannels because the encoder likes it that way
        samplesToEncode += nSamples;
        if (samplesToEncode >= BLOCK_SIZE) {
            try {
                enc.encodeSamples(Integer.MAX_VALUE, false); //encode all available samples
            } catch (Throwable ex) {
            }
            samplesToEncode = 0;
        }
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
