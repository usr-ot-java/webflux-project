create table products (
    id bigserial primary key,
    name varchar(255)
);

insert into products (id, name) values (1, 'one'),
                                       (2, 'two'),
                                       (3, 'three'),
                                       (100, 'one hundred'),
                                       (101, 'one hundred and one');
