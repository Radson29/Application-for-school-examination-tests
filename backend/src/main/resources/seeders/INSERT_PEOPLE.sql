
INSERT INTO groups (name, createdAt, modifiedAt)
VALUES  ('1a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        ('1b', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        ('1c', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        ('2a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO people (firstName, lastName, pesel, createdAt, modifiedAt)
VALUES  ('Jerzy',    'Dudzic', '33311122211', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        ('Andrzej',  'Krasny', '22211122211', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        ('Marek',    'Sztywny','00001412223', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        ('Tomasz',   'Działo', '00011122212', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        ('Bear',     'Grylls', '00011122211', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        ('Kurt',     'Cobain', '00011122210', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO people_groups (groupId, personId)
VALUES  (1, 3),
        (2, 4),
        (3, 5),
        (4, 6);

INSERT INTO logins (personId, roleId, login, password, resetPassword)
VALUES  (1,  3, 'root','$2a$10$gpnrnkyMzbcbVgOE5g4bd.CjfSs8duF9cEINVKGjUpmQ1n7y20a6a', 0),
        (2,  2, 'teacher','$2a$10$gpnrnkyMzbcbVgOE5g4bd.CjfSs8duF9cEINVKGjUpmQ1n7y20a6a', 0),
        (3,  1, 'patryk.s','$2a$10$gpnrnkyMzbcbVgOE5g4bd.CjfSs8duF9cEINVKGjUpmQ1n7y20a6a', 0),
        (4,  1, 'tomasz.d','$2a$10$gpnrnkyMzbcbVgOE5g4bd.CjfSs8duF9cEINVKGjUpmQ1n7y20a6a', 0),
        (5,  1, 'bear.b','$2a$10$gpnrnkyMzbcbVgOE5g4bd.CjfSs8duF9cEINVKGjUpmQ1n7y20a6a', 0),
        (6,  1, 'kurt.c','$2a$10$gpnrnkyMzbcbVgOE5g4bd.CjfSs8duF9cEINVKGjUpmQ1n7y20a6a', 0);
