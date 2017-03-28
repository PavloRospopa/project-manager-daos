package com.gmail.at.rospopa.pavlo.projectmanager.domain;

import com.gmail.at.rospopa.pavlo.projectmanager.util.Prototype;

import java.sql.Date;

public class Sprint extends Entity {
    private String name;
    private Date startDate;
    private Date completionDate;
    private Date expectedCompletionDate;
    private Sprint previousSprint;
    private Project project;

    public Sprint() {
    }

    public Sprint(Long id) {
        super(id);
    }

    public Sprint(Long id, String name, Date startDate, Date completionDate, Date expectedCompletionDate,
                  Sprint previousSprint, Project project) {
        super(id);
        this.name = name;
        this.startDate = startDate;
        this.completionDate = completionDate;
        this.expectedCompletionDate = expectedCompletionDate;
        this.previousSprint = previousSprint;
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public Date getExpectedCompletionDate() {
        return expectedCompletionDate;
    }

    public void setExpectedCompletionDate(Date expectedCompletionDate) {
        this.expectedCompletionDate = expectedCompletionDate;
    }

    public Sprint getPreviousSprint() {
        return previousSprint;
    }

    public void setPreviousSprint(Sprint previousSprint) {
        this.previousSprint = previousSprint;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return "Sprint{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", project id='" + project.getId() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sprint)) return false;

        Sprint sprint = (Sprint) o;

        if (getId() != null ? !getId().equals(sprint.getId()) : sprint.getId() != null) return false;
        if (name != null ? !name.equals(sprint.name) : sprint.name != null) return false;
        if (startDate != null ? !startDate.equals(sprint.startDate) : sprint.startDate != null) return false;
        if (completionDate != null ? !completionDate.equals(sprint.completionDate) : sprint.completionDate != null)
            return false;
        if (expectedCompletionDate != null ? !expectedCompletionDate.equals(sprint.expectedCompletionDate) : sprint.expectedCompletionDate != null)
            return false;
        if (previousSprint != null ? (previousSprint.getId() != null ? !previousSprint.getId().equals(sprint.previousSprint.getId())
                : sprint.previousSprint.getId() != null) : sprint.previousSprint != null) return false;
        return project != null ? (project.getId() != null ? project.getId().equals(sprint.project.getId())
                : sprint.project.getId() == null) : sprint.project == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (completionDate != null ? completionDate.hashCode() : 0);
        result = 31 * result + (expectedCompletionDate != null ? expectedCompletionDate.hashCode() : 0);
        result = 31 * result + (previousSprint != null ? (previousSprint.getId() != null ? previousSprint.getId().hashCode() : 0) : 0);
        result = 31 * result + (project != null ? (project.getId() != null ? project.getId().hashCode() : 0) : 0);
        return result;
    }

    @Override
    public Prototype clone() {
        Sprint previousSprint = getPreviousSprint() != null ?
                new Sprint(getPreviousSprint().getId()) : null;
        Project project = getProject() != null ?
                new Project(getProject().getId()) : null;

        return new Sprint(getId(), getName(), getStartDate(), getCompletionDate(), getExpectedCompletionDate(),
                previousSprint, project);
    }
}
