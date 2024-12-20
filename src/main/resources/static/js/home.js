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
        return info;

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


    // Current game
    let currentGameDiv = document.getElementById('current-game');

    if(!currentGameDiv) {
        currentGameDiv = document.createElement('div');
        currentGameDiv.id = 'current-game';

        document.body.appendChild(currentGameDiv);
    }

    fetchUserInfo().then(info => {

        // Get user current game id (may be null)
        const currentGameId = info.currentGameId;

        // Clear current game div anyway to show actual info
        currentGameDiv.innerHTML = '';

        if(currentGameId) {
            // Add buttons and label

            // Create div
            currentGameIdLabel = document.createElement('p');
            currentGameIdLabel.textContent = `Внимание! Вы все еще в игре с ID: ${currentGameId}`;
            currentGameIdLabel.id = 'current-game-id-label';
            currentGameDiv.appendChild(currentGameIdLabel);

            // Create back to game button
            const backToGameButton = document.createElement('button');
            backToGameButton.innerText = 'Вернуться в игру';
            backToGameButton.id = 'back-to-game-button';
            currentGameDiv.appendChild(backToGameButton);

            // Create leave button
            const leaveCurrentGameButton = document.createElement('button');
            leaveCurrentGameButton.innerText = 'Покинуть текущую игру';
            leaveCurrentGameButton.id = 'leave-current-game-button';
            currentGameDiv.appendChild(leaveCurrentGameButton);

            // Disable join & create game buttons
            const createGameButton = document.getElementById('create-game');
            const joinGameButton = document.getElementById('join-game');
            createGameButton.disabled = true;
            joinGameButton.disabled = true;

            // Back to game button handler
            backToGameButton.addEventListener('click', async () => {
                window.location.href = `/game?id=${currentGameId}`;
            });

            // Leave current game button handler
            leaveCurrentGameButton.addEventListener('click', async () => {
                try {
                    const response = await fetch(`/api/game/leave`, {
                        method: 'GET',
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    });
                    if (response.ok) {
                        // Clear current game div
                        currentGameDiv.innerHTML = '';

                        // Enable join & create game buttons
                        const createGameButton = document.getElementById('create-game');
                        const joinGameButton = document.getElementById('join-game');
                        createGameButton.disabled = false;
                        joinGameButton.disabled = false;
                    } else {
                        // Ошибка при выходе из игры
                        currentGameDiv.innerText = 'Ошибка при выходе из игры.';
                    }
                } catch (error) {
                    currentGameDiv.innerText = 'Произошла ошибка: ' + error.message;
                }
            });
        }
    });


    // Приветствие пользователя

    const userData = parseJwt(token);
    const username = userData.sub;

    document.getElementById('welcome-message').innerText = `Добро пожаловать, ${username}!`;


    // Rating message
    fetchUserInfo().then(info => {
        document.getElementById('rating-message').innerText = `Ваш рейтинг: ${info.rating}`;
    });


    document.getElementById('create-game').addEventListener('click', () => {
        window.location.href = '/game/create';
    });


    document.getElementById('join-game').addEventListener('click', () => {
        const messageDiv = document.getElementById('message');

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

    document.getElementById('create-tournament').addEventListener('click', () => {
        window.location.href = '/tournament/create';
    });

    document.getElementById('join-tournament').addEventListener('click', () => {
        const messageDiv = document.getElementById('message');

        // Создаем поле для ввода ID игры, если оно еще не создано
        let tournamentIdInput = document.getElementById('game-id-input');
        if (!tournamentIdInput) {
            tournamentIdInput = document.createElement('input');
            tournamentIdInput.type = 'text';
            tournamentIdInput.id = 'game-id-input';
            tournamentIdInput.placeholder = 'Введите ID игры';
            messageDiv.appendChild(tournamentIdInput);

            const joinTournamentButton = document.createElement('button');
            joinTournamentButton.innerText = 'Присоединиться';
            joinTournamentButton.id = 'confirm-join-game';
            messageDiv.appendChild(joinTournamentButton);

            // Обработчик нажатия на кнопку "Присоединиться"
            joinTournamentButton.addEventListener('click', async () => {
                const tournamentId = tournamentIdInput.value.trim();
                if (tournamentId) {
                    try {
                        const response = await fetch(`/api/game/tournament/join?id=${tournamentId}`, {
                            method: 'GET',
                            headers: {
                                'Authorization': `Bearer ${token}`
                            }
                        });

                        if (response.ok) {
                            // Переход на страницу игры
                            window.location.href = `/tournament?id=${tournamentId}`;
                        } else {
                            // Ошибка при присоединении к игре
                            messageDiv.innerText = 'Ошибка при присоединении к турниру. Пожалуйста, проверьте ID.';
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

    document.getElementById('logout').addEventListener('click', () => {
        localStorage.removeItem('accessToken');
        window.location.href = '/auth/login';
    });


});