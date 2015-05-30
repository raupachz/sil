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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import org.sil.config.Configuration;
import java.util.logging.Logger;

/**
 * Accepts incoming socket connections and passes them on to a handler
 */
public class HttpServerThread extends Thread {
    
    private static final Logger logger = Logger.getLogger(HttpServerThread.class.getName());
    
    private final Configuration config;
    private final ExecutorService es;

    public HttpServerThread(Configuration config) {
        super("http-server-0");
        this.config = config;
        this.es = new HttpThreadPoolExecutor(10, 200, 60000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        ServerSocketChannel ssc;
        try {
            ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(config.getPort()));
        } catch (IOException e) {
           logger.log(Level.SEVERE, e, () -> "Unable to bind to port: " + config.getPort());
            return;
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                SocketChannel sc = ssc.accept();
                sc.setOption(StandardSocketOptions.TCP_NODELAY, true);
                HttpHandler handler = new HttpHandler(config, sc);
                es.execute(handler);
            } catch (ClosedByInterruptException e) {
                logger.log(Level.INFO, "No longer listening on port: {0}", config.getPort());
            } catch (IOException e) {
                logger.log(Level.WARNING, e, () -> "Could not accept connection to this socket.");
            }
        }

    }

    static class HttpThreadPoolExecutor extends ThreadPoolExecutor {

        public HttpThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new ArrayBlockingQueue(100), new HttpThreadFactory(), new DefaultRejectedExecutionHandler());
        }

    }

    static class HttpThreadFactory implements ThreadFactory {

        private static final AtomicInteger counter = new AtomicInteger();

        @Override
        public Thread newThread(Runnable target) {
            ThreadGroup group = Thread.currentThread().getThreadGroup();
            String name = "http-" + counter.getAndIncrement();
            logger.log(Level.INFO, "[{0}]", name);
            return new HttpThread(group, target, name);
        }

    }

    static class DefaultRejectedExecutionHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable target, ThreadPoolExecutor executor) {
            // this should yield some exception...
        }

    }

}
