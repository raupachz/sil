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
package org.sil.request;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;
import org.sil.request.Request.Method;

public class RequestDecoder {
    
    private static final byte G = 71;
    private static final byte E = 69;
    private static final byte T = 84;
    
    private static final byte sp = 32; // SPACE
    private static final byte cr = 13; // \r
    private static final byte lf = 10; // \n
    private static final byte cl = 58; // :
    private static final byte sl = 47; // /
    
    public Request decode(ByteBuffer bb) {
        Objects.requireNonNull(bb);
        // Return value
        Request req = null;
        // we want to read from the buffer
        int i = 0;
        // the amount of bytes in the buffer
        final int limit = bb.limit();
        // A minimal request: GET / HTTP/1.1 (16 bytes)
        if (limit - i <= 16) {
            return null;
        }
        // Idk about this one, I try to avoid bb.get()
        byte[] ba = bb.array();

        if (ba[i++] == G && 
            ba[i++] == E && 
            ba[i++] == T &&
            ba[i++] == sp) {
            // store current offset;
            int offset = i;
            // Read until next space byte or eof
            while (ba[i] != sp && ++i < limit);
            // Did we reach the limit?
            if (i == limit) {
                return null;
            }
            // Read path
            String path = new String(ba, offset, i - offset, StandardCharsets.UTF_8);
            // Return request object with parsed values
            req = new Request(Method.GET, path);
        } 
        return req;
    }
    
    int indexOf(byte b, byte[] src, int offset, int limit) {
        while (src[offset] != b && ++offset < limit) ;
        return offset == limit ? -1 : offset;
    }
    
}
