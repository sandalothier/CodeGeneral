package com.brain.fisc.repository;

import com.brain.fisc.domain.DateOperation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the DateOperation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DateOperationRepository extends MongoRepository<DateOperation, String> {}
