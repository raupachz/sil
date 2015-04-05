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
package org.sil.entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class EntityFactory {
    
    private final Path root;
    
    public EntityFactory(Path root) {
        this.root = root;
    }
    
    public Optional<Entity> get(String rawURI) throws IOException {
        Objects.requireNonNull(rawURI);
        
        Path physicalPath = physicalPathOf(rawURI);
        
        if (!Files.exists(physicalPath, LinkOption.NOFOLLOW_LINKS)
                || Files.isDirectory(physicalPath, LinkOption.NOFOLLOW_LINKS)) {
            return Optional.empty();
        }
        
        long size = Files.size(physicalPath);
        Instant lastModified = Files.getLastModifiedTime(physicalPath, LinkOption.NOFOLLOW_LINKS).toInstant();
        String contentType = Files.probeContentType(physicalPath);
        
        Entity entity = new DefaultEntity(physicalPath, lastModified, size, contentType);
        return Optional.of(entity);
    }
    
    Path physicalPathOf(String rawURI) {
        if (rawURI.charAt(0) == '/') {
            if (rawURI.length() > 1) {
                rawURI = rawURI.substring(1);
            } else {
                rawURI = "";
            }
        }
        return root.resolve(rawURI);
    }
    
}
