INSERT INTO people (firstName, lastName, pesel, createdAt, modifiedAt)
    VALUES  ('Jerzy',    'Dudzic', '33311122211', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO logins (personId, roleId, login, password, resetPassword)
    VALUES  (1,  3, 'root','$2a$10$gpnrnkyMzbcbVgOE5g4bd.CjfSs8duF9cEINVKGjUpmQ1n7y20a6a', 0);