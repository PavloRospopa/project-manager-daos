insert into object_types(name, description)
values ('Project', 'Class that represents projects in company');
insert into object_types(name, description)
values ('Sprint', 'Type that represents sprints in projects');
insert into object_types(name, description)
values ('Task', 'Class that represents tasks in sprints');
insert into object_types(name, description)
values ('TaskDelegation', 'Class that incapsulates task delegation info');
insert into object_types(name, description)
values ('TaskTimeRequest', 'Task Time Request class');
insert into object_types(name, description)
values ('User', 'Abstract class that represents users in system');
insert into object_types(parent_id, name, description)
values (6, 'Administrator', 'Administrator class');
insert into object_types(parent_id, name, description)
values (6, 'Customer', 'Customer class');
insert into object_types(parent_id, name, description)
values (6, 'Employee', 'Employee class');
insert into object_types(parent_id, name, description)
values (6, 'ProjectManager', 'Project Manager class');


insert into attr_types (name)
values ('TEXT_VALUE');
insert into attr_types (name)
values ('NUMBER_VALUE');
insert into attr_types (name)
values ('DATE_VALUE');
insert into attr_types (name)
values ('REFERENCE');


insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (1, 0, 'name', 'Name of entity (e.g. user name, project name, sprint name and so on)');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (3, 0, 'startDate', 'Start date (of project, sprint)');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (3, 0, 'completionDate', 'Completion date (of project or sprint)');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (3, 0, 'expectedCompletionDate', 'Expected completion date (of project or sprint)');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (4, 0, 'customer', 'Attribute that describes customer of given project');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (4, 0, 'projectManager', 'Attribute that describes project manager of given project');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (2, 0, 'estimatedTime', 'Estimated time of given task');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (2, 0, 'spentTime', 'Spent time of given task');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (4, 0, 'parent', 'Parent task');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (4, 0, 'sprint', 'Sprint in which task is created');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (1, 0, 'requiredEmpPosition', 'Minimal employee position required for solving this task');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (1, 0, 'description', 'Task description');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (1, 0, 'status', 'Status attribute for Task, TaskDelegation and TaskTimeRequest entities');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (4, 0, 'task', 'Attribute for describing task that corresponds to given TaskDelegation or TaskTimeRequest object');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (4, 0, 'employee', 'Attribute for describing employee that corresponds to given TaskDelegation object');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (3, 0, 'startDateTime', 'Date and time of starting solving task. Attribute of TaskDelegation class');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (3, 0, 'completionDateTime', 'Date and time of completion task. Attribute of TaskDelegation class');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (2, 0, 'newEstimatedTime', 'TaskTimeRequest Attribute for holding new estimated time of task');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (1, 0, 'surname', 'Surname of user');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (1, 0, 'username', 'User`s username');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (1, 0, 'password', 'User`s password');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (1, 0, 'email', 'User`s email');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (1, 0, 'role', 'User`s role in system');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (1, 0, 'position', 'Employee position');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (1, 0, 'company', 'Name of the customer`s company');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (4, 0, 'project', 'Project to which given Sprint belongs');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (4, 0, 'previousSprint', 'Previous sprint of given Sprint object');
insert into attributes (attr_type_id, is_multiple, NAME, DESCRIPTION)
values (4, 1, 'taskDependency', 'Attribute that describes dependencies between different tasks. Is Multiple (like list)');


insert into attr_binds(object_type_id, attr_id)
values (1, 1);
insert into attr_binds(object_type_id, attr_id)
values (1, 2);
insert into attr_binds(object_type_id, attr_id)
values (1, 3);
insert into attr_binds(object_type_id, attr_id)
values (1, 4);
insert into attr_binds(object_type_id, attr_id)
values (1, 5);
insert into attr_binds(object_type_id, attr_id)
values (1, 6);

