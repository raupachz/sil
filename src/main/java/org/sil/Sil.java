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

import org.sil.util.Getopt;
import java.io.PrintStream;
import static java.lang.System.*;

public class Sil {

    private static final PrintStream out = System.out;

    public static void main(String args[]) throws InterruptedException {
        Getopt g = new Getopt(args, "hp:v");
        int ch;
        while ((ch = g.getopt()) != -1) {
            switch (ch) {
                case 'h':
                    usage();
                    System.exit(0);
                case 'v':
                    version();
                    System.exit(0);
                case '?':
                    System.out.println("Unrecognized option: -" + g.getOptopt());
                    System.exit(0);
            }
        }

        final Httpd httpd = new Httpd();

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                out.println("Stopping sil");
                httpd.interrupt();
            }
        });

        out.println("Starting sil/1.0 on port 8080 with directory .");
        httpd.start();
        out.println("Hit CTRL-C to stop");
    }

    static void usage() {
        out.println("usage: java -jar sil.jar [options] [path]");
        out.println();
        out.println("Options:");
        out.println("  -h                 Display help information");
        out.println("  -p                 Port to use, default is 8080");
        out.println("  -v                 Display version information");
    }

    static void version() {
        out.println("sil <version> <sha1>; ISOUTC");
        out.print("Java version: ");
        out.print(getProperty("java.version"));
        out.print(", vendor: ");
        out.println(getProperty("java.vendor"));
        out.print("Java home: ");
        out.println(getProperty("java.home"));
        out.print("OS name: \"");
        out.print(getProperty("os.name"));
        out.print("\", version: \"");
        out.print(getProperty("os.version"));
        out.print("\", arch: \"");
        out.print(getProperty("os.arch"));
        out.println("\"");
    }

}
