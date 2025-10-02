//document.addEventListener('DOMContentLoaded', () => {
//    loadJobListForScreening();
//});
//
//async function loadJobListForScreening() {
//    const jobSelect = document.getElementById('jobSelect');
//    try {
//        // This endpoint requires ROLE_ADMIN
//        const jobs = await fetchApi('/api/jobs', 'GET');
//        jobs.forEach(job => {
//            const option = document.createElement('option');
//            option.value = job.id;
//            option.textContent = `${job.title} (${job.location})`;
//            jobSelect.appendChild(option);
//        });
//    } catch (error) {
//        document.getElementById('message').textContent = 'Failed to load jobs for selection.';
//        console.error('Error loading jobs:', error);
//    }
//}
//
//async function fetchScreeningResults() {
//    const jobId = document.getElementById('jobSelect').value;
//    const resultsDiv = document.getElementById('screeningResults');
//    const messageElement = document.getElementById('message');
//    resultsDiv.innerHTML = '';
//
//    if (!jobId) {
//        if (messageElement) {
//            messageElement.className = 'text-muted';
//            messageElement.textContent = 'Select a job to view ranked applications.';
//        }
//        return;
//    }
//
//    try {
//        const applications = await fetchApi(`/api/screening/job/${jobId}`, 'GET');
//
//        if (messageElement) {
//            messageElement.textContent = '';
//            messageElement.className = '';
//        }
//
//        if (applications.length === 0) {
//            if (messageElement) {
//                messageElement.className = 'text-warning';
//                messageElement.textContent = 'No applications found for this job yet.';
//            }
//            return;
//        }
//
//        const header = document.createElement('div');
//        header.className = 'col-12 mb-3';
//        header.innerHTML = '<h4>Ranked Candidates</h4>';
//        resultsDiv.appendChild(header);
//
//        applications.forEach((app, index) => {
//
//            const score = app.screeningScore ? (app.screeningScore * 100).toFixed(0) : 0;
//
//            let scoreClass = 'text-muted'; // Default class
//                        if (score >= 80) {
//                            scoreClass = 'text-success'; // High score
//                        } else if (score >= 50) {
//                            scoreClass = 'text-warning'; // Medium score
//                        } else {
//                            scoreClass = 'text-danger'; // Low score or no score
//                        }
//
//            const card = document.createElement('div');
//            card.className = 'col-md-6 mb-4';
//            card.innerHTML = `
//                <div class="card shadow-sm">
//                    <div class="card-body">
//                        <h5 class="card-title">${index + 1}. ${app.candidate.firstName} ${app.candidate.lastName}</h5>
//                        <p class="card-text mb-1">Score: <span class="${scoreClass} fw-bold">${score}%</span></p>
//                        <p class="card-text mb-3">Status: <span class="badge bg-primary">${app.status}</span></p>
//                        <button class="btn btn-sm btn-info me-2" onclick="viewResume(${app.id})">View Resume</button>
//                        <button class="btn btn-sm btn-success me-2" onclick="updateStatus(${app.id}, 'INTERVIEW_SCHEDULED')">Interview</button>
//                        <button class="btn btn-sm btn-danger" onclick="updateStatus(${app.id}, 'REJECTED')">Reject</button>
//                    </div>
//                </div>
//            `;
//            resultsDiv.appendChild(card);
//        });
//
//    } catch (error) {
//        // Only show error if the request itself failed
//        resultsDiv.innerHTML = '';
//        messageElement.className = 'text-danger';
//        messageElement.textContent = 'Error fetching results: ' + error.message;
//        console.error('Screening error:', error);
//    }
//}
//
//async function updateStatus(applicationId, newStatus) {
//    try {
//        // NOTE: Endpoint moved to /api/applications/
//        await fetchApi(`/api/applications/${applicationId}/status?status=${newStatus}`, 'PUT');
//        // Do not use alert, use a better UI notification, but for now:
//        console.log(`Application status updated to: ${newStatus}`);
//        fetchScreeningResults(); // Reload the list to refresh the status display
//    } catch (error) {
//        alert('Failed to update status: ' + error.message);
//    }
//}
//
//async function viewResume(applicationId) {
//    try {
//        // Endpoint from the new ResumeDataController
//        const resumeData = await fetchApi(`/api/admin/resumes/application/${applicationId}`, 'GET');
//
//        // Display a modal or an alert with the parsed data
//        alert(
//            `Parsed Resume Data:\n` +
//            `Name: ${resumeData.parsedName}\n` +
//            `Email: ${resumeData.parsedEmail}\n` +
//            `Skills: ${resumeData.parsedSkills}\n\n` +
//            `Raw Text (First 200 chars):\n${resumeData.rawText.substring(0, 200)}...`
//        );
//    } catch (error) {
//        alert('Failed to retrieve resume data. Ensure ResumeDataController is set up and data exists: ' + error.message);
//    }
//}


