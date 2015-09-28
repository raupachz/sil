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

import java.nio.ByteBuffer;
import org.testng.annotations.Test;

@Test
public class TestByteBuffer {
    
    public void test_fb() {
        int a = 1 << 31;
        System.out.println(a);
    }
    
    public void test_methods() {
        byte b = 1;
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.put(b);
        bb.put(b);
        bb.flip();
        
        System.out.println("position = " + bb.position());
        System.out.println("   limit = " + bb.limit());
        System.out.println("capacity = " + bb.capacity());
        System.out.println("remaining= " + bb.hasRemaining());
        bb.get();
        System.out.println("position = " + bb.position());
        System.out.println("   limit = " + bb.limit());
        
        System.out.println("capacity = " + bb.capacity());
        System.out.println("remaining= " + bb.hasRemaining());
    }
    
}
