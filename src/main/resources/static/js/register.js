document.getElementById('registration-form').addEventListener('submit', async function(event) {
    event.preventDefault(); // Предотвращаем стандартное поведение формы

    const login = document.getElementById('login').value;
    const password = document.getElementById('password').value;

    const response = await fetch('/api/auth/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ login, password })
    });

    const messageDiv = document.getElementById('message');

    if (response.ok) {
        const data = await response.json();
        messageDiv.innerText = `Пользователь ${data.login} успешно зарегистрирован!`;
    } else {
        const errorText = await response.text();
        messageDiv.innerText = `Ошибка: ${errorText}`;
    }
});

// Обработчик для кнопки входа
document.getElementById('login-button').addEventListener('click', () => {
    window.location.href = '/auth/login';
});