insert into attr_binds(object_type_id, attr_id)
values (6, 1);
insert into attr_binds(object_type_id, attr_id)
values (6, 19);
insert into attr_binds(object_type_id, attr_id)
values (6, 20);
insert into attr_binds(object_type_id, attr_id)
values (6, 21);
insert into attr_binds(object_type_id, attr_id)
values (6, 22);
insert into attr_binds(object_type_id, attr_id)
values (6, 23);

insert into attr_binds(object_type_id, attr_id)
values (2, 1);
insert into attr_binds(object_type_id, attr_id)
values (2, 2);
insert into attr_binds(object_type_id, attr_id)
values (2, 3);
insert into attr_binds(object_type_id, attr_id)
values (2, 4);
insert into attr_binds(object_type_id, attr_id)
values (2, 26);
insert into attr_binds(object_type_id, attr_id)
values (2, 27);

insert into attr_binds(object_type_id, attr_id)
values (3, 1);
insert into attr_binds(object_type_id, attr_id)
values (3, 7);
insert into attr_binds(object_type_id, attr_id)
values (3, 8);
insert into attr_binds(object_type_id, attr_id)
values (3, 9);
insert into attr_binds(object_type_id, attr_id)
values (3, 10);
insert into attr_binds(object_type_id, attr_id)
values (3, 11);
insert into attr_binds(object_type_id, attr_id)
values (3, 12);
insert into attr_binds(object_type_id, attr_id)
values (3, 13);
insert into attr_binds(object_type_id, attr_id)
values (3, 28);

insert into attr_binds(object_type_id, attr_id)
values (4, 13);
insert into attr_binds(object_type_id, attr_id)
values (4, 14);
insert into attr_binds(object_type_id, attr_id)
values (4, 15);
insert into attr_binds(object_type_id, attr_id)
values (4, 16);
insert into attr_binds(object_type_id, attr_id)
values (4, 17);

insert into attr_binds(object_type_id, attr_id)
values (5, 13);
insert into attr_binds(object_type_id, attr_id)
values (5, 14);
insert into attr_binds(object_type_id, attr_id)
values (5, 18);

insert into attr_binds(object_type_id, attr_id)
values (8, 25);

insert into attr_binds(object_type_id, attr_id)
values (9, 24);


insert into objects(object_type_id, name)
values(8, 'F Customer');

insert into params(object_id, attr_id, text_value)
values(1, 1, 'Petro');
insert into params(object_id, attr_id, text_value)
values(1, 19, 'Timoshenko');
insert into params(object_id, attr_id, text_value)
values(1, 20, 'piratuk');
insert into params(object_id, attr_id, text_value)
values(1, 21, 'some_password');
insert into params(object_id, attr_id, text_value)
values(1, 22, 'sobaka@gmail.com');
insert into params(object_id, attr_id, text_value)
values(1, 23, 'CUSTOMER');
insert into params(object_id, attr_id, text_value)
values(1, 25, 'Taggart Transcontinental');


insert into objects(object_type_id, name)
values(10, 'Project manager');

insert into params(object_id, attr_id, text_value)
values(2, 1, 'Mike');
insert into params(object_id, attr_id, text_value)
values(2, 19, 'Dubovski');
insert into params(object_id, attr_id, text_value)
values(2, 20, 'prmanager');
insert into params(object_id, attr_id, text_value)
values(2, 21, 'pr1');
insert into params(object_id, attr_id, text_value)
values(2, 22, 'kot@mail.ru');
insert into params(object_id, attr_id, text_value)
values(2, 23, 'PROJECT_MANAGER');


insert into objects(object_type_id, name)
values(1, 'First project');

insert into params(object_id, attr_id, text_value)
values(3, 1, 'First Project');
insert into params(object_id, attr_id, date_value)
values(3, 2, TO_DATE('2014/11/22', 'yyyy/mm/dd'));
insert into params(object_id, attr_id, date_value)
values(3, 3, TO_DATE('2016/12/31', 'yyyy/mm/dd'));
insert into params(object_id, attr_id, date_value)
values(3, 4, TO_DATE('2017/01/06', 'yyyy/mm/dd'));
insert into refs(object_id, attr_id, reference)
values(3, 5, 1);
insert into refs(object_id, attr_id, reference)
values(3, 6, 2);


