const API_BASE = "http://localhost:8080";

// --- AUTHENTICATION ---
async function login() {
    let email = document.getElementById("Email").value;
    let password = document.getElementById("Password").value;
    
    try {
        const res = await fetch(`${API_BASE}/User/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });
        
        if (!res.ok) {
            alert("Login failed. Check your credentials.");
            return;
        }
        
        const data = await res.json();
        localStorage.setItem("token", data.token);
        window.location.href = "posts.html";
    } catch (error) {
        alert("Server error during login.");
    }
}

async function register() {
    let registerEmail = document.getElementById("rEmail").value;
    let registerPassword = document.getElementById("rPassword").value;
    let Username = document.getElementById("rUsername").value;
    
    try {
        const response = await fetch(`${API_BASE}/User/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                userEmail: registerEmail,
                password: registerPassword,
                userName: Username,
                userRole: "USER",
            })
        });
        
        if (response.ok) {
            alert("User registered successfully! You can now login.");
            toggleAuth(); // Switches UI back to login
        }
    } catch (error) {
        alert("Registration failed: " + error);
    }
}

// --- OAUTH CONNECTIONS ---
async function linkedinlogin() {
    const response = await fetch(`${API_BASE}/oauth2/linkedin/login`, {
        method: "POST",
        headers: { "Authorization": "Bearer " + localStorage.getItem("token") }
    });
    const linkedinurl = await response.text();
    window.location.href = linkedinurl;
}

async function metalogin() {
    const response = await fetch(`${API_BASE}/oauth/meta/login`, {
        method: "GET",
        headers: { "Authorization": "Bearer " + localStorage.getItem("token") }
    });
    const metaurl = await response.text();
    window.location.href = metaurl;
}

async function mastodonlogin() {
    const response = await fetch(`${API_BASE}/oauth/mastodon/login`, {
        method: "GET",
        headers: { "Authorization": "Bearer " + localStorage.getItem("token") }
    });
    const mastodonurl = await response.text();
    window.location.href = mastodonurl;
}

// --- POST MANAGEMENT ---
async function createPost() {

    const content = document.querySelector("#content").value;
    const publish = document.querySelector("#publish").checked;

    const platformsList = Array.from(
        document.querySelectorAll(".platform:checked")
    ).map(i => i.value);

    console.log("Platforms selected:", platformsList);

    let requestBody = {
        content,
        platformsList,
        publish
    };

    const scheduledInput = document.getElementById("scheduledAt").value;

    if (!publish && scheduledInput) {
        requestBody.scheduledAt = scheduledInput + ":00";
    }

    console.log("Request body:", requestBody);

    try {
        const response = await fetch(`${API_BASE}/Post/create`, {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("token"),
                "Content-Type": "application/json",
            },
            body: JSON.stringify(requestBody)
        });

        const data = await response.json();

        if (response.ok) {
            document.getElementById("responseBox").innerText =
                `Post created successfully! ID: ${data.postId}`;

            document.getElementById("content").value = "";
            getAllPosts();
        } else {
            alert(data.message || "Failed to create post");
        }

    } catch (err) {
        console.error(err);
        alert("Server error");
    }
}

// FIXED: This now properly loops and displays posts
function renderPosts(postsList) {
    const container = document.getElementById("postContainer");
    if (!container) return;
    
    container.innerHTML = ""; // Clear current list

    postsList.forEach(post => {
        const card = document.createElement("div");
        card.className = "post-card";

        const statusClass = post.status === "PUBLISHED" ? "status-published" : "status-draft";

        card.innerHTML = `
            <div>
                <span class="status-badge ${statusClass}">${post.status}</span>
                <p style="margin-top:10px; font-size: 0.95rem; line-height: 1.4;">${post.content}</p>
            </div>
            <div style="margin-top: 15px; border-top: 1px solid #eee; padding-top: 10px; display:flex; justify-content:space-between; align-items:center;">
                <small style="color: #999;">ID: ${post.postId}</small>
                ${post.status !== "PUBLISHED" ? 
                    `<button onclick="publishPost(${post.postId})" style="font-size: 0.75rem; padding: 5px 10px;">Publish Now</button>` 
                    : `<span style="font-size: 0.7rem; color: #166534; font-weight: bold;">✓ Live</span>`
                }
            </div>
        `;
        container.appendChild(card);
    });
}

async function getAllPosts() {
    let token = "Bearer " + localStorage.getItem("token");
    try {
        let response = await fetch(`${API_BASE}/Post/getAll`, {
            method: "GET",
            headers: { "Authorization": token }
        });
        let postsList = await response.json();
        renderPosts(postsList);
    } catch (e) { 
        console.error("Fetch error:", e); 
    }
}

async function publishPost(postId) {
    let token = "Bearer " + localStorage.getItem("token");
    try {
        const response = await fetch(`${API_BASE}/Post/Publish?postId=${postId}`, {
            method: "POST",
            headers: { "Authorization": token }
        });
        if (response.ok) {
            alert("Posted Successfully!");
            getAllPosts();
        } else {
            alert("Publishing failed. Check API logs.");
        }
    } catch (e) { 
        alert("Error publishing: " + e); 
    }
}