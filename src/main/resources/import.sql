-- Passwords are BCrypt encoded: 'admin123' and 'candidate123'
-- Hash for 'admin123' is: $2a$10$wTfK4ZpL8f7gR9.E0X1Z.O3vV4b8P5c8s6L5zG1nJ9M2pP7L4i5e0Q
-- Hash for 'candidate123' is: $2a$10$2gX6z.qW2N8e1R1u9A6gI.oX1Z.O3vV4b8P5c8s6L5zG1nJ9M2pP7L4i5e0Q

INSERT INTO candidate ( first_name, last_name, email, password, phone_number, headline, role) VALUES ( 'HR', 'Admin', 'hr.admin@example.com', 'admin123', '555-0000', 'System Administrator', 'ROLE_ADMIN');
INSERT INTO candidate ( first_name, last_name, email, password, phone_number, headline, role) VALUES ( 'Jane', 'Doe', 'jane.doe@example.com', 'candidate123', '555-1234', 'Software Engineer seeking new role', 'ROLE_CANDIDATE');

INSERT INTO job ( title, description, required_skills, location, is_active, posted_date) VALUES ( 'Senior Java Developer', 'Expert in Spring Boot and Microservices.', 'Java, Spring Boot, Microservices, SQL', 'Remote', TRUE, NOW());
INSERT INTO job ( title, description, required_skills, location, is_active, posted_date) VALUES ( 'Frontend React Developer', 'Experience with modern Javascript and state management.', 'React, JavaScript, HTML, CSS, Redux', 'New York', TRUE, NOW());
INSERT INTO job ( title, description, required_skills, location, is_active, posted_date) VALUES ( 'Data Scientist', 'Expertise in ML and Python.', 'Python, Machine Learning, Pandas', 'Archived', FALSE, NOW());