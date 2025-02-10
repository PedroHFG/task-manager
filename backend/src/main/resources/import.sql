-- Insert tb_role
INSERT INTO tb_role (authority) VALUES ('ROLE_USER');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');

-- Insert tb_user
INSERT INTO tb_user (name, email, password, created_At) VALUES ('John Doe', 'john@example.com', '123456', NOW());
INSERT INTO tb_user (name, email, password, created_At) VALUES ('Jane Smith', 'jane@example.com', '123456', NOW());
INSERT INTO tb_user (name, email, password, created_At) VALUES ('Alice Brown', 'alice@example.com', '123456', NOW());

-- Insert associação tb_user e tb_role
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);