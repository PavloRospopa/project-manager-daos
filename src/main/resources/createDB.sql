CREATE TABLE object_types (
         object_type_id NUMBER(5) NOT NULL,
         parent_id      NUMBER(5),
         name           VARCHAR2(20) NOT NULL,
         description    VARCHAR2(200)
         );

CREATE TABLE objects (
         object_id      NUMBER(5),
         object_type_id NUMBER(5) NOT NULL,
         name           VARCHAR2(100),
         description    VARCHAR2(200)
         );

CREATE TABLE attributes (
         attr_id        NUMBER(5),
         attr_type_id   NUMBER(2) NOT NULL,
         is_multiple    NUMBER(1) NOT NULL,
         name           VARCHAR2(30) NOT NULL,
         description    VARCHAR2(200)
         );         

CREATE TABLE attr_types (
        attr_type_id  NUMBER(2),
        name          VARCHAR2(20) NOT NULL
);

CREATE TABLE attr_binds (
        attr_id         NUMBER(5) NOT NULL,
        object_type_id  NUMBER(5) NOT NULL,
        details         VARCHAR2(200)
);

CREATE TABLE params (
        object_id       NUMBER(5) NOT NULL,    
        attr_id         NUMBER(5) NOT NULL,
        text_value      VARCHAR2(1000),
        number_value    NUMBER,
        date_value      DATE
);

CREATE TABLE refs (
        object_id       NUMBER(5) NOT NULL,
        attr_id         NUMBER(5) NOT NULL,
        reference       NUMBER(5) NOT NULL          
);


ALTER TABLE object_types
ADD CONSTRAINT object_types_pk PRIMARY KEY (object_type_id);
ALTER TABLE object_types
ADD CONSTRAINT child_parent_fk FOREIGN KEY (parent_id) 
    REFERENCES object_types(object_type_id);
    
ALTER TABLE objects
ADD CONSTRAINT objects_pk PRIMARY KEY(object_id);
ALTER TABLE objects 
ADD CONSTRAINT object_type_fkey FOREIGN KEY(object_type_id) 
          REFERENCES object_types (object_type_id);

ALTER TABLE attr_types
ADD CONSTRAINT attr_types_pk PRIMARY KEY(attr_type_id);
                  
ALTER TABLE attributes
ADD CONSTRAINT attributes_pk PRIMARY KEY(attr_id);
ALTER TABLE attributes 
ADD CONSTRAINT attribute_fkey FOREIGN KEY(attr_type_id) 
          REFERENCES attr_types (attr_type_id);

ALTER TABLE attr_binds
ADD CONSTRAINT attr_binds_pk PRIMARY KEY(attr_id, object_type_id); 
ALTER TABLE attr_binds
ADD CONSTRAINT attr_fkey FOREIGN KEY(attr_id) REFERENCES attributes  (attr_id);                
ALTER TABLE attr_binds
ADD CONSTRAINT object_type_binds_fkey FOREIGN KEY(object_type_id) REFERENCES object_types (object_type_id);
                       
ALTER TABLE params
ADD CONSTRAINT params_pk PRIMARY KEY(object_id, attr_id); 
ALTER TABLE params
ADD CONSTRAINT params_object_id_fkey FOREIGN KEY(object_id) REFERENCES objects(object_id);
ALTER TABLE params
ADD CONSTRAINT params_attr_id_fkey FOREIGN KEY(attr_id) REFERENCES attributes (attr_id);                                                              

ALTER TABLE refs
ADD CONSTRAINT refs_pk PRIMARY KEY(object_id, attr_id, reference); 
ALTER TABLE refs
ADD CONSTRAINT refs_object_id_fkey FOREIGN KEY(object_id) REFERENCES objects(object_id);               
ALTER TABLE refs
ADD CONSTRAINT refs_attr_id_fkey FOREIGN KEY(attr_id) REFERENCES attributes(attr_id);
ALTER TABLE refs
ADD CONSTRAINT reference_fkey FOREIGN KEY(reference) REFERENCES objects(object_id);


