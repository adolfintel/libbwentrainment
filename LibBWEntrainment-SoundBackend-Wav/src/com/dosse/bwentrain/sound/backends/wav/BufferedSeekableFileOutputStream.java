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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * Had to make this because on some systems RandomAccessFile syncs after every
 * write, making this backend atrociously slow
 *
 * @author Federico
 */
public class BufferedSeekableFileOutputStream extends OutputStream {

    private RandomAccessFile raf;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private static final int MAX_BUFFER_SIZE = 1048576; //1M max buffer size

    public BufferedSeekableFileOutputStream(File file, String mode) throws FileNotFoundException {
        raf = new RandomAccessFile(file, mode);
    }

    private void syncBuffer() throws IOException {
        if (baos.size() > 0) {
            raf.write(baos.toByteArray());
            baos.reset();
        }
    }

    @Override
    public void write(byte[] data) throws IOException {
        if (data.length > MAX_BUFFER_SIZE - baos.size()) {
            syncBuffer();
            raf.write(data);
        } else {
            baos.write(data);
        }
    }

    @Override
    public void write(int b) throws IOException {
        write(new byte[]{(byte) b});
    }

    @Override
    public void close() throws IOException {
        syncBuffer();
        raf.close();
    }

    @Override
    public void flush() throws IOException {
        syncBuffer();
    }

    public void seek(long position) throws IOException {
        syncBuffer();
        raf.seek(position);
    }

    public long getFileSize() throws IOException {
        syncBuffer();
        return raf.getFilePointer();
    }

}
