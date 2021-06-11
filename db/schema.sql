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