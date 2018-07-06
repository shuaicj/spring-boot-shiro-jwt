-- users
-----------------------
insert into users(username, password) values ('alice', 'alice-password');
insert into users(username, password) values ('bob', 'bob-password');
insert into users(username, password) values ('chris', 'chris-password');
insert into users(username, password) values ('david', 'david-password');


-- roles of users
-----------------------
insert into user_roles(username, role_name) values ('alice', 'admin');
insert into user_roles(username, role_name) values ('bob', 'user');
insert into user_roles(username, role_name) values ('chris', 'file-operator');
insert into user_roles(username, role_name) values ('david', 'log-archiver');


-- permissions of roles
-----------------------

-- The 'admin' role has all permissions.
insert into roles_permissions(role_name, permission) values ('admin', '*');

-- The 'user' role can read and write files. This line can also be replaced by two lines:
--   insert into roles_permissions(role_name, permission) values ('user', 'files:read');
--   insert into roles_permissions(role_name, permission) values ('user', 'files:write');
insert into roles_permissions(role_name, permission) values ('user', 'files:read,write');

-- The 'file-operator' role can do anything to files.
insert into roles_permissions(role_name, permission) values ('file-operator', 'files:*');

-- The 'log-archiver' role can read and archive log files.
insert into roles_permissions(role_name, permission) values ('log-archiver', 'files:read,archive:log');