insert into objects(object_type_id, name)
values(2, '1st Sprint');

insert into params(object_id, attr_id, text_value)
values(4, 1, '1st Sprint of First project');
insert into params(object_id, attr_id, date_value)
values(4, 2, TO_DATE('2014/11/23', 'yyyy/mm/dd'));
insert into params(object_id, attr_id, date_value)
values(4, 3, TO_DATE('2015/06/27', 'yyyy/mm/dd'));
insert into params(object_id, attr_id, date_value)
values(4, 4, TO_DATE('2015/06/30', 'yyyy/mm/dd'));
insert into refs(object_id, attr_id, reference)
values(4, 26, 3);


insert into objects(object_type_id, name)
values(2, '2nd Sprint');

insert into params(object_id, attr_id, text_value)
values(5, 1, '2nd Sprint of First project');
insert into params(object_id, attr_id, date_value)
values(5, 2, TO_DATE('2015/06/28', 'yyyy/mm/dd'));
insert into params(object_id, attr_id, date_value)
values(5, 3, TO_DATE('2016/03/08', 'yyyy/mm/dd'));
insert into params(object_id, attr_id, date_value)
values(5, 4, TO_DATE('2016/03/06', 'yyyy/mm/dd'));
insert into refs(object_id, attr_id, reference)
values(5, 26, 3);
insert into refs(object_id, attr_id, reference)
values(5, 27, 4);


insert into objects(object_type_id, name)
values(2, '3rd Sprint');

insert into params(object_id, attr_id, text_value)
values(6, 1, '3rd Sprint of First project');
insert into params(object_id, attr_id, date_value)
values(6, 2, TO_DATE('2016/03/08', 'yyyy/mm/dd'));
insert into params(object_id, attr_id, date_value)
values(6, 3, TO_DATE('2016/12/31', 'yyyy/mm/dd'));
insert into params(object_id, attr_id, date_value)
values(6, 4, TO_DATE('2017/01/06', 'yyyy/mm/dd'));
insert into refs(object_id, attr_id, reference)
values(6, 26, 3);
insert into refs(object_id, attr_id, reference)
values(6, 27, 5);


insert into objects(object_type_id, name)
values(3, 'Task1 of 1st sprint');

insert into params(object_id, attr_id, text_value)
values(7, 1, 'implement daos');
insert into params(object_id, attr_id, number_value)
values(7, 7, 8);
insert into params(object_id, attr_id, number_value)
values(7, 8, 7);
insert into refs(object_id, attr_id, reference)
values(7, 10, 4);
insert into params(object_id, attr_id, text_value)
values(7, 11, 'JUNIOR');
insert into params(object_id, attr_id, text_value)
values(7, 12, 'write dao classes to all entities in domain');
insert into params(object_id, attr_id, text_value)
values(7, 13, 'COMPLETED');


insert into objects(object_type_id, name)
values(3, 'Task2 of 1st sprint');

insert into params(object_id, attr_id, text_value)
values(8, 1, 'implement business layer');
insert into params(object_id, attr_id, number_value)
values(8, 7, 12);
insert into params(object_id, attr_id, number_value)
values(8, 8, 14);
insert into refs(object_id, attr_id, reference)
values(8, 10, 4);
insert into params(object_id, attr_id, text_value)
values(8, 11, 'MIDDLE');
insert into params(object_id, attr_id, text_value)
values(8, 12, 'write service classes');
insert into params(object_id, attr_id, text_value)
values(8, 13, 'COMPLETED');


insert into objects(object_type_id, name)
values(3, 'Task of 2nd sprint');

