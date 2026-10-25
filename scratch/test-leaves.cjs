async function test() {
    const loginRes = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: 'teacher.sharma', password: 'Teacher@123' })
    });
    const login = await loginRes.json();
    console.log('Login:', login);
    const token = login.data?.accessToken || login.data?.token;

    const leavesRes = await fetch('http://localhost:8080/api/finance/leaves', {
        headers: { 'Authorization': 'Bearer ' + token }
    });
    console.log('Leaves status:', leavesRes.status);
    const text = await leavesRes.text();
    console.log('Leaves body:', text);
}

test().catch(console.error);
