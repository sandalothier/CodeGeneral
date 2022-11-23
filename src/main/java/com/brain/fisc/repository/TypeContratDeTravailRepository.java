package com.brain.fisc.repository;

import com.brain.fisc.domain.TypeContratDeTravail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the TypeContratDeTravail entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TypeContratDeTravailRepository extends MongoRepository<TypeContratDeTravail, String> {}
