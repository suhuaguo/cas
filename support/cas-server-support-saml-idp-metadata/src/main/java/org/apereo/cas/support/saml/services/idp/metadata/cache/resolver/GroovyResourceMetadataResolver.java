package org.apereo.cas.support.saml.services.idp.metadata.cache.resolver;

import org.apereo.cas.configuration.model.support.saml.idp.SamlIdPProperties;
import org.apereo.cas.support.saml.OpenSamlConfigBean;
import org.apereo.cas.support.saml.services.SamlRegisteredService;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.ResourceUtils;
import org.apereo.cas.util.ScriptingUtils;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.opensaml.saml.metadata.resolver.MetadataResolver;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This is {@link GroovyResourceMetadataResolver}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
public class GroovyResourceMetadataResolver extends BaseSamlRegisteredServiceMetadataResolver {
    public GroovyResourceMetadataResolver(final SamlIdPProperties samlIdPProperties,
                                          final OpenSamlConfigBean configBean) {
        super(samlIdPProperties, configBean);
    }

    @Override
    public Collection<? extends MetadataResolver> resolve(final SamlRegisteredService service) {
        try {
            val metadataLocation = service.getMetadataLocation();
            LOGGER.info("Loading SAML metadata via [{}]", metadataLocation);
            val metadataResource = ResourceUtils.getResourceFrom(metadataLocation);
            final Object[] args = {service, this.configBean, this.samlIdPProperties, LOGGER};
            val metadataResolver =
                ScriptingUtils.executeGroovyScript(metadataResource, args, MetadataResolver.class);
            if (metadataResolver != null) {
                return CollectionUtils.wrap(metadataResolver);
            }
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return new ArrayList<>(0);
    }

    @Override
    public boolean supports(final SamlRegisteredService service) {
        val metadataLocation = service.getMetadataLocation();
        return ScriptingUtils.isExternalGroovyScript(metadataLocation);
    }

    @Override
    public boolean isAvailable(final SamlRegisteredService service) {
        if (supports(service)) {
            val metadataLocation = service.getMetadataLocation();
            return ResourceUtils.doesResourceExist(metadataLocation);
        }
        return false;
    }
}