insert into params(object_id, attr_id, text_value)
values(9, 1, 'implement presentation layer');
insert into params(object_id, attr_id, number_value)
values(9, 7, 10);
insert into params(object_id, attr_id, number_value)
values(9, 8, 10);
insert into refs(object_id, attr_id, reference)
values(9, 10, 5);
insert into params(object_id, attr_id, text_value)
values(9, 11, 'MIDDLE');
insert into params(object_id, attr_id, text_value)
values(9, 12, 'write controller and jsps');
insert into params(object_id, attr_id, text_value)
values(9, 13, 'COMPLETED');


insert into objects(object_type_id, name)
values(3, 'Task of 3rd sprint');

insert into params(object_id, attr_id, text_value)
values(10, 1, 'enhance front end');
insert into params(object_id, attr_id, number_value)
values(10, 7, 6);
insert into params(object_id, attr_id, number_value)
values(10, 8, 5);
insert into refs(object_id, attr_id, reference)
values(10, 10, 6);
insert into params(object_id, attr_id, text_value)
values(10, 11, 'JUNIOR');
insert into params(object_id, attr_id, text_value)
values(10, 12, 'add js to pages');
insert into params(object_id, attr_id, text_value)
values(10, 13, 'COMPLETED');


insert into objects(object_type_id, name)
values(9, 'Junior Employee');

insert into params(object_id, attr_id, text_value)
values(11, 1, 'Robert');
insert into params(object_id, attr_id, text_value)
values(11, 19, 'Kolezki');
insert into params(object_id, attr_id, text_value)
values(11, 20, 'kolezki');
insert into params(object_id, attr_id, text_value)
values(11, 21, '111');
insert into params(object_id, attr_id, text_value)
values(11, 22, 'kolezki@gmail.com');
insert into params(object_id, attr_id, text_value)
values(11, 23, 'EMPLOYEE');
insert into params(object_id, attr_id, text_value)
values(11, 24, 'JUNIOR');

insert into objects(object_type_id, name)
values(9, 'Middle Employee');

insert into params(object_id, attr_id, text_value)
values(12, 1, 'Ray');
insert into params(object_id, attr_id, text_value)
values(12, 19, 'Fuko');
insert into params(object_id, attr_id, text_value)
values(12, 20, 'rayfuko');
insert into params(object_id, attr_id, text_value)
values(12, 21, 'rayray');
insert into params(object_id, attr_id, text_value)
values(12, 22, 'fuko@gmail.com');
insert into params(object_id, attr_id, text_value)
values(12, 23, 'EMPLOYEE');
insert into params(object_id, attr_id, text_value)
values(12, 24, 'MIDDLE');


insert into objects(object_type_id, name)
values(4, 'TaskDelegation for task1 of sprint1 and junior employee');

insert into params(object_id, attr_id, text_value)
values(13, 13, 'COMPLETED');
insert into refs(object_id, attr_id, reference)
values(13, 14, 7);
insert into refs(object_id, attr_id, reference)
values(13, 15, 11);
insert into params(object_id, attr_id, date_value)
values(13, 16, TO_DATE('2014/11/23/09/00', 'yyyy/mm/dd/HH24/MI'));
insert into params(object_id, attr_id, date_value)
values(13, 17, TO_DATE('2014/11/23/16/00', 'yyyy/mm/dd/HH24/MI'));


insert into objects(object_type_id, name)
values(4, 'TaskDelegation for task2 of sprint1 and middle employee');

insert into params(object_id, attr_id, text_value)
values(14, 13, 'COMPLETED');
insert into refs(object_id, attr_id, reference)
values(14, 14, 8);
insert into refs(object_id, attr_id, reference)
values(14, 15, 12);
insert into params(object_id, attr_id, date_value)
values(14, 16, TO_DATE('2015/01/09/08/00', 'yyyy/mm/dd/HH24/MI'));
insert into params(object_id, attr_id, date_value)
values(14, 17, TO_DATE('2015/01/10/11/00', 'yyyy/mm/dd/HH24/MI'));


insert into objects(object_type_id, name)
values(4, 'TaskDelegation for sprint2`s task and middle employee');

