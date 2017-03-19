DELETE FROM params;
DELETE FROM attr_binds;
DELETE FROM refs;
DELETE FROM attributes;
DELETE FROM attr_types;
DELETE FROM objects;
DELETE FROM object_types;

drop sequence object_types_seq;
drop sequence attributes_seq;
drop sequence attr_types_seq;
drop sequence objects_seq;

CREATE SEQUENCE object_types_seq START WITH 1 INCREMENT BY 1 NOMAXVALUE;
CREATE SEQUENCE attributes_seq START WITH 1 INCREMENT BY 1 NOMAXVALUE;
CREATE SEQUENCE attr_types_seq START WITH 1 INCREMENT BY 1 NOMAXVALUE;
CREATE SEQUENCE objects_seq START WITH 1 INCREMENT BY 1 NOMAXVALUE;