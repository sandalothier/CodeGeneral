package com.brain.fisc.repository;

import com.brain.fisc.domain.Poste;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Poste entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PosteRepository extends MongoRepository<Poste, String> {}
