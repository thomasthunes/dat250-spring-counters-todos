package no.hvl.dat250.rest.todos;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Rest-Endpoint for todos.
 */
@RestController
@RequestMapping("/todos")
public class TodoController {
  public static final String TODO_WITH_THE_ID_X_NOT_FOUND = "Todo with the id %s not found!";
  private final Gson gson = new Gson();
  private List<Todo> todos = new ArrayList<>();


  @PostMapping
  public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
    try {
      todo.setId(UUID.randomUUID().getMostSignificantBits());
      todos.add(todo);
      return new ResponseEntity<>(todo, HttpStatus.CREATED);
    } catch (Exception e) {
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
      Todo todo = findTodoById(id);

      if (todo != null) {
        return new ResponseEntity<>(todo, HttpStatus.OK);
      } else {
        String errorMessage = String.format(TODO_WITH_THE_ID_X_NOT_FOUND, id);
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping
  public ResponseEntity<List<Todo>> getAllTodos() {
    try {
      if (!todos.isEmpty()) {
        return new ResponseEntity<>(todos, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @PutMapping("/{id}")
  public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo updatedTodo) {
    try {
      Todo existingTodo = findTodoById(id);

      if (existingTodo != null) {
        existingTodo.setSummary(updatedTodo.getSummary());
        existingTodo.setDescription(updatedTodo.getDescription());

        return new ResponseEntity<>(existingTodo, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private boolean deleteTodoById(Long id) {
    Todo todoToDelete = null;
    for (Todo todo : todos) {
      if (todo.getId() != null && todo.getId().equals(id)) {
        todoToDelete = todo;
        break;
      }
    }

    if (todoToDelete != null) {
      todos.remove(todoToDelete);
      return true;
    } else {
      return false;
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteTodo(@PathVariable Long id) {
    try {
      boolean deleted = deleteTodoById(id);

      if (deleted) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      } else {
        String errorMessage = String.format(TODO_WITH_THE_ID_X_NOT_FOUND, id);
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
