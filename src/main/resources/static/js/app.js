// app.js — Script principal Yooni (authentification, navigation, WebSocket)

const API_BASE = '/api/auth'; // Base des endpoints d'authentification

// Gère la soumission du formulaire de connexion
document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault(); // Empêche le rechargement de la page
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    try {
        const response = await fetch(`${API_BASE}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('accessToken', data.accessToken);   // Stocke le token d'accès
            localStorage.setItem('refreshToken', data.refreshToken); // Stocke le refresh token
            alert('Connexion réussie !');
        } else {
            alert('E-mail ou mot de passe incorrect.');
        }
    } catch (err) {
        alert('Erreur de connexion au serveur.');
    }
});