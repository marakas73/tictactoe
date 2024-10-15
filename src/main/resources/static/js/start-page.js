document.addEventListener('DOMContentLoaded', () => {
    const messageDiv = document.getElementById('message');

    document.getElementById('register').addEventListener('click', () => {
        // Переход к странице регистрации
        window.location.href = '/auth/register';
    });

    document.getElementById('login').addEventListener('click', () => {
        // Переход к странице логина
        window.location.href = '/auth/login';
    });
});
