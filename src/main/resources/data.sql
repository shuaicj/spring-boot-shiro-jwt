-- users
-----------------------

-- <'alice', bcrypt('alice-password')>
insert into users(username, password) values ('alice', '$2a$10$ezJDL4CP0jMOsXO/0SmvJetPMehGdiIDYzh2SKsfkW5c57Bz33o2m');
-- <'bob', bcrypt('bob-password')>
insert into users(username, password) values ('bob', '$2a$10$vPZXpQ37RHVQhfI1jPCC9.6.w7LsmPu4Fee0FCF45rOXqv99UtQoa');
-- <'chris', bcrypt('chris-password')>
insert into users(username, password) values ('chris', '$2a$10$zo967KgB3M5kDcyRH.k6KegnSGKzfggZnG2wtCongD8FtIJTSrBnW');
-- <'david', bcrypt('david-password')>
insert into users(username, password) values ('david', '$2a$10$5H1gBZKAXY8UZsUwUbdGZux3GvXoOVP.urSI6RKH/TRsqGbtRb/UW');


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

