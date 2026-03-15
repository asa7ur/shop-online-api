-- =========================
-- USERS
-- =========================
INSERT INTO users (name, email, password)
VALUES ('Ana Garcia', 'ana@email.com', '$2b$12$FVRijCavVZ7Qt15.CQssHe9m/6eLAdjAv0PiOKFIjMU161wApxzye'),
       ('Luis Perez', 'luis@email.com', '$2b$12$FVRijCavVZ7Qt15.CQssHe9m/6eLAdjAv0PiOKFIjMU161wApxzye');

-- =========================
-- ROLES
-- =========================
INSERT IGNORE INTO roles (id, name)
VALUES (1, 'ROLE_ADMIN'),
       (3, 'ROLE_USER');

-- =========================
-- USERS_ROLES
-- =========================
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (2, 2);

-- =========================
-- CATEGORIES
-- =========================
INSERT INTO categories (name, description)
VALUES ('Electronics', 'Electronic devices'),
       ('Books', 'Books and educational material');

-- =========================
-- PRODUCTS
-- =========================
INSERT INTO products (name, description, price, category_id)
VALUES
-- Electrónica (1)
('Lenovo Laptop', '15 inch laptop with Intel i5 processor', 899.99, 1),
('HP Laptop', '14 inch laptop with AMD Ryzen 5', 749.00, 1),
('Bluetooth Headphones', 'Wireless over-ear headphones', 59.90, 1),
('Wireless Earbuds', 'True wireless earbuds with charging case', 39.99, 1),
('Mechanical Keyboard', 'RGB mechanical keyboard with blue switches', 79.99, 1),
('Wireless Mouse', 'Ergonomic wireless mouse', 24.50, 1),
('Gaming Mouse', 'High precision gaming mouse', 49.99, 1),
('27 Inch Monitor', 'Full HD LED monitor', 189.00, 1),
('USB-C Hub', 'Multiport adapter with HDMI and USB ports', 34.99, 1),
('External Hard Drive', '1TB USB 3.0 external hard drive', 64.90, 1),
('SSD 500GB', '500GB solid state drive', 59.00, 1),
('Smartphone Android', '6.5 inch Android smartphone', 299.99, 1),
('Tablet 10 Inch', '10 inch tablet with 64GB storage', 219.00, 1),
('Spring Boot Book', 'Practical guide to Spring Boot', 39.95, 2),
('Java Programming Book', 'Complete guide to Java programming', 45.00, 2),
('Clean Code', 'A Handbook of Agile Software Craftsmanship', 42.95, 2),
('Effective Java', 'Best practices for the Java platform', 47.50, 2),
('Microservices with Spring', 'Building microservices using Spring', 44.90, 2),
('Design Patterns Book', 'Elements of Reusable Object-Oriented Software', 49.99, 2),
('Hibernate in Action', 'Mastering Hibernate ORM', 41.00, 2),
('Docker for Developers', 'Containerization for modern applications', 38.90, 2),
('REST APIs with Spring', 'Developing RESTful APIs using Spring', 36.50, 2),
('Learning SQL', 'Beginner to advanced SQL guide', 29.95, 2),
('Web Development Basics', 'HTML, CSS and JavaScript fundamentals', 27.99, 2);

-- =========================
-- ORDERS
-- =========================
INSERT INTO orders (user_id)
VALUES (1),
       (2);

-- =========================
-- ORDER_PRODUCTS
-- =========================
INSERT INTO order_products (order_id, product_id, quantity)
VALUES (1, 1, 1), -- Order 1 → Laptop
       (1, 2, 1), -- Order 1 → Headphones
       (2, 3, 1); -- Order 2 → Book
