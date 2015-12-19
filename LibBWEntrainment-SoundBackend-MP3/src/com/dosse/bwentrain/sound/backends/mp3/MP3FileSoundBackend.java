/*
 * Copyright (C) 2015 Federico Dossena.
 *
 * LGPLv3 License
 */
package com.dosse.bwentrain.sound.backends.mp3;

import com.dosse.bwentrain.sound.ISoundDevice;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import net.sourceforge.lame.lowlevel.LameEncoder;
import net.sourceforge.lame.mp3.Lame;
import net.sourceforge.lame.mp3.MPEGMode;

/**
 *
 * @author Federico
 */
public class MP3FileSoundBackend implements ISoundDevice {

    private LameEncoder lame;
    private FileOutputStream fos;
    private boolean closed = false, opened = false;
    private int nChannels, sampleRate;
    private float vol = 1;

    public MP3FileSoundBackend(String path, int sampleRate, int nChannels, int kbps) throws FileNotFoundException {
        fos = new FileOutputStream(path);
        this.sampleRate = sampleRate;
        this.nChannels = nChannels;
        lame = new LameEncoder(new AudioFormat(sampleRate, 16, nChannels, true, false), kbps, nChannels == 1 ? MPEGMode.MONO : MPEGMode.JOINT_STEREO, Lame.QUALITY_HIGHEST, false);
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        if (closed || !opened) {
            return;
        }
        closed = true;
        lame.close();
        try {
            fos.close();
        } catch (IOException ex) {
        }
    }

    @Override
    public void open() {
        if (closed || opened) {
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
    private byte[] mp3Buffer = null;

    @Override
    public void write(float[] data) {
        if (closed || !opened) {
            return;
        }
        if (tempBuffer == null || tempBuffer.length != data.length * 2) { //we must reallocate the tempBuffer
            tempBuffer = new byte[data.length * 2];
        }
        if (mp3Buffer == null) {
            mp3Buffer = new byte[lame.getPCMBufferSize()];
        }
        //convert floats to 16 bit samples
        short temp;
        for (int i = 0; i < data.length; i++) {
            //data[i] = data[i] < -1 ? -1 : data[i] > 1 ? 1 : data[i]; //code for clipping instead of overflow, commented out to improve performance
            temp = (short) (data[i] * vol * Short.MAX_VALUE);
            tempBuffer[2 * i] = (byte) temp;
            tempBuffer[2 * i + 1] = (byte) (temp >> 8);
        }
        int written = lame.encodeBuffer(tempBuffer, 0, tempBuffer.length, mp3Buffer);
        if (written != 0) {
            try {
                fos.write(mp3Buffer, 0, written);
            } catch (IOException ex) {
            }
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
