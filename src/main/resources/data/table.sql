drop table if exists users;
create table users
(
    id   bigint not null ,
    email varchar(20),
    first_name varchar (50),
    last_name varchar (100),
    password varchar (255) not null,
    role varchar (50) ,
    status varchar (100)
);
