-- =============================================
-- V2: Seed initial data (permissions, roles, admin user)
-- Password: BCrypt hash of '123456'
-- =============================================

-- Seed Permissions
INSERT INTO permissions (id, name, api_path, method, module, active) VALUES
-- PRODUCTS
(gen_random_uuid()::VARCHAR, 'Create a product', '/products', 'POST', 'PRODUCTS', true),
(gen_random_uuid()::VARCHAR, 'Import product from excel', '/products/excel/import', 'POST', 'PRODUCTS', true),
(gen_random_uuid()::VARCHAR, 'Update a product', '/products/{id}', 'PUT', 'PRODUCTS', true),
(gen_random_uuid()::VARCHAR, 'Delete a product', '/products/{id}', 'DELETE', 'PRODUCTS', true),
(gen_random_uuid()::VARCHAR, 'Get a product by id', '/products/{id}', 'GET', 'PRODUCTS', true),
(gen_random_uuid()::VARCHAR, 'Get products with pagination', '/products', 'GET', 'PRODUCTS', true),
(gen_random_uuid()::VARCHAR, 'Delete all product in list', '/products/deleteAll', 'DELETE', 'PRODUCTS', true),
-- CATEGORIES
(gen_random_uuid()::VARCHAR, 'Create a category', '/categories', 'POST', 'CATEGORIES', true),
(gen_random_uuid()::VARCHAR, 'Update a category', '/categories/{id}', 'PUT', 'CATEGORIES', true),
(gen_random_uuid()::VARCHAR, 'Delete a category', '/categories/{id}', 'DELETE', 'CATEGORIES', true),
(gen_random_uuid()::VARCHAR, 'Get a category by id', '/categories/{id}', 'GET', 'CATEGORIES', true),
(gen_random_uuid()::VARCHAR, 'Get categories with pagination', '/categories', 'GET', 'CATEGORIES', true),
-- PERMISSIONS
(gen_random_uuid()::VARCHAR, 'Create a permission', '/permissions', 'POST', 'PERMISSIONS', true),
(gen_random_uuid()::VARCHAR, 'Update a permission', '/permissions/{id}', 'PUT', 'PERMISSIONS', true),
(gen_random_uuid()::VARCHAR, 'Delete a permission', '/permissions/{id}', 'DELETE', 'PERMISSIONS', true),
(gen_random_uuid()::VARCHAR, 'Get a permission by id', '/permissions/{id}', 'GET', 'PERMISSIONS', true),
(gen_random_uuid()::VARCHAR, 'Get permissions with pagination', '/permissions', 'GET', 'PERMISSIONS', true),
-- TAGS
(gen_random_uuid()::VARCHAR, 'Create a tag', '/tags', 'POST', 'TAGS', true),
(gen_random_uuid()::VARCHAR, 'Update a tag', '/tags/{id}', 'PUT', 'TAGS', true),
(gen_random_uuid()::VARCHAR, 'Delete a tag', '/tags/{id}', 'DELETE', 'TAGS', true),
(gen_random_uuid()::VARCHAR, 'Get a tag by id', '/tags/{id}', 'GET', 'TAGS', true),
(gen_random_uuid()::VARCHAR, 'Get tags with pagination', '/tags', 'GET', 'TAGS', true),
-- ROLES
(gen_random_uuid()::VARCHAR, 'Create a role', '/roles', 'POST', 'ROLES', true),
(gen_random_uuid()::VARCHAR, 'Update a role', '/roles/{id}', 'PUT', 'ROLES', true),
(gen_random_uuid()::VARCHAR, 'Delete a role', '/roles/{id}', 'DELETE', 'ROLES', true),
(gen_random_uuid()::VARCHAR, 'Get a role by id', '/roles/{id}', 'GET', 'ROLES', true),
(gen_random_uuid()::VARCHAR, 'Get roles with pagination', '/roles', 'GET', 'ROLES', true),
-- USERS
(gen_random_uuid()::VARCHAR, 'Create a user', '/users', 'POST', 'USERS', true),
(gen_random_uuid()::VARCHAR, 'Import user', '/users/excel/import', 'POST', 'USERS', true),
(gen_random_uuid()::VARCHAR, 'Export user', '/users/excel/export', 'GET', 'USERS', true),
(gen_random_uuid()::VARCHAR, 'Update a user', '/users/{id}', 'PUT', 'USERS', true),
(gen_random_uuid()::VARCHAR, 'Delete a user', '/users/{id}', 'DELETE', 'USERS', true),
(gen_random_uuid()::VARCHAR, 'Get a user by id', '/users/{id}', 'GET', 'USERS', true),
(gen_random_uuid()::VARCHAR, 'Get users with pagination', '/users', 'GET', 'USERS', true),
(gen_random_uuid()::VARCHAR, 'Get my information', '/users/my-info', 'GET', 'USERS', true),
-- ORDERS
(gen_random_uuid()::VARCHAR, 'Create a order', '/orders', 'POST', 'ORDERS', true),
(gen_random_uuid()::VARCHAR, 'Export order', '/orders/excel/export', 'GET', 'ORDERS', true),
(gen_random_uuid()::VARCHAR, 'Update a order', '/orders/{id}', 'PUT', 'ORDERS', true),
(gen_random_uuid()::VARCHAR, 'Delete a order', '/orders/{id}', 'DELETE', 'ORDERS', true),
(gen_random_uuid()::VARCHAR, 'Get a order by id', '/orders/{id}', 'GET', 'ORDERS', true),
(gen_random_uuid()::VARCHAR, 'Get orders with pagination', '/orders', 'GET', 'ORDERS', true),
(gen_random_uuid()::VARCHAR, 'Get order history', '/orders/history', 'GET', 'ORDERS', true),
-- ORDER DETAILS
(gen_random_uuid()::VARCHAR, 'Create a order details', '/orderdetails', 'POST', 'ORDERDETAILS', true),
(gen_random_uuid()::VARCHAR, 'Get order details with pagination', '/orderdetails', 'GET', 'ORDERDETAILS', true),
(gen_random_uuid()::VARCHAR, 'Get order details by id', '/orderdetails/{id}', 'GET', 'ORDERDETAILS', true),
(gen_random_uuid()::VARCHAR, 'Update a order details', '/orderdetails/{id}', 'PUT', 'ORDERDETAILS', true),
(gen_random_uuid()::VARCHAR, 'Delete a order details', '/orderdetails/{id}', 'DELETE', 'ORDERDETAILS', true),
-- CARTS
(gen_random_uuid()::VARCHAR, 'Get cart by user', '/carts', 'GET', 'CARTS', true),
(gen_random_uuid()::VARCHAR, 'Add product to cart', '/carts/add', 'POST', 'CARTS', true),
(gen_random_uuid()::VARCHAR, 'Change product quantity', '/carts/change', 'POST', 'CARTS', true),
(gen_random_uuid()::VARCHAR, 'Delete cart detail', '/carts/delete/{id}', 'DELETE', 'CARTS', true),
-- FILES
(gen_random_uuid()::VARCHAR, 'Upload a file', '/file/upload', 'POST', 'FILES', true);

