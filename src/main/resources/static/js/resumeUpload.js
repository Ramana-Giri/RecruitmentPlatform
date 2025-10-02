document.addEventListener('DOMContentLoaded', () => {
    const uploadForm = document.getElementById('uploadForm');
    if (uploadForm) {
        uploadForm.addEventListener('submit', handleUploadAndApply);
        displayJobInfo();
    }
});

function displayJobInfo() {
    const params = new URLSearchParams(window.location.search);
    const jobTitle = params.get('jobTitle');
    if (jobTitle) {
        document.getElementById('jobTitleDisplay').textContent = `Applying for: ${decodeURIComponent(jobTitle)}`;
    }
}

async function handleUploadAndApply(event) {
    event.preventDefault();

    const fileInput = document.getElementById('resumeFile');
    const messageElement = document.getElementById('message');
    const file = fileInput.files[0];

    const params = new URLSearchParams(window.location.search);
    const jobId = params.get('jobId');
    const candidateId = params.get('candidateId');

    if (!file || !jobId || !candidateId) {
        messageElement.className = 'mt-3 text-center text-danger';
        messageElement.textContent = 'Missing file or application context. Please go back to job listings.';
        return;
    }

    const formData = new FormData();
    formData.append('file', file);

    messageElement.className = 'mt-3 text-center text-info';
    messageElement.textContent = 'Uploading and processing resume... This may take a moment.';

    try {
        const response = await fetch(`${BASE_URL}/api/apply/${jobId}/candidate/${candidateId}`, {
            method: 'POST',
            body: formData,
            // Assuming the user is logged in, you would add security headers here:
            // headers: { 'Authorization': 'Bearer ' + localStorage.getItem('authToken') }
        });

        const resultText = await response.text();

        if (response.ok) {
            messageElement.className = 'mt-3 text-center text-success';
            messageElement.textContent = resultText;
            alert(resultText);
        } else {
            messageElement.className = 'mt-3 text-center text-danger';
            messageElement.textContent = `Application failed: ${resultText}`;
        }
    } catch (error) {
        messageElement.className = 'mt-3 text-center text-danger';
        messageElement.textContent = 'A network error occurred: ' + error.message;
        console.error('Upload error:', error);
    }
}