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
package org.sil.response;

import java.nio.ByteBuffer;
import static java.nio.charset.StandardCharsets.*;
import static org.sil.util.ASCII.*;

public class ResponseEncoder {

    public void encode(Response response, ByteBuffer bb) {
        // Status line
        bb.put(response.getVersion().getBytes(UTF_8))
                .put(SPACE)
                .put(response.getCode().getBytes(UTF_8))
                .put(SPACE)
                .put(response.getPhrase().getBytes(UTF_8))
                .put(CR)
                .put(LF);
        
        // Response headers
        for (String name : response.getHeaderNames()) {
            String value = response.getHeaderValue(name);
            bb.put(name.getBytes(UTF_8));
            bb.put(COLON);
            bb.put(SPACE);
            bb.put(value.getBytes(UTF_8));
            bb.put(CR);
            bb.put(LF);
        }
        bb.put(CR);
        bb.put(LF);
    }

}
