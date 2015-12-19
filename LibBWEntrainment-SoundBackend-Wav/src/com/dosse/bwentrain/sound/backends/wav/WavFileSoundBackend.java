/*
 * Copyright (C) 2014 dosse.
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
package com.dosse.bwentrain.sound.backends.wav;

import com.dosse.bwentrain.sound.ISoundDevice;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Outputs as .wav audio file. Uncompressed.
 * @author dosse
 */
public class WavFileSoundBackend implements ISoundDevice {

    private final BufferedSeekableFileOutputStream raf;
    private final int sampleRate, nChannels;
    private boolean closed = false, opened = false;
    private float vol = 1;

    public WavFileSoundBackend(String path, int sampleRate, int nChannels) throws FileNotFoundException, Exception {
        raf = new BufferedSeekableFileOutputStream(new File(path), "rws");
        if (sampleRate < 8000) {
            throw new Exception("Sample rate must be >=8000");
        }
        this.sampleRate = sampleRate;
        if (nChannels < 1) {
            throw new Exception("nChannels must be >=1");
        }
        this.nChannels = nChannels;
    }

    private void writeS(String s) {
        for (int i = 0; i < s.length(); i++) {
            try {
                raf.write(s.charAt(i));
            } catch (IOException ex) {
            }
        }
    }

    private void writeInt(int val) {
        try {
            raf.write(val);
            raf.write(val >> 8);
            raf.write(val >> 16);
            raf.write(val >> 24);
        } catch (IOException ex) {
        }

    }

    private void writeShort(short val) {
        try {
            raf.write(val);
            raf.write(val >> 8);
        } catch (IOException ex) {
        }
    }

    private void writeHeader() {
        writeS("RIFF");
        writeInt(0); //file length in bytes, unknown before the device is closed, fixed by fixLenght method
        writeS("WAVE");
        writeS("fmt ");
        writeInt(16); //subchunk1 size
        writeShort((short) 1); //uncompressed samples
        writeShort((short) nChannels);
        writeInt(sampleRate);
        writeInt(2 * sampleRate * nChannels); //byterate=(bitsPerSample/8)*sampleRate*nChannels. seems to be useless
        writeShort((short) 4); //blockalign
        writeShort((short) 16); //bits per sample
        writeS("data");
        writeInt(0); //data length in bytes, unknown before the device is closed, fixed by fixLenght method
    }

    private void fixHeader() {
        try {
            long fileLength = raf.getFileSize();
            //fix file length in bytes
            raf.seek(4);
            writeInt((int) fileLength);
            //fix data lenght in bytes
            raf.seek(40);
            writeInt((int) (fileLength - 36)); //36 is the size of this stupid header
        } catch (IOException ex) {
            System.err.println("ouch " + ex);
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
        closed = true;
        if (opened) {
            fixHeader();
        }
        try {
            raf.close();
        } catch (IOException ex) {
        }
    }

    @Override
    public void open() {
        if (opened || closed) {
            return;
        }
        writeHeader();
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
            temp = (short) (data[i] * vol * Short.MAX_VALUE);
            tempBuffer[2 * i] = (byte) temp;
            tempBuffer[2 * i + 1] = (byte) (temp >> 8);
        }
        try {
            raf.write(tempBuffer);
        } catch (IOException ex) {
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
