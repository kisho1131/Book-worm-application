package com.bookworm.user;

import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Slice;

import java.awt.print.Pageable;

public interface BookByUserRepository extends CassandraRepository<BooksByUser, String> {

    Slice<BooksByUser> findAllById(String id, CassandraPageRequest pageable);
}
