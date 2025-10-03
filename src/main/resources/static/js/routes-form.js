// Routes form JavaScript functionality

// Toggle rewrite options based on rewrite type selection
function toggleRewriteOptions() {
    const rewriteOptions = document.getElementById('rewriteOptions');
    const rewriteType = document.getElementById('rewriteType').value;

    if (rewriteType === 'rewrite') {
        rewriteOptions.style.display = 'block';
    } else {
        rewriteOptions.style.display = 'none';
    }
    
    updateExample();
}

// Update example text based on form inputs
function updateExample() {
    const incomingPath = document.getElementById('incomingPath').value || '/ye';
    const targetUri = document.getElementById('targetUri').value || 'http://192.168.0.188:8000';
    const rewriteType = document.getElementById('rewriteType').value;
    const targetPath = document.getElementById('targetPath').value || '/api';
    
    let forwardPath = '';
    
    if (rewriteType === 'strip') {
        forwardPath = '/token';
    } else if (rewriteType === 'rewrite') {
        forwardPath = targetPath + '/token';
    } else {
        forwardPath = incomingPath + '/token';
    }
    
    const exampleElement = document.getElementById('exampleText');
    if (exampleElement) {
        exampleElement.innerHTML = 
            `Request: http://localhost:8087${incomingPath}/token<br>` +
            `Forwards to: ${targetUri}${forwardPath}`;
    }
}

// Initialize event listeners when DOM is loaded
document.addEventListener("DOMContentLoaded", () => {
    // Initialize rewrite options visibility
    const rewriteTypeElement = document.getElementById('rewriteType');
    if (rewriteTypeElement) {
        toggleRewriteOptions();
        
        // Add event listeners for form inputs
        document.getElementById('incomingPath')?.addEventListener('input', updateExample);
        document.getElementById('targetUri')?.addEventListener('input', updateExample);
        document.getElementById('targetPath')?.addEventListener('input', updateExample);
        document.getElementById('rewriteType')?.addEventListener('change', toggleRewriteOptions);
    }
});
