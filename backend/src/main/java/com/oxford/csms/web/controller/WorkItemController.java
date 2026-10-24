package com.oxford.csms.web.controller;

import com.oxford.csms.common.ApiResponse;
import com.oxford.csms.domain.admin.model.WorkItem;
import com.oxford.csms.domain.admin.service.WorkItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/work-items")
public class WorkItemController {

    private final WorkItemService workItemService;

    public WorkItemController(WorkItemService workItemService) {
        this.workItemService = workItemService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkItem>>> getWorkItems(
            @RequestParam(required = false) String username,
            Authentication authentication) {
        if (username != null && !username.isBlank()) {
            return ResponseEntity.ok(ApiResponse.ok(workItemService.getWorkItemsForUser(username)));
        }
        if (authentication != null && authentication.getName() != null) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGEMENT"));
            if (!isAdmin) {
                return ResponseEntity.ok(ApiResponse.ok(workItemService.getWorkItemsForUser(authentication.getName())));
            }
        }
        return ResponseEntity.ok(ApiResponse.ok(workItemService.getAllWorkItems()));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<ApiResponse<List<WorkItem>>> getByUser(@PathVariable String username) {
        return ResponseEntity.ok(ApiResponse.ok(workItemService.getWorkItemsForUser(username)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workItemService.getWorkItemById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WorkItem>> createWorkItem(
            @RequestBody WorkItem item,
            Authentication authentication) {
        if ((item.getAssignedByUsername() == null || item.getAssignedByUsername().isBlank()) && authentication != null) {
            item.setAssignedByUsername(authentication.getName());
        }
        return ResponseEntity.ok(ApiResponse.ok("Work item created", workItemService.createWorkItem(item)));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<WorkItem>> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Work item status updated", workItemService.toggleStatus(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkItem>> updateWorkItem(
            @PathVariable Long id,
            @RequestBody WorkItem details) {
        return ResponseEntity.ok(ApiResponse.ok("Work item updated", workItemService.updateWorkItem(id, details)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteWorkItem(@PathVariable Long id) {
        workItemService.deleteWorkItem(id);
        return ResponseEntity.ok(ApiResponse.ok("Work item deleted", Map.of("id", id, "deleted", true)));
    }
}
