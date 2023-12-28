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
    id       bigint       not null,
    name     varchar(255) not null,
    password varchar(255) not null,
    token    varchar(255) not null,
    type     integer      not null,
    primary key (id),
    unique (name)
);

create table building
(
    id          bigint       not null,
    name        varchar(255) not null,
    tag         varchar(255) not null,
    description text         not null,
    details     text         not null,
    latitude    double       not null,
    longitude   double       not null,
    primary key (id),
    unique (name)
);

create table building_photo
(
    id         bigint not null,
    buildingId bigint not null,
    path       text   not null,
    primary key (id),
    constraint foreign key (buildingId) references building (id)
);

#busline name
create table bus
(
    id   bigint       not null,
    name varchar(255) not null,
    primary key (id),
    unique (name)
);

create table station
(
    id        bigint       not null,
    name      varchar(255) not null,
    latitude  double       not null,
    longitude double       not null,
    primary key (id),
    unique(name)
);

#busline
create table route
(
    busId     bigint not null,
    stationId bigint not null,
    stopOrder int    not null,
    primary key (busId, stopOrder),
    constraint foreign key (busId) references bus (id),
    constraint foreign key (stationId) references station (id)
);

create table comment
(
    id          bigint not null,
    buildingId  bigint not null,
    commenterId bigint not null,
    content     text   not null,
    status      int    not null,
    time        bigint not null,
    primary key (id),
    constraint foreign key (buildingId) references building (id),
    constraint foreign key (commenterId) references `user` (id)
);

create table comment_photo
(
    id        bigint not null,
    commentId bigint not null,
    path      text   not null,
    primary key (id),
    constraint foreign key (commentId) references comment (id)
);

create table store
(
    id   bigint       not null,
    name varchar(255) not null,
    primary key (id),
    unique (name)
);

create table goods
(
    id       bigint        not null,
    name     varchar(255)  not null,
    storeId  bigint        not null,
    price    numeric(8, 2) not null,
    quantity int           not null,
    hidden   boolean       not null,
    primary key (id),
    constraint foreign key (storeId) references store (id),
    unique (storeId, name)
);

create table goods_photo
(
    id      bigint not null,
    goodsId bigint not null,
    path    text   not null,
    primary key (id),
    constraint foreign key (goodsId) references goods (id)
);

create table `order`
(
    id        bigint not null,
    status    int    not null,
    purchaser bigint not null,
    time      bigint not null,
    primary key (id),
    constraint foreign key (purchaser) references `user` (id)
);

create table order_item
(
    orderId bigint        not null,
    goodsId bigint        not null,
    amount  int           not null,
    price   numeric(8, 2) not null,
    primary key (orderId, goodsId),
    constraint foreign key (orderId) references `order` (id),
    constraint foreign key (goodsId) references goods (id)
);

