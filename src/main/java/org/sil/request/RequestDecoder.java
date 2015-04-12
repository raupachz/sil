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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sil.request.Request.Method;
import org.sil.util.Hex;

public class RequestDecoder {

    private static final byte G = 71;
    private static final byte E = 69;
    private static final byte T = 84;

    private static final byte sp = 32; // SPACE
    private static final byte cr = 13; // \r
    private static final byte lf = 10; // \n
    private static final byte cl = 58; // :
    private static final byte sl = 47; // /

    public Optional<Request> decode(ByteBuffer bb) {
        Objects.requireNonNull(bb);
        
        // Things we need to decode
        Request.Method method;
        String rawURI;
        String httpVersion;
        
        // Local variables
        final int limit = bb.limit();
        int i = 0, mark = 0;
        
        // Removed in a future version
        try {
            new Hex().dump(bb, System.out);
        } catch (IOException ex) {
            Logger.getLogger(RequestDecoder.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // A minimal request has at least 16 bytes (GET / HTTP/1.1)
        if (limit <= 16) {
            return Optional.empty();
        }
        
        // Idk about this one. Works because we use non-direct buffer
        byte[] ba = bb.array();
        
        // --------------------------------------------------------------------
        // Parse Http method
        // --------------------------------------------------------------------
        if (ba[0] == G && ba[1] == E && ba[2] == T && ba[3] == sp) {
            method = Method.GET;
        } else {
            return Optional.empty();
        }
        
        // --------------------------------------------------------------------
        // Parse URI
        // --------------------------------------------------------------------
        mark = method.name().length() + 1;
        i = indexOf(sp, ba, mark, limit);
        if (i == -1) {
            return Optional.empty();
        } else {
            rawURI = new String(ba, mark, i - mark, StandardCharsets.UTF_8);
        }
        // --------------------------------------------------------------------
        // Parse HTTP-Version
        // --------------------------------------------------------------------
        mark = i + 1;
        i = indexOf(cr, ba, mark, limit);
        if (i == -1) {
            return Optional.empty();
        } else {
            httpVersion = new String(ba, mark, i - mark, StandardCharsets.UTF_8);
        }
        Request request = new Request(method, rawURI, httpVersion);
        return Optional.of(request);
    }

    int indexOf(byte b, byte[] src, int offset, int limit) {
        while (src[offset] != b && ++offset < limit) ;
        return offset == limit ? -1 : offset;
    }

}
