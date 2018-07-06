drop table if exists users;

create table users (
    id bigint primary key auto_increment,
    username varchar(100) not null unique,
    password varchar(100) not null
);

drop table if exists user_roles;

create table user_roles (
    id bigint primary key auto_increment,
    username varchar(100) not null,
    role_name varchar(100) not null
);

drop table if exists roles_permissions;

create table roles_permissions (
    id bigint primary key auto_increment,
    role_name varchar(100) not null,
    permission varchar(100) not null
);