insert into params(object_id, attr_id, text_value)
values(15, 13, 'COMPLETED');
insert into refs(object_id, attr_id, reference)
values(15, 14, 9);
insert into refs(object_id, attr_id, reference)
values(15, 15, 12);
insert into params(object_id, attr_id, date_value)
values(15, 16, TO_DATE('2015/06/29/08/00', 'yyyy/mm/dd/HH24/MI'));
insert into params(object_id, attr_id, date_value)
values(15, 17, TO_DATE('2015/06/29/18/00', 'yyyy/mm/dd/HH24/MI'));


insert into objects(object_type_id, name)
values(4, 'TaskDelegation for sprint3`s task and jun employee');

insert into params(object_id, attr_id, text_value)
values(16, 13, 'COMPLETED');
insert into refs(object_id, attr_id, reference)
values(16, 14, 10);
insert into refs(object_id, attr_id, reference)
values(16, 15, 11);
insert into params(object_id, attr_id, date_value)
values(16, 16, TO_DATE('2016/12/30/08/00', 'yyyy/mm/dd/HH24/MI'));
insert into params(object_id, attr_id, date_value)
values(16, 17, TO_DATE('2016/12/30/13/00', 'yyyy/mm/dd/HH24/MI'));


insert into objects(object_type_id, name)
values(4, 'TaskDelegation for sprint3`s task and mid employee');

insert into params(object_id, attr_id, text_value)
values(17, 13, 'COMPLETED');
insert into refs(object_id, attr_id, reference)
values(17, 14, 10);
insert into refs(object_id, attr_id, reference)
values(17, 15, 12);
insert into params(object_id, attr_id, date_value)
values(17, 16, TO_DATE('2016/12/30/08/00', 'yyyy/mm/dd/HH24/MI'));
insert into params(object_id, attr_id, date_value)
values(17, 17, TO_DATE('2016/12/30/12/50', 'yyyy/mm/dd/HH24/MI'));


insert into objects(object_type_id, name)
values(5, 'Task time request of task');

insert into params(object_id, attr_id, text_value)
values(18, 13, 'REFUSED');
insert into refs(object_id, attr_id, reference)
values(18, 14, 10);
insert into params(object_id, attr_id, number_value)
values(18, 18, 15);


insert into objects(object_type_id, name)
values(7, 'Admin');

insert into params(object_id, attr_id, text_value)
values(19, 1, 'Anton');
insert into params(object_id, attr_id, text_value)
values(19, 19, 'Pupkin');
insert into params(object_id, attr_id, text_value)
values(19, 20, 'admin');
insert into params(object_id, attr_id, text_value)
values(19, 21, 'admin111');
insert into params(object_id, attr_id, text_value)
values(19, 22, 'admin@mail.ru');
insert into params(object_id, attr_id, text_value)
values(19, 23, 'ADMINISTRATOR');


insert into objects(object_type_id, name)
values(1, 'Second Project');

insert into params(object_id, attr_id, text_value)
values(20, 1, 'VideoChat');
insert into params(object_id, attr_id, date_value)
values(20, 2, TO_DATE('2017/03/12', 'yyyy/mm/dd'));
insert into params(object_id, attr_id, date_value)
values(20, 3, null);
insert into params(object_id, attr_id, date_value)
values(20, 4, TO_DATE('2017/06/12', 'yyyy/mm/dd'));
insert into refs(object_id, attr_id, reference)
values(20, 5, 1);
insert into refs(object_id, attr_id, reference)
values(20, 6, 2);


insert into objects(object_type_id, name)
values(2, 'VideoChat`s Sprint');

insert into params(object_id, attr_id, text_value)
values(21, 1, 'VideoChat`s 1st Sprint');
insert into params(object_id, attr_id, date_value)
values(21, 2, TO_DATE('2017/03/12', 'yyyy/mm/dd'));
insert into params(object_id, attr_id, date_value)
values(21, 3, null);
insert into params(object_id, attr_id, date_value)
values(21, 4, TO_DATE('2017/04/15', 'yyyy/mm/dd'));
insert into refs(object_id, attr_id, reference)
values(21, 26, 20);


