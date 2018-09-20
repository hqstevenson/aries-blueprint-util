package com.pronoia.aries.blueprint.cm;

import com.pronoia.aries.blueprint.cm.internal.RequiredPersistentIdImpl;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequiredConfigurationListener  implements ConfigurationListener, RequiredConfigurationListenerMBean {
    public static final String CONFIGURATION_SERVICE_PROPERTY = "required-persistent-id";

    static AtomicInteger configurationListenerCounter = new AtomicInteger(1);

    final BundleContext bundleContext;

    String requiredConfigurationListenerId;
    ObjectName requiredConfigurationListenerObjectName;

    Date startTime;
    Date stopTime;

    List<Pattern> persistentIdBlacklistPatterns;
    List<Pattern> persistentIdWhitelistPatterns;

    ServiceRegistration<ConfigurationListener> configurationListenerServiceRegistration;
    Map<String, ServiceRegistration<RequiredPersistentId>> configurationServiceRegistrations = new ConcurrentHashMap<>();

    Logger log = LoggerFactory.getLogger(this.getClass());

    public RequiredConfigurationListener(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public String getRequiredConfigurationListenerId() {
        if (requiredConfigurationListenerId == null || requiredConfigurationListenerId.isEmpty()) {
            requiredConfigurationListenerId = String.format("required-configuration-listener-%d", configurationListenerCounter.getAndIncrement());
        }

        return requiredConfigurationListenerId;
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }

    @Override
    public Date getStopTime() {
        return stopTime;
    }

    @Override
    public boolean isRunning() {
        return configurationListenerServiceRegistration != null;
    }

    public void setRequiredConfigurationListenerId(String requiredConfigurationListenerId) {
        this.requiredConfigurationListenerId = requiredConfigurationListenerId;
    }

    @Override
    public List<String> getPersistentIdBlacklists() {
        List<String> answer = new LinkedList<>();

        if (hasPersistentIdBlacklistPatterns()) {
            for (Pattern pattern : persistentIdBlacklistPatterns) {
                answer.add(pattern.pattern());
            }
        }
        return answer;
    }

    public void setPersistentIdBlacklists(List<String> persistentIdBlacklists) {
        if (persistentIdBlacklistPatterns == null) {
            persistentIdBlacklistPatterns = new LinkedList<>();
        } else {
            persistentIdBlacklistPatterns.clear();
        }

        addPatternsFromStrings(persistentIdBlacklistPatterns, persistentIdBlacklists);
    }

    @Override
    public List<String> getPersistentIdWhitelists() {
        List<String> answer = new LinkedList<>();

        if (hasPersistentIdWhitelistPatterns()) {
            for (Pattern pattern : persistentIdWhitelistPatterns) {
                answer.add(pattern.pattern());
            }
        }
        return answer;
    }

    public void setPersistentIdWhitelists(List<String> persistentIdWhitelists) {
        if (persistentIdWhitelistPatterns == null) {
            persistentIdWhitelistPatterns = new LinkedList<>();
        } else {
            persistentIdWhitelistPatterns.clear();
        }

        addPatternsFromStrings(persistentIdWhitelistPatterns, persistentIdWhitelists);
    }

    public boolean hasPersistentIdBlacklistPatterns() {
        return persistentIdBlacklistPatterns != null && !persistentIdBlacklistPatterns.isEmpty();
    }

    public List<Pattern> getPersistentIdBlacklistPatterns() {
        return persistentIdBlacklistPatterns;
    }

    public void setPersistentIdBlacklistPatterns(List<Pattern> persistentIdBlacklistPatterns) {
        this.persistentIdBlacklistPatterns = persistentIdBlacklistPatterns;
    }

    public boolean hasPersistentIdWhitelistPatterns() {
        return persistentIdWhitelistPatterns != null && !persistentIdWhitelistPatterns.isEmpty();
    }

    public List<Pattern> getPersistentIdWhitelistPatterns() {
        return persistentIdWhitelistPatterns;
    }

    public void setPersistentIdWhitelistPatterns(List<Pattern> persistentIdWhitelistPatterns) {
        this.persistentIdWhitelistPatterns = persistentIdWhitelistPatterns;
    }

    public void initialize() {
        registerMBean();
        start();
    }

    public void destroy() {
        stop();
        unregisterMBean();
    }

    /**
     * Start the listener.
     */
    @Override
    public void start() {
        configurationListenerServiceRegistration = bundleContext.registerService(ConfigurationListener.class, this, null);
        startTime = new Date();
        checkExistingConfigurationPids();
    }


    /**
     * Start the listener.
     */
    @Override
    public void stop() {
        log.info("Stopping '{}'", this.getClass().getSimpleName());
        if (configurationServiceRegistrations != null && !configurationServiceRegistrations.isEmpty()) {
            for (String pid : configurationServiceRegistrations.keySet()) {
                unregisterServiceForPid(pid);
            }
        }

        configurationListenerServiceRegistration.unregister();
        configurationListenerServiceRegistration = null;
        stopTime = new Date();
    }

    @Override
    public void restart() {
        stop();
        try {
            Thread.sleep(5000);
            start();
        } catch (InterruptedException interruptedEx) {
            log.warn("Restart was interrupted - required configuration listener will not be restarted", interruptedEx);
        }
    }

    @Override
    public void configurationEvent(ConfigurationEvent event) {
        String pid = event.getPid();

        if (isIgnoredPid(pid)) {
            log.debug("Ignoring event for system PID '{}'", pid);
        } else {
            int type = event.getType();
            switch (type) {
            case ConfigurationEvent.CM_UPDATED:
                registerServiceForPid(pid);
                break;
            case ConfigurationEvent.CM_DELETED:
                unregisterServiceForPid(pid);
                break;
            case ConfigurationEvent.CM_LOCATION_CHANGED:
                log.debug("Ignoring CM_LOCATION_CHANGED Configuration event for PID '{}'", pid);
                break;
            default:
                // Shouldn't ever get here
                log.warn("Ignoring unexpected configuration event type '{}' for PID '{}'", type, pid);
                break;
            }
        }
    }

    /**
     * Check the currently registered PIDs in ConfigurationAdmin.
     */
    public synchronized void checkExistingConfigurationPids() {
        ServiceReference<ConfigurationAdmin> configurationAdminServiceReference = bundleContext.getServiceReference(ConfigurationAdmin.class);
        try {
            ConfigurationAdmin configAdmin = bundleContext.getService(configurationAdminServiceReference);
            Configuration[] configurations = configAdmin.listConfigurations(null);
            if (configurations != null && configurations.length > 0) {
                for (Configuration configuration : configurations) {
                    String pid = configuration.getPid();
                    if (!isIgnoredPid(pid)) {
                        registerServiceForPid(pid);
                    }
                }
            }
        } catch (IOException | InvalidSyntaxException listConfigEx) {
            log.error("Exception encountered listing configurations", listConfigEx);
        } finally {
            if (configurationAdminServiceReference != null) {
                bundleContext.ungetService(configurationAdminServiceReference);
            }
        }
    }


    /**
     * Register a service for the configuration PID.
     *
     * @param pid the PID to register a service for
     */
    synchronized void registerServiceForPid(String pid) {
        if (!configurationServiceRegistrations.containsKey(pid)) {
            log.info("Registering '{}' service for PID '{}'", RequiredPersistentId.class.getName(), pid);
            Hashtable<String, String> serviceProperties = new Hashtable<>();
            serviceProperties.put(CONFIGURATION_SERVICE_PROPERTY, pid);
            ServiceRegistration<RequiredPersistentId> serviceRegistration = bundleContext.registerService(RequiredPersistentId.class, new RequiredPersistentIdImpl(pid), serviceProperties);
            configurationServiceRegistrations.put(pid, serviceRegistration);
        }
    }

    /**
     * Unregister a service for the configuration PID, if a service has been registered.
     *
     * @param pid the PID to unregister an existing service, if one has been registered
     */
    synchronized void unregisterServiceForPid(String pid) {
        if (configurationServiceRegistrations.containsKey(pid)) {
            log.info("Unregistering '{}' service for PID '{}'", RequiredPersistentId.class.getName(), pid);
            configurationServiceRegistrations.get(pid).unregister();
            configurationServiceRegistrations.remove(pid);
        }
    }

    /**
     * Determine if a PID is a candidate for registration.
     *
     * @param pid the PID to inspect
     *
     * @return true if the PID is a candidate for service registration; false otherwise
     */
    boolean isIgnoredPid(String pid) {
        if (hasPersistentIdWhitelistPatterns()) {
            for (Pattern pattern : persistentIdWhitelistPatterns) {
                if (pattern.matcher(pid).matches()) {
                    log.info("Whitelist pattern {} matched pid {}", pattern.pattern(), pid);
                    return false;
                }
            }
        }

        if (hasPersistentIdBlacklistPatterns()) {
            for (Pattern pattern : persistentIdBlacklistPatterns) {
                if (pattern.matcher(pid).matches()) {
                    log.info("Blacklist pattern {} matched pid {}", pattern.pattern(), pid);
                    return true;
                }
            }
        }

        return false;
    }

    List<String> createStringList(List<Pattern> patterns) {
        List<String> answer = new LinkedList<>();

        if (patterns != null && !patterns.isEmpty()) {
            for (Pattern pattern : patterns) {
                answer.add(pattern.pattern());
            }
        }

        return answer;
    }

    void addPatternsFromStrings(List<Pattern> patternList, String ... strings) {
        if (strings != null && strings.length > 0) {
            if (patternList == null) {
                throw new IllegalArgumentException("setPatternsFromStrings(List<String>, String ...) - list of patterns cannot be null");
            }

            for (String str : strings) {
                try {
                    patternList.add(Pattern.compile(str));
                } catch (PatternSyntaxException syntaxEx) {
                    log.warn("Exception encountered compiling pattern - ignoring pattern {}", str, syntaxEx);
                }
            }
        } else {
            log.warn("Null or Empty Array of Strings passed to setPatternsFromStrings");
        }
    }

    void addPatternsFromStrings(List<Pattern> patternList, List<String> strings) {
        if (strings != null && !strings.isEmpty()) {
            if (patternList == null) {
                throw new IllegalArgumentException("setPatternsFromStrings(List<String>, List<Pattern> - list of patterns cannot be null");
            }

            for (String str : strings) {
                try {
                    patternList.add(Pattern.compile(str));
                } catch (PatternSyntaxException syntaxEx) {
                    log.warn("Exception encountered compiling pattern - ignoring pattern {}", str, syntaxEx);
                }
            }
        } else {
            log.warn("Null or Empty List of Strings passed to setPatternsFromStrings");
        }
    }

    void registerMBean() {
        String newRequiredConfigurationObjectNameString = String.format("com.pronoia.aries.util:type=%s,id=%s", this.getClass().getSimpleName(), getRequiredConfigurationListenerId());
        try {
            requiredConfigurationListenerObjectName = new ObjectName(newRequiredConfigurationObjectNameString);
        } catch (MalformedObjectNameException malformedNameEx) {
            log.warn("Failed to create ObjectName for string {} - MBean will not be registered", newRequiredConfigurationObjectNameString, malformedNameEx);
            return;
        }

        try {
            ManagementFactory.getPlatformMBeanServer().registerMBean(this, requiredConfigurationListenerObjectName);
        } catch (InstanceAlreadyExistsException allreadyExistsEx) {
            log.warn("MBean already registered for required configuration listener {}", requiredConfigurationListenerObjectName, allreadyExistsEx);
        } catch (MBeanRegistrationException registrationEx) {
            log.warn("MBean registration failure for required configuration listener {}", newRequiredConfigurationObjectNameString, registrationEx);
        } catch (NotCompliantMBeanException nonCompliantMBeanEx) {
            log.warn("Invalid MBean for consumer factory {}", newRequiredConfigurationObjectNameString, nonCompliantMBeanEx);
        }

    }

    void unregisterMBean() {
        if (requiredConfigurationListenerObjectName != null) {
            try {
                ManagementFactory.getPlatformMBeanServer().unregisterMBean(requiredConfigurationListenerObjectName);
            } catch (InstanceNotFoundException | MBeanRegistrationException unregisterEx) {
                log.warn("Failed to unregister required configuration listener MBean {}", requiredConfigurationListenerObjectName.getCanonicalName(), unregisterEx);
            } finally {
                requiredConfigurationListenerObjectName = null;
            }
        }
    }

}
