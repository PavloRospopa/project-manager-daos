create or replace trigger test_object_types_trigger
before insert on object_types
for each row
   begin
     select object_types_seq.nextval into :new.object_type_id from dual;
   end;
/
create or replace trigger test_attributes_trigger
before insert on attributes
for each row
   begin
     select attributes_seq.nextval into :new.attr_id from dual;
   end;
/
create or replace trigger test_attr_types_trigger
before insert on attr_types
for each row
   begin
     select attr_types_seq.nextval into :new.attr_type_id from dual;
   end;
/
create or replace trigger test_objects_trigger
before insert on objects
for each row
   begin
     select objects_seq.nextval into :new.object_id from dual;
   end; 
/