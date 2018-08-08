package org.apereo.cas.services;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is {@link RestfulServiceRegistry}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@RequiredArgsConstructor
public class RestfulServiceRegistry extends AbstractServiceRegistry {
    private final transient RestTemplate restTemplate;
    private final String url;
    private final MultiValueMap<String, String> headers;

    @Override
    public RegisteredService save(final RegisteredService registeredService) {
        val responseEntity = restTemplate.exchange(this.url, HttpMethod.POST,
            new HttpEntity<>(registeredService, this.headers), RegisteredService.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        return null;
    }

    @Override
    public boolean delete(final RegisteredService registeredService) {
        val responseEntity = restTemplate.exchange(this.url, HttpMethod.DELETE,
            new HttpEntity<>(registeredService, this.headers), Integer.class);
        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    @Override
    public Collection<RegisteredService> load() {
        val responseEntity = restTemplate.exchange(this.url, HttpMethod.GET,
            new HttpEntity<>(this.headers), RegisteredService[].class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            val results = responseEntity.getBody();
            return Stream.of(results).collect(Collectors.toList());
        }
        return new ArrayList<>(0);
    }

    @Override
    public RegisteredService findServiceById(final long id) {
        val url = StringUtils.appendIfMissing(this.url, "/").concat(String.valueOf(id));
        val responseEntity = restTemplate.exchange(url, HttpMethod.GET,
            new HttpEntity<>(id, this.headers), RegisteredService.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        return null;
    }

    @Override
    public RegisteredService findServiceById(final String id) {
        val url = StringUtils.appendIfMissing(this.url, "/").concat(String.valueOf(id));
        val responseEntity = restTemplate.exchange(url, HttpMethod.GET,
            new HttpEntity<>(id, this.headers), RegisteredService.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        return null;
    }

    @Override
    public long size() {
        return load().size();
    }
}
