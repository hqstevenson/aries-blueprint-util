package com.pronoia.aries.blueprint.cm;

import java.util.Date;
import java.util.List;


public interface RequiredConfigurationListenerMBean {

    Date getStartTime();
    Date getStopTime();
    String getRequiredConfigurationListenerId();

    int getRegistrationDelay();

    List<String> getPersistentIdWhitelists();
    List<String> getPersistentIdBlacklists();

    boolean isRunning();

    void start();
    void stop();
    void restart();
}
