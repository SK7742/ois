package com.oxford.csms.domain.admin.service;

import com.oxford.csms.common.ResourceNotFoundException;
import com.oxford.csms.domain.admin.model.WorkItem;
import com.oxford.csms.domain.admin.repository.WorkItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WorkItemServiceImpl implements WorkItemService {

    private final WorkItemRepository workItemRepository;

    public WorkItemServiceImpl(WorkItemRepository workItemRepository) {
        this.workItemRepository = workItemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkItem> getWorkItemsForUser(String username) {
        return workItemRepository.findByAssignedToUsernameIgnoreCaseOrderByCreatedAtDesc(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkItem> getAllWorkItems() {
        return workItemRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkItem getWorkItemById(Long id) {
        return workItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work item not found with id: " + id));
    }

    @Override
    public WorkItem createWorkItem(WorkItem item) {
        if (item.getStatus() == null) {
            item.setStatus(WorkItem.Status.PENDING);
        }
        return workItemRepository.save(item);
    }

    @Override
    public WorkItem updateWorkItem(Long id, WorkItem details) {
        WorkItem existing = getWorkItemById(id);
        existing.setTitle(details.getTitle());
        existing.setDescription(details.getDescription());
        if (details.getItemType() != null) existing.setItemType(details.getItemType());
        if (details.getPriority() != null) existing.setPriority(details.getPriority());
        if (details.getStatus() != null) existing.setStatus(details.getStatus());
        if (details.getDueDate() != null) existing.setDueDate(details.getDueDate());
        return workItemRepository.save(existing);
    }

    @Override
    public WorkItem toggleStatus(Long id) {
        WorkItem existing = getWorkItemById(id);
        if (existing.getStatus() == WorkItem.Status.COMPLETED) {
            existing.setStatus(WorkItem.Status.PENDING);
        } else {
            existing.setStatus(WorkItem.Status.COMPLETED);
        }
        return workItemRepository.save(existing);
    }

    @Override
    public void deleteWorkItem(Long id) {
        WorkItem existing = getWorkItemById(id);
        workItemRepository.delete(existing);
    }
}
