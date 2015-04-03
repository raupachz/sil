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
package org.sil.util;

import org.sil.util.Hex;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class TestHex {
    
    public final static Charset utf8 = Charset.forName("UTF-8");
    
    private Hex hex;
    
    @BeforeSuite
    public void initialize() {
        hex = new Hex();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_null() throws IOException {
        hex.dump(null, null);
    }
    
    @Test
    public void test_dump_buff1() throws IOException {
        String src = "A";
        ByteBuffer buffer = ByteBuffer.wrap(src.getBytes(utf8));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        hex.dump(buffer, out);
        
        String expected = "00000000  41                                               |A|" + System.lineSeparator();
        String actual = new String(out.toByteArray(), utf8);
        
        assertEquals(actual, expected);
    }
    
    @Test
    public void test_dump_buff0() throws IOException {
        ByteBuffer empty = ByteBuffer.wrap(new byte[0]);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        hex.dump(empty, out);
        
        String expected = "";
        String actual = new String(out.toByteArray(), utf8);
        
        assertEquals(actual, expected);
    }
            
    @Test
    public void test_dump_buff16() throws IOException {
        String src = "0123456789ABCDEF";
        ByteBuffer buffer = ByteBuffer.wrap(src.getBytes(utf8));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        hex.dump(buffer, out);
        
        String expected = "00000000  30 31 32 33 34 35 36 37 38 39 41 42 43 44 45 46  |0123456789ABCDEF|" + System.lineSeparator();
        String actual = new String(out.toByteArray(), utf8);
        
        assertEquals(actual, expected);
    }
    
    @Test
    public void test_dump_buff17() throws IOException {
        String src = "0123456789ABCDEFA";
        ByteBuffer buffer = ByteBuffer.wrap(src.getBytes(utf8));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        hex.dump(buffer, out);
        
        String actual = new String(out.toByteArray(), utf8);
        
        System.out.println(actual);
    }
    
    
}
