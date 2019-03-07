/*
 * Copyright (c) 2017-2018 Bosch Software Innovations GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/epl-2.0/index.php
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.ditto.services.policies.persistence.config;

import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.services.utils.config.ConfigWithFallback;
import org.eclipse.ditto.services.utils.config.ScopedConfig;

import com.typesafe.config.Config;

/**
 * Provides the configuration settings of a policy entity's activity check.
 */
@Immutable
final class DefaultActivityCheckConfig implements ActivityCheckConfig, Serializable {

    private static final String CONFIG_PATH = "activity-check";

    private static final long serialVersionUID = 1939220217385377454L;

    private final Duration inactiveInterval;
    private final Duration deletedInterval;

    private DefaultActivityCheckConfig(final ScopedConfig scopedConfig) {
        inactiveInterval = scopedConfig.getDuration(ActivityCheckConfigValue.INACTIVE_INTERVAL.getConfigPath());
        deletedInterval = scopedConfig.getDuration(ActivityCheckConfigValue.DELETED_INTERVAL.getConfigPath());
    }

    /**
     * Returns an instance of the default activity check config based on the settings of the specified Config.
     *
     * @param config is supposed to provide the settings of the activity check config at {@value #CONFIG_PATH}.
     * @return instance
     * @throws org.eclipse.ditto.services.utils.config.DittoConfigError if {@code config} is invalid.
     */
    public static DefaultActivityCheckConfig of(final Config config) {
        return new DefaultActivityCheckConfig(
                ConfigWithFallback.newInstance(config, CONFIG_PATH, ActivityCheckConfigValue.values()));
    }

    @Override
    public Duration getInactiveInterval() {
        return inactiveInterval;
    }

    @Override
    public Duration getDeletedInterval() {
        return deletedInterval;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DefaultActivityCheckConfig that = (DefaultActivityCheckConfig) o;
        return Objects.equals(inactiveInterval, that.inactiveInterval) &&
                Objects.equals(deletedInterval, that.deletedInterval);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inactiveInterval, deletedInterval);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "inactiveInterval=" + inactiveInterval +
                ", deletedInterval=" + deletedInterval +
                "]";
    }

}
