package hexlet.code.specification;

import hexlet.code.model.Task;


import org.springframework.data.jpa.domain.Specification;

public class TaskSpecifications {

    public static Specification<Task> titleContains(String titleCont) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + titleCont.toLowerCase() + "%");
    }

    public static Specification<Task> assigneeEquals(Long assigneeId) {
        return (root, query, cb) -> cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    public static Specification<Task> statusEquals(String statusSlug) {
        return (root, query, cb) -> cb.equal(root.get("taskStatus").get("slug"), statusSlug);
    }
}
