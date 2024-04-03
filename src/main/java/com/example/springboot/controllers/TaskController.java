package com.example.springboot.controllers;

import com.example.springboot.dtos.TasksRecordDTO;
import com.example.springboot.models.TaskModel;
import com.example.springboot.repositories.TasksRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TaskController {
    @Autowired
    TasksRepository tasksRepository;

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskModel>> getAllTasks() {
        List<TaskModel> productsList = tasksRepository.findAll();

        if(!productsList.isEmpty()) {
            for (TaskModel product : productsList) {
                UUID id = product.getTaskId();
                product.add(linkTo(methodOn(TaskController.class).getOneTask(id)).withSelfRel());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(productsList);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<Object> getOneTask(@PathVariable(value="id")UUID id) {
        Optional<TaskModel> product = tasksRepository.findById(id);

        if(product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        product.get().add(linkTo(methodOn(TaskController.class).getAllTasks()).withRel("ProductsList"));

        return ResponseEntity.status(HttpStatus.OK).body(product.get());
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<Object> updateTask(
            @PathVariable(value="id")UUID id,
            @RequestBody @Valid TasksRecordDTO tasksRecordDTO
    ) {
        Optional<TaskModel> task = tasksRepository.findById(id);

        if(task.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }

        var productModel = task.get();
        BeanUtils.copyProperties(tasksRecordDTO, productModel);

        return ResponseEntity.status(HttpStatus.OK).body(tasksRepository.save(productModel));
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskModel> saveTask(@RequestBody @Valid TasksRecordDTO tasksRecordDTO) {
        var productModel = new TaskModel();
        BeanUtils.copyProperties(tasksRecordDTO, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(tasksRepository.save(productModel));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Object> deleteTask(@PathVariable(value="id")UUID id) {
        Optional<TaskModel> task = tasksRepository.findById(id);

        if(task.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }

        tasksRepository.delete(task.get());

        return ResponseEntity.status(HttpStatus.CREATED).body("Task deleted");
    }
}
