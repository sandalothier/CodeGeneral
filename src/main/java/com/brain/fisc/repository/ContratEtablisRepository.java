package com.brain.fisc.repository;

import com.brain.fisc.domain.ContratEtablis;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the ContratEtablis entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ContratEtablisRepository extends MongoRepository<ContratEtablis, String> {}
