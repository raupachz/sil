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

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.sil.ResponseHeaderName.*;

public class Response {
    
    private final List<ResponseHeader> responseHeaders;
    
    private Status status;
    
    public Response() {
        this.responseHeaders = new ArrayList<>();
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public void addHeader(ResponseHeader header) {
        responseHeaders.add(header);
    }
    
    public void removeHeader(ResponseHeader header) {
        responseHeaders.remove(header);
    }
    
    public Collection<ResponseHeader> getResponseHeaders() {
        return responseHeaders;
    }
    
    public static Response blank() {
        return new Response();
    }
    
    public static Response of(Status status) {
        Response res = new Response();
        res.setStatus(status);
        res.addHeader(new ResponseHeader(Server, "sil"));
        res.addHeader(new ResponseHeader(Connection, "close"));
        res.addHeader(new ResponseHeader(ContentLength, "0"));
        
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("GMT"));
        String dateValue = DateTimeFormatter.RFC_1123_DATE_TIME.format(now);
        
        res.addHeader(new ResponseHeader(Date, dateValue));
        
        return res;
    }
    
}
