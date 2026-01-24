// Universal Theme Toggle and Mobile Menu Handler for Flicker

// Theme Toggle Functionality
function initThemeToggle() {
    const themeToggleSidebar = document.getElementById('theme-toggle-sidebar');
    const body = document.body;

    // Prevent double initialization
    if (themeToggleSidebar && themeToggleSidebar.getAttribute('data-init-theme') === 'true') {
        return;
    }

    const currentTheme = localStorage.getItem('theme') || 'dark';
    body.setAttribute('data-theme', currentTheme);

    function updateThemeIcon(theme) {
        if (themeToggleSidebar) {
            themeToggleSidebar.innerHTML = theme === 'light' ? 'Switch to Dark' : 'Switch to Light';
        }
    }

    updateThemeIcon(currentTheme);

    function toggleTheme() {
        const currentTheme = body.getAttribute('data-theme') || 'dark';
        const newTheme = currentTheme === 'light' ? 'dark' : 'light';
        body.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
        updateThemeIcon(newTheme);
    }

    if (themeToggleSidebar) {
        themeToggleSidebar.setAttribute('data-init-theme', 'true');
        themeToggleSidebar.addEventListener('click', toggleTheme);
    }
}

// Sidebar Toggle (Desktop + Mobile)
function initSidebarToggle() {
    const menuToggle = document.getElementById('menuToggle');
    const sidebar = document.getElementById('sidebar');
    const navOverlay = document.getElementById('navOverlay');
    const body = document.body;

    if (!menuToggle || !sidebar) {
        return;
    }

    // Prevent double initialization
    if (menuToggle.getAttribute('data-init-sidebar') === 'true') {
        return;
    }
    menuToggle.setAttribute('data-init-sidebar', 'true');

    // Check if mobile
    function isMobile() {
        return window.innerWidth <= 768;
    }

    menuToggle.addEventListener('click', () => {
        if (isMobile()) {
            sidebar.classList.toggle('open');
            if (navOverlay) {
                navOverlay.classList.toggle('active');
            }
        } else {
            body.classList.toggle('sidebar-collapsed');
        }
    });

    if (navOverlay) {
        navOverlay.addEventListener('click', () => {
            sidebar.classList.remove('open');
            navOverlay.classList.remove('active');
        });
    }

    const navLinks = sidebar.querySelectorAll('a');
    navLinks.forEach(link => {
        link.addEventListener('click', () => {
            if (isMobile()) {
                sidebar.classList.remove('open');
                if (navOverlay) {
                    navOverlay.classList.remove('active');
                }
            }
        });
    });

    window.addEventListener('resize', () => {
        if (!isMobile()) {
            sidebar.classList.remove('open');
            if (navOverlay) {
                navOverlay.classList.remove('active');
            }
        } else {
            body.classList.remove('sidebar-collapsed');
        }
    });
}

// Global Modal Utilities
function showModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.style.display = 'flex';
}

function hideModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.style.display = 'none';
}

// Initialize on DOM load
document.addEventListener('DOMContentLoaded', () => {
    initThemeToggle();
    initSidebarToggle();
});
