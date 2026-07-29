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
// --- AI GENERATOR FUNCTIONS ---
async function generateAIDrafts() {
    const rawContent = document.getElementById("aiRawContent").value;
    const userFeedback = document.getElementById("aiInstructions").value;
    const tone = document.getElementById("aiTone").value;
    
    const targetPlatforms = Array.from(document.querySelectorAll(".ai-platform:checked")).map(cb => cb.value);
    
    if (targetPlatforms.length === 0) {
        alert("Please select at least one target platform.");
        return;
    }

    if (!rawContent.trim() && !userFeedback.trim()) {
        alert("Please provide either Raw Content or a Topic/Instruction.");
        return;
    }
    
    const btnText = document.getElementById("generateBtnText");
    const btnIcon = document.getElementById("generateBtnIcon");
    const loader = document.getElementById("generateLoader");
    
    btnText.innerText = "Generating...";
    btnIcon.classList.add("hidden");
    loader.classList.remove("hidden");
    
    const requestBody = {
        rawContent,
        userFeedback,
        tone,
        targetPlatforms
    };
       try {
        const response = await fetch(`${API_BASE}/api/ai/generate`, {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("token"),
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestBody)
        });
        
        if (response.ok) {
            const data = await response.json();
            renderAIDrafts(data.generatedDrafts);
        } else {
            alert("Failed to generate AI drafts.");
        }
    } catch (e) {
        alert("Server Error while generating drafts: " + e.message);
    } finally {
        btnText.innerText = "Generate Magic";
        btnIcon.classList.remove("hidden");
        loader.classList.add("hidden");
    }
}

function renderAIDrafts(draftsMap) {
    document.getElementById("aiResultsPlaceholder").classList.add("hidden");
    const container = document.getElementById("aiDraftsContainer");
    container.classList.remove("hidden");
    container.innerHTML = "";
    
    for (const [platform, content] of Object.entries(draftsMap)) {
        const card = document.createElement("div");
        card.className = "ai-draft-card";
        card.id = `draft-card-${platform}`;
        
        card.innerHTML = `
            <div class="draft-header">
                <span class="draft-platform-badge">${platform}</span>
                <button class="btn btn-primary" style="padding: 5px 12px; font-size: 0.8rem;" onclick="useDraft('${platform}')">Use this draft</button>
            </div>
            <textarea id="draft-text-${platform}" class="draft-textarea">${content}</textarea>
            <div class="refinement-box">
                <input type="text" id="refine-input-${platform}" class="refinement-input" placeholder="e.g. Make it shorter, add more hashtags...">
                <button class="btn btn-ghost" onclick="refineAIDraft('${platform}')" style="padding: 0 15px;">
                    <svg width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M4 12v8h8m-8-8l16-8-8 16m8-16v8m-8-8H4"/></svg>
                </button>
            </div>
        `;
        container.appendChild(card);
    }
}

async function refineAIDraft(platform) {
    const previousDraft = document.getElementById(`draft-text-${platform}`).value;
    const userFeedback = document.getElementById(`refine-input-${platform}`).value;
    const rawContent = document.getElementById("aiRawContent").value;
    
    if (!userFeedback) return;
    
    // UI Loading state for the specific draft
    const refineInput = document.getElementById(`refine-input-${platform}`);
    refineInput.disabled = true;
    refineInput.value = "Refining draft...";
    
    const requestBody = {
        platform,
        previousDraft,
        userFeedback,
        rawContent
    };
    
    try {
        const response = await fetch(`${API_BASE}/api/ai/refine`, {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("token"),
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestBody)
        });
        
        if (response.ok) {
            const data = await response.json();
            document.getElementById(`draft-text-${platform}`).value = data.content;
            refineInput.value = "";
        } else {
            alert("Failed to refine draft.");
            refineInput.value = userFeedback;
        }
    } catch (e) {
        alert("Server Error while refining: " + e.message);
        refineInput.value = userFeedback;
    } finally {
        refineInput.disabled = false;
    }
}

function useDraft(platform) {
    const text = document.getElementById(`draft-text-${platform}`).value;
    sessionStorage.setItem("aiDraftText", text);
    window.location.href = "posts.html";
}
