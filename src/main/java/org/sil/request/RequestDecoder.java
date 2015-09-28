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
import org.sil.request.Request.Method;
import static org.sil.util.ASCII.*;

public class RequestDecoder {
    
    private static final int TRUE  = 0;
    private static final int FALSE = 1;
    private static final int MAYBE = 2;
    
    enum State {
        Successful,
        Failed,
        Incomplete
    }
    
    private State state;
    private Request request;
    
//    public State decode() {
//        Objects.requireNonNull(bb);
//        
//        // Things we need to decode
//        Request request;
//        Request.Method method;
//        String path;
//        String httpVersion;
//        
//
//        // Local variables and helpers
//        final ArrayDeque<String> st = new ArrayDeque<>();
//        String name, value;
//        final int limit = bb.limit();
//        int o, i, j, p;
//
//        // --------------------------------------------------------------------
//        // Parse Method
//        // --------------------------------------------------------------------
//        Decoded<Method> opt = method(bb);
//        if (opt.isPresent()) {
//            method = opt.get();
//        } else if (opt.isIncomplete()) {
//            return Optional.empty();
//        } else {
//            ;
//        }
//        
//        // ----------------------------------------------------------------
//        // Parse path
//        // ----------------------------------------------------------------
//        
//        
//        
//        if (o == Successful) {
//            
//        } else if (o -)
//        
//        
//        // --------------------------------------------------------------------
//        // Parse URI
//        // --------------------------------------------------------------------
//        p = method.name().length() + 1;
//        i = indexOf(SPACE, ba, p, limit);
//        if (i == -1) {
//            return Decoded.Flawed;
//        } else {
//            path = new String(ba, p, i - p, StandardCharsets.UTF_8);
//        }
//        p = i + 1;
//        
//        // --------------------------------------------------------------------
//        // Parse HTTP-Version
//        // --------------------------------------------------------------------
//        if (ba[p++] == H
//                && ba[p++] == T
//                && ba[p++] == T
//                && ba[p++] == P
//                && ba[p++] == SLASH
//                && ba[p++] == $1
//                && ba[p++] == DOT) {
//
//            if (ba[p] == $1 && ba[++p] == CR && ba[++p] == LF) {
//                httpVersion = HttpVersion.HTTP11;
//            } else if (ba[p] == $0 && ba[++p] == CR && ba[++p] == LF) {
//                httpVersion = HttpVersion.HTTP10;
//            } else {
//                return Decoded.Flawed;
//            }
//        } else {
//            return Decoded.Flawed;
//        }
//
//        // --------------------------------------------------------------------
//        // Parse Headers
//        // --------------------------------------------------------------------
//        p++;
//        while (ba[p] != CR && ba[p + 1] != LF) {
//            i = indexOf(CR, ba, p, limit);
//            j = indexOf(COLON, ba, p, i);
//            
//            name = new String(ba, p, j - p, StandardCharsets.UTF_8);
//            value = new String(ba, j + 2, i - j, StandardCharsets.UTF_8);
//            
//            st.add(name);
//            st.add(value);
//            
//            p = i + 2;
//        }
//        
//        i = 0;
//        String[][] headers = new String[st.size() >> 1][];
//        while (!st.isEmpty()) {
//           headers[i++] = new String[] { st.pop(), st.pop() }; 
//        }
//        
//        request = new Request(method, rawURI, httpVersion, headers);
//        return new Decoded(request);
//    }
    
    
    
    // -- Helpers 

    int indexOf(byte b, byte[] src, int offset, int limit) {
        while (src[offset] != b && ++offset < limit);
        return offset == limit ? -1 : offset;
    }
    
    int matches(ByteBuffer bb, byte c) {
        if (bb.hasRemaining()) {
            byte b = bb.get();
            if (b == c) {
                return TRUE; // successful
            } else {
                return FALSE; // failed
            }
        } else {
            if (bb.limit() == bb.capacity()) {
                return FALSE; // there won't be more input
            } else {
                return MAYBE; // we need more input
            }
        }
    }
        
        
//        int result;
//        final int n = Math.max(bb.remaining(), args.length);
//        for (int i = 0; i < n; i++) {
//            boolean match = bb.get() == args[i];
//            if (!match) {
//                return Flawed;
//            }
//        }
//        result = (n == args.length) ? Successful : Partial;
//        return result;
//    }
    
   
}
