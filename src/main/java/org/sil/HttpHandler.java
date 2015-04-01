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

import org.sil.response.ResponseEncoder;
import org.sil.response.Response;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpHandler implements Runnable {
    
    private final SocketChannel sc;
    private final Instant connectedAt;
    private final RequestDecoder decoder;
    private final ResponseEncoder encoder;
    private final Processor processor;
    
    public HttpHandler(SocketChannel sc, Instant connectedAt) {
        this.sc = sc;
        this.connectedAt = connectedAt;
        this.decoder = new RequestDecoder();
        this.encoder = new ResponseEncoder();
        this.processor = new Processor();
    }
    
    public HttpThread getThread() {
        return (HttpThread) Thread.currentThread();
    }

    @Override
    public void run() {
        ByteBuffer requestBuffer = getThread().getBuffer();
        requestBuffer.clear();
        try {
            sc.read(requestBuffer);
            Request request = decoder.decode(requestBuffer);
            Response response = processor.process(request);
            ByteBuffer responseBuffer = encoder.encode(response);
            sc.write(responseBuffer);
            sc.close();
        } catch (IOException ex) {
            Logger.getLogger(HttpHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
}
