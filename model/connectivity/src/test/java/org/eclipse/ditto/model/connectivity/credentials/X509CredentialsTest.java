/*
 * Copyright (c) 2017 Bosch Software Innovations GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/epl-2.0/index.php
 *
 * Contributors:
 *    Bosch Software Innovations GmbH - initial contribution
 */
package org.eclipse.ditto.model.connectivity.credentials;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

/**
 * Tests {@link org.eclipse.ditto.model.connectivity.credentials.X509Credentials}.
 */
public final class X509CredentialsTest {

    @Test
    public void testHashCodeAndEquals() {
        EqualsVerifier.forClass(X509Credentials.class).verify();
    }

    @Test
    public void assertImmutability() {
        assertInstancesOf(X509Credentials.class, areImmutable());
    }

    @Test
    public void testJsonSerialization() {
        final Credentials original = X509Credentials.newBuilder()
                .clientCertificate("bad certificate")
                .clientKey("bad key")
                .trustedCertificates("bad ca")
                .build();
        final Credentials deserialized = Credentials.fromJson(original.toJson());
        assertThat(deserialized).isEqualTo(original);
    }
}
