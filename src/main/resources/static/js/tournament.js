let intervalId = null;
var tournamentId = -1;

function startPolling() {
    intervalId = setInterval(fetchTournamentState, 1000);
}

function stopPolling() {
    clearInterval(intervalId);
}

async function getTournamentState() {
    try {
        const response = await fetch(`api/game/tournament/state?id=${tournamentId}`, {
            method: 'GET',
        });

        // Проверяем, успешен ли ответ
        if (!response.ok) {
            throw new Error(`Ошибка: ${response.status}`);
        }

        const tournamentState = await response.json(); // Парсим JSON
        return tournamentState;
    } catch (error) {
        console.error('Ошибка при получении данных:', error);
        throw error;
    }
}

async function fetchTournamentState() {
    try {
        const tournamentState = await getTournamentState();
        if(tournamentState) {
            updateTournament(tournamentState);
        }
    } catch (error) {
        console.error('Ошибка при опросе состояния турнира:', error);
        document.getElementById('error-message').innerText = error.message;
    }
}

function updateTournament(tournamentState) {
    const totalRounds = Math.ceil(Math.log2(tournamentState.playersCount)); // Определяем количество раундов
    const tournamentContainer = document.getElementById('tournament-container');

    let roundPlayers = [...tournamentState.playersLogin]; // Игроки, которые участвуют в текущем раунде

    tournamentContainer.innerHTML = ''; // Очищаем контейнер перед рендером

    // Для каждого раунда создаем его структуру
    for (let round = 1; round <= totalRounds + 1; round++) {
        const roundDiv = document.createElement('div');
        roundDiv.className = 'round';

        const roundGames = [];
        // Формируем игры для текущего раунда
        for (let i = 0; i < roundPlayers.length; i += 2) {
            const gameDiv = document.createElement('div');
            gameDiv.className = 'game';

            // Если второй игрок есть, отображаем пару игроков
            const player1 = roundPlayers[i] || '';
            const player2 = roundPlayers[i + 1] || '';

            // Отображаем игроков
            gameDiv.innerHTML = `
                <div><a>${player1}</a></div>
                <div><a>${player2}</a></div>
            `;

            roundGames.push(gameDiv);
        }

        // Добавляем игры в текущий раунд
        roundGames.forEach(game => roundDiv.appendChild(game));

        // Добавляем стрелочки, если это не последний раунд
        if (round !== totalRounds) {
            const arrowDiv = document.createElement('div');
            arrowDiv.className = 'arrow-horizontal';
            roundDiv.appendChild(arrowDiv);
        }

        tournamentContainer.appendChild(roundDiv);

        // Подготовка списка игроков для следующего раунда (победители)
        roundPlayers = [];

        // Если у нас есть информация о победителях для текущего раунда
        // Обновим список игроков, чтобы он содержал победителей
        if (tournamentState.roundWinnersLogin[round]) {
            roundPlayers = tournamentState.roundWinnersLogin[round]; // Заполняем победителей
        }
    }
}

async function handleTournamentStartClick() {
    // Start tournament
    try {
        const response = await fetch('api/game/tournament/start', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
            }
        });

        document.getElementById('error-message').innerText = 'Турнир запущен';
    } catch (error) {
        console.error('Ошибка при попытке старта:', error);
        document.getElementById('error-message').innerText = error.message;
    }
}



document.addEventListener('DOMContentLoaded', async () => {
    const params = new URLSearchParams(window.location.search);
    tournamentId = params.get('id');

    // Init
    fetchTournamentState()

    startPolling()

    document.getElementById('start-button').onclick = () => handleTournamentStartClick();
});