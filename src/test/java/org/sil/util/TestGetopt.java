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

import org.sil.util.Getopt;
import java.util.Arrays;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class TestGetopt {
    
    @DataProvider(name = "test_getopt")
    public static Object[][] data_test_getopt() {
        return new Object[][] {
            { new String[] {}, false, false, null, new String[] {}},
            { new String[] { "-a", "-b" }, true, true, null, new String[] {}},
            { new String[] { "-ab" }, true, true, null, new String[] {}},
            { new String[] { "-c", "foo" }, false, false, "foo", new String[] {}},
            { new String[] { "-cfoo" }, false, false, "foo", new String[] {}},
            { new String[] { "arg1" }, false, false, null, new String[] { "arg1"}},
            { new String[] { "-a", "arg1" }, true, false, null, new String[] { "arg1"}},
            { new String[] { "-c", "foo", "arg1" }, false, false, "foo", new String[] { "arg1"}},
            { new String[] { "-a", "--", "-b" }, true, false, null, new String[] { "-b"}},
            { new String[] { "-a", "-" }, true, false, null, new String[] { "-"}}
        };
    }
    
    @Test(dataProvider = "test_getopt")
    public void test_getopt(String args[], boolean a, boolean b, String c, String[] nonArgs) {
        Getopt g = new Getopt(args, "abc:");

        boolean aflag = false;
        boolean bflag = false;
        String cvalue = null;
        int i;
        int ch;

        while ((ch = g.getopt()) != -1) {
            switch (ch) {
                case 'a':
                    aflag = true;
                    break;
                case 'b':
                    bflag = true;
                    break;
                case 'c':
                    cvalue = g.getOptarg();
                    break;
                case '?':
                    if (g.getOptopt() == 'c') {
                        fail("Option -" + g.getOptopt() + " requires an argument.");
                    } else {
                        fail("Unknown option -" + g.getOptopt());
                    }
                default:
                    fail("Unkown return value: " + c);
            }
        }
        
        assertEquals(a, aflag);
        assertEquals(b, bflag);
        assertEquals(c, cvalue);
        assertEquals(nonArgs, Arrays.copyOfRange(args, g.getOptind(), args.length));
    }
    
}
