drop table if exists order_item;
drop table if exists `order`;
drop table if exists goods_photo;
drop table if exists goods;
drop table if exists store;
drop table if exists comment_photo;
drop table if exists comment;
drop table if exists route;
drop table if exists station;
drop table if exists bus;
drop table if exists building_photo;
drop table if exists building;
drop table if exists `user`;

create table `user`
(
    name     varchar(255) not null,
    password varchar(255) not null,
    token    varchar(255) not null,
    type     integer      not null,
    primary key (name)
);

create table building
(
    name        varchar(255) not null,
    description text         not null,
    details     text         not null,
    latitude    double       not null,
    longitude   double       not null,
    primary key (name)
);

create table building_photo
(
    id       int          not null,
    building varchar(255) not null,
    path     text         not null,
    primary key (id),
    constraint foreign key (building) references building (name)
);

create table bus
(
    name varchar(255) not null,
    primary key (name)
);

create table station
(
    name      varchar(255) not null,
    latitude  double       not null,
    longitude double       not null,
    primary key (name)
);

create table route
(
    bus     varchar(255) not null,
    station varchar(255) not null,
    `order` int          not null,
    primary key (bus, station),
    constraint foreign key (bus) references bus (name),
    constraint foreign key (station) references station (name)
);

create table comment
(
    id        int          not null,
    building  varchar(255) not null,
    commenter varchar(255) not null,
    content   text         not null,
    approved  bool         not null,
    primary key (id),
    constraint foreign key (building) references building (name),
    constraint foreign key (commenter) references `user` (name)
);

create table comment_photo
(
    id         int  not null,
    comment_id int  not null,
    path       text not null,
    primary key (id),
    constraint foreign key (comment_id) references comment (id)
);

create table store
(
    name varchar(255) not null,
    primary key (name)
);

create table goods
(
    id       int           not null,
    name     varchar(255)  not null,
    store    varchar(255)  not null,
    price    numeric(8, 2) not null,
    quantity int           not null,
    primary key (id),
    constraint foreign key (store) references store (name)
);

create table goods_photo
(
    goods_id int  not null,
    path     text not null,
    constraint foreign key (goods_id) references goods (id)
);

create table `order`
(
    id        int          not null,
    status    varchar(255) not null,
    purchaser varchar(255) not null,
    time      time         not null,
    primary key (id),
    constraint foreign key (purchaser) references `user` (name)
);

create table order_item
(
    order_id int not null,
    goods_id int not null,
    amount   int not null,
    primary key (order_id, goods_id),
    constraint foreign key (order_id) references `order` (id),
    constraint foreign key (goods_id) references goods (id)
);

