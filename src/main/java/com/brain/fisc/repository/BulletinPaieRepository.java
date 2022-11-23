package com.brain.fisc.repository;

import com.brain.fisc.domain.BulletinPaie;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the BulletinPaie entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BulletinPaieRepository extends MongoRepository<BulletinPaie, String> {}
