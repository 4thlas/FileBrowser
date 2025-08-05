alter table word_file_map
drop foreign key word_file_map_words_id_fk;

alter table word_file_map
    add constraint word_file_map_words_id_fk
        foreign key (word_id) references words (id)
            on update cascade on delete cascade;
