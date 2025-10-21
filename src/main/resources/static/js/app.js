// Custom JavaScript for MovieReserve

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Initialize popovers
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });

    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => {
                alert.remove();
            }, 500);
        }, 5000);
    });

    // Add loading states to forms
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function() {
            const submitBtn = this.querySelector('button[type="submit"]');
            if (submitBtn) {
                const originalText = submitBtn.innerHTML;
                submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status"></span>Processing...';
                submitBtn.disabled = true;
                
                // Re-enable after 10 seconds as fallback
                setTimeout(() => {
                    submitBtn.innerHTML = originalText;
                    submitBtn.disabled = false;
                }, 10000);
            }
        });
    });

    // Movie card hover effects
    const movieCards = document.querySelectorAll('.movie-card');
    movieCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-5px)';
            this.style.boxShadow = '0 10px 25px rgba(0, 0, 0, 0.15)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = '0 2px 10px rgba(0, 0, 0, 0.1)';
        });
    });

    // Search functionality
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            const movieCards = document.querySelectorAll('.movie-card');
            
            movieCards.forEach(card => {
                const title = card.querySelector('.card-title').textContent.toLowerCase();
                const description = card.querySelector('.card-text').textContent.toLowerCase();
                
                if (title.includes(searchTerm) || description.includes(searchTerm)) {
                    card.parentElement.style.display = 'block';
                } else {
                    card.parentElement.style.display = 'none';
                }
            });
        });
    }

    // Genre filter functionality
    const genreFilter = document.getElementById('genreFilter');
    if (genreFilter) {
        genreFilter.addEventListener('change', function() {
            const selectedGenre = this.value;
            const movieCards = document.querySelectorAll('.movie-card');
            
            movieCards.forEach(card => {
                const genre = card.querySelector('.card-text.text-muted').textContent;
                
                if (!selectedGenre || genre.includes(selectedGenre)) {
                    card.parentElement.style.display = 'block';
                } else {
                    card.parentElement.style.display = 'none';
                }
            });
        });
    }

    // Smooth scrolling for anchor links
    const anchorLinks = document.querySelectorAll('a[href^="#"]');
    anchorLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const targetId = this.getAttribute('href').substring(1);
            const targetElement = document.getElementById(targetId);
            
            if (targetElement) {
                targetElement.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });

    // Add fade-in animation to elements
    const animatedElements = document.querySelectorAll('.card, .hero-section, .feature-icon');
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('fade-in');
            }
        });
    });

    animatedElements.forEach(element => {
        observer.observe(element);
    });
});

// Utility functions
function showAlert(message, type = 'info', duration = 5000) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        <i class="fas fa-info-circle me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    const container = document.querySelector('.container') || document.body;
    container.insertBefore(alertDiv, container.firstChild);
    
    if (duration > 0) {
        setTimeout(() => {
            alertDiv.style.transition = 'opacity 0.5s ease';
            alertDiv.style.opacity = '0';
            setTimeout(() => {
                alertDiv.remove();
            }, 500);
        }, duration);
    }
}

function confirmAction(message, callback) {
    if (confirm(message)) {
        callback();
    }
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

function formatDateTime(dateString) {
    return new Date(dateString).toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// API helper functions
async function makeRequest(url, options = {}) {
    try {
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error('Request failed:', error);
        showAlert('Request failed. Please try again.', 'danger');
        throw error;
    }
}

// Booking functions
function bookMovie(movieId) {
    window.location.href = `/booking/movie/${movieId}`;
}

function cancelBooking(bookingId) {
    confirmAction('Are you sure you want to cancel this booking?', async () => {
        try {
            const response = await makeRequest(`/booking-management-service/api/v1/bookings/cancel/${bookingId}`, {
                method: 'POST'
            });
            
            if (response.code === 200) {
                showAlert('Booking cancelled successfully!', 'success');
                setTimeout(() => {
                    location.reload();
                }, 1500);
            } else {
                showAlert('Error cancelling booking: ' + response.message, 'danger');
            }
        } catch (error) {
            showAlert('Error cancelling booking', 'danger');
        }
    });
}

function viewBooking(bookingId) {
    // Implement view booking functionality
    console.log('View booking:', bookingId);
    // You can redirect to a booking details page or show a modal
}

// Movie management functions
function editMovie(movieId) {
    // Implement edit movie functionality
    console.log('Edit movie:', movieId);
    // You can redirect to an edit page or show a modal
}

function deleteMovie(movieId) {
    confirmAction('Are you sure you want to delete this movie?', async () => {
        try {
            const response = await makeRequest(`/movie-management-service/api/v1/movies/admin/${movieId}`, {
                method: 'DELETE'
            });
            
            if (response.code === 200) {
                showAlert('Movie deleted successfully!', 'success');
                setTimeout(() => {
                    location.reload();
                }, 1500);
            } else {
                showAlert('Error deleting movie: ' + response.message, 'danger');
            }
        } catch (error) {
            showAlert('Error deleting movie', 'danger');
        }
    });
}

// Form validation helpers
function validateEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function validatePassword(password) {
    return password.length >= 8;
}

function validatePhone(phone) {
    const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
    return phoneRegex.test(phone.replace(/\s/g, ''));
}

// Local storage helpers
function saveToLocalStorage(key, value) {
    try {
        localStorage.setItem(key, JSON.stringify(value));
    } catch (error) {
        console.error('Error saving to localStorage:', error);
    }
}

function getFromLocalStorage(key, defaultValue = null) {
    try {
        const item = localStorage.getItem(key);
        return item ? JSON.parse(item) : defaultValue;
    } catch (error) {
        console.error('Error reading from localStorage:', error);
        return defaultValue;
    }
}

// Session helpers
function checkSession() {
    return fetch('/user-management-service/api/v1/users/visitors/check')
        .then(response => response.json())
        .then(data => data.success)
        .catch(error => {
            console.error('Session check failed:', error);
            return false;
        });
}

// Export functions for use in other scripts
window.MovieReserve = {
    showAlert,
    confirmAction,
    formatCurrency,
    formatDate,
    formatDateTime,
    makeRequest,
    bookMovie,
    cancelBooking,
    viewBooking,
    editMovie,
    deleteMovie,
    validateEmail,
    validatePassword,
    validatePhone,
    saveToLocalStorage,
    getFromLocalStorage,
    checkSession
};
