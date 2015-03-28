/*
 * Copyright (c) 2015, Björn Raupach
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.sil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

public class Hex {

    private static final int BYTES_PER_LINE = 16;
    
    private static final char[] CHARS = {'0', '1', '2', '3', '4', '5', '6',
        '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    
    private static final byte[] NEWLINE = System.lineSeparator().getBytes();

    public void dump(ByteBuffer buffer, OutputStream out) throws IOException {
        Objects.requireNonNull(buffer, "buffer");
        Objects.requireNonNull(out, "out");

        int pos = buffer.position();
        int limit = buffer.limit();
        
        for (int l = 0; l < limit / BYTES_PER_LINE; l++, pos += BYTES_PER_LINE) {
            line(pos, out);
            out.write(' ');
            out.write(' ');
            hex(buffer, pos, BYTES_PER_LINE, out);
            out.write(' ');
            out.write('|');
            ascii(buffer, pos, BYTES_PER_LINE, out);
            out.write('|');
            out.write(NEWLINE);
        }
        
        int left = limit % 16;
        if (left > 0) {
            int padding = BYTES_PER_LINE - left;
            line(pos, out);
            out.write(' ');
            out.write(' ');
            hex(buffer, pos, left, out);
            for (int i = 0; i < padding; i++) {
                out.write(' ');
                out.write(' ');
                out.write(' ');
            }
            out.write(' ');
            out.write('|');
            ascii(buffer, pos, left, out);
            out.write('|');
            out.write(NEWLINE);
        }
        
    }

    void line(int n, OutputStream out) throws IOException {
        out.write(CHARS[n >> 28 & 0xf]);
        out.write(CHARS[n >> 24 & 0xf]);
        out.write(CHARS[n >> 20 & 0xf]);
        out.write(CHARS[n >> 16 & 0xf]);
        out.write(CHARS[n >> 12 & 0xf]);
        out.write(CHARS[n >> 8 & 0xf]);
        out.write(CHARS[n >> 4 & 0xf]);
        out.write(CHARS[n & 0x0f]);

    }

    void hex(ByteBuffer buffer, int offset, int length, OutputStream out) throws IOException {
        for (int i = 0; i < length; i++) {
            byte b = buffer.get(offset + i);
            out.write(CHARS[b >> 4 & 0xf]);
            out.write(CHARS[b & 0x0f]);
            out.write(' ');
        }
    }

    void ascii(ByteBuffer buffer, int offset, int length, OutputStream out) throws IOException {
        for (int i = 0; i < length; i++) {
            byte b = buffer.get(offset + i);
            if (b > 31 && b < 127) {
                out.write((char) b);
            } else {
                out.write('.');
            }
        }
    }

}
