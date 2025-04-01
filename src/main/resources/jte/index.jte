@import java.util.Arrays
@import java.util.List
@import co.jrstudios.models.Project

@param List<Project> projects

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Panel - Projects</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        :root {
            --primary-bg: #ffffff;
            --secondary-bg: #f8f9fa;
            --primary-text: #212529;
            --secondary-text: #6c757d;
            --accent: #000000;
            --border: #dee2e6;
            --hover: #e9ecef;
            --tag-bg: #f0f0f0;
            --overlay: rgba(0, 0, 0, 0.5);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
        }

        body {
            background-color: var(--secondary-bg);
            color: var(--primary-text);
            min-height: 100vh;
            display: flex;
        }

        .sidebar {
            width: 250px;
            background-color: var(--accent);
            color: white;
            height: 100vh;
            position: fixed;
            left: 0;
            top: 0;
            overflow-y: auto;
            transition: all 0.3s ease;
        }

        .logo {
            padding: 25px 20px;
            font-size: 24px;
            font-weight: bold;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }

        .nav-links {
            padding: 20px 0;
        }

        .nav-link {
            display: flex;
            align-items: center;
            padding: 15px 20px;
            color: rgba(255, 255, 255, 0.7);
            text-decoration: none;
            transition: all 0.3s ease;
        }

        .nav-link i {
            margin-right: 10px;
            font-size: 18px;
        }

        .nav-link.active, .nav-link:hover {
            background-color: rgba(255, 255, 255, 0.1);
            color: white;
        }

        .main-content {
            flex: 1;
            margin-left: 250px;
            padding: 30px;
            transition: all 0.3s ease;
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }

        .header h1 {
            font-size: 28px;
            font-weight: 600;
        }

        .search-bar {
            display: flex;
            align-items: center;
            background-color: var(--primary-bg);
            border-radius: 8px;
            padding: 10px 15px;
            border: 1px solid var(--border);
        }

        .search-bar input {
            border: none;
            outline: none;
            background-color: transparent;
            font-size: 14px;
            width: 250px;
            margin-left: 10px;
        }

        .btn {
            background-color: var(--accent);
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 8px;
            font-size: 14px;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            font-weight: 500;
        }

        .btn i {
            margin-right: 8px;
        }

        .btn:hover {
            opacity: 0.9;
        }

        .filters {
            display: flex;
            gap: 15px;
            margin-bottom: 20px;
        }

        .filter {
            background-color: var(--primary-bg);
            border: 1px solid var(--border);
            border-radius: 8px;
            padding: 10px 15px;
            font-size: 14px;
            cursor: pointer;
            display: flex;
            align-items: center;
        }

        .filter i {
            margin-right: 8px;
        }

        .projects-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 20px;
        }

        .project-card {
            background-color: var(--primary-bg);
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
            padding: 20px;
            transition: all 0.3s ease;
        }

        .project-card:hover {
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
            transform: translateY(-5px);
        }

        .project-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }

        .project-id {
            background-color: var(--tag-bg);
            color: var(--secondary-text);
            padding: 5px 10px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: 500;
        }

        .project-title {
            font-size: 18px;
            font-weight: 600;
            margin-bottom: 10px;
        }

        .project-description {
            color: var(--secondary-text);
            font-size: 14px;
            margin-bottom: 15px;
            line-height: 1.5;
        }

        .project-tags {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            margin-bottom: 15px;
        }

        .tag {
            background-color: var(--tag-bg);
            border-radius: 4px;
            padding: 5px 10px;
            font-size: 12px;
            font-weight: 500;
        }

        .project-location {
            display: flex;
            align-items: center;
            color: var(--secondary-text);
            font-size: 14px;
        }

        .project-location i {
            margin-right: 8px;
        }

        .project-actions {
            display: flex;
            gap: 10px;
            margin-top: 15px;
            padding-top: 15px;
            border-top: 1px solid var(--border);
        }

        .action-btn {
            background-color: transparent;
            border: 1px solid var(--border);
            border-radius: 6px;
            padding: 8px;
            cursor: pointer;
            transition: all 0.3s ease;
            color: var(--secondary-text);
        }

        .action-btn:hover {
            background-color: var(--hover);
            color: var(--primary-text);
        }

        /* Modal Styles */
        .modal-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: var(--overlay);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 1000;
            opacity: 0;
            pointer-events: none;
            transition: opacity 0.3s ease;
        }

        .modal-overlay.active {
            opacity: 1;
            pointer-events: auto;
        }

        .modal {
            background-color: var(--primary-bg);
            border-radius: 12px;
            width: 90%;
            max-width: 600px;
            box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
            transform: translateY(-20px);
            transition: transform 0.3s ease;
            max-height: 90vh;
            overflow-y: auto;
        }

        .modal-overlay.active .modal {
            transform: translateY(0);
        }

        .modal-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 20px 24px;
            border-bottom: 1px solid var(--border);
        }

        .modal-title {
            font-size: 20px;
            font-weight: 600;
        }

        .close-modal {
            background: none;
            border: none;
            cursor: pointer;
            font-size: 20px;
            color: var(--secondary-text);
            transition: color 0.3s ease;
        }

        .close-modal:hover {
            color: var(--primary-text);
        }

        .modal-body {
            padding: 24px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
            font-size: 14px;
        }

        .form-control {
            width: 100%;
            padding: 12px 14px;
            border: 1px solid var(--border);
            border-radius: 8px;
            font-size: 14px;
            transition: border-color 0.3s ease;
        }

        .form-control:focus {
            outline: none;
            border-color: var(--accent);
        }

        .tags-input-container {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            padding: 8px 12px;
            border: 1px solid var(--border);
            border-radius: 8px;
            cursor: text;
        }

        .tags-input-container .tag {
            display: flex;
            align-items: center;
            background-color: var(--tag-bg);
            padding: 5px 8px;
            border-radius: 4px;
        }

        .tag-remove {
            margin-left: 6px;
            cursor: pointer;
            font-size: 14px;
        }

        .tags-input {
            flex: 1;
            border: none;
            outline: none;
            padding: 5px 0;
            min-width: 100px;
        }

        .modal-footer {
            padding: 20px 24px;
            border-top: 1px solid var(--border);
            display: flex;
            justify-content: flex-end;
            gap: 12px;
        }

        .btn-secondary {
            background-color: var(--secondary-bg);
            color: var(--primary-text);
            border: 1px solid var(--border);
        }

        .btn-secondary:hover {
            background-color: var(--hover);
        }

        .auth-section {
            background-color: var(--secondary-bg);
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 20px;
        }

        .auth-message {
            font-size: 13px;
            color: var(--secondary-text);
            margin-top: 5px;
        }

        .token-status {
            display: inline-block;
            width: 8px;
            height: 8px;
            border-radius: 50%;
            margin-right: 5px;
        }

        .token-status.valid {
            background-color: #28a745;
        }

        .token-status.invalid {
            background-color: #dc3545;
        }

        .notification {
            position: fixed;
            top: 20px;
            right: 20px;
            background-color: var(--primary-bg);
            color: var(--primary-text);
            padding: 15px 20px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            z-index: 1100;
            transform: translateY(-100px);
            opacity: 0;
            transition: all 0.3s ease;
        }

        .notification.show {
            transform: translateY(0);
            opacity: 1;
        }

        .notification.success {
            border-left: 4px solid #28a745;
        }

        .notification.error {
            border-left: 4px solid #dc3545;
        }

        /* Image upload styling */
        .image-upload-container {
            margin-bottom: 15px;
        }

        .image-upload-area {
            border: 2px dashed var(--border);
            border-radius: 8px;
            padding: 30px;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .image-upload-area:hover {
            border-color: var(--accent);
        }

        .image-upload-area.highlight {
            border-color: var(--accent);
            background-color: rgba(0, 0, 0, 0.02);
        }

        .image-upload-area i {
            font-size: 24px;
            color: var(--secondary-text);
            margin-bottom: 10px;
        }

        .image-upload-area p {
            color: var(--secondary-text);
            font-size: 14px;
            margin: 0;
        }

        .image-preview-container {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            margin-top: 15px;
        }

        .image-preview {
            position: relative;
            width: 100px;
            height: 100px;
            border-radius: 8px;
            overflow: hidden;
            border: 1px solid var(--border);
        }

        .image-preview img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .image-remove {
            position: absolute;
            top: 5px;
            right: 5px;
            background-color: rgba(0, 0, 0, 0.6);
            color: white;
            border-radius: 50%;
            width: 20px;
            height: 20px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            font-size: 12px;
        }

        .upload-progress {
            position: absolute;
            bottom: 0;
            left: 0;
            height: 4px;
            background-color: var(--accent);
            width: 0%;
            transition: width 0.3s ease;
        }
        .image-label {
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            background-color: rgba(0, 0, 0, 0.7);
            color: white;
            padding: 2px 5px;
            font-size: 10px;
            text-overflow: ellipsis;
            overflow: hidden;
            white-space: nowrap;
        }

        .existing-image {
            border: 1px solid var(--accent);
        }
        @media (max-width: 1024px) {
            .sidebar {
                width: 80px;
            }

            .logo {
                padding: 25px 0;
                text-align: center;
                font-size: 16px;
            }

            .nav-link span {
                display: none;
            }

            .nav-link i {
                margin-right: 0;
                font-size: 20px;
            }

            .main-content {
                margin-left: 80px;
            }
        }

        @media (max-width: 768px) {
            .projects-grid {
                grid-template-columns: 1fr;
            }

            .header {
                flex-direction: column;
                align-items: flex-start;
                gap: 15px;
            }

            .search-bar {
                width: 100%;
            }

            .search-bar input {
                width: 100%;
            }

            .filters {
                overflow-x: auto;
                width: 100%;
                padding-bottom: 10px;
            }
        }
    </style>
</head>
<body>
<div class="sidebar">
    <div class="logo">AdminPanel</div>
    <div class="nav-links">
        <a href="#" class="nav-link active">
            <i class="fas fa-th-large"></i>
            <span>Dashboard</span>
        </a>
        <a href="#" class="nav-link">
            <i class="fas fa-project-diagram"></i>
            <span>Projects</span>
        </a>
        <a href="#" class="nav-link">
            <i class="fas fa-tasks"></i>
            <span>Tasks</span>
        </a>
        <a href="#" class="nav-link">
            <i class="fas fa-users"></i>
            <span>Team</span>
        </a>
        <a href="#" class="nav-link">
            <i class="fas fa-calendar"></i>
            <span>Calendar</span>
        </a>
        <a href="#" class="nav-link">
            <i class="fas fa-chart-bar"></i>
            <span>Reports</span>
        </a>
        <a href="#" class="nav-link">
            <i class="fas fa-cog"></i>
            <span>Settings</span>
        </a>
    </div>
</div>

<main class="main-content">
    <div class="header">
        <h1>Projects</h1>
        <div class="controls">
            <div class="search-bar">
                <i class="fas fa-search"></i>
                <input type="text" placeholder="Search projects...">
            </div>
        </div>
    </div>

    <div class="filters">
        <button class="filter">
            <i class="fas fa-filter"></i>
            All Projects
        </button>
        <button class="filter">
            <i class="fas fa-map-marker-alt"></i>
            Location
        </button>
        <button class="filter">
            <i class="fas fa-tags"></i>
            Tags
        </button>
        <button class="btn" id="newProjectBtn">
            <i class="fas fa-plus"></i>
            New Project
        </button>
    </div>

    <div class="projects-grid">
        @for(Project project : projects)
            <div class="project-card">
                <div class="project-header">
                    <div class="project-id">#${project.getId()}</div>
                    <div class="project-actions">
                        <button class="action-btn edit-btn" data-project-id="${project.getId()}">
                            <i class="fas fa-pencil-alt"></i>
                        </button>
                        <button class="action-btn delete-btn" data-project-id="${project.getId()}">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
                <h3 class="project-title">${project.getTitle()}</h3>
                <p class="project-description">${project.getDescription()}</p>
                <div class="project-tags">
                    @for(int i = 0; i < Arrays.stream(project.getTags()).toList().size(); i++)
                        <span class="tag">${Arrays.stream(project.getTags()).toList().get(i)}</span>
                    @endfor
                </div>
                <div class="project-location">
                    <i class="fas fa-map-marker-alt"></i>
                    ${project.getLocation()}
                </div>
            </div>
        @endfor
    </div>
</main>

<!-- Edit Project Modal -->
<div class="modal-overlay" id="editProjectModal">
    <div class="modal">
        <div class="modal-header">
            <h3 class="modal-title">Edit Project</h3>
            <button class="close-modal" id="closeModal">&times;</button>
        </div>
        <form id="editProjectForm" enctype="multipart/form-data">
            <input type="hidden" id="projectId" name="id">
            <div class="modal-body">

                <div class="form-group">
                    <label for="projectTitle" class="form-label">Project Title</label>
                    <input type="text" id="projectTitle" name="title" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="projectDescription" class="form-label">Description</label>
                    <textarea id="projectDescription" name="description" class="form-control" rows="3" required></textarea>
                </div>
                <div class="form-group">
                    <label class="form-label">Tags</label>
                    <div class="tags-input-container" id="tagsContainer">
                        <input type="text" class="tags-input" id="tagsInput" placeholder="Add a tag...">
                    </div>
                    <input type="hidden" id="tagsHidden" name="tags">
                </div>
                <div class="form-group">
                    <label class="form-label">Project Images</label>
                    <div class="image-upload-container">
                        <div class="image-upload-area" id="imageDropArea">
                            <i class="fas fa-cloud-upload-alt"></i>
                            <p>Drag & drop images here or click to browse</p>
                            <input type="file" id="imageUpload" name="image" multiple accept="image/*" class="file-input" hidden>
                        </div>
                        <div class="image-preview-container" id="imagePreviewContainer"></div>
                    </div>
                </div>
                <div class="form-group">
                    <label for="projectLocation" class="form-label">Location</label>
                    <select id="projectLocation" name="location" class="form-control">
                        <option value="On site">On site</option>
                        <option value="Remote">Remote</option>
                        <option value="Hybrid">Hybrid</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" id="cancelEdit">Cancel</button>
                <button type="button" class="btn" id="saveChangesBtn">Save Changes</button>
            </div>
        </form>
    </div>
</div>

<!-- Notification -->
<div class="notification" id="notification"></div>

<script>
    @raw
    // Project data from server for JavaScript access
    const projectsData = [
            @endraw
            @for(int i = 0; i < projects.size(); i++)
            @raw{@endraw
            id: ${projects.get(i).getId()},
            title: "${projects.get(i).getTitle()}",
            description: "${projects.get(i).getDescription()}",
            tags: [
                @for(int j = 0; j < Arrays.stream(projects.get(i).getTags()).toList().size(); j++)
                "${Arrays.stream(projects.get(i).getTags()).toList().get(j)}"${j < Arrays.stream(projects.get(i).getTags()).toList().size() - 1 ? "," : ""}
                @endfor
            ],
            location: "${projects.get(i).getLocation()}",
            // Add imagePaths if available
            imagePaths: [
                @if(projects.get(i).getImagePaths() != null)
                @for(int k = 0; k < projects.get(i).getImagePaths().length; k++)
                "${projects.get(i).getImagePaths()[k]}"${k < projects.get(i).getImagePaths().length - 1 ? "," : ""}
                @endfor
                @endif
            ]
            @raw}@endraw${i < projects.size() - 1 ? "," : ""}
        @endfor
        @raw
    ];

    // DOM Elements
    const editModal = document.getElementById('editProjectModal');
    const closeModalBtn = document.getElementById('closeModal');
    const cancelEditBtn = document.getElementById('cancelEdit');
    const editProjectForm = document.getElementById('editProjectForm');
    const projectIdInput = document.getElementById('projectId');
    const projectTitleInput = document.getElementById('projectTitle');
    const projectDescriptionInput = document.getElementById('projectDescription');
    const projectLocationInput = document.getElementById('projectLocation');
    const tagsContainer = document.getElementById('tagsContainer');
    const tagsInput = document.getElementById('tagsInput');
    const tagsHiddenInput = document.getElementById('tagsHidden');
    const imageDropArea = document.getElementById('imageDropArea');
    const imageUpload = document.getElementById('imageUpload');
    const imagePreviewContainer = document.getElementById('imagePreviewContainer');
    const editButtons = document.querySelectorAll('.edit-btn');
    const deleteButtons = document.querySelectorAll('.delete-btn');
    const newProjectBtn = document.getElementById('newProjectBtn');
    const saveChangesBtn = document.getElementById('saveChangesBtn');
    const notification = document.getElementById('notification');

    // Keep track of selected files
    let selectedFiles = [];
    // Current tags array
    let currentTags = [];
    // Track existing images
    let existingImages = [];

    // Check if token is in localStorage and populate field
    document.addEventListener('DOMContentLoaded', function() {
        // Initialize image upload
        initImageUpload();

        // Tags input handling
        tagsInput.addEventListener('keydown', function(e) {
            if (e.key === 'Enter' || e.key === ',') {
                e.preventDefault();
                addTag(this.value);
            }
        });

        tagsInput.addEventListener('blur', function() {
            if (this.value.trim()) {
                addTag(this.value);
            }
        });

        // Event delegation for tag removal
        tagsContainer.addEventListener('click', function(e) {
            if (e.target.classList.contains('tag-remove')) {
                const tagToRemove = e.target.getAttribute('data-tag');
                removeTag(tagToRemove);
            }
        });
    });

    // Initialize image upload functionality
    function initImageUpload() {
        // Trigger file input when clicking on the drop area
        imageDropArea.addEventListener('click', () => {
            imageUpload.click();
        });

        // Handle file selection
        imageUpload.addEventListener('change', handleFiles);

        // Handle drag and drop events
        ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
            imageDropArea.addEventListener(eventName, preventDefaults, false);
        });

        function preventDefaults(e) {
            e.preventDefault();
            e.stopPropagation();
        }

        // Add highlighting while dragging
        ['dragenter', 'dragover'].forEach(eventName => {
            imageDropArea.addEventListener(eventName, highlight, false);
        });

        ['dragleave', 'drop'].forEach(eventName => {
            imageDropArea.addEventListener(eventName, unhighlight, false);
        });

        function highlight() {
            imageDropArea.classList.add('highlight');
        }

        function unhighlight() {
            imageDropArea.classList.remove('highlight');
        }

        // Handle dropped files
        imageDropArea.addEventListener('drop', (e) => {
            const dt = e.dataTransfer;
            const files = dt.files;
            handleFiles({ target: { files } });
        });
    }

    // Handle the selected files
    function handleFiles(e) {
        const files = Array.from(e.target.files);
        files.forEach(file => {
            // Check if it's an image
            if (!file.type.match('image.*')) {
                showNotification('Only image files are allowed', 'error');
                return;
            }

            // Check file size (limit to 5MB)
            if (file.size > 5 * 1024 * 1024) {
                showNotification('Image size should be less than 5MB', 'error');
                return;
            }

            // Add to selected files array
            selectedFiles.push(file);

            // Create a preview
            const reader = new FileReader();
            reader.onload = (e) => {
                addImagePreview(e.target.result, selectedFiles.length - 1);
            };
            reader.readAsDataURL(file);
        });
    }

    // Add image preview for new uploads
    function addImagePreview(src, index) {
        const previewElement = document.createElement('div');
        previewElement.className = 'image-preview';
        previewElement.innerHTML = `
            <img src="${src}" alt="Preview">
            <div class="image-remove" data-index="${index}">&times;</div>
            <div class="upload-progress"></div>
        `;

        // Add remove functionality
        const removeBtn = previewElement.querySelector('.image-remove');
        removeBtn.addEventListener('click', () => {
            // Remove from selectedFiles array
            selectedFiles.splice(index, 1);

            // Remove preview element
            previewElement.remove();

            // Re-index the remaining preview elements
            const removeBtns = document.querySelectorAll('.image-remove');
            removeBtns.forEach((btn, i) => {
                btn.setAttribute('data-index', i);
            });
        });

        imagePreviewContainer.appendChild(previewElement);
    }

    // Add preview for existing images
    function addExistingImagePreview(path, fileName, index) {
        // Create a preview element with placeholder or actual image
        const previewElement = document.createElement('div');
        previewElement.className = 'image-preview existing-image';

        // Create image URL - we'll use a placeholder or actual image path depending on how they're served
        // If images are served from a public directory, you can use the actual path
        // Otherwise, use a placeholder image
        const imageUrl = `/uploads/${fileName}`; // Adjust based on your server setup
        let isImage = false;
        fetch(imageUrl, { method: 'GET' })
            .then(response => {
                // Check the Content-Type header
                const contentType = response.headers.get('content-type');
                if (!contentType || !contentType.startsWith('image/')) {
                    isImage = false;
                    throw new Error('Response is not an image');
                }
                // If it is an image, convert it to a Blob (or process as needed)
                return response.blob();
            })
            .then(blob => {
                // Use the image blob (e.g., create an object URL)
                const imageObjectUrl = URL.createObjectURL(blob);
                console.log("Image loaded:", imageObjectUrl);
                isImage = true;
                // Do something with the image
            })
            .catch(error => {
                console.error("Error:", error.message);
                // Handle the case when the response is not an image
            });



        const cacheUrl = `/images/${fileName}`; // Adjust based on your server setup

        previewElement.innerHTML = `
    <img src="${isImage ? imageUrl : cacheUrl}" alt="${fileName}" onerror="this.src='/uploads/404.png'">
    <div class="image-label" title="${fileName}">
        ${fileName.length > 10 ? fileName.substring(0, 10) + '...' : fileName}
    </div>
    <div class="image-remove existing-remove" data-index="${index}">&times;</div>
`;

        // Add remove functionality
        const removeBtn = previewElement.querySelector('.existing-remove');
        removeBtn.addEventListener('click', () => {
            // Remove from the existingImages array
            existingImages.splice(index, 1);

            // Remove preview element
            previewElement.remove();

            // Re-index the remaining preview elements
            const removeBtns = document.querySelectorAll('.existing-remove');
            removeBtns.forEach((btn, i) => {
                btn.setAttribute('data-index', i);
            });
        });

        imagePreviewContainer.appendChild(previewElement);
    }

    // Clear all images
    function clearImages() {
        selectedFiles = [];
        existingImages = [];
        imagePreviewContainer.innerHTML = '';
        // Reset the file input
        imageUpload.value = '';
    }

    // Render tags function
    function renderTags() {
        // Clear existing tags (except input)
        const tagElements = tagsContainer.querySelectorAll('.tag');
        tagElements.forEach(tag => tag.remove());

        // Add each tag
        currentTags.forEach(tag => {
            const tagElement = document.createElement('span');
            tagElement.classList.add('tag');
            tagElement.innerHTML = `${tag} <span class="tag-remove" data-tag="${tag}">&times;</span>`;
            tagsContainer.insertBefore(tagElement, tagsInput);
        });

        // Update hidden input value
        tagsHiddenInput.value = JSON.stringify(currentTags);
    }

    // Add tag function
    function addTag(tag) {
        tag = tag.trim();
        if (tag && !currentTags.includes(tag)) {
            currentTags.push(tag);
            renderTags();
            tagsInput.value = '';
        }
    }

    // Remove tag function
    function removeTag(tag) {
        currentTags = currentTags.filter(t => t !== tag);
        renderTags();
    }

    // Open modal function
    function openModal(projectId) {
        const project = projectsData.find(p => p.id == projectId);

        if (project) {
            // Clear the form first
            editProjectForm.reset();

            // Populate form fields
            projectIdInput.value = project.id;
            projectTitleInput.value = project.title;
            projectDescriptionInput.value = project.description;
            projectLocationInput.value = project.location;

            // Reset and populate tags
            currentTags = [...project.tags];
            renderTags();

            // Clear previous images
            clearImages();

            // Reset existing images array
            existingImages = [];

            // Load existing images if available
            if (project.imagePaths && project.imagePaths.length > 0) {
                // Add existing images to the preview
                project.imagePaths.forEach((path, index) => {
                    if (path && path.trim() !== '') {
                        // Extract file name from path for display
                        const fileName = path.split('/').pop();

                        // Add to tracking array
                        existingImages.push({
                            path: path,
                            fileName: fileName
                        });

                        // Display in the preview area
                        addExistingImagePreview(path, fileName, index);
                    }
                });
            }

            // Show modal
            editModal.classList.add('active');
        }
    }

    // Open modal for new project
    function openNewProjectModal() {
        // Clear the form
        editProjectForm.reset();

        // Reset hidden id field
        projectIdInput.value = '';

        // Default location value
        projectLocationInput.value = 'On site';

        // Reset tags
        currentTags = [];
        renderTags();

        // Clear images
        clearImages();

        // Show modal
        editModal.classList.add('active');
    }

    // Close modal function
    function closeModal() {
        editModal.classList.remove('active');
    }

    // Save changes button handler
    saveChangesBtn.addEventListener('click', function() {
        // Get token from localStorage
        const token = localStorage.getItem('authToken');
        if (!token) {
            showNotification('Authentication token not found. Please log in again.', 'error');
            setTimeout(() => {
                window.location.href = '/login';
            },2000);
            return;
        }

        // Create FormData object
        const formData = new FormData();

        // Add basic form fields
        formData.append('id', projectIdInput.value);
        formData.append('title', projectTitleInput.value);
        formData.append('description', projectDescriptionInput.value);
        formData.append('location', projectLocationInput.value);
        formData.append('tags', tagsHiddenInput.value);

        // Add existing image paths that weren't removed
        if (existingImages.length > 0) {
            formData.append('existingImages', JSON.stringify(existingImages.map(img => img.path)));
        }

        // Add new image files
        selectedFiles.forEach(file => {
            formData.append('image', file);
        });

        // Determine if this is a new project or update
        const isNewProject = !projectIdInput.value;
        const url = isNewProject ? '/api/projects' : '/api/projects';
        const method = isNewProject ? 'POST' : 'PUT';

        // Send data to server
        fetch(url, {
            method: method,
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                showNotification(`Project ${isNewProject ? 'created' : 'updated'} successfully`, 'success');
                closeModal();

                // Refresh the page to show updated data
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            })
            .catch(error => {
                console.error('Error:', error);
                showNotification(`Error: ${error.message}`, 'error');
            });
    });

    // Show notification
    function showNotification(message, type) {
        notification.textContent = message;
        notification.className = `notification ${type} show`;

        setTimeout(() => {
            notification.className = 'notification';
        }, 3000);
    }

    // Event Listeners
    editButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const projectId = this.getAttribute('data-project-id');
            openModal(projectId);
        });
    });

    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const projectId = this.getAttribute('data-project-id');
            deleteProject(projectId);
        });
    });

    newProjectBtn.addEventListener('click', function(e) {
        e.preventDefault();
        openNewProjectModal();
    });

    closeModalBtn.addEventListener('click', closeModal);
    cancelEditBtn.addEventListener('click', closeModal);

    // Close modal when clicking outside
    editModal.addEventListener('click', function(e) {
        if (e.target === editModal) {
            closeModal();
        }
    });

    // Delete project function
    function deleteProject(projectId) {
        const token = localStorage.getItem('authToken');
        if (!token) {
            showNotification('Authentication token not found. Please log in again.', 'error');
            return;
        }

        if (confirm('Are you sure you want to delete this project?')) {
            fetch(`/api/projects/${projectId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(data => {
                    showNotification('Project deleted successfully', 'success');

                    // Refresh the page to show updated data
                    setTimeout(() => {
                        window.location.reload();
                    }, 1000);
                })
                .catch(error => {
                    console.error('Error:', error);
                    showNotification(`Error: ${error.message}`, 'error');
                });
        }
    }
    @endraw
</script>
</body>
</html>
