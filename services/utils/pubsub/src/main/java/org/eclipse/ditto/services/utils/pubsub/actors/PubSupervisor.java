/*
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.ditto.services.utils.pubsub.actors;

import javax.annotation.Nullable;

import org.eclipse.ditto.services.utils.akka.LogUtil;
import org.eclipse.ditto.services.utils.pubsub.DistributedAcks;
import org.eclipse.ditto.services.utils.pubsub.ddata.DData;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.DiagnosticLoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

/**
 * Supervisor of actors dealing with publications.
 * <pre>
 * {@code
 *                                PubSupervisor
 *                                      +
 *              supervises one-for-many |
 *             +------------------------+
 *             |                        |
 *             |                        |
 *             v                        v
 *        Publisher                PubUpdater
 *         +                         +
 *         |                         |
 *         |                         |
 *         |                         |Member removed:
 *         |                         |write local
 *         |                         |to be distributed later
 *         |read local               |
 *         |                         v
 *         +--------------------> DDataReplicator
 * }
 * </pre>
 */
public final class PubSupervisor extends AbstractPubSubSupervisor {

    private final DiagnosticLoggingAdapter log = LogUtil.obtain(this);

    private final DData<ActorRef, ?, ?> ddata;
    private final DistributedAcks distributedAcks;

    @Nullable private ActorRef publisher;

    @SuppressWarnings("unused")
    private PubSupervisor(final DData<ActorRef, ?, ?> ddata, final DistributedAcks distributedAcks) {
        super();
        this.ddata = ddata;
        this.distributedAcks = distributedAcks;
    }

    /**
     * Create Props object for this actor.
     *
     * @param ddata read-write access to the distributed data.
     * @param distributedAcks access to the declared ack labels ddata.
     * @return the Props object.
     */
    public static Props props(final DData<ActorRef, ?, ?> ddata, final DistributedAcks distributedAcks) {
        return Props.create(PubSupervisor.class, ddata, distributedAcks);
    }

    @Override
    protected Receive createPubSubBehavior() {
        return ReceiveBuilder.create()
                .match(Publisher.Request.class, this::isPublisherAvailable, this::publish)
                .match(Publisher.Request.class, this::publisherUnavailable)
                .build();
    }

    @Override
    protected void onChildFailure() {
        publisher = null;
    }

    @Override
    protected void startChildren() {
        startChild(PubUpdater.props(ddata.getWriter()), PubUpdater.ACTOR_NAME_PREFIX);
        publisher = startChild(Publisher.props(ddata.getReader(), distributedAcks), Publisher.ACTOR_NAME_PREFIX);
    }

    private boolean isPublisherAvailable() {
        return publisher != null;
    }

    @SuppressWarnings("ConstantConditions")
    private void publish(final Publisher.Request publish) {
        publisher.tell(publish, getSender());
    }

    private void publisherUnavailable(final Publisher.Request publish) {
        log.error("Publisher unavailable. Dropping <{}>", publish);
    }
}
