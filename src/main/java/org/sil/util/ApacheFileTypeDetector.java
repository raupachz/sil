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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.nio.charset.StandardCharsets.*;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ApacheFileTypeDetector extends FileTypeDetector {

    private String[][] mimeTypes;
    private final Comparator<String[]> cmp
            = (final String[] sa1, final String[] sa2) -> sa1[1].compareTo(sa2[1]);

    public ApacheFileTypeDetector() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream in = classLoader.getResourceAsStream("mime.types")) {
            loadMimeTypes(in);
        } catch (IOException e) {
            throw new RuntimeException("mime.types", e);
        }
    }

    public ApacheFileTypeDetector(InputStream in) throws IOException {
        loadMimeTypes(in);
    }

    private void loadMimeTypes(InputStream in) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, UTF_8))) {
            // In mime.types a single line can have more than one extension
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#")) {
                    String[] tokens = line.split("\\s+");
                    if (tokens.length > 2) {
                        String mime = tokens[0];
                        for (int i = 1; i < tokens.length; i++) {
                            String extension = tokens[i];
                            String extraLine = mime + " " + extension;
                            lines.add(extraLine);
                        }
                    } else {
                        lines.add(line);
                    }
                }
            }
            // Now gather all lines in a two-dimensional array
            mimeTypes = new String[lines.size()][];
            for (int i = 0; i < lines.size(); i++) {
                mimeTypes[i] = lines.get(i).split("\\s+");
                assert mimeTypes[i].length == 2;
            }
            // sort by extension
            Arrays.sort(mimeTypes, cmp);
        }
    }

    String probeMimeType(final String extension) {
        final String[] key = new String[]{null, extension};
        int i = Arrays.binarySearch(mimeTypes, key, cmp);
        return i < 0 ? "application/octet-stream" : mimeTypes[i][0];
    }

    String getExtension(Path path) {
        String extension = "";
        String filename = path.getFileName().toString().toLowerCase();
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            extension = filename.substring(i + 1);
        }
        return extension;
    }

    @Override
    public String probeContentType(Path path) throws IOException {
        String extension = getExtension(path);
        return probeMimeType(extension);
    }

}
