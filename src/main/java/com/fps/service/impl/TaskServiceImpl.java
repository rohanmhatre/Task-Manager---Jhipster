package com.fps.service.impl;

import com.fps.service.TaskService;
import com.fps.domain.Task;
import com.fps.repository.TaskRepository;
import com.fps.repository.search.TaskSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Task.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService{

    private final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);
    
    @Inject
    private TaskRepository taskRepository;
    
    @Inject
    private TaskSearchRepository taskSearchRepository;
    
    /**
     * Save a task.
     * 
     * @param task the entity to save
     * @return the persisted entity
     */
    public Task save(Task task) {
        log.debug("Request to save Task : {}", task);
        Task result = taskRepository.save(task);
        taskSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the tasks.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Task> findAll(Pageable pageable) {
        log.debug("Request to get all Tasks");
        Page<Task> result = taskRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one task by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Task findOne(Long id) {
        log.debug("Request to get Task : {}", id);
        Task task = taskRepository.findOne(id);
        return task;
    }

    /**
     *  Delete the  task by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Task : {}", id);
        taskRepository.delete(id);
        taskSearchRepository.delete(id);
    }

    /**
     * Search for the task corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Task> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Tasks for query {}", query);
        return taskSearchRepository.search(queryStringQuery(query), pageable);
    }

    public void notify(Long id){
        log.debug("Request to notify Task to : {}" , id);
        // code for notify
    }

    public void notifyall(){
        log.debug("Request to notify all Scheduled and Incomplete Tasks");
        // code for notifyAll
    }
}
