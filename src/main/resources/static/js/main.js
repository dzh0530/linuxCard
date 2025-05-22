// Main JavaScript for Linux.do CDK Distribution System

document.addEventListener('DOMContentLoaded', function() {
    // Initialize the application
    initApp();
    
    // Setup event listeners
    setupEventListeners();
});

// Initialize the application
function initApp() {
    // Check if user is logged in
    checkAuthStatus();
    
    // Initialize copy buttons
    initCopyButtons();
}

// Setup event listeners
function setupEventListeners() {
    // Login button
    const loginBtn = document.querySelector('.login-btn');
    if (loginBtn) {
        loginBtn.addEventListener('click', function() {
            window.location.href = '/api/auth/login';
        });
    }
    
    // Logout button
    const logoutBtn = document.querySelector('.logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function() {
            logout();
        });
    }
    
    // CDK claim button
    const claimBtn = document.querySelector('.claim-btn');
    if (claimBtn) {
        claimBtn.addEventListener('click', function() {
            const activityId = this.getAttribute('data-activity-id');
            claimCdk(activityId);
        });
    }
    
    // Form submission
    const cdkPublishForm = document.getElementById('cdkPublishForm');
    if (cdkPublishForm) {
        cdkPublishForm.addEventListener('submit', function(e) {
            e.preventDefault();
            submitCdkForm(this);
        });
    }
}

// Check authentication status
function checkAuthStatus() {
    fetch('/api/users/me')
        .then(response => response.json())
        .then(data => {
            if (data.success && data.data) {
                // User is logged in
                updateUIForLoggedInUser(data.data);
            } else {
                // User is not logged in
                updateUIForLoggedOutUser();
            }
        })
        .catch(error => {
            console.error('Error checking auth status:', error);
            updateUIForLoggedOutUser();
        });
}

// Update UI for logged in user
function updateUIForLoggedInUser(user) {
    const userArea = document.querySelector('.user-area');
    if (userArea) {
        userArea.innerHTML = `
            <div class="user-info">
                <img src="${user.avatar || '/images/default-avatar.png'}" alt="${user.username}">
                <span>${user.username}</span>
                <span class="trust-level">信任等级: ${user.trustLevel}</span>
                <button class="logout-btn">退出登录</button>
            </div>
        `;
        
        // Re-attach logout event listener
        const logoutBtn = document.querySelector('.logout-btn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', logout);
        }
    }
    
    // Update claim area if on activity detail page
    const claimArea = document.querySelector('.claim-area');
    if (claimArea) {
        const activityId = claimArea.getAttribute('data-activity-id');
        const minTrustLevel = parseInt(claimArea.getAttribute('data-min-trust-level'));
        const hasClaimed = claimArea.getAttribute('data-has-claimed') === 'true';
        
        if (hasClaimed) {
            // User has already claimed a CDK
            fetchClaimedCdk(activityId);
        } else if (user.trustLevel >= minTrustLevel) {
            // User can claim
            claimArea.innerHTML = `
                <div class="can-claim">
                    <p>您当前的信任等级为 ${user.trustLevel}，符合领取条件</p>
                    <button class="claim-btn btn primary" data-activity-id="${activityId}">领取CDK</button>
                </div>
            `;
            
            // Re-attach claim event listener
            const claimBtn = document.querySelector('.claim-btn');
            if (claimBtn) {
                claimBtn.addEventListener('click', function() {
                    claimCdk(activityId);
                });
            }
        } else {
            // User has insufficient trust level
            claimArea.innerHTML = `
                <div class="insufficient-trust">
                    <p>您当前的信任等级为 ${user.trustLevel}，需要信任等级 ${minTrustLevel}+ 才能领取</p>
                    <a href="https://linux.do/t/topic/12345" target="_blank" class="btn secondary">如何提升信任等级?</a>
                </div>
            `;
        }
    }
}

