package no.hvl.dat250.rest.todos;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Rest-Endpoint for todos.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class TodoController {
  private List<Todo> todos = new ArrayList<>();
  Long count = 0L;
  public static final String TODO_WITH_THE_ID_X_NOT_FOUND = "Todo with the id %s not found!";



  @PostMapping("/todos")
  public Todo createTodo(@RequestBody Todo todo){
    this.count += 1L;
    todo.setId(count);
    todos.add(todo);
    return todo;
  }

  @GetMapping("/todos/{id}")
  public ResponseEntity getTodo(@PathVariable Long id) throws IOException {
    for (Todo todo : todos) {
      if (todo.getId() == id) {
        return ResponseEntity.ok(todo);
      }
    }
    String msg = String.format(TODO_WITH_THE_ID_X_NOT_FOUND, id);
    return ResponseEntity.ok(msg);
  }



  @PutMapping("/{id}")
  public Todo updateTodo(@PathVariable Long id, @RequestBody Todo updatedTodo){
    Todo todo = findTodoById(id);
    todo.setSummary(updatedTodo.getSummary());
    todo.setDescription(updatedTodo.getDescription());
    return todo;
  }

  public Todo findTodoById(Long id){
    for (Todo todo : todos){
      Long todo_id = todo.getId();
      if (todo_id == id){
        return todo;
      }
    }
    return null;
  }

@GetMapping("/todos")
  public List<Todo> getAllTodos(){
    return todos;
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

  @DeleteMapping("/todos/{id}")
  public ResponseEntity deleteTodo(@PathVariable String id) {
    boolean deleted = deleteTodoById(Long.parseLong(id));

    if (deleted) {
      return ResponseEntity.noContent().build();
    }
    String msg = String.format(TODO_WITH_THE_ID_X_NOT_FOUND, id);
    return ResponseEntity.ok(msg);
  }
}
