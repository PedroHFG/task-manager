-- Insert tb_role
INSERT INTO tb_role (authority) VALUES ('ROLE_USER');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');

-- Insert tb_user
INSERT INTO tb_user (name, email, password, created_At) VALUES ('John Doe', 'john@example.com', '$2a$10$IjdE3RH0y77VCOKjB/JIPOZiEVALzatHsTd1qSnoaFJc22iMtH8Ay', NOW());
INSERT INTO tb_user (name, email, password, created_At) VALUES ('Jane Smith', 'jane@example.com', '$2a$10$IjdE3RH0y77VCOKjB/JIPOZiEVALzatHsTd1qSnoaFJc22iMtH8Ay', NOW());
INSERT INTO tb_user (name, email, password, created_At) VALUES ('Alice Brown', 'alice@example.com', '$2a$10$IjdE3RH0y77VCOKjB/JIPOZiEVALzatHsTd1qSnoaFJc22iMtH8Ay', NOW());
INSERT INTO tb_user (name, email, password, created_At) VALUES ('Pedro Henrique', 'pedro@example.com', '$2a$10$IjdE3RH0y77VCOKjB/JIPOZiEVALzatHsTd1qSnoaFJc22iMtH8Ay', NOW());

-- Insert associação tb_user e tb_role
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (3, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (4, 1);

-- Inserir Tasks
INSERT INTO tb_task (title, description, status, due_date, user_id, created_At) VALUES ('Finalizar relatório de vendas', 'Elaborar relatório financeiro com dados de vendas do último trimestre.', 0, '2024-12-20', 3, NOW());
INSERT INTO tb_task (title, description, status, due_date, user_id, created_At) VALUES ('Revisar código do projeto X', 'Revisar o código da API para resolver problemas de performance.', 0, '2024-12-15', 1, NOW());
INSERT INTO tb_task (title, description, status, due_date, user_id, created_At) VALUES ('Reunião com a equipe', 'Organizar a reunião para discutir o progresso do projeto e definir próximos passos.', 1, '2024-12-01', 2, NOW());
INSERT INTO tb_task (title, description, status, due_date, user_id, created_At) VALUES ('Planejar a apresentação para o cliente', 'Criar slides de apresentação e ensaiar a fala para o cliente.', 2, '2024-11-30', 2, NOW());
INSERT INTO tb_task (title, description, status, due_date, user_id, created_At) VALUES ('Atualizar o sistema de gerenciamento', 'Verificar e aplicar as atualizações de segurança no sistema.', 1, '2024-12-30', 1, NOW());
INSERT INTO tb_task (title, description, status, due_date, user_id, created_At) VALUES ('Atualizar o Power Bi', 'Fazer atualizações de relatórios de performance.', 2, '2024-12-30', 1, NOW());
