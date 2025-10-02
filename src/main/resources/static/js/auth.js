
// --- EVENT LISTENERS ---
document.addEventListener('DOMContentLoaded', () => {
    // 1. Attach listener for the Registration form (ID: registrationForm)
    const registrationForm = document.getElementById('registrationForm');
    if (registrationForm) {
        registrationForm.addEventListener('submit', handleRegistration);
    }

    // 2. Attach listener for the Login form (ID: loginForm)
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
});

// --- REGISTRATION HANDLER ---
async function handleRegistration(event) {
    event.preventDefault();
    
    const messageEl = document.getElementById('registrationMessage');
    messageEl.textContent = 'Registering...';
    messageEl.className = 'mt-3 text-center text-info';
    
    const requestBody = {
        firstName: document.getElementById('firstName').value,
        lastName: document.getElementById('lastName').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        role: document.getElementById('role').value
    };

    try {
        await fetchApi('/api/candidates/register', 'POST', requestBody);
        
        messageEl.className = 'mt-3 text-center text-success';
        messageEl.textContent = 'Registration successful! Redirecting to login...';
        
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 2000);
        
    } catch (error) {
        messageEl.className = 'mt-3 text-center text-danger';
        messageEl.textContent = 'Registration failed: ' + error.message;
        console.error('Registration error:', error);
    }
}

// --- LOGIN HANDLER ---
async function handleLogin(event) {
    event.preventDefault();
    const messageEl = document.getElementById('loginMessage');
    
    // FIX: Use the correct IDs from login.html
    const requestBody = {
        email: document.getElementById('loginEmail').value,    // Corrected ID
        password: document.getElementById('loginPassword').value // Corrected ID
    };

    messageEl.textContent = 'Logging in...';
    messageEl.className = 'mt-3 text-center text-info';

    try {
        const response = await fetchApi('/api/candidates/login', 'POST', requestBody);
        
        // Assuming your backend /api/candidates/login returns the Candidate object on success
        localStorage.setItem('userId', response.id);
        localStorage.setItem('userRole', response.role.replace('ROLE_', '')); // Store as ADMIN or CANDIDATE

        messageEl.className = 'mt-3 text-center text-success';
        messageEl.textContent = 'Login successful! Redirecting...';

        if (response.role.includes('ADMIN')) {
            window.location.href = 'admin_dashboard.html';
        } else {
            window.location.href = 'candidate_dashboard.html';
        }

    } catch (error) {
        messageEl.className = 'mt-3 text-center text-danger';
        messageEl.textContent = 'Login failed. Invalid credentials.';
        console.error('Login error:', error);
    }
}