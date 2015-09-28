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
package org.sil.log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import org.sil.request.Request;
import org.sil.response.Response;
import org.sil.util.Commons;
import org.testng.annotations.Test;

public class TestAccessLog {
    
    @Test
    public void test_log() throws UnknownHostException {
        Request request = new Request(InetAddress.getLocalHost(), Request.Method.GET, "/index.html", "HTTP/1.1");
        Response response = new Response.Builder()
                .version("HTTP/1.1")
                .code(200)
                .phrase("Ok")
                .header("Server", "sil/1.0")
                .header("Date", Commons.RFC1123_DATE_TIME_FORMATTER.format(ZonedDateTime.now()))
                .header("Content-Length", "0")
                .header("Connection", "close")
                .build();
        
        AccessLogger log = AccessLogger.INSTANCE;
        StringBuilder sb = new StringBuilder();
        log.log(sb, request, response);
        System.out.println(sb.toString());
    }
    
}
