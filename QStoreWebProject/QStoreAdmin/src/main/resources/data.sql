-- INSERTS
INSERT INTO roles(name, description)
VALUES ('Admin', 'global management'),
       ('SalesManager', 'manage products, customers, orders'),
       ('ContentManager', 'manage products, categories, brands, articles');

INSERT INTO users(email, first_name, last_name, password, enabled)
VALUES ('danila_paramonov@gmail.com', 'Danila', 'Paramonov',
        '$2y$10$EbGlr6.T0knUsw1qMEVbmup0GaX1XDSNbLfEDOVY3JXe/WWL9YwU2', 1),
       ('max_morozov@gmail.com', 'Max', 'Morozov',
        '$2y$10$j5d92uhbxB8NZu2wvzA/weE65BW9ajhd4jOYho/m2KlIWag6K2jGy', 1),
       ('sasha_beliy@gmail.com', 'Sasha', 'Beliy',
        '$2y$10$v05ebaVPN9qbSEV67OVyde6S0QUUIZSR8gQY9TBeG7tAAE4qtTogy', 1),
       ('nikita_anikievich@gmail.com', 'Nikita', 'Anikievich',
        '$2y$10$xPO63IA3kQ.DcLJDv8zvweS8pIJTYQKKnnX9cIDbFL0VRNhZohsjq', 1);

INSERT INTO users_roles(user_id, role_id)
VALUES (1, 1),
       (2, 2),
       (2, 3),
       (3, 2),
       (3, 3),
       (4, 1);
