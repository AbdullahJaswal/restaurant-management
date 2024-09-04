-- SQLite DB Init (File: restaurant.db)

-- Food
CREATE TABLE food
(
    id            INTEGER PRIMARY KEY AUTOINCREMENT                                NOT NULL,
    name          TEXT UNIQUE CHECK (name IN ('Burrito', 'Fries', 'Soda', 'Meal')) NOT NULL,
    price         DECIMAL(4, 1),
    prep_minutes  INT,
    prep_capacity INT,
    curr_capacity INT,
    image_file    TEXT                                                             NOT NULL DEFAULT 'default-placeholder.jpg'
);

INSERT INTO food (name, price, prep_minutes, prep_capacity, curr_capacity, image_file)
VALUES ('Burrito', 7.0, 9, 2, 0, 'burrito.jpg'),
       ('Fries', 4.0, 8, 5, 5, 'fries.jpg'),
       ('Soda', 2.5, 0, 0, 0, 'soda.jpg'),
       ('Meal', NULL, NULL, NULL, 0, 'meal.jpg');

-- User
CREATE TABLE user
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    first_name TEXT                              NOT NULL,
    last_name  TEXT                              NOT NULL,
    username   TEXT UNIQUE                       NOT NULL,
    password   TEXT                              NOT NULL,
    is_vip     BOOLEAN DEFAULT FALSE             NOT NULL,
    email      TEXT    DEFAULT NULL,
    credit     INTEGER DEFAULT 0                 NOT NULL
);

INSERT INTO user (first_name, last_name, username, password, is_vip, email, credit)
VALUES ('AdminF', 'AdminL', 'admin', 'admin', TRUE, 'admin@admin.com', 300),
       ('UserF', 'UserL', 'user', 'user', False, NULL, 0);

-- Order
CREATE TABLE "order"
(
    id                  INTEGER PRIMARY KEY AUTOINCREMENT                                         NOT NULL,
    user_id             INTEGER                                                                   NOT NULL REFERENCES user (id) ON DELETE SET NULL ON UPDATE CASCADE,
    status              TEXT CHECK (status IN ('AWAIT_FOR_COLLECTION', 'COLLECTED', 'CANCELLED')) NOT NULL,
    order_time          TEXT                                                                      NOT NULL,
    prep_time           TEXT                                                                      NOT NULL,
    collection_time     TEXT,
    total_cost          DECIMAL(5, 1)                                                             NOT NULL,
    user_payment_amount DECIMAL(5, 1),
    credits_used        INTEGER DEFAULT 0                                                         NOT NULL
);

CREATE TABLE order_item
(
    order_id INTEGER NOT NULL REFERENCES "order" (id) ON DELETE CASCADE ON UPDATE CASCADE,
    food_id  INTEGER NOT NULL REFERENCES food (id) ON DELETE CASCADE ON UPDATE CASCADE,
    quantity INTEGER NOT NULL,
    PRIMARY KEY (order_id, food_id)
);

-- Cart
CREATE TABLE cart
(
    id          INTEGER PRIMARY KEY AUTOINCREMENT                                           NOT NULL,
    user_id     INTEGER                                                                     NOT NULL REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE,
    burrito_qty INTEGER                                                    DEFAULT 0        NOT NULL,
    fries_qty   INTEGER                                                    DEFAULT 0        NOT NULL,
    soda_qty    INTEGER                                                    DEFAULT 0        NOT NULL,
    meal_qty    INTEGER                                                    DEFAULT 0        NOT NULL,
    status      TEXT CHECK (status IN ('ACTIVE', 'CANCELED', 'COMPLETED')) DEFAULT 'ACTIVE' NOT NULL,
    order_id    INTEGER REFERENCES "order" (id) ON DELETE SET NULL ON UPDATE CASCADE,
    created_at  TEXT                                                                        NOT NULL,
    updated_at  TEXT                                                                        NOT NULL
);
