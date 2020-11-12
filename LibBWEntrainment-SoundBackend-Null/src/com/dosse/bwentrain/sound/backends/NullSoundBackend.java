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
package com.dosse.bwentrain.sound.backends;

import com.dosse.bwentrain.sound.ISoundDevice;

/**
 * A fake ISoundDevice. Doesn't output anything, but behaves like a proper
 * ISoundDevice
 *
 * @author dosse
 */
public class NullSoundBackend implements ISoundDevice {

    private boolean closed = false;
    private float vol = 0;

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        closed = true;
    }

    @Override
    public void open() {
    }

    @Override
    public int getChannelCount() {
        return 0;
    }

    @Override
    public int getBitsPerSample() {
        return 0;
    }

    @Override
    public float getSampleRate() {
        return 0;
    }

    @Override
    public void write(float[] data) {
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
