async function fetchUserInfo() {
    try {
        const response = await fetch('api/user/info', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
            }
        });

        if (!response.ok) {
            throw new Error('Ошибка получения информации о пользователе');
        }

        const info = await response.json();
        return info.rating; // Возвращаем рейтинг

    } catch (error) {
        console.error('Ошибка при получении информации о пользователе:', error);
        return null; // Возвращаем null в случае ошибки
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('accessToken');

    if (!token) {
        // Если токен отсутствует, перенаправьте на страницу логина
        window.location.href = '/auth/login';
    }

    // Функция для декодирования JWT-токена
    function parseJwt(token) {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(c =>
            '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
        ).join(''));

        return JSON.parse(jsonPayload);
    }

    const userData = parseJwt(token);
    const username = userData.sub;

    // Приветствие пользователя
    document.getElementById('welcome-message').innerText = `Добро пожаловать, ${username}!`;

    // Rating message
    fetchUserInfo().then(rating => {
        document.getElementById('rating-message').innerText = `Ваш рейтинг: ${rating}`;
    });

    const messageDiv = document.getElementById('message');

    document.getElementById('create-game').addEventListener('click', () => {
        window.location.href = '/game/create';
    });

    document.getElementById('join-game').addEventListener('click', () => {
        // Создаем поле для ввода ID игры, если оно еще не создано
        let gameIdInput = document.getElementById('game-id-input');
        if (!gameIdInput) {
            gameIdInput = document.createElement('input');
            gameIdInput.type = 'text';
            gameIdInput.id = 'game-id-input';
            gameIdInput.placeholder = 'Введите ID игры';
            messageDiv.appendChild(gameIdInput);

            const joinGameButton = document.createElement('button');
            joinGameButton.innerText = 'Присоединиться';
            joinGameButton.id = 'confirm-join-game';
            messageDiv.appendChild(joinGameButton);

            // Обработчик нажатия на кнопку "Присоединиться"
            joinGameButton.addEventListener('click', async () => {
                const gameId = gameIdInput.value.trim();
                if (gameId) {
                    try {
                        const response = await fetch(`/api/game/join?id=${gameId}`, {
                            method: 'GET',
                            headers: {
                                'Authorization': `Bearer ${token}`
                            }
                        });

                        if (response.ok) {
                            // Переход на страницу игры
                            window.location.href = `/game?id=${gameId}`;
                        } else {
                            // Ошибка при присоединении к игре
                            messageDiv.innerText = 'Ошибка при присоединении к игре. Пожалуйста, проверьте ID.';
                        }
                    } catch (error) {
                        messageDiv.innerText = 'Произошла ошибка: ' + error.message;
                    }
                } else {
                    messageDiv.innerText = 'Пожалуйста, введите ID игры.';
                }
            });
        }
    });

    document.getElementById('game-history').addEventListener('click', () => {
        window.location.href = '/history';
    });

    document.getElementById('leaderboard').addEventListener('click', () => {
            window.location.href = '/leaderboard';
        });

    document.getElementById('logout').addEventListener('click', () => {
        localStorage.removeItem('accessToken');
        window.location.href = '/auth/login';
    });

});