document.getElementById('login-form').addEventListener('submit', async function(event) {
    event.preventDefault(); // Предотвращаем стандартное поведение формы

    const login = document.getElementById('login').value;
    const password = document.getElementById('password').value;

    const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ login, password })
    });

    const messageDiv = document.getElementById('message');

    if (response.ok) {
        const data = await response.json();
        // Сохраняем access token в localStorage
        localStorage.setItem('accessToken', data.accessToken);
        messageDiv.innerText = `Вы успешно вошли как ${login}.`;
        // Перенаправление на главную страницу
        window.location.href = '/home';
    } else {
        const errorText = await response.text();
        messageDiv.innerText = `Ошибка: ${errorText}`;
    }
});

// Обработчик для кнопки регистрации
document.getElementById('register-button').addEventListener('click', () => {
    window.location.href = '/auth/register';
});
