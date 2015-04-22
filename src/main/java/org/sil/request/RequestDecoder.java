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
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Optional;
import org.sil.HttpVersion;
import org.sil.request.Request.Method;

public class RequestDecoder {

    private static final byte d0 = 48;
    private static final byte d1 = 49;

    private static final byte E = 69;
    private static final byte G = 71;
    private static final byte H = 72;
    private static final byte P = 80;
    private static final byte T = 84;

    private static final byte cr = 13; // \r
    private static final byte lf = 10; // \n
    private static final byte sp = 32; // SPACE
    private static final byte dt = 46; // .
    private static final byte sl = 47; // /
    private static final byte cl = 58; // :

    public Optional<Request> decode(ByteBuffer bb) {
        Objects.requireNonNull(bb);

        // Things we need to decode
        Request request;
        Request.Method method;
        String rawURI;
        HttpVersion httpVersion;

        // Local variables and helpers
        final ArrayDeque<String> st = new ArrayDeque<>();
        String name, value;
        final int limit = bb.limit();
        int i, j, p;

        // A minimal request has at least 16 bytes (GET / HTTP/1.1)
        if (limit < 16) {
            return Optional.empty();
        }

        // Works because we use non-direct buffer. Not happy about this one.
        byte[] ba = bb.array();

        // --------------------------------------------------------------------
        // Parse Method
        // --------------------------------------------------------------------
        if (ba[0] == G
                && ba[1] == E
                && ba[2] == T
                && ba[3] == sp) {
            method = Method.GET;
        } else {
            return Optional.empty();
        }

        // --------------------------------------------------------------------
        // Parse URI
        // --------------------------------------------------------------------
        p = method.name().length() + 1;
        i = indexOf(sp, ba, p, limit);
        if (i == -1) {
            return Optional.empty();
        } else {
            rawURI = new String(ba, p, i - p, StandardCharsets.UTF_8);
        }
        p = i + 1;
        
        // --------------------------------------------------------------------
        // Parse HTTP-Version
        // --------------------------------------------------------------------
        if (ba[p++] == H
                && ba[p++] == T
                && ba[p++] == T
                && ba[p++] == P
                && ba[p++] == sl
                && ba[p++] == d1
                && ba[p++] == dt) {

            if (ba[p] == d1 && ba[++p] == cr && ba[++p] == lf) {
                httpVersion = HttpVersion.HTTP11;
            } else if (ba[p] == d0 && ba[++p] == cr && ba[++p] == lf) {
                httpVersion = HttpVersion.HTTP10;
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }

        // --------------------------------------------------------------------
        // Parse Headers
        // --------------------------------------------------------------------
        p++;
        while (ba[p] != cr && ba[p + 1] != lf) {
            i = indexOf(cr, ba, p, limit);
            j = indexOf(cl, ba, p, i);
            
            name = new String(ba, p, j - p, StandardCharsets.UTF_8);
            value = new String(ba, j + 2, i - j, StandardCharsets.UTF_8);
            
            st.add(name);
            st.add(value);
            
            p = i + 2;
        }
        
        i = 0;
        String[][] headers = new String[st.size() >> 1][];
        while (!st.isEmpty()) {
           headers[i++] = new String[] { st.pop(), st.pop() }; 
        }
        
        request = new Request(method, rawURI, httpVersion, headers);
        return Optional.of(request);
    }

    int indexOf(byte b, byte[] src, int offset, int limit) {
        while (src[offset] != b && ++offset < limit);
        return offset == limit ? -1 : offset;
    }

}
