package com.az.schoolofnet.Webflux.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.az.schoolofnet.Webflux.model.Todo;

@Repository
public interface TodoRepository extends CrudRepository<Todo, Long> {

}
