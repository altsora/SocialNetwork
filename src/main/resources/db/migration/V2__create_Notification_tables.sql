create table notification_type
(
    id int8 generated by default as identity,
	code varchar(255) not null,
    name varchar(255) not null,
    primary key (id)
);

create table notification_settings
(
    id int8 generated by default as identity,
	person_id int8 not null,
    notification_type_id int8 not null,
	enable boolean,
    primary key (id)
);

create table notification
(
    id int8 generated by default as identity,
	type_id int8 not null,
	sent_time timestamp with time zone,
	entity_id int8 not null,
	info varchar(255),
	person_id int8 not null,
    contact varchar(255) not null,
	is_readed boolean not null,
    primary key (id)
);

/*notification_settings*/
alter table if exists notification_settings
    add constraint FK_NOTIFICATION_SETTINGS_TYPE_ID
    foreign key (notification_type_id) references notification_type;
alter table if exists notification_settings
    add constraint FK_NOTIFICATION_SETTINGS_PERSON_ID
    foreign key (person_id) references person;

/*notification*/
alter table if exists notification
    add constraint FK_NOTIFICATION_TYPE_ID
    foreign key (type_id) references notification_type;
alter table if exists notification
    add constraint FK_NOTIFICATION_PERSON_ID
    foreign key (person_id) references person;