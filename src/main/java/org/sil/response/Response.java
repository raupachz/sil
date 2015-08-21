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

import java.time.ZonedDateTime;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Optional;
import org.sil.HttpVersion;
import org.sil.entity.Entity;
import org.sil.util.Commons;

/**
 * The {@code Request} class represents a HTTP response message. A Response is
 * immutable and thread-safe.
 */
public class Response {

    private final HttpVersion version;
    private final String code;
    private final String phrase;
    private final String[][] headers;
    private final Optional<Entity> entity;
    private final ZonedDateTime timestamp;

    public Response(HttpVersion version, String code, String phrase, String[][] headers, Optional<Entity> entity) {
        this.version = version;
        this.code = code;
        this.phrase = phrase;
        this.headers = headers;
        this.entity = entity;
        this.timestamp = ZonedDateTime.now();
    }

    public HttpVersion getVersion() {
        return version;
    }

    public String getCode() {
        return code;
    }

    public String getPhrase() {
        return phrase;
    }

    public Iterable<String> getHeaderNames() {
        return () -> new Commons.IteratorImpl(headers);
    }

    public String getHeaderValue(String header) {
        final String[] key = new String[]{header, null};
        int i = Arrays.binarySearch(headers, key, Commons.cmp);
        return headers[i][1];
    }

    public Optional<Entity> getEntity() {
        return entity;
    }
    
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
            

    public static class Builder {

        private HttpVersion _version;
        private Integer _code;
        private String _phrase;
        private final ArrayDeque<String[]> _stack;
        private Entity _entity;

        public Builder() {
            this._stack = new ArrayDeque<>();
        }

        public Builder version(HttpVersion version) {
            this._version = version;
            return this;
        }

        public Builder phrase(String phrase) {
            this._phrase = phrase;
            return this;
        }

        public Builder code(int code) {
            this._code = code;
            return this;
        }

        public Builder header(String name, String value) {
            this._stack.add(new String[]{name, value});
            return this;
        }

        public Builder entity(Entity entity) {
            this._entity = entity;
            return this;
        }

        public Response build() {
            if (_version == null) {
                throw new NullPointerException("version");
            }
            if (_code == null) {
                throw new NullPointerException("code");
            }
            if (_phrase == null) {
                throw new NullPointerException("phrase");
            }

            final int n = _stack.size();
            String[][] headers = new String[n][];
            for (int i = 0; i < n; i++) {
                headers[i] = _stack.pop();
            }
            Arrays.sort(headers, Commons.cmp);

            Optional<Entity> entity = (_entity == null) ? Optional.empty() : Optional.of(_entity);

            return new Response(_version, _code.toString(), _phrase, headers, entity);
        }

    }

}
