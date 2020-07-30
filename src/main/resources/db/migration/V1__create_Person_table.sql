create table person (
    id int8 generated by default as identity,
    first_name varchar(255),
    last_name varchar(255),
    reg_date timestamp with time zone,
    birth_date date,
    e_mail varchar(255),
    phone varchar(255),
    password varchar(255),
    photo varchar(255),
    about varchar(255),
    city varchar(255),
    country varchar(255),
    confirmation_code varchar(255),
    is_approved boolean,
    messages_permission varchar(255),
    last_online_time timestamp with time zone,
    is_blocked boolean,
    is_online boolean,
    is_deleted boolean,
    primary key (id)
)
