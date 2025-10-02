document.addEventListener('DOMContentLoaded', () => {
    loadJobsForManagement();
    const form = document.getElementById('jobPostingForm');
    if (form) {
        form.addEventListener('submit', handleJobSubmit);
    }
});

function resetForm() {
    document.getElementById('jobPostingForm').reset();
    document.getElementById('jobId').value = '';
    document.getElementById('submitBtn').textContent = 'Post Job';
    document.getElementById('formTitle').textContent = 'Create New Job Posting';
    document.getElementById('cancelBtn').style.display = 'none';
}

async function loadJobsForManagement() {
    const jobList = document.getElementById('jobList');
    try {
        // This endpoint requires ROLE_ADMIN
        const allJobs = await fetchApi('/api/jobs', 'GET');
        jobList.innerHTML = '';
        allJobs.forEach(job => {
            const statusClass = job.active ? 'success' : 'secondary';
            const statusText = job.active ? 'Active' : 'Archived';
            const jobItem = document.createElement('div');
            jobItem.className = 'col-lg-6 mb-3';
            jobItem.innerHTML = `
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">${job.title} <span class="badge bg-${statusClass}">${statusText}</span></h5>
                        <p class="card-text">${job.location} | Skills: ${job.requiredSkills}</p>
                        <button class="btn btn-sm btn-info me-2" onclick="editJob(${job.id})">Edit</button>
                        <button class="btn btn-sm btn-danger" onclick="archiveJob(${job.id})" ${job.active ? '' : 'disabled'}>Archive</button>
                    </div>
                </div>
            `;
            jobList.appendChild(jobItem);
        });
    } catch (error) {
        jobList.innerHTML = `<div class="col-12"><p class="text-danger">Failed to load jobs: ${error.message}</p></div>`;
        console.error('Error loading jobs:', error);
    }
}

async function handleJobSubmit(event) {
    event.preventDefault();
    const jobId = document.getElementById('jobId').value;
    const method = jobId ? 'PUT' : 'POST';
    const endpoint = jobId ? `/api/jobs/${jobId}` : '/api/jobs';

    const jobData = {
        title: document.getElementById('title').value,
        location: document.getElementById('location').value,
        requiredSkills: document.getElementById('requiredSkills').value,
        description: document.getElementById('description').value,
        active: document.getElementById('isActive').checked
    };

    try {
        await fetchApi(endpoint, method, jobData);
        alert(`Job ${jobId ? 'updated' : 'posted'} successfully!`);
        resetForm();
        loadJobsForManagement();

    } catch (error) {
        alert('Job operation failed: ' + error.message);
        console.error('Job submission error:', error);
    }
}

async function editJob(id) {
    try {
        const job = await fetchApi(`/api/jobs/${id}`, 'GET');

        document.getElementById('jobId').value = job.id;
        document.getElementById('title').value = job.title;
        document.getElementById('location').value = job.location;
        document.getElementById('requiredSkills').value = job.requiredSkills;
        document.getElementById('description').value = job.description;
        document.getElementById('isActive').checked = job.active;

        document.getElementById('formTitle').textContent = 'Edit Job Posting (ID: ' + job.id + ')';
        document.getElementById('submitBtn').textContent = 'Update Job';
        document.getElementById('cancelBtn').style.display = 'inline-block';
        const createTabButton = document.getElementById('create-tab');
        if (createTabButton) {
            const tab = new bootstrap.Tab(createTabButton);
            tab.show();
        }
        window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch (error) {
        alert('Could not load job for editing: ' + error.message);
    }
}

async function archiveJob(id) {
    if (confirm('Are you sure you want to archive this job? It will no longer be visible to candidates.')) {
        try {
            // Note: DELETE mapping on the backend actually sets isActive to false
            await fetchApi(`/api/jobs/${id}`, 'DELETE');
            alert('Job archived successfully.');
            loadJobsForManagement();
        } catch (error) {
            alert('Failed to archive job: ' + error.message);
        }
    }
}