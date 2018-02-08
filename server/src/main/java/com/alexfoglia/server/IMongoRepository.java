package com.alexfoglia.server;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface IMongoRepository extends MongoRepository<DatabaseGrid,String> {

}