insert into objects(object_type_id, name)
values(3, 'Task1 of VideoChat`s 1st Sprint');

insert into params(object_id, attr_id, text_value)
values(22, 1, 'Develop architecture');
insert into params(object_id, attr_id, number_value)
values(22, 7, 10);
insert into params(object_id, attr_id, number_value)
values(22, 8, null);
insert into refs(object_id, attr_id, reference)
values(22, 10, 21);
insert into params(object_id, attr_id, text_value)
values(22, 11, 'MIDDLE');
insert into params(object_id, attr_id, text_value)
values(22, 12, 'develop Videochat`s architecture');
insert into params(object_id, attr_id, text_value)
values(22, 13, 'ACTIVE');


insert into objects(object_type_id, name)
values(3, 'Task2 of VideoChat`s 1st Sprint');

insert into params(object_id, attr_id, text_value)
values(23, 1, 'design website appearance');
insert into params(object_id, attr_id, number_value)
values(23, 7, 6);
insert into params(object_id, attr_id, number_value)
values(23, 8, null);
insert into refs(object_id, attr_id, reference)
values(23, 10, 21);
insert into params(object_id, attr_id, text_value)
values(23, 11, 'JUNIOR');
insert into params(object_id, attr_id, text_value)
values(23, 12, 'design website appearance');
insert into params(object_id, attr_id, text_value)
values(23, 13, 'UNASSIGNED');


insert into objects(object_type_id, name)
values(4, 'TaskDelegation for task1 of videochat sprint1');

insert into params(object_id, attr_id, text_value)
values(24, 13, 'ACTIVE');
insert into refs(object_id, attr_id, reference)
values(24, 14, 22);
insert into refs(object_id, attr_id, reference)
values(24, 15, 12);
insert into params(object_id, attr_id, date_value)
values(24, 16, TO_DATE('2017/03/12/16/00', 'yyyy/mm/dd/HH24/MI'));
insert into params(object_id, attr_id, date_value)
values(24, 17, null);


insert into objects(object_type_id, name)
values(4, 'TaskDelegation for task2 of videochat sprint1');

insert into params(object_id, attr_id, text_value)
values(25, 13, 'UNCONFIRMED');
insert into refs(object_id, attr_id, reference)
values(25, 14, 23);
insert into refs(object_id, attr_id, reference)
values(25, 15, 11);
insert into params(object_id, attr_id, date_value)
values(25, 16, null);
insert into params(object_id, attr_id, date_value)
values(25, 17, null);


insert into objects(object_type_id, name)
values(8, 'Space X Customer');

insert into params(object_id, attr_id, text_value)
values(26, 1, 'Semen');
insert into params(object_id, attr_id, text_value)
values(26, 19, 'Rodan');
insert into params(object_id, attr_id, text_value)
values(26, 20, 'srodan');
insert into params(object_id, attr_id, text_value)
values(26, 21, 'rodanrodan');
insert into params(object_id, attr_id, text_value)
values(26, 22, 'rodan@gmail.com');
insert into params(object_id, attr_id, text_value)
values(26, 23, 'CUSTOMER');
insert into params(object_id, attr_id, text_value)
values(26, 25, 'Space X');


insert into objects(object_type_id, name)
values(10, 'Second Project manager');

insert into params(object_id, attr_id, text_value)
values(27, 1, 'Denis');
insert into params(object_id, attr_id, text_value)
values(27, 19, 'Wolf');
insert into params(object_id, attr_id, text_value)
values(27, 20, 'wolfmanager');
insert into params(object_id, attr_id, text_value)
values(27, 21, 'manager');
insert into params(object_id, attr_id, text_value)
values(27, 22, 'wolf@mail.ru');
insert into params(object_id, attr_id, text_value)
values(27, 23, 'PROJECT_MANAGER');