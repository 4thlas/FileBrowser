create table files
(
    id            bigint auto_increment
        primary key,
    filename      varchar(255) not null,
    size          bigint       not null,
    last_modified date         not null
);

create table words
(
    id   bigint auto_increment
        primary key,
    word varchar(255) not null
);

create table word_file_map
(
    word_id bigint not null,
    file_id bigint not null,
    constraint word_file_map_files_id_fk
        foreign key (file_id) references files (id)
            on update cascade on delete cascade,
    constraint word_file_map_words_id_fk
        foreign key (word_id) references words (id)
            on delete cascade
);