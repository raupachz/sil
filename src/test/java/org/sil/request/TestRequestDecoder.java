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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.sil.HttpVersion;
import org.sil.request.Request.Method;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;

public class TestRequestDecoder {
    
    final Charset utf8 = StandardCharsets.UTF_8;
    RequestDecoder decoder;
    
    @BeforeMethod
    public void beforeMethod() {
        decoder = new RequestDecoder();
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void test_decode_null() {
        decoder.decode(null);
    }
    
    @Test
    public void test_decode_empty() {
        decoder.decode(ByteBuffer.allocate(0));
    }
    
    @Test
    public void test_decode_partial_garbage() {
        String request = "GET 938544562809809830945";
        ByteBuffer bb = utf8.encode(request);
        Optional<Request> req = decoder.decode(bb);
        assertFalse(req.isPresent());
    }
    
    @Test
    public void test_decode_total_garbage() {
        String request = "234938544562809809830945";
        ByteBuffer bb = utf8.encode(request);
        Optional<Request> req = decoder.decode(bb);
        assertFalse(req.isPresent());
    }
    
    @Test
    public void test_decode_http10() {
        String request = "GET / HTTP/1.0\r\n";
        ByteBuffer bb = utf8.encode(request);
        Optional<Request> opt = decoder.decode(bb);
        assertTrue(opt.isPresent());
        
        Request req = opt.get();
        assertEquals(req.getMethod(), Method.GET);
        assertEquals(req.getRawURI(), "/");
        assertEquals(req.getHttpVersion(), HttpVersion.HTTP10);
    }
    
    @Test
    public void test_decode_missing_path() {
        String request = "GET HTTP/1.1\r\nHost: www.example.com\r\n\r\n";
        ByteBuffer bb = utf8.encode(request);
        Optional<Request> req = decoder.decode(bb);
        assertFalse(req.isPresent());
    }
    
    @Test
    public void test_decode_slash() {
        String request = "GET / HTTP/1.1\r\nHost: www.example.com\r\n\r\n";
        ByteBuffer bb = utf8.encode(request);
        Optional<Request> opt = decoder.decode(bb);
        assertTrue(opt.isPresent());
        
        Request req = opt.get();
        assertEquals(req.getMethod(), Method.GET);
        assertEquals(req.getRawURI(), "/");
        assertEquals(req.getHttpVersion(), HttpVersion.HTTP11);
    }
    
    @Test
    public void test_decode_indexhtml() {
        String request = "GET /index.html HTTP/1.1\r\nHost: www.example.com\r\n\r\n";
        ByteBuffer bb = utf8.encode(request);
        Optional<Request> opt = decoder.decode(bb);
        assertTrue(opt.isPresent());
        
        Request req = opt.get();
        assertEquals(req.getMethod(), Method.GET);
        assertEquals(req.getRawURI(), "/index.html");
        assertEquals(req.getHttpVersion(), HttpVersion.HTTP11);
    }
    
    @Test
    public void test_decode_directories() {
        String request = "GET /a/b/c HTTP/1.1\r\nHost: www.example.com\r\n\r\n";
        ByteBuffer bb = utf8.encode(request);
        Optional<Request> opt = decoder.decode(bb);
        assertTrue(opt.isPresent());
        
        Request req = opt.get();
        assertEquals(req.getMethod(), Method.GET);
        assertEquals(req.getRawURI(), "/a/b/c");
        assertEquals(req.getHttpVersion(), HttpVersion.HTTP11);
    }
    
     
}