CREATE SEQUENCE object_types_seq START WITH 1 INCREMENT BY 1 NOMAXVALUE;
CREATE SEQUENCE attributes_seq START WITH 1 INCREMENT BY 1 NOMAXVALUE;
CREATE SEQUENCE attr_types_seq START WITH 1 INCREMENT BY 1 NOMAXVALUE;
CREATE SEQUENCE objects_seq START WITH 1 INCREMENT BY 1 NOMAXVALUE;

   
CREATE OR REPLACE VIEW projects_view AS
SELECT
    projects.object_id as id, 
    name.text_value as name,
    startDate.date_value as startDate,
    completionDate.date_value as completionDate, 
    expectedCompletionDate.date_value as expectedCompletionDate,
    customers.reference as customer_id,
    project_managers.reference as project_manager_id
    from objects projects 
    join object_types on projects.object_type_id = object_types.object_type_id AND object_types.name = 'Project'
    join params name on (projects.object_id = name.object_id AND name.attr_id = 1)
    join params startDate on (projects.object_id = startDate.object_id AND startDate.attr_id = 2)  
    join params completionDate on (projects.object_id = completionDate.object_id AND completionDate.attr_id = 3)                                                                                       
    join params expectedCompletionDate on (projects.object_id = expectedCompletionDate.object_id AND expectedCompletionDate.attr_id = 4)                                                                                     
    left join refs customers on (projects.object_id = customers.object_id AND customers.attr_id = 5)  
    left join refs project_managers on (projects.object_id = project_managers.object_id AND project_managers.attr_id = 6) 
WITH READ ONLY;  

CREATE OR REPLACE VIEW sprints_view AS
SELECT 
    sprints.object_id as id, 
    name.text_value as name,
    startDate.date_value as startDate,
    completionDate.date_value as completionDate, 
    expectedCompletionDate.date_value as expectedCompletionDate,
    projects.reference as project_id,
    previousSprint.reference as previousSprint_id
    from objects sprints 
    join object_types on (sprints.object_type_id = object_types.object_type_id AND object_types.name = 'Sprint')
    join params name on (sprints.object_id = name.object_id AND name.attr_id = 1)
    join params startDate on (sprints.object_id = startDate.object_id AND startDate.attr_id = 2)  
    join params completionDate on (sprints.object_id = completionDate.object_id AND completionDate.attr_id = 3)                                                                                       
    join params expectedCompletionDate on (sprints.object_id = expectedCompletionDate.object_id AND expectedCompletionDate.attr_id = 4)                                                                                     
    join refs projects on (sprints.object_id = projects.object_id AND projects.attr_id = 26)  
    left join refs previousSprint on (sprints.object_id = previousSprint.object_id AND previousSprint.attr_id = 27)                                                                                        
WITH READ ONLY; 

CREATE OR REPLACE VIEW tasks_view AS
SELECT 
    tasks.object_id as id, 
    name.text_value as name,
    estimatedTime.number_value as estimatedTime,
    spentTime.number_value as spentTime, 
    requiredEmpPosition.text_value as requiredEmpPosition,
    description.text_value as description,
    status.text_value as status,
    parents.reference as parentTask_id,
    sprints.reference as sprint_id
    from objects tasks 
    join object_types on (tasks.object_type_id = object_types.object_type_id AND object_types.name = 'Task')
    join params name on (tasks.object_id = name.object_id AND name.attr_id = 1)
    join params estimatedTime on (tasks.object_id = estimatedTime.object_id AND estimatedTime.attr_id = 7)  
    join params spentTime on (tasks.object_id = spentTime.object_id AND spentTime.attr_id = 8)                                                                                       
    join params requiredEmpPosition on (tasks.object_id = requiredEmpPosition.object_id AND requiredEmpPosition.attr_id = 11) 
    join params description on (tasks.object_id = description.object_id AND description.attr_id = 12)  
    join params status on (tasks.object_id = status.object_id AND status.attr_id = 13)                                                                                        
    left join refs parents on (tasks.object_id = parents.object_id AND parents.attr_id = 9) 
    join refs sprints on (tasks.object_id = sprints.object_id AND sprints.attr_id = 10)                                                                                        
WITH READ ONLY;

