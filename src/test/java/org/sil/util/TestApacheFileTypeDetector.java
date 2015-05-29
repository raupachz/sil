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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class TestApacheFileTypeDetector {

    final Path mimeTypes = Paths.get(System.getProperty("user.dir"), "src/main/resources", "mime.types");
    ApacheFileTypeDetector detector;
    
    @BeforeTest
    public void beforeTest() throws IOException {
        InputStream in = new FileInputStream(mimeTypes.toFile());
        detector = new ApacheFileTypeDetector(in);
    }
    
    @Test
    public void test_multidimensional_arrys() {
        String[][] m = new String[1][];
        assertNull(m[0]);
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void test_null() {
        detector.probeMimeType(null);
    }
    
    @Test
    public void test_empty() {
        String extension = "";
        String expected = "application/octet-stream";
        String actual = detector.probeMimeType(extension);
        assertEquals(actual, expected);
    }
    
    @Test
    public void test_unkown() {
        String extension = "thehip";
        String expected = "application/octet-stream";
        String actual = detector.probeMimeType(extension);
        assertEquals(actual, expected);
    }
    
    @Test
    public void test_onepkg() {
        String extension = "onepkg";
        String expected = "application/onenote";
        String actual = detector.probeMimeType(extension);
        assertEquals(actual, expected);
    }

    @Test
    public void test_probeMimeType() throws IOException {
        Charset utf8 = StandardCharsets.UTF_8;
        List<String> lines = Files.lines(mimeTypes, utf8)
                                .filter(l -> !l.startsWith("#"))
                                .collect(Collectors.toList());
        Collections.shuffle(lines);
        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            String expected = tokens[0];
            
            for (int i = 1; i < tokens.length; i++) {
                String extension = tokens[i];
                String actual = detector.probeMimeType(extension);
                assertEquals(actual, expected, "with extension " + extension);
            }
        }
    }

}
