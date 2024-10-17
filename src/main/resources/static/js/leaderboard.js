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

document.addEventListener('DOMContentLoaded', async () => {
    try {
        const response = await fetch('/api/user/leaderboard', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
            }
        });

        if (!response.ok) {
            throw new Error('Ошибка при получении данных');
        }

        const data = await response.json();
        const userInfoDiv = document.getElementById('user-info');
        const playerPlace = data.playerPlace || 'не найдено';

        const info = await fetchUserInfo();
        userInfoDiv.innerText = `${info.login}, ваше место в рейтинге: ${playerPlace}`;

        const leaderboardBody = document.getElementById('leaderboard-body');

        if (Array.isArray(data.leaderBoard)) {
            data.leaderBoard.forEach((ratingData) => {
                const row = document.createElement('tr');
                row.innerHTML = `<td>${ratingData.first}</td><td>${ratingData.second}</td>`;
                leaderboardBody.appendChild(row);
            });
        }
    } catch (error) {
        console.error('Ошибка:', error);
        document.getElementById('user-info').innerText = 'Не удалось загрузить данные';
    }
});

