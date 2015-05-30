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

import org.sil.request.Request;
import org.sil.request.RequestDecoder;
import org.sil.response.ResponseEncoder;
import org.sil.response.Response;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sil.config.Configuration;

public class HttpHandler implements Runnable {
    
    private static final Logger logger = Logger.getLogger(HttpHandler.class.getName());
    
    private final Configuration config;
    private final SocketChannel sc;
    private final RequestDecoder decoder;
    private final ResponseEncoder encoder;
    private final Processor processor;
    private final Instant connectedAt;
    private Instant lastReadWriteAt;
    
    public HttpHandler(Configuration config, SocketChannel sc) {
        this.connectedAt = Instant.now();
        this.lastReadWriteAt = connectedAt;
        this.config = config;
        this.sc = sc;
        this.decoder = new RequestDecoder();
        this.encoder = new ResponseEncoder();
        this.processor = new Processor();
    }
    
    HttpThread getThread() {
        return (HttpThread) Thread.currentThread();
    }
    
    public Instant getConnectedAt() {
        return connectedAt;
    }
    
    public Instant getLastReadWriteAt() {
        return lastReadWriteAt;
    }

    @Override
    public void run() {
        ByteBuffer bb = getThread().getBuffer();
        bb.clear();
//        try {
//            // read everything into the buffer
//            while (bb.hasRemaining() && sc.read(bb) != -1) ;
//            bb.flip();
//            
//            if (config.isDebug()) {
//                new Hex().dump(bb, System.out);
//                bb.rewind();
//            }
//            
//            Decoded dec = decoder.decode(bb);
//            switch (dec.getResult()) {
//                case Flawed : sc.close(); return;
//                case Partial: sc.close(); return;
//            }
//            
//            if (dec.getResult() == Decoded.Result.Successful) {
//                Request request = dec.getRequest();
//                Response response = processor.process(request);
//            }
//            
//            // write the whole buffer
//            bb.flip();
//            while (bb.hasRemaining() && sc.write(bb) != -1) ;
//            
//            //Request request = decoder.decode(requestBuffer);
//            //Response response = processor.process(request);
//            //ByteBuffer responseBuffer = encoder.encode(response);
//            //sc.write(responseBuffer);
//            sc.close();
//        } catch (IOException ex) {
//            Logger.getLogger(HttpHandler.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        Request request = readRequest();
        Response response = processor.process(request);
        writeResponse(response);
        // TODO Remove Late
        close();
    }
    
    Request readRequest() {
        return null;
    }
    
    void writeResponse(Response response) {
        try {
            ByteBuffer bb = getThread().getBuffer();
            bb.clear();
            encoder.encode(response, bb);
            bb.flip();
            while (bb.hasRemaining()) {
                sc.write(bb);
            }
            sc.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, e, () -> "Could not write response to channel.");
        }
    }
    
    /**
     * Closes this handler and all its resources
     */
    public synchronized void close() {
        try {
            sc.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, e, () -> "Could not close handler.");
        }
    }
    
}