// Update UI for logged out user
function updateUIForLoggedOutUser() {
    const userArea = document.querySelector('.user-area');
    if (userArea) {
        userArea.innerHTML = `
            <button class="login-btn">使用Linux.do登录</button>
        `;
        
        // Re-attach login event listener
        const loginBtn = document.querySelector('.login-btn');
        if (loginBtn) {
            loginBtn.addEventListener('click', function() {
                window.location.href = '/api/auth/login';
            });
        }
    }
    
    // Update claim area if on activity detail page
    const claimArea = document.querySelector('.claim-area');
    if (claimArea) {
        claimArea.innerHTML = `
            <div class="not-logged-in">
                <p>请先使用Linux.do账号登录后领取</p>
                <button class="login-btn">使用Linux.do登录</button>
            </div>
        `;
        
        // Re-attach login event listener
        const loginBtn = claimArea.querySelector('.login-btn');
        if (loginBtn) {
            loginBtn.addEventListener('click', function() {
                window.location.href = '/api/auth/login';
            });
        }
    }
}

// Logout function
function logout() {
    fetch('/api/auth/logout')
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Redirect to home page
                window.location.href = '/';
            }
        })
        .catch(error => {
            console.error('Error logging out:', error);
        });
}

// Claim CDK function
function claimCdk(activityId) {
    fetch(`/api/activities/${activityId}/claim`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success && data.data) {
            // Show claimed CDK
            const claimArea = document.querySelector('.claim-area');
            if (claimArea) {
                claimArea.innerHTML = `
                    <div class="claimed">
                        <p>您已成功领取此CDK</p>
                        <div class="cdk-display">
                            <input type="text" value="${data.data.code}" readonly>
                            <button class="copy-btn" data-clipboard-text="${data.data.code}">复制</button>
                        </div>
                    </div>
                `;
                
                // Initialize copy button
                initCopyButtons();
            }
        } else {
            alert(data.message || '领取失败，请稍后再试');
        }
    })
    .catch(error => {
        console.error('Error claiming CDK:', error);
        alert('领取失败，请稍后再试');
    });
}

// Fetch claimed CDK
function fetchClaimedCdk(activityId) {
    fetch(`/api/activities/${activityId}/claimed`)
        .then(response => response.json())
        .then(data => {
            if (data.success && data.data) {
                const claimArea = document.querySelector('.claim-area');
                if (claimArea) {
                    claimArea.innerHTML = `
                        <div class="claimed">
                            <p>您已成功领取此CDK</p>
                            <div class="cdk-display">
                                <input type="text" value="${data.data.code}" readonly>
                                <button class="copy-btn" data-clipboard-text="${data.data.code}">复制</button>
                            </div>
                        </div>
                    `;
                    
                    // Initialize copy button
                    initCopyButtons();
                }
            }
        })
        .catch(error => {
            console.error('Error fetching claimed CDK:', error);
        });
}

// Initialize copy buttons
function initCopyButtons() {
    const copyButtons = document.querySelectorAll('.copy-btn');
    copyButtons.forEach(button => {
        button.addEventListener('click', function() {
            const text = this.getAttribute('data-clipboard-text');
            navigator.clipboard.writeText(text)
                .then(() => {
                    const originalText = this.textContent;
                    this.textContent = '已复制';
                    setTimeout(() => {
                        this.textContent = originalText;
                    }, 2000);
                })
                .catch(err => {
                    console.error('Failed to copy text: ', err);
                });
        });
    });
}

// Submit CDK form
function submitCdkForm(form) {
    const formData = new FormData(form);
    const cdkText = formData.get('cdkList');
    
    // Split CDK text into array
    const cdkList = cdkText.split('\n')
        .map(line => line.trim())
        .filter(line => line.length > 0);
    
    const data = {
        title: formData.get('title'),
        description: formData.get('description'),
        usageGuide: formData.get('usageGuide'),
        minTrustLevel: parseInt(formData.get('minTrustLevel')),
        startTime: formData.get('startTime'),
        endTime: formData.get('endTime'),
        isPublic: formData.get('isPublic') === 'true',
        cdkList: cdkList
    };
    
    fetch('/api/activities', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Redirect to activity detail page
            window.location.href = `/activity/${data.data.id}`;
        } else {
            alert(data.message || '发布失败，请检查表单内容');
        }
    })
    .catch(error => {
        console.error('Error submitting form:', error);
        alert('发布失败，请稍后再试');
    });
}
