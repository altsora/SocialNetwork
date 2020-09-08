create type friendship_status_code as enum ('FRIEND', 'REQUEST', 'BLOCKED', 'DECLINED', 'SUBSCRIBED');

create table friendship (
    id int8 generated by default as identity,
    src_person_id int8 not null,
    dst_person_id int8 not null,
    time timestamp with time zone not null,
    name varchar(255),
    status friendship_status_code not null,
    primary key (id)
);