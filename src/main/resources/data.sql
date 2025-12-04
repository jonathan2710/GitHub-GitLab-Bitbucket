--Datos de prueba para User
INSERT INTO users (username, password, role, blocked, created_at, update_at) VALUES ('admin','123456','ADMIN', false, CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO users (username, password, role, blocked, created_at, update_at) VALUES ('user1','123456','USER', false, CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO users (username, password, role, blocked, created_at, update_at) VALUES ('user2','123456','USER', false, CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO users (username, password, role, blocked, created_at, update_at) VALUES ('user_block','123456','USER', true, CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
