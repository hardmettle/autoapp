create table cars (
  id  int not null,
  title varchar not null,
  fuel_name varchar not null,
  fuel_renewable boolean not null,
  price int not null,
  used boolean not null,
  mileage int,
  registration varchar,
  primary key(id)
);