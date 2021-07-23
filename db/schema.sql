create table post (
    id serial primary key,
    name text,
    description text,
    created timestamp
);

create table candidate (
    id serial primary key,
    name text,
    photoId text
);

create table users (
    id serial primary key,
    name text,
    email text unique,
    password text
);

create table cities (
	id serial primary key,
	city text
)