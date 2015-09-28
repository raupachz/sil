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

import org.sil.handler.Handler;
import org.sil.worker.Workers;
import org.sil.acceptor.Acceptor;
import java.lang.management.ManagementFactory;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.OperationsException;
import org.sil.request.RequestDecoder;
import org.sil.response.ResponseEncoder;

public class Server {

    /* Configuration options */
    private final int port = 8080;
    private final int corePoolSize = 10;
    private final int maximumPoolSize = 200;
    private final long keepAliveTime = 60;
    private final BlockingQueue<Runnable> handlerQueue = new ArrayBlockingQueue(100);

    /* Configuration options */
    private final RequestDecoder decoder;
    private final ResponseEncoder encoder;

    /* Configuration options */
    private final Acceptor acceptor;
    private final Workers workers;

    public Server() {
        this.acceptor = new Acceptor(this);
        this.workers = new Workers(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, handlerQueue);
        this.decoder = new RequestDecoder();
        this.encoder = new ResponseEncoder();
    }

    public void initialize() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            mbs.registerMBean(acceptor, new ObjectName("org.sil:type=Acceptor"));
            mbs.registerMBean(workers,  new ObjectName("org.sil:type=Workers"));
        } catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void start() {
        workers.prestartAllCoreThreads();
        acceptor.start();
    }

    public void stop() {
        acceptor.interrupt();
        workers.shutdown();
    }

    public void register(SocketChannel channel) {
        Handler handler = new Handler(this, channel, decoder, encoder);
        workers.submit(handler);
    }

    public int getPort() {
        return port;
    }

}
