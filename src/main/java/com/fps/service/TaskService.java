package com.fps.service;

import com.fps.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Task.
 */
public interface TaskService {

    /**
     * Save a task.
     * 
     * @param task the entity to save
     * @return the persisted entity
     */
    Task save(Task task);

    /**
     *  Get all the tasks.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Task> findAll(Pageable pageable);

    /**
     *  Get the "id" task.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    Task findOne(Long id);

    /**
     *  Delete the "id" task.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the task corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Task> search(String query, Pageable pageable);


    void notify(Long id);

    void notifyall();
}
