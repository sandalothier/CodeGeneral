package com.brain.fisc.repository;

import com.brain.fisc.domain.Conge;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Conge entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CongeRepository extends MongoRepository<Conge, String> {}
