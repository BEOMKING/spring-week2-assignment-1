package com.codesoom.assignment;

import com.codesoom.assignment.application.TaskService;
import com.codesoom.assignment.domain.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"InnerClassMayBeStatic", "NonAsciiCharacters"})
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DisplayName("TaskController 클래스")
class TaskControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TaskService taskService;

    Task createTask(String title) throws Exception {
        Task task = new Task(0, title);
        MvcResult mvcResult = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andReturn();
        return objectMapper.readValue(mvcResult.getRequest().getContentAsString(), Task.class);
    }

    @AfterEach
    void clearData() {
        taskService.clearTasks();
    }

    @Nested
    @DisplayName("GET 메소드 리퀘스트는")
    class Describe_GET {
        @Nested
        @DisplayName("tasks가 비어있다면")
        class Context_empty_task {
            @Test
            @DisplayName("빈 배열을 리턴한다")
            void it_returns_empty_array() throws Exception {
                mockMvc.perform(get("/tasks"))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[]"));
            }
        }

        @Nested
        @DisplayName("존재하지 않는 task id를 요청한다면")
        class Context_invalid_task_id {
            @Test
            @DisplayName("404 에러를 리턴한다")
            void it_returns_404_error() throws Exception {
                mockMvc.perform(get("/tasks/0"))
                        .andExpect(status().isNotFound());
            }
        }

        @Nested
        @DisplayName("존재하는 task id를 요청한다면")
        class Context_exist_id {
            @Test
            @DisplayName("task 객체를 리턴한다")
            void it_returns_task_object() throws Exception {
                Task createdTask = createTask("Get Sleep");
                mockMvc.perform(get("/tasks/0"))
                        .andExpect(status().isOk())
                        .andExpect(content().json(objectMapper.writeValueAsString(createdTask)));
            }
        }
    }

    @Nested
    @DisplayName("POST 메소드 레퀘스트")
    class Describe_POST {
        @Nested
        @DisplayName("task를 생성한다면")
        class Context_create_task {
            @Test
            @DisplayName("생성한 task를 리턴한다")
            void it_return_created_task() throws Exception {
                Task expectedTask = new Task(0, "Get Sleep");
                mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedTask)))
                        .andExpect(status().isCreated());
            }
        }
    }

    @Nested
    @DisplayName("PUT/PATCH 메소드 리퀘스트")
    class Describe_PUT {
        @Nested
        @DisplayName("존재하는 task id를 수정요청 한다면")
        class Context_exist_id {
            @Test
            @DisplayName("수정된 task 객체를 리턴한다")
            void it_returns_updated_task() throws Exception {
                Task expectedTask = createTask("Get Sleep");
                expectedTask.setTitle("Do Study");
                mockMvc.perform(patch("/tasks/" + expectedTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedTask)))
                        .andExpect(status().isOk())
                        .andExpect(content().json(objectMapper.writeValueAsString(expectedTask)));

                expectedTask.setTitle("Earning Money..");
                mockMvc.perform(put("/tasks/" + expectedTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedTask)))
                        .andExpect(status().isOk())
                        .andExpect(content().json(objectMapper.writeValueAsString(expectedTask)));
            }
        }

        @Nested
        @DisplayName("존재하지 않는 task id를 수정요청 한다면")
        class Context_not_exist_id {
            @Test
            @DisplayName("404 에러를 리턴한다")
            void it_returns_404_error() throws Exception {
                Task expectedTask = new Task(0, "Do Study");
                mockMvc.perform(patch("/tasks/" + expectedTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedTask)))
                        .andExpect(status().isNotFound());
            }
        }
    }

    @Nested
    @DisplayName("DELETE 리퀘스트 메소드는")
    class Describe_DELETE {
        @Nested
        @DisplayName("존재하는 task id를 삭제요청을 한다면")
        class Context_exist_id {
            @Test
            @DisplayName("201 코드를 리턴한다")
            void it_returns_201_code() throws Exception {
                Task expectedTask = createTask("Get Sleep");
                mockMvc.perform(delete("/tasks/" + expectedTask.getId()))
                        .andExpect(status().isNoContent());
            }
        }

        @Nested
        @DisplayName("존재하지 않는 task id를 삭제요청을 한다면")
        class Context_not_exist_id {
            @Test
            @DisplayName("404 에러를 리턴한다")
            void it_returns_404_error() throws Exception {
                Task expectedTask = new Task(0, "Get Sleep");
                mockMvc.perform(delete("/tasks/" + expectedTask.getId()))
                        .andExpect(status().isNotFound());
            }
        }
    }
}

