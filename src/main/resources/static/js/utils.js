const BASE_URL = 'http://localhost:8080';

async function fetchApi(endpoint, method, body = null) {
    const options = {
        method: method,
        headers: {
            'Content-Type': 'application/json',
            // Simple token storage (replace with secure method in production)
            // 'Authorization': 'Bearer ' + localStorage.getItem('authToken')
        }
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    const response = await fetch(BASE_URL + endpoint, options);

    if (!response.ok) {
        let errorText = await response.text();
        throw new Error(errorText || `API Error: ${response.status} ${response.statusText}`);
    }

    // Check if the response contains content, specifically JSON content
        const contentType = response.headers.get("content-type");

        if (contentType && contentType.includes("application/json")) {
            // Response is JSON, parse it
            return response.json();
        }

    // Handle 204 No Content
    if (response.status === 204 || response.headers.get("content-length") === "0") {
        return null;
    }

    return response.text();
}