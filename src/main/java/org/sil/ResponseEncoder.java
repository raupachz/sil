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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ResponseEncoder {
    
    private static final byte sp = 32; // SPACE
    private static final byte cr = 13; // \r
    private static final byte lf = 10; // \n
    private static final byte cl = 58; // :
    
    public ByteBuffer encode(Response response) {
        // allocate heap buffer
        ByteBuffer bb = ByteBuffer.allocate(4096);
        // write response to buffer
        statusLine(response, bb);
        responseHeaders(response, bb);
        empyLine(bb);
        // prepare buffer for writing
        bb.flip();
        return bb;
    }
    
    void statusLine(Response response, ByteBuffer bb) {
        bb.put(HttpVersion.Http11.toString().getBytes(StandardCharsets.UTF_8));
        bb.put(sp);
        bb.put(response.getStatus().toString().getBytes(StandardCharsets.UTF_8));
        bb.put(cr);
        bb.put(lf);
    }
    
    void responseHeaders(Response response, ByteBuffer bb) {
        for (ResponseHeader rh : response.getResponseHeaders()) {
            bb.put(rh.getName().toString().getBytes(StandardCharsets.UTF_8));
            bb.put(cl);
            bb.put(sp);
            bb.put(rh.getValue().getBytes(StandardCharsets.UTF_8));
            bb.put(cr);
            bb.put(lf);
        }
    }
    
    void empyLine(ByteBuffer bb) {
        bb.put(cr);
        bb.put(lf);
    }
    
}
