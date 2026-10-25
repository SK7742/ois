USE school;

UPDATE users 
SET username = 'shivam.kumar', 
    full_name = 'Er. Shivam Kumar (Lead Architect)', 
    email = 'shivam.kumar@oxford.edu' 
WHERE username = 'sundram.kumar' OR full_name LIKE '%Sundram%';

UPDATE employees 
SET first_name = 'Shivam', 
    email = 'shivam.kumar@oxford.edu' 
WHERE first_name = 'Sundram' OR email = 'sundram.kumar@oxford.edu';

UPDATE activity_clubs 
SET incharge_teacher_name = 'Er. Shivam Kumar' 
WHERE incharge_teacher_name LIKE '%Sundram%';

UPDATE activity_events 
SET organizer = 'Er. Shivam Kumar' 
WHERE organizer LIKE '%Sundram%';

UPDATE inquiry_comments 
SET author_name = REPLACE(author_name, 'Sundram', 'Shivam'),
    author_role = REPLACE(author_role, 'Sundram', 'Shivam')
WHERE author_name LIKE '%Sundram%' OR author_role LIKE '%Sundram%';

UPDATE work_items 
SET assigned_to_name = 'Er. Shivam Kumar', 
    assigned_to_username = 'shivam.kumar' 
WHERE assigned_to_username = 'sundram.kumar' OR assigned_to_name LIKE '%Sundram%';

UPDATE work_items 
SET assigned_by_name = REPLACE(assigned_by_name, 'Sundram', 'Shivam'),
    assigned_by_username = REPLACE(assigned_by_username, 'sundram.kumar', 'shivam.kumar')
WHERE assigned_by_username = 'sundram.kumar' OR assigned_by_name LIKE '%Sundram%';

UPDATE timetable_slots 
SET teacher_name = 'Er. Shivam Kumar' 
WHERE teacher_name LIKE '%Sundram%';

SELECT username, full_name, email FROM users WHERE username IN ('shivam.kumar', 'sundram.kumar');
SELECT id, first_name, last_name, email, designation FROM employees WHERE first_name IN ('Shivam', 'Sundram');
