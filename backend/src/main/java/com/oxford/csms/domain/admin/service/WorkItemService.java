package com.oxford.csms.domain.admin.service;

import com.oxford.csms.domain.admin.model.WorkItem;
import java.util.List;

public interface WorkItemService {

    List<WorkItem> getWorkItemsForUser(String username);

    List<WorkItem> getAllWorkItems();

    WorkItem getWorkItemById(Long id);

    WorkItem createWorkItem(WorkItem item);

    WorkItem updateWorkItem(Long id, WorkItem details);

    WorkItem toggleStatus(Long id);

    void deleteWorkItem(Long id);
}
