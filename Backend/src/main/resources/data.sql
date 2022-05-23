INSERT INTO ROLE (name) VALUES  ('ROLE_ADMIN'),('ROLE_INTERMEDIATE'),('ROLE_END_USER');

INSERT INTO users( common_name, email, name, organization_name, password, role_id, surname, username, address)
VALUES ('Admin', 'admin4@gmail.com', 'Andjela', 'localhost', '$2a$10$QYw1MprXu9X2WReJsu754.CmZWzuYF1nkuhTdBx8TrOVnTozxPbne', 1, 'Djuric', 'admin', 'aaa'),--Password1234!
       ( 'Petra Jovic', 'petra4@gmail.com', 'Petra', 'continental', '$2a$10$QYw1MprXu9X2WReJsu754.CmZWzuYF1nkuhTdBx8TrOVnTozxPbne', 2, 'Jovic', 'petra', 'aaa'),--Password1234!
       ( 'Lenka Isidora Aleksic', 'lenka4@gmail.com', 'Lenka', 'continental', '$2a$10$QYw1MprXu9X2WReJsu754.CmZWzuYF1nkuhTdBx8TrOVnTozxPbne', 3, 'Aleksic', 'lenka', 'aaa');--Password1234!

