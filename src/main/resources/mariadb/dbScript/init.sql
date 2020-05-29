-- create database if not exists rock DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

create table attachment
(
    id                    varchar(32)  not null primary key,
    name                  varchar(255) not null,
    type                  varchar(50)  null,
    content               longblob     not null,
    size                  int default length(content),
    content_type          varchar(255) null,
    recorder_type         varchar(50)  null,
    recorder_id           varchar(32)  null,
    create_user_id        varchar(32)  null,
    create_username       varchar(255) null,
    create_user_full_name varchar(255) null,
    create_time           datetime(3)  not null,
    index ix_attachment_recorder_id (recorder_id)
);

create table if not exists customer
(
    id                    varchar(32)  not null primary key,
    name                  varchar(255) not null unique comment '名称',
    charge_user_full_name varchar(255) null comment '负责人姓名',
    charge_user_email     varchar(255) null comment '负责人邮箱',
    charge_user_phone1    varchar(255) null comment '负责人电话1',
    charge_user_phone2    varchar(255) null comment '负责人电话2',
    charge_user_phone3    varchar(255) null comment '负责人电话3'
) comment '客户';

create table if not exists computer_room
(
    id          varchar(32)  not null primary key,
    name        varchar(255) not null comment '名称',
    customer_id varchar(32)  null comment '所属客户（允许空值）',
    remark      text comment '备注',
    constraint fk_computer_root_customer_id foreign key (customer_id) references customer (id) on delete set null
) comment '机房';

create table if not exists equipment_model
(
    id       varchar(32)  not null primary key,
    brand_id varchar(32)  not null comment '品牌',
    name     varchar(255) not null comment '型号名称',
    constraint foreign key fk_equipment_model_brand_id (brand_id) references dictionary_code (id) on delete restrict
) comment '品牌型号';

create table if not exists equipment
(
    id               varchar(32)  not null primary key,
    name             varchar(255) not null comment '名称',
    category_id      varchar(32)  null comment '类别',
    type_id          varchar(32)  not null comment '类型',
    model_id         varchar(32)  not null comment '品牌型号',
    owner            varchar(32)  null comment '所有权归属',
    serial_number    varchar(255) comment '序列号',
    manufacture_date datetime(3)  null comment '生产日期',
    computer_room_id varchar(32)  null comment '所属机房',
    create_time      datetime(3)  not null default current_timestamp(3) comment '记录生成时间',
    constraint foreign key fk_equipment_category_id (category_id) references dictionary_code (id) on delete set null,
    constraint foreign key fk_equipment_model_id (model_id) references equipment_model (id) on delete restrict,
    constraint foreign key fk_equipment_type_id (type_id) references dictionary_code (id) on delete restrict,
    constraint foreign key fk_equipment_owner (owner) references customer (id) on delete restrict,
    constraint foreign key fk_equipment_computer_room_id (computer_room_id) references computer_room (id) on delete restrict
) comment '设备';


create table if not exists cable
(
    id               varchar(32)  not null primary key,
    name             varchar(255) not null unique,
    in_equipment_id  varchar(32)  null,
    out_equipment_id varchar(32)  null,
    type_id          varchar(32)  not null,
    constraint fk_cable_in_equipment_id foreign key (in_equipment_id) references equipment (id),
    constraint fk_cable_out_equipment_id foreign key (out_equipment_id) references equipment (id),
    constraint fk_cable_type_id foreign key (type_id) references dictionary_code (id)
);

create table if not exists cable_point
(
    id           varchar(32)  not null primary key,
    name         varchar(255) not null unique,
    order_number int          null,
    cable_id     varchar(32)  not null,
    constraint cable_point_cable_id foreign key (cable_id) references cable (id)
);

create table if not exists task
(
    id                    varchar(32)  not null primary key,
    code                  varchar(255) not null unique comment '任务编号，按规律生成，具有可读性',
    name                  varchar(255) not null comment '任务名称',
    parent_id             varchar(32)  null comment '父任务编号，如果为null代表没有父任务',
    computer_room_id      varchar(32)  not null comment '关联机房的编号',
    equipment_id          varchar(32)  null comment '关联设备的编号',
    remark                text comment '备注',
    create_time           datetime(3)  not null comment '任务创建时间',
    task_type             varchar(255) null comment '任务类别，枚举类型',
    task_status           varchar(255) null comment '任务状态，枚举类型',
    create_user_id        varchar(32)  null comment '创建用户id，如果为null，代表系统创建的',
    create_user_name      varchar(32)  null comment '创建用户帐号，如果为null，代表系统创建的',
    create_user_full_name varchar(255) null comment '创建用户名称，如果为null，代表系统创建的',
    finish_user_id        varchar(32)  null comment '执行用户id，如果为null，代表系统处理',
    finish_user_name      varchar(32)  null comment '执行用户帐号，如果为null，代表系统处理',
    finish_user_full_name varchar(255) null comment '执行用户名称，如果为null，代表系统处理',
    finish                boolean      not null comment '是否已经完成',
    finish_time           datetime(3)  null,
    constraint fk_task_computer_room_id foreign key (computer_room_id) references computer_room (id) on delete restrict,
    constraint fk_task_equipment_id foreign key (equipment_id) references equipment (id) on delete restrict
) comment '任务';