CREATE OR REPLACE VIEW taskDelegations_view AS
SELECT 
    taskDelegations.object_id as id, 
    startDateTime.date_value as startDateTime,
    completionDateTime.date_value as completionDateTime, 
    status.text_value as status,
    tasks.reference as task_id,
    employees.reference as employee_id
    from objects taskDelegations 
    join object_types on (taskDelegations.object_type_id = object_types.object_type_id AND object_types.name = 'TaskDelegation')
    join params startDateTime on (taskDelegations.object_id = startDateTime.object_id AND startDateTime.attr_id = 16)  
    join params completionDateTime on (taskDelegations.object_id = completionDateTime.object_id AND completionDateTime.attr_id = 17)                                                                                        
    join params status on (taskDelegations.object_id = status.object_id AND status.attr_id = 13)                                                                                        
    join refs tasks on (taskDelegations.object_id = tasks.object_id AND tasks.attr_id = 14) 
    join refs employees on (taskDelegations.object_id = employees.object_id AND employees.attr_id = 15)                                                                                        
WITH READ ONLY; 

CREATE OR REPLACE VIEW taskTimeRequests_view AS
SELECT 
    taskTimeRequests.object_id as id, 
    newEstimatedTime.number_value as newEstimatedTime,
    status.text_value as status,
    tasks.reference as task_id
    from objects taskTimeRequests 
    join object_types on (taskTimeRequests.object_type_id = object_types.object_type_id AND object_types.name = 'TaskTimeRequest')
    join params newEstimatedTime on (taskTimeRequests.object_id = newEstimatedTime.object_id AND newEstimatedTime.attr_id = 18)                                                                                      
    join params status on (taskTimeRequests.object_id = status.object_id AND status.attr_id = 13)                                                                                        
    join refs tasks on (taskTimeRequests.object_id = tasks.object_id AND tasks.attr_id = 14)                                                                                       
WITH READ ONLY;

CREATE OR REPLACE VIEW users_view AS
SELECT 
    users.object_id as id, 
    name.text_value as name,
    surname.text_value as surname,
    username.text_value as username,
    password.text_value as password,
    email.text_value as email,
    role.text_value as role
    from objects users 
    join object_types on (users.object_type_id = object_types.object_type_id AND object_types.object_type_id in
                                                                               (SELECT object_type_id 
                                                                                FROM object_types
                                                                                START WITH name = 'User'
                                                                                CONNECT BY PRIOR object_type_id = parent_id))
    join params name on (users.object_id = name.object_id AND name.attr_id = 1)
    join params surname on (users.object_id = surname.object_id AND surname.attr_id = 19)  
    join params username on (users.object_id = username.object_id AND username.attr_id = 20)                                                                                       
    join params password on (users.object_id = password.object_id AND password.attr_id = 21) 
    join params email on (users.object_id = email.object_id AND email.attr_id = 22)  
    join params role on (users.object_id = role.object_id AND role.attr_id = 23)                                                                                                                                                                               
WITH READ ONLY;   

CREATE OR REPLACE VIEW projectManagers_view AS
SELECT users_view.id, 
       users_view.name, 
       users_view.surname, 
       users_view.username, 
       users_view.password, 
       users_view.email
    FROM users_view
    WHERE users_view.role = 'PROJECT_MANAGER'
WITH READ ONLY;   

CREATE OR REPLACE VIEW administrators_view AS
SELECT users_view.id, 
       users_view.name, 
       users_view.surname, 
       users_view.username, 
       users_view.password, 
       users_view.email
    FROM users_view
    WHERE users_view.role = 'ADMINISTRATOR'
WITH READ ONLY;

CREATE OR REPLACE VIEW customers_view AS
SELECT users_view.id, 
       users_view.name, 
       users_view.surname, 
       users_view.username, 
       users_view.password, 
       users_view.email,
       company.text_value as company
    FROM users_view
    join params company on (users_view.role = 'CUSTOMER' AND users_view.id = company.object_id 
                            AND company.attr_id = 25)
WITH READ ONLY;

CREATE OR REPLACE VIEW employees_view AS
SELECT users_view.id, 
       users_view.name, 
       users_view.surname, 
       users_view.username, 
       users_view.password, 
       users_view.email,
       position.text_value as position
    FROM users_view
    join params position on (users_view.role = 'EMPLOYEE' AND users_view.id = position.object_id 
                            AND position.attr_id = 24)
WITH READ ONLY;