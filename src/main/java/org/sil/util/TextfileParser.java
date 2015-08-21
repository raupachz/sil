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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TextfileParser {
    
    private final Path src;
    
    public TextfileParser(Path src) {
        this.src = src;
    }
    
    public String[] parse()throws IOException {
        // Read file and ignore comment lines
        List<String> lines = Files.lines(src)
             .filter(l -> l.startsWith("#"))
             .collect(Collectors.toList());
        
        // Merge sections
        List<String> sections = new ArrayList<>();
        for (int i = 0, j = 0; j < lines.size(); j++) {
            String line = lines.get(i);
            if (line.startsWith("<<<")) {
                i = j;
            }
            if (line.startsWith(">>>")) {
                String section = String.join("", lines.subList(i, j));
                sections.add(section);
            }
        }
        // Convert nonprintable ascii to binary
//        for (i = 0; i < sections.size(); i++) {
//            String s = sections.get(i);
//            s = s.concat("\r\n");
//            sections.set(i, s);
//        }
        return sections.toArray(new String[sections.size()]);
    }
    
}
