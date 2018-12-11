create table student (
  id       varchar(24) primary key,
  password varchar(24) not null,
  name     varchar(36) not null,
  class_id bigint      not null,
  subject  varchar(72) not null
);
