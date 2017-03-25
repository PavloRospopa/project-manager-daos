package com.gmail.at.rospopa.pavlo.projectmanager.domain;

import com.gmail.at.rospopa.pavlo.projectmanager.util.Prototype;

public class Customer extends User {
    String company;

    public Customer() {
    }

    public Customer(Long id) {
        super(id);
    }

    public Customer(Long id, String name, String surname, String username, String password, String email,
                    Role role, String company) {
        super(id, name, surname, username, password, email, role);
        this.company = company;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", company='" + company + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        if (!super.equals(o)) return false;

        Customer customer = (Customer) o;

        return company != null ? company.equals(customer.company) : customer.company == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (company != null ? company.hashCode() : 0);
        return result;
    }

    @Override
    public Prototype clone() {
        return new Customer(getId(), getName(), getSurname(), getUsername(), getPassword(),
                getEmail(), getRole(), getCompany());
    }
}
