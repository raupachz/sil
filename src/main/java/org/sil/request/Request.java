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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.sil.HttpVersion;

public final class Request {
    
    private static final Comparator<String[]> cmp = 
            (final String[] sa1, final String[] sa2) -> sa1[0].compareTo(sa2[0]);
    
    private static final String[][] empty = new String[0][];
    
    private final Method method;
    private final String rawURI;
    private final HttpVersion httpVersion;
    private final String[][] headers;
    
    Request(Method method, String rawURI, HttpVersion httpVersion) {
        this.method = method;
        this.rawURI = rawURI;
        this.httpVersion = httpVersion;
        this.headers = empty;
    }
    
    Request(Method method, String rawURI, HttpVersion httpVersion, String[][] headers) {
        this.method = method;
        this.rawURI = rawURI;
        this.httpVersion = httpVersion;
        this.headers = headers;
    }
    
    public Method getMethod() {
        return method;
    }

    public String getRawURI() {
        return rawURI;
    }
    
    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
    
    public Iterator<String> getHeaderNames() {
        return new Iterator<String>() {

            int i = 0;

            @Override
            public boolean hasNext() {
                return i < headers.length;
            }

            @Override
            public String next() {
                if (hasNext()) {
                    return headers[i++][0];
                } else {
                    throw new NoSuchElementException("next");
                }
            }
            
        };
    }
    
    public Optional<String> getHeaderValue(String header) {
        final String[] key = new String[] { header, null};
        int i = Arrays.binarySearch(headers, key, cmp);
        return i < 0 ? Optional.empty() : Optional.of(headers[i][1]);
    }
    
    public static enum Method {
        GET,
        HEAD
    }
    
}
