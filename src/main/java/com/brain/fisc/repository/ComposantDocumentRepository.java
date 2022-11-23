package com.brain.fisc.repository;

import com.brain.fisc.domain.ComposantDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the ComposantDocument entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ComposantDocumentRepository extends MongoRepository<ComposantDocument, String> {}
