package com.fps.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fps.domain.Task;
import com.fps.service.MailService;
import com.fps.service.TaskService;
import com.fps.web.rest.util.HeaderUtil;
import com.fps.web.rest.util.PaginationUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Task.
 */
@RestController
@RequestMapping("/api")
public class TaskResource {

	private final Logger log = LoggerFactory.getLogger(TaskResource.class);

	@Inject
	private TaskService taskService;

	@Inject
	private MailService mail;

	/**
	 * POST /tasks : Create a new task.
	 *
	 * @param task
	 *            the task to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new task, or with status 400 (Bad Request) if the task has
	 *         already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/tasks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Task> createTask(@RequestBody Task task)
			throws URISyntaxException {
		log.debug("REST request to save Task : {}", task);
		if (task.getId() != null) {
			return ResponseEntity
					.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("task", "idexists",
									"A new task cannot already have an ID"))
					.body(null);
		}
		Task result = taskService.save(task);
		return ResponseEntity
				.created(new URI("/api/tasks/" + result.getId()))
				.headers(
						HeaderUtil.createEntityCreationAlert("task", result
								.getId().toString())).body(result);
	}

	/**
	 * PUT /tasks : Updates an existing task.
	 *
	 * @param task
	 *            the task to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         task, or with status 400 (Bad Request) if the task is not valid,
	 *         or with status 500 (Internal Server Error) if the task couldnt be
	 *         updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/tasks", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Task> updateTask(@RequestBody Task task)
			throws URISyntaxException {
		log.debug("REST request to update Task : {}", task);
		if (task.getId() == null) {
			return createTask(task);
		}
		Task result = taskService.save(task);
		return ResponseEntity
				.ok()
				.headers(
						HeaderUtil.createEntityUpdateAlert("task", task.getId()
								.toString())).body(result);
	}

	/**
	 * GET /tasks : get all the tasks.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of tasks in
	 *         body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/tasks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Task>> getAllTasks(Pageable pageable)
			throws URISyntaxException {
		log.debug("REST request to get a page of Tasks");
		Page<Task> page = taskService.findAll(pageable);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
				page, "/api/tasks");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	/**
	 * GET /tasks/:id : get the "id" task.
	 *
	 * @param id
	 *            the id of the task to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the task,
	 *         or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/tasks/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Task> getTask(@PathVariable Long id) {
		log.debug("REST request to get Task : {}", id);
		Task task = taskService.findOne(id);
		return Optional.ofNullable(task)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /tasks/:id : delete the "id" task.
	 *
	 * @param id
	 *            the id of the task to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/tasks/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
		log.debug("REST request to delete Task : {}", id);
		taskService.delete(id);
		return ResponseEntity
				.ok()
				.headers(
						HeaderUtil.createEntityDeletionAlert("task",
								id.toString())).build();
	}

	/**
	 * SEARCH /_search/tasks?query=:query : search for the task corresponding to
	 * the query.
	 *
	 * @param query
	 *            the query of the task search
	 * @return the result of the search
	 */
	@RequestMapping(value = "/_search/tasks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Task>> searchTasks(@RequestParam String query,
			Pageable pageable) throws URISyntaxException {
		log.debug("REST request to search for a page of Tasks for query {}",
				query);
		Page<Task> page = taskService.search(query, pageable);
		HttpHeaders headers = PaginationUtil
				.generateSearchPaginationHttpHeaders(query, page,
						"/api/_search/tasks");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/task/notify/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void notifyTask(@PathVariable Long id) {
		log.debug("Request to notify Task to : {}", id);
		// code for notify
	}

	@RequestMapping(value = "/task/notifyall", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void notifyallTask() {
		try {
			log.debug("Request to notify all Scheduled and Incomplete Tasks");
			// code for notifyAll

			mail.sendEmail("mhatrerohan1990@gmail.com", "hi", "hello", true,
					true);
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