document.addEventListener('DOMContentLoaded', () => {
    loadJobListForScreening();
});

async function loadJobListForScreening() {
    const jobSelect = document.getElementById('jobSelect');
    const messageElement = document.getElementById('message'); // Get the message element here too

    try {
        // This endpoint requires ROLE_ADMIN
        const jobs = await fetchApi('/api/jobs', 'GET');
        jobs.forEach(job => {
            const option = document.createElement('option');
            option.value = job.id;
            option.textContent = `${job.title} (${job.location})`;
            jobSelect.appendChild(option);
        });
    } catch (error) {
        if (messageElement) {
             messageElement.className = 'text-danger';
             messageElement.textContent = 'Failed to load jobs for selection.';
        }
        console.error('Error loading jobs:', error);
    }
}

async function fetchScreeningResults() {
    const jobId = document.getElementById('jobSelect').value;
    const resultsDiv = document.getElementById('screeningResults');
    const messageElement = document.getElementById('message');

    // FIX: 1. Clear previous results (Stops stacking candidates)
    resultsDiv.innerHTML = '';

    // Set loading message on the persistent element
    if (messageElement) {
        messageElement.className = 'text-info';
        messageElement.textContent = 'Loading screening results...';
    }

    if (!jobId) {
        if (messageElement) {
            messageElement.className = 'text-muted';
            messageElement.textContent = 'Select a job to view ranked applications.';
        }
        return;
    }

    try {
        const applications = await fetchApi(`/api/screening/job/${jobId}`, 'GET');

        // Clear the loading message after a successful API call
        if (messageElement) {
            messageElement.textContent = '';
            messageElement.className = '';
        }

        if (applications.length === 0) {
            if (messageElement) {
                messageElement.className = 'text-warning';
                messageElement.textContent = 'No applications found for this job yet.';
            }
            return;
        }

        const header = document.createElement('div');
        header.className = 'col-12 mb-3';
        header.innerHTML = '<h4>Ranked Candidates</h4>';
        resultsDiv.appendChild(header);

        applications.forEach((app, index) => {

            const score = app.screeningScore ? (app.screeningScore * 100).toFixed(0) : 0;

            // FIX: Defined scoreClass variable (Stops ReferenceError)
            let scoreClass = 'text-muted';
            if (score >= 80) {
                scoreClass = 'text-success';
            } else if (score >= 50) {
                scoreClass = 'text-warning';
            } else {
                scoreClass = 'text-danger';
            }

            const card = document.createElement('div');
            card.className = 'col-md-6 mb-4';
            card.innerHTML = `
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h5 class="card-title">${index + 1}. ${app.candidate.firstName} ${app.candidate.lastName}</h5>
                        <p class="card-text mb-1">Score: <span class="${scoreClass} fw-bold">${score}%</span></p>
                        <p class="card-text mb-3">Status: <span class="badge bg-primary">${app.status}</span></p>
                        <button class="btn btn-sm btn-info me-2" onclick="viewResume(${app.id})">View Resume</button>
                        <button class="btn btn-sm btn-success me-2" onclick="updateStatus(${app.id}, 'INTERVIEW_SCHEDULED')">Interview</button>
                        <button class="btn btn-sm btn-danger" onclick="updateStatus(${app.id}, 'REJECTED')">Reject</button>
                    </div>
                </div>
            `;
            resultsDiv.appendChild(card);
        });

    } catch (error) {
        // Only show error if the request itself failed
        resultsDiv.innerHTML = '';
        if (messageElement) {
            messageElement.className = 'text-danger';
            messageElement.textContent = 'Error fetching results: ' + error.message;
        }
        console.error('Screening error:', error);
    }
}

async function updateStatus(applicationId, newStatus) {
    try {
        await fetchApi(`/api/applications/${applicationId}/status?status=${newStatus}`, 'PUT');
        console.log(`Application status updated to: ${newStatus}`);
        fetchScreeningResults(); // Reload the list to refresh the status display
    } catch (error) {
        alert('Failed to update status: ' + error.message);
    }
}

async function viewResume(applicationId) {
    try {
        const resumeData = await fetchApi(`/api/admin/resumes/application/${applicationId}`, 'GET');

        alert(
            `Parsed Resume Data:\n` +
            `Name: ${resumeData.parsedName || 'N/A'}\n` +
            `Email: ${resumeData.parsedEmail || 'N/A'}\n` +
            `Skills: ${resumeData.parsedSkills || 'N/A'}\n\n` +
            `Raw Text (First 200 chars):\n${(resumeData.rawText || 'N/A').substring(0, 200)}...`
        );
    } catch (error) {
        alert('Failed to retrieve resume data. Ensure ResumeDataController is set up and data exists: ' + error.message);
    }
}