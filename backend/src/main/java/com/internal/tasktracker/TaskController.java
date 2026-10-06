package com.internal.tasktracker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/api/tasks")
    public ResponseEntity<?> searchTasks(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {

        // Normalize query input
        String query = q == null ? "" : q.trim();
        String searchTerm = "%" + query.toLowerCase() + "%";

        // Parse status filter
        String normalizedStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                normalizedStatus = TaskStatus.valueOf(status.toUpperCase(Locale.ROOT)).name();
            } catch (IllegalArgumentException ex) {
                return ResponseEntity.badRequest().body("status must be OPEN, IN_PROGRESS, or DONE");
            }
        }

        if (page < 1 || pageSize < 1 || pageSize > 100) {
            return ResponseEntity.badRequest().body("page must be at least 1 and pageSize must be between 1 and 100");
        }

        System.out.println("[TaskController] q=\"" + query + "\" status=" + normalizedStatus
                + " page=" + page + " pageSize=" + pageSize
                + " complexity=none");

        List<Task> allResults = taskRepository.searchTasks(searchTerm, normalizedStatus);

        long requestedStart = (long) (page - 1) * pageSize;
        int start = requestedStart >= allResults.size() ? allResults.size() : (int) requestedStart;
        int end = Math.min(start + pageSize, allResults.size());
        List<Task> pageResults = allResults.subList(start, end);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", pageResults);
        response.put("total", allResults.size());
        response.put("page", page);
        response.put("pageSize", pageSize);

        return ResponseEntity.ok(response);
    }
}
