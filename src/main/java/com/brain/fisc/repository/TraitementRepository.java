package com.brain.fisc.repository;

import com.brain.fisc.domain.Traitement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Traitement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TraitementRepository extends MongoRepository<Traitement, String> {}
