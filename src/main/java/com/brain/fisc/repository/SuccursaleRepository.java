package com.brain.fisc.repository;

import com.brain.fisc.domain.Succursale;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Succursale entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SuccursaleRepository extends MongoRepository<Succursale, String> {}
