package com.az.schoolofnet.Webflux.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.az.schoolofnet.Webflux.model.Todo;
import com.az.schoolofnet.Webflux.repository.TodoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@RestController
@RequestMapping("/todos")
public class TodoController {

	@Autowired
	private  TodoRepository todoRepository;
	
	@Autowired
	private TransactionTemplate transactionTemplate;
	
	@Autowired
	@Qualifier("jdbcScheduler")
	private Scheduler jdbcScheduler;

	public TodoController(TodoRepository todoRepository, TransactionTemplate transactionTemplate, Scheduler jdbcScheduler) {
		this.todoRepository = todoRepository;
		this.transactionTemplate = transactionTemplate;
		this.jdbcScheduler = jdbcScheduler;
	}
	
	//just in by name| Apenas pelo nome
	@GetMapping("/{id}")
	@ResponseBody
	public Mono<Todo> finById(@PathVariable("id") Long id){
		return Mono.justOrEmpty(this.todoRepository.findById(id));
	}
	
	@GetMapping
	@ResponseBody
	public Flux<Todo>findAll(){
		return Flux.defer(()-> Flux.fromIterable(this.todoRepository.findAll())).subscribeOn(jdbcScheduler);
	}

	@PostMapping
	public Mono<Todo> save(@Valid @RequestBody Todo todo) {
		Mono<Todo> op = Mono.fromCallable(()-> this.transactionTemplate.execute(action->{
			Todo newTodo = this.todoRepository.save(todo);
			return newTodo;
		}));
		
		return op;
	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> remove(@PathVariable("id") Long id){
		var exists = this.todoRepository.findById(id);
		
		if(exists.isPresent()) {
			return Mono.fromCallable(()-> this.transactionTemplate.execute(action->{
				this.todoRepository.deleteById(id);
				return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
			})).subscribeOn(jdbcScheduler);
		}
		return null;
		
	}
	
	
	
	
	
	
}
