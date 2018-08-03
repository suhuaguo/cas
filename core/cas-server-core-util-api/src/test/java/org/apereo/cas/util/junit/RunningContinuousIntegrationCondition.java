package org.apereo.cas.util.junit;

/**
 * This is {@link RunningContinuousIntegrationCondition}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class RunningContinuousIntegrationCondition implements IgnoreCondition {
    @Override
    public Boolean isSatisfied() {
        return "true".equalsIgnoreCase(System.getenv("TRAVIS"))
            || "true".equalsIgnoreCase(System.getProperty("CI", Boolean.FALSE.toString()))
            || "true".equalsIgnoreCase(System.getenv("CI"));
    }
}
