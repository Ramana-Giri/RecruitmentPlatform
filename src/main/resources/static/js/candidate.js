document.addEventListener('DOMContentLoaded', () => {
    // We load open jobs first since the tab starts active
    loadOpenJobs();

    // Add logout functionality
    document.getElementById('logoutBtn').addEventListener('click', logout);

    const candidateId = getCurrentCandidateId();
        if (candidateId) {
            document.getElementById('candidateIdInput').value = candidateId;
        }

    document.getElementById('applicationForm').addEventListener('submit', handleApplicationSubmission);
});

// Helper function to get the current candidate ID
function getCurrentCandidateId() {
    // Fetches the candidate ID stored during login
    const candidateId = localStorage.getItem('userId');
    if (candidateId) {
        return candidateId;
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
        const appliedJobIds = new Set(
            appliedJobs
                .map(app => app.job?.id)
                .filter(id => id != null)
        );

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
            const job = app.job || {};
            // FIX: Use these safe variables in the template below
            const jobTitle = job.title || 'Job Title Unavailable';
            const jobLocation = job.location || 'Location N/A';
            const applicationDate = app.appliedDate || app.applicationDate || Date.now();
            const statusClass = getStatusBadge(app.status);

            const card = document.createElement('div');
            card.className = 'col-md-6 col-lg-4 mb-4';
            card.innerHTML = `
                <div class="card shadow-sm h-100">
                    <div class="card-body">
                        <h5 class="card-title">${jobTitle}</h5> <p class="card-subtitle mb-2 text-muted">${jobLocation}</p> <p class="card-text">
                            Status: <span class="badge ${statusClass}">${app.status.replace('_', ' ')}</span>
                        </p>
//                        <small class="text-secondary">Applied on: ${new Date(applicationDate).toLocaleDateString()}</small>
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

// --- UTILITY/MODAL LOGIC ---

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

// This function now acts as the central trigger for the application modal
function applyToJob(jobId, jobTitle) {
    document.getElementById('applicationForm').reset();
    document.getElementById('applicationStatusMessage').innerHTML = '';

    // Set Job ID and Title
    document.getElementById('jobIdToApply').value = jobId;
    document.getElementById('jobTitleToApply').textContent = jobTitle;

    // Show the modal
    const applicationModal = new bootstrap.Modal(document.getElementById('applicationModal'));
    applicationModal.show();
}


async function showJobDetails(jobId) {
    try {
        const job = await fetchApi(`/api/jobs/${jobId}`, 'GET');

        // Populate Modal Content
        document.getElementById('jobDetailsModalLabel').textContent = job.title;
        document.getElementById('jobDetailsLocation').textContent = job.location;

        // Use safe fallback for missing fields
        document.getElementById('jobDetailsDescription').innerHTML =
            (job.description || 'No description provided.').replace(/\n/g, '<br>');

        // Use safe fallback for missing fields (assuming requiredSkills is the backend field)
        document.getElementById('jobDetailsRequirements').innerHTML =
            (job.requiredSkills || 'No specific requirements listed.').replace(/\n/g, '<br>');

        // Set the Apply button action
        const applyBtn = document.getElementById('applyFromDetailsBtn');
        applyBtn.onclick = () => {
            // Close details modal
            const detailsModal = bootstrap.Modal.getInstance(document.getElementById('jobDetailsModal'));
            if (detailsModal) detailsModal.hide();

            // FIXED: Call the defined function, applyToJob, which resolves the ReferenceError
            applyToJob(job.id, job.title);
        };

        // Show the modal
        const jobDetailsModal = new bootstrap.Modal(document.getElementById('jobDetailsModal'));
        jobDetailsModal.show();

    } catch (error) {
        // We log the real error but show a friendlier message to the user
        console.error('Job Details Error:', error);
        alert('Failed to load job details. Please check the console for API errors.');
    }
}

async function handleApplicationSubmission(event) {
    event.preventDefault(); // Stop the default form submission

    const form = event.target;
    const submitBtn = document.getElementById('submitApplicationBtn');
    const statusMsg = document.getElementById('applicationStatusMessage');

    submitBtn.disabled = true;
    statusMsg.innerHTML = '<p class="text-info">Submitting application and uploading resume...</p>';

    try {
        // FormData is CRITICAL for handling file uploads (multipart/form-data)
        const formData = new FormData(form);

        const candidateId = getCurrentCandidateId();
        if (!candidateId) {
            throw new Error("Candidate session lost. Please log in again.");
        }

        // POST to your backend endpoint: /api/applications/submit
        const response = await fetch('/api/applications/submit', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token')
                // No Content-Type header is set when using FormData
            },
            body: formData
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Server responded with status ${response.status}: ${errorText}`);
        }

        // Close the modal on success
        const applicationModal = bootstrap.Modal.getInstance(document.getElementById('applicationModal'));
        if (applicationModal) applicationModal.hide();

        alert("Application successful! You can now track its status in the 'Applied Jobs' tab.");
        loadOpenJobs();
        loadAppliedJobs();

    } catch (error) {
        statusMsg.innerHTML = `<p class="text-danger">Submission failed: ${error.message}</p>`;
        console.error('Application Submission Error:', error);
    } finally {
        submitBtn.disabled = false;
    }
}


function logout() {
    // Clear credentials and redirect
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('userRole');
    window.location.href = 'login.html';
}