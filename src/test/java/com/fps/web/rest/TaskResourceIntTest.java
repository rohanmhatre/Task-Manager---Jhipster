package com.fps.web.rest;

import com.fps.FilmSolutionsApp;
import com.fps.domain.Task;
import com.fps.repository.TaskRepository;
import com.fps.service.TaskService;
import com.fps.repository.search.TaskSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fps.domain.enumeration.Status;
import com.fps.domain.enumeration.Priority;

/**
 * Test class for the TaskResource REST controller.
 *
 * @see TaskResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FilmSolutionsApp.class)
@WebAppConfiguration
@IntegrationTest
public class TaskResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final ZonedDateTime DEFAULT_DATE_ASSIGNED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DATE_ASSIGNED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DATE_ASSIGNED_STR = dateTimeFormatter.format(DEFAULT_DATE_ASSIGNED);

    private static final ZonedDateTime DEFAULT_DATE_COMPLETION = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DATE_COMPLETION = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DATE_COMPLETION_STR = dateTimeFormatter.format(DEFAULT_DATE_COMPLETION);

    private static final Status DEFAULT_STATUS = Status.SCHEDULED;
    private static final Status UPDATED_STATUS = Status.STARTED;

    private static final Priority DEFAULT_PRIORITY = Priority.HIGHEST;
    private static final Priority UPDATED_PRIORITY = Priority.HIGH;

    @Inject
    private TaskRepository taskRepository;

    @Inject
    private TaskService taskService;

    @Inject
    private TaskSearchRepository taskSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTaskMockMvc;

    private Task task;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TaskResource taskResource = new TaskResource();
        ReflectionTestUtils.setField(taskResource, "taskService", taskService);
        this.restTaskMockMvc = MockMvcBuilders.standaloneSetup(taskResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        taskSearchRepository.deleteAll();
        task = new Task();
        task.setName(DEFAULT_NAME);
        task.setDescription(DEFAULT_DESCRIPTION);
        task.setDate_assigned(DEFAULT_DATE_ASSIGNED);
        task.setDate_completion(DEFAULT_DATE_COMPLETION);
        task.setStatus(DEFAULT_STATUS);
        task.setPriority(DEFAULT_PRIORITY);
    }

    @Test
    @Transactional
    public void createTask() throws Exception {
        int databaseSizeBeforeCreate = taskRepository.findAll().size();

        // Create the Task

        restTaskMockMvc.perform(post("/api/tasks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(task)))
                .andExpect(status().isCreated());

        // Validate the Task in the database
        List<Task> tasks = taskRepository.findAll();
        assertThat(tasks).hasSize(databaseSizeBeforeCreate + 1);
        Task testTask = tasks.get(tasks.size() - 1);
        assertThat(testTask.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTask.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTask.getDate_assigned()).isEqualTo(DEFAULT_DATE_ASSIGNED);
        assertThat(testTask.getDate_completion()).isEqualTo(DEFAULT_DATE_COMPLETION);
        assertThat(testTask.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testTask.getPriority()).isEqualTo(DEFAULT_PRIORITY);

        // Validate the Task in ElasticSearch
        Task taskEs = taskSearchRepository.findOne(testTask.getId());
        assertThat(taskEs).isEqualToComparingFieldByField(testTask);
    }

    @Test
    @Transactional
    public void getAllTasks() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the tasks
        restTaskMockMvc.perform(get("/api/tasks?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].date_assigned").value(hasItem(DEFAULT_DATE_ASSIGNED_STR)))
                .andExpect(jsonPath("$.[*].date_completion").value(hasItem(DEFAULT_DATE_COMPLETION_STR)))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.toString())));
    }

    @Test
    @Transactional
    public void getTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get the task
        restTaskMockMvc.perform(get("/api/tasks/{id}", task.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(task.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.date_assigned").value(DEFAULT_DATE_ASSIGNED_STR))
            .andExpect(jsonPath("$.date_completion").value(DEFAULT_DATE_COMPLETION_STR))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTask() throws Exception {
        // Get the task
        restTaskMockMvc.perform(get("/api/tasks/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTask() throws Exception {
        // Initialize the database
        taskService.save(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task
        Task updatedTask = new Task();
        updatedTask.setId(task.getId());
        updatedTask.setName(UPDATED_NAME);
        updatedTask.setDescription(UPDATED_DESCRIPTION);
        updatedTask.setDate_assigned(UPDATED_DATE_ASSIGNED);
        updatedTask.setDate_completion(UPDATED_DATE_COMPLETION);
        updatedTask.setStatus(UPDATED_STATUS);
        updatedTask.setPriority(UPDATED_PRIORITY);

        restTaskMockMvc.perform(put("/api/tasks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTask)))
                .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> tasks = taskRepository.findAll();
        assertThat(tasks).hasSize(databaseSizeBeforeUpdate);
        Task testTask = tasks.get(tasks.size() - 1);
        assertThat(testTask.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTask.getDate_assigned()).isEqualTo(UPDATED_DATE_ASSIGNED);
        assertThat(testTask.getDate_completion()).isEqualTo(UPDATED_DATE_COMPLETION);
        assertThat(testTask.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTask.getPriority()).isEqualTo(UPDATED_PRIORITY);

        // Validate the Task in ElasticSearch
        Task taskEs = taskSearchRepository.findOne(testTask.getId());
        assertThat(taskEs).isEqualToComparingFieldByField(testTask);
    }

    @Test
    @Transactional
    public void deleteTask() throws Exception {
        // Initialize the database
        taskService.save(task);

        int databaseSizeBeforeDelete = taskRepository.findAll().size();

        // Get the task
        restTaskMockMvc.perform(delete("/api/tasks/{id}", task.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean taskExistsInEs = taskSearchRepository.exists(task.getId());
        assertThat(taskExistsInEs).isFalse();

        // Validate the database is empty
        List<Task> tasks = taskRepository.findAll();
        assertThat(tasks).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTask() throws Exception {
        // Initialize the database
        taskService.save(task);

        // Search the task
        restTaskMockMvc.perform(get("/api/_search/tasks?query=id:" + task.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].date_assigned").value(hasItem(DEFAULT_DATE_ASSIGNED_STR)))
            .andExpect(jsonPath("$.[*].date_completion").value(hasItem(DEFAULT_DATE_COMPLETION_STR)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.toString())));
    }
}
