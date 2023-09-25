package no.hvl.dat250.rest.todos;
import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.util.*;

/**
 * Rest-Endpoint for todos.
 */
@RestController
@RequestMapping("/todos")
public class TodoController {
  //@LocalServerPort
  private int port = 8080;
  public static final String TODO_WITH_THE_ID_X_NOT_FOUND = "Todo with the id %s not found!";
  private final Gson gson = new Gson();
  private final OkHttpClient client = new OkHttpClient();
  private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  private List<Todo> todos = new ArrayList<>();

  private String getBaseURL() {
    return "http://localhost:" + port + "/";
  }

  private String doRequest(Request request) {
    try (Response response = client.newCall(request).execute()) {
      return Objects.requireNonNull(response.body()).string();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String doPostRequest(Todo todo) {
    // Prepare request and add the body
    okhttp3.RequestBody body = okhttp3.RequestBody.create(gson.toJson(todo), JSON);

    Request request = new Request.Builder()
            .url(getBaseURL() + "todos")
            .post(body)
            .build();

    return doRequest(request);
  }

  // Define methods to handle HTTP requests here
  @PostMapping
  public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
    // Implement logic to create a new Todo item and return it
    // Execute post request
    try {
      todo.setId(UUID.randomUUID().getMostSignificantBits()); // Replace this with your logic

      // You can also save the Todo object to an in-memory list or any data structure
      // For demonstration purposes, we'll assume you have a List<Todo> to store Todos
      todos.add(todo); // Add the new Todo to your list

      // Return a ResponseEntity with the created Todo item and HTTP status 201 (Created)
      return new ResponseEntity<>(todo, HttpStatus.CREATED);
    } catch (Exception e) {
      // Handle any exceptions that may occur during the creation process
      // Return an appropriate error response if needed
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  public Todo findTodoById(Long id){
    for (Todo todo : todos){
      Long todo_id = todo.getId();
      if (todo_id.equals(id)){
        return todo;
      }
    }
    return null;
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getTodoById(@PathVariable Long id) {
    try {
      // Implement logic to retrieve a Todo item by its ID
      Todo todo = findTodoById(id); // Replace this with your actual logic

      // Check if the Todo item was found
      if (todo != null) {
        // Return a ResponseEntity with the Todo item and HTTP status 200 (OK)
        return new ResponseEntity<>(todo, HttpStatus.OK);
      } else {
        // Return an HTTP status of 404 (Not Found) if the Todo item is not found
        String errorMessage = String.format(TODO_WITH_THE_ID_X_NOT_FOUND, id);
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      // Handle any exceptions that may occur during the retrieval process
      // Return an appropriate error response if needed
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping
  public ResponseEntity<List<Todo>> getAllTodos() {
    try {
      // Implement logic to retrieve all Todo items
      //List<Todo> todos = findAllTodos(); // Replace this with your actual logic

      // Check if any Todo items are found
      if (!todos.isEmpty()) {
        // Return a ResponseEntity with the list of Todo items and HTTP status 200 (OK)
        return new ResponseEntity<>(todos, HttpStatus.OK);
      } else {
        // Return an empty list with HTTP status 200 (OK) if no Todo items are found
        return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
      }
    } catch (Exception e) {
      // Handle any exceptions that may occur during the retrieval process
      // Return an appropriate error response if needed
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @PutMapping("/{id}")
  public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo updatedTodo) {
    try {
      // Implement logic to update the Todo item by its ID
      Todo existingTodo = findTodoById(id); // Replace this with your actual logic

      // Check if the Todo item with the given ID exists
      if (existingTodo != null) {
        // Update the existing Todo item with the values from the updatedTodo
        existingTodo.setSummary(updatedTodo.getSummary());
        existingTodo.setDescription(updatedTodo.getDescription());

        // Return a ResponseEntity with the updated Todo item and HTTP status 200 (OK)
        return new ResponseEntity<>(existingTodo, HttpStatus.OK);
      } else {
        // Return an HTTP status of 404 (Not Found) if the Todo item is not found
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      // Handle any exceptions that may occur during the update process
      // Return an appropriate error response if needed
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Implement a method to delete a Todo item by its ID
  private boolean deleteTodoById(Long id) {
    // Find the Todo item with the specified ID
    Todo todoToDelete = null;
    for (Todo todo : todos) {
      if (todo.getId() != null && todo.getId().equals(id)) {
        todoToDelete = todo;
        break;
      }
    }

    // If the Todo item with the specified ID is found, remove it
    if (todoToDelete != null) {
      todos.remove(todoToDelete);
      return true; // Indicates successful deletion
    } else {
      return false; // Indicates that the Todo item with the given ID was not found
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteTodo(@PathVariable Long id) {
    try {
      // Implement logic to delete the Todo item by its ID
      boolean deleted = deleteTodoById(id); // Replace this with your actual logic

      // Check if the Todo item with the given ID was deleted
      if (deleted) {
        // Return an HTTP status of 204 (No Content) to indicate a successful deletion
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      } else {
        // Return an HTTP status of 404 (Not Found) if the Todo item is not found
        String errorMessage = String.format(TODO_WITH_THE_ID_X_NOT_FOUND, id);
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      // Handle any exceptions that may occur during the deletion process
      // Return an appropriate error response if needed
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
