CREATE TABLE IF NOT EXISTS USERS (
                                     ID          BIGINT generated by default as identity primary key,
                                     NAME        VARCHAR(64) not null,
                                     EMAIL       VARCHAR(64) not null,
                                     CONSTRAINT un_email UNIQUE (EMAIL)
);

CREATE TABLE IF NOT EXISTS ITEMS (
                                     ID              BIGINT generated by default as identity primary key,
                                     NAME            VARCHAR(64) not null,
                                     DESCRIPTION     VARCHAR(64) not null,
                                     IS_AVAILABLE    BOOLEAN not null,
                                     OWNER_ID        BIGINT,
                                     REQUEST_ID      BIGINT,
                                     CONSTRAINT fk_user
                                         FOREIGN KEY (owner_id)
                                             REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS BOOKINGS (
                                        ID              BIGINT generated by default as identity primary key,
                                        START_TIME      TIMESTAMP WITHOUT TIME ZONE not null,
                                        END_TIME        TIMESTAMP WITHOUT TIME ZONE not null,
                                        ITEM_ID         BIGINT,
                                        BOOKER_ID       BIGINT,
                                        STATUS          VARCHAR,
                                        CONSTRAINT fk_item
                                            FOREIGN KEY (item_id)
                                                REFERENCES items(id),
                                        CONSTRAINT fk_booker
                                            FOREIGN KEY (booker_id)
                                                REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS COMMENTS (
                                        ID              BIGINT generated by default as identity primary key,
                                        TEXT            VARCHAR not null,
                                        ITEM_ID         BIGINT not null,
                                        AUTHOR_ID       BIGINT not null,
                                        CONSTRAINT fk_item_comment
                                            FOREIGN KEY (item_id)
                                                REFERENCES items(id),
                                        CONSTRAINT fk_author
                                            FOREIGN KEY (author_id)
                                                REFERENCES users(id)
);