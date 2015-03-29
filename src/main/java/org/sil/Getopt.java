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

/*
 * This file is a modified version from the following newsgroup posting:
 *
 * Newsgroups: mod.std.unix
 * Subject: public domain AT&T getopt source
 * Date: 3 Nov 85 19:34:15 GMT
 *
 * Here's something you've all been waiting for:  the AT&T public domain
 * source for getopt(3).  It is the code which was given out at the 1985
 * UNIFORUM conference in Dallas.  I obtained it by electronic mail
 * directly from AT&T.  The people there assure me that it is indeed
 * in the public domain.
 */
package org.sil;

import java.util.Arrays;
import java.util.Objects;

public class Getopt {
    
    private final String[] args;
    private final String optstring;

    private int optind;
    private char optopt;
    private String optarg;
    private int sp;
    
    public Getopt(String[] args, String optstring) {
        Objects.requireNonNull(args);
        Objects.requireNonNull(optstring);
        this.args = Arrays.copyOf(args, args.length);
        this.optstring = optstring;
        this.sp = 1;
    }

    public int getopt() {
        char ch;
        int cp;
        
        if (sp == 1) {
            if (optind >= args.length || args[optind].charAt(0) != '-' || args[optind].length() == 1) {
                return -1;
            } else if (args[optind].equals("--")) {
                optind++;
                return -1;
            }
        }
        optopt = ch = args[optind].charAt(sp);
        if ((cp = optstring.indexOf(ch)) == -1) {
            if (++sp >= args[optind].length()) {
                optind++;
                sp = 1;
            }
            return '?';
        }
        if (++cp < optstring.length() && optstring.charAt(cp) == ':') {
            if (sp + 1 < args[optind].length()) {
                optarg = args[optind++].substring(sp + 1);
            } else if (++optind >= args.length) {
                sp = 1;
                return '?';
            } else {
                optarg = args[optind++];
            }
            sp = 1;
        } else {
            if (++sp >= args[optind].length()) {
                sp = 1;
                optind++;
            }
            optarg = null;
        }
        return ch;
    }
    
    public String getOptarg() {
        return optarg;
    }
    
    public char getOptopt() {
        return optopt;
    }
    
    public int getOptind() {
        return optind;
    }

}
