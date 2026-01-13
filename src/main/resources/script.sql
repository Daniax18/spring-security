create table product(
    id int auto_increment primary key,
    name varchar(50),
    price double
);

create table users(
    id int auto_increment primary key,
    user_name varchar(100),
    email varchar(50),
    password TEXT,
    role varchar(50)
);