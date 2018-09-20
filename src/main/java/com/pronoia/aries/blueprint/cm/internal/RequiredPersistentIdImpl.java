package com.pronoia.aries.blueprint.cm.internal;

import com.pronoia.aries.blueprint.cm.RequiredPersistentId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequiredPersistentIdImpl implements RequiredPersistentId {
    final String persistentId;

    Logger log = LoggerFactory.getLogger(this.getClass());

    public RequiredPersistentIdImpl(String persistentId) {
        if (persistentId == null || persistentId.isEmpty()) {
            throw new IllegalArgumentException("Persistent ID argument cannot be null or empty");
        }
        this.persistentId = persistentId;

        log.info("Starting service marking required persistentId={}", persistentId);
    }


    @Override
    public String getPersistentId() {
        return persistentId;
    }
}
