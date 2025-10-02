document.addEventListener('DOMContentLoaded', () => {
    // We load open jobs first since the tab starts active
    loadOpenJobs();

    // Add logout functionality
    document.getElementById('logoutBtn').addEventListener('click', logout);
});

// Helper function to get the current candidate ID
function getCurrentCandidateId() {
    // Fetches the candidate ID stored during login
    const candidateJson = localStorage.getItem('userId');
    if (candidateJson) {
        return candidateJson;
    }
    console.error("Candidate ID not found in localStorage (Key: 'userId').");
    return null;
}

// --- OPEN JOBS LOGIC (Jobs the candidate has NOT applied to) ---

async function loadOpenJobs() {
    const candidateId = getCurrentCandidateId();
    const messageEl = document.getElementById('openJobsMessage');
    const listEl = document.getElementById('openJobsList');

    listEl.innerHTML = '';
    messageEl.textContent = 'Fetching open opportunities...';

    if (!candidateId) {
        messageEl.textContent = 'Error: Candidate session lost. Please log in again.';
        return;
    }

    try {
        // Fetch all active jobs using your JobController endpoint
        const allJobs = await fetchApi('/api/jobs/active', 'GET');

        // Fetch applied jobs list to filter locally (best done on backend if possible)
        const appliedJobs = await fetchApi(`/api/applications/candidate/${candidateId}`, 'GET');
        const appliedJobIds = new Set(appliedJobs.map(app => app.jobPosting.id));

        // Filter: Only show jobs the candidate has NOT applied to
        const openJobs = allJobs.filter(job => !appliedJobIds.has(job.id));

        if (openJobs.length === 0) {
            messageEl.textContent = appliedJobIds.size > 0
                ? 'You have applied to all current openings!'
                : 'No active job postings available right now.';
            return;
        }

        messageEl.textContent = ''; // Clear message on success

        openJobs.forEach(job => {
            const card = document.createElement('div');
            card.className = 'col-md-6 col-lg-4 mb-4';
            card.innerHTML = `
                <div class="card shadow-sm h-100">
                    <div class="card-body">
                        <h5 class="card-title text-primary">${job.title}</h5>
                        <p class="card-subtitle mb-2 text-muted">${job.location}</p>
                        <p class="card-text">${job.description.substring(0, 100)}...</p>
                        <button class="btn btn-sm btn-outline-primary me-2" onclick="showJobDetails(${job.id})">Details</button>
                        <button class="btn btn-sm btn-success" onclick="applyToJob(${job.id}, '${job.title}')">Apply Now</button>
                    </div>
                </div>
            `;
            listEl.appendChild(card);
        });

    } catch (error) {
        messageEl.textContent = 'Failed to load open jobs: ' + error.message;
        console.error('Open Jobs Error:', error);
    }
}

// --- APPLIED JOBS LOGIC (Jobs the candidate HAS applied to, with status) ---

async function loadAppliedJobs() {
    const candidateId = getCurrentCandidateId();
    const messageEl = document.getElementById('appliedJobsMessage');
    const listEl = document.getElementById('appliedJobsList');

    listEl.innerHTML = '';
    messageEl.textContent = 'Fetching your application history...';

    if (!candidateId) {
        messageEl.textContent = 'Error: Candidate session lost. Please log in again.';
        return;
    }

    try {
        // Fetch applications specific to the candidate using the existing endpoint
        const applications = await fetchApi(`/api/applications/candidate/${candidateId}`, 'GET');

        if (applications.length === 0) {
            messageEl.textContent = 'You have not applied to any jobs yet.';
            return;
        }

        messageEl.textContent = ''; // Clear message on success

        applications.forEach(app => {
            // Note: We expect 'jobPosting' property to exist within the Application object
            const job = app.jobPosting;
            const statusClass = getStatusBadge(app.status);

            const card = document.createElement('div');
            card.className = 'col-md-6 col-lg-4 mb-4';
            card.innerHTML = `
                <div class="card shadow-sm h-100">
                    <div class="card-body">
                        <h5 class="card-title">${job.title}</h5>
                        <p class="card-subtitle mb-2 text-muted">${job.location}</p>
                        <p class="card-text">
                            Status: <span class="badge ${statusClass}">${app.status.replace('_', ' ')}</span>
                        </p>
                        <small class="text-secondary">Applied on: ${new Date(app.applicationDate).toLocaleDateString()}</small>
                    </div>
                </div>
            `;
            listEl.appendChild(card);
        });

    } catch (error) {
        messageEl.textContent = 'Failed to load applied jobs: ' + error.message;
        console.error('Applied Jobs Error:', error);
    }
}

// --- UTILITY/MOCK LOGIC ---

function getStatusBadge(status) {
    switch (status) {
        case 'SUBMITTED':
            return 'bg-secondary';
        case 'INTERVIEW_SCHEDULED':
            return 'bg-info text-dark';
        case 'OFFER_EXTENDED':
            return 'bg-success';
        case 'REJECTED':
            return 'bg-danger';
        default:
            return 'bg-primary';
    }
}

function applyToJob(jobId, jobTitle) {
    alert(`Applying to job: ${jobTitle} (ID: ${jobId}).\n\n**Next step:** We need a form or modal to handle resume upload and application submission (POST /api/applications/submit).`);
}

function showJobDetails(jobId) {
    alert(`Displaying details for job ID: ${jobId}.\n\n**Next step:** Implement a modal or dedicated page to show the full job description.`);
}

function logout() {
    // Clear credentials and redirect
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('userRole');
    window.location.href = 'login.html';
}
