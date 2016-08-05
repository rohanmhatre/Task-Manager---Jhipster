package com.fps.repository.search;

import com.fps.domain.Owner;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Owner entity.
 */
public interface OwnerSearchRepository extends ElasticsearchRepository<Owner, Long> {
}
