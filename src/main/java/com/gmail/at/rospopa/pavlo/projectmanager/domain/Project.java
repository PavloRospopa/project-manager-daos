package com.gmail.at.rospopa.pavlo.projectmanager.domain;

import java.sql.Date;
import java.util.Objects;

public class Project extends Entity {
    private String name;
    private Date startDate;
    private Date completionDate;
    private Date expectedCompletionDate;
    private Customer customer;
    private ProjectManager projectManager;

    public Project() {
    }

    public Project(Long id) {
        super(id);
    }

    public Project(Long id, String name, Date startDate, Date completionDate, Date expectedCompletionDate,
                   Customer customer, ProjectManager projectManager) {
        super(id);
        this.name = name;
        this.startDate = startDate;
        this.completionDate = completionDate;
        this.expectedCompletionDate = expectedCompletionDate;
        this.customer = customer;
        this.projectManager = projectManager;
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public ProjectManager getProjectManager() {
        return projectManager;
    }

    public void setProjectManager(ProjectManager projectManager) {
        this.projectManager = projectManager;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", customer=" + customer +
                ", projectManager=" + projectManager +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;

        Project project = (Project) o;

        if (getId() != null ? !getId().equals(project.getId()) : project.getId() != null) return false;
        if (name != null ? !name.equals(project.name) : project.name != null) return false;
        if (startDate != null ? !startDate.equals(project.startDate) : project.startDate != null) return false;
        if (completionDate != null ? !completionDate.equals(project.completionDate) : project.completionDate != null)
            return false;
        if (expectedCompletionDate != null ? !expectedCompletionDate.equals(project.expectedCompletionDate)
                : project.expectedCompletionDate != null) return false;
        if (customer != null ? (customer.getId() != null ? !customer.getId().equals(project.customer.getId())
                : project.customer.getId() != null) : project.customer != null) return false;
        return projectManager != null ? (projectManager.getId() != null ? projectManager.getId().equals(project.projectManager.getId())
                : project.projectManager.getId() == null) : project.projectManager == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (completionDate != null ? completionDate.hashCode() : 0);
        result = 31 * result + (expectedCompletionDate != null ? expectedCompletionDate.hashCode() : 0);
        result = 31 * result + (customer != null ? (customer.getId() != null ? customer.getId().hashCode() : 0) : 0);
        result = 31 * result + (projectManager != null ? (projectManager.getId() != null ?
                projectManager.getId().hashCode() : 0) : 0);
        return result;
    }
}
