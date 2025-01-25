CREATE TABLE IF NOT EXISTS day_of_week
(
    id           serial      NOT NULL,
    day_of_week  varchar(50) NOT NULL,
    PRIMARY KEY (id)
);

insert into day_of_week(day_of_week)
values ('MONDAY'), ('TUESDAY'),('WEDNESDAY'),('THURSDAY'),('FRIDAY'),('SATURDAY'),('SUNDAY');