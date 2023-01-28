create table if not exists garden_item
(
    time                time  not null ,
    celsius_degree      float not null ,
    humidity            float not null ,
    analog              float not null ,
    moisture            float not null
);
