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

import java.net.InetAddress;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;
import org.sil.HttpVersion;
import org.sil.util.Commons;

/**
 * The {@code Request} class represents a HTTP request message.
 * A Requests is immutable and thread-safe.
 */
public final class Request {
    
    public enum Method {
        GET,
        HEAD
    }
    
    private static final String[][] empty = new String[0][];
    
    private final InetAddress remoteAddress;
    private final Method method;
    private final String path;
    private final HttpVersion version;
    private final String[][] headers;
    private final ZonedDateTime timestamp; 
    
    public Request(Method method, String path, HttpVersion version) {
        this(null, method, path, version, empty);
    }
    
    public Request(Method method, String path, HttpVersion version, String[][] headers) {
        this(null, method, path, version, headers);
    }
    
    public Request(InetAddress remoteAddress, Method method, String path, HttpVersion version) {
        this(remoteAddress, method, path, version, null);
    }
    
     public Request(InetAddress remoteAddress, Method method, String path, HttpVersion version, String[][] headers) {
        this.remoteAddress = remoteAddress;
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.timestamp = ZonedDateTime.now();
    }
    
    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }
    
    public Method getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
    
    public HttpVersion getVersion() {
        return version;
    }
    
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
    
    public Iterable<String> getHeaderNames() {
        return () -> new Commons.IteratorImpl(headers);
    }
    
    public Optional<String> getHeaderValue(String header) {
        final String[] key = new String[] { header, null};
        int i = Arrays.binarySearch(headers, key, Commons.cmp);
        return i < 0 ? Optional.empty() : Optional.of(headers[i][1]);
    }
    
}
