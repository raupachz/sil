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
package org.sil.acceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sil.Server;

/**
 * Accepts socket channels and passes them to the engine for
 * further processing.
 *
 * Interrupt this thread to stop listening.
 */
public class Acceptor extends Thread implements AcceptorMXBean {

    private static final Logger logger = Logger.getLogger(Acceptor.class.getName());

    private final Server server;
    private final int port;

    public Acceptor(Server server) {
        super("http-acceptor");
        this.server = server;
        this.port = server.getPort();
    }

    @Override
    public void run() {
        ServerSocketChannel socket;
        try {   
            socket = ServerSocketChannel.open();
            socket.bind(new InetSocketAddress(port));
            logger.log(Level.INFO, "Listening on port: {0}", port);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e, () -> "Unable to bind to port: " + port);
            return;
        }

        while (!isInterrupted()) {
            try {
                SocketChannel channel = socket.accept();
                server.register(channel);
            } catch (ClosedByInterruptException ignore) {
                logger.log(Level.INFO, "No longer listening on port: {0}", port);
            } catch (IOException e) {
                logger.log(Level.WARNING, e, () -> "Could not accept connection.");
            }
        }
    }
    
    @Override
    public int getPort() {
        return port;
    }

}
