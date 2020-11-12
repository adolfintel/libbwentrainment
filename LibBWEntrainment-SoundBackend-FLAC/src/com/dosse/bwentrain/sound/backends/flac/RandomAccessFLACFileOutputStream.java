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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import javaFlacEncoder.FLACOutputStream;

/**
 *
 * @author dosse
 */
public class RandomAccessFLACFileOutputStream implements FLACOutputStream {

    private RandomAccessFile raf;

    public RandomAccessFLACFileOutputStream(String path) throws FileNotFoundException {
        raf=new RandomAccessFile(path,"rws");
    }

    @Override
    public long seek(long l) throws IOException {
        raf.seek(l);
        return raf.getFilePointer();
    }

    @Override
    public int write(byte[] bytes, int i, int i1) throws IOException {
        raf.write(bytes, i, i1);
        return i1;
    }

    @Override
    public long size() {
        try {
            return raf.length();
        } catch (IOException ex) {
            return 0;
        }
    }

    @Override
    public void write(byte b) throws IOException {
        raf.write(b);
    }

    @Override
    public boolean canSeek() {
        return true;
    }

    @Override
    public long getPos() {
        try {
            return raf.getFilePointer();
        } catch (IOException ex) {
            return 0;
        }
    }

    public void close() {
        try {
            raf.close();
        } catch (IOException ex) {
        }
    }
}
