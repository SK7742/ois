USE school;
UPDATE users SET password = '$2a$10$pkQjvCEoit4dSU9U0n/mOejtFKE5oCCkVlZraTNZNC8GRa9L2ADnO' WHERE username = 'shivam.kumar';
SELECT username, full_name, password FROM users WHERE username = 'shivam.kumar';