-- Seed ADMIN Role
INSERT INTO roles (id, name, description, active) VALUES
('admin-role-id', 'ADMIN', 'Admin has all permissions', true);

-- Seed USER Role
INSERT INTO roles (id, name, description, active) VALUES
('user-role-id', 'USER', 'Default user role', true);

-- Assign all permissions to ADMIN role
INSERT INTO permission_role (role_id, permission_id)
SELECT 'admin-role-id', id FROM permissions;

-- Seed Admin User (password: 123456)
INSERT INTO users (id, username, password, name, role_id) VALUES
('admin-user-id', 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqadIPi6u8.FemMUIxPRCFPKJfG', 'Admin', 'admin-role-id');

-- Assign necessary permissions to USER role
INSERT INTO permission_role (role_id, permission_id)
SELECT 'user-role-id', id FROM permissions
WHERE (api_path = '/products' AND method = 'GET')
   OR (api_path = '/products/{id}' AND method = 'GET')
   OR (api_path = '/categories' AND method = 'GET')
   OR (api_path = '/categories/{id}' AND method = 'GET')
   OR (api_path = '/tags' AND method = 'GET')
   OR (api_path = '/tags/{id}' AND method = 'GET')
   OR (api_path = '/users/my-info' AND method = 'GET')
   OR (api_path = '/orders' AND method = 'POST')
   OR (api_path = '/orders/{id}' AND method = 'GET')
   OR (api_path = '/orders/{id}' AND method = 'PUT')
   OR (api_path = '/orders/history' AND method = 'GET')
   OR (api_path = '/carts' AND method = 'GET')
   OR (api_path = '/carts/add' AND method = 'POST')
   OR (api_path = '/carts/change' AND method = 'POST')
   OR (api_path = '/carts/delete/{id}' AND method = 'DELETE');
