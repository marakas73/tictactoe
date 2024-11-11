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
        document.getElementById('errorMessage').innerText = error.message;
    }
}

// Функция для рендеринга сетки турнира
function updateTournament(tournamentState) {
    const tournamentGrid = document.getElementById('tournament-grid');
    tournamentGrid.innerHTML = ''; // Очищаем старую сетку

    // Количество раундов (логарифм по основанию 2 от количества игроков)
    const rounds = Math.log2(tournamentState.playersCount);

    let table = '<table>';

    // Заголовок таблицы (раунды)
    table += '<thead><tr>';
    for (let round = 1; round <= rounds; round++) {
        table += `<th>Раунд ${round}</th>`;
    }
    table += '</tr></thead>';

    // Создаем массив матчей для каждого раунда
    const roundsData = [];

    // Заполняем данные для остальных раундов, используя победителей из roundGames
    for (let round = 1; round <= rounds; round++) {
        const roundGame = tournamentState.roundGames[round] || [];
        let row = roundGame.map(game => game === null ? null : game); // Маппинг победителей или null
        roundsData.push(row);
    }

    // Теперь строим таблицу по столбцам (раунды сверху вниз)
    let maxRows = tournamentState.playersCount / 2;
    for (let rowIndex = 0; rowIndex < maxRows; rowIndex++) {
        table += '<tr>';

        // Для каждого раунда (столбца) мы выводим данные по строкам (игрокам)
        for (let roundIndex = 0; roundIndex < roundsData.length; roundIndex++) {
            if(rowIndex >= roundsData[roundIndex].length)
                continue;

            const roundGame = roundsData[roundIndex][rowIndex];
            // Для последующих раундов показываем победителей или пустые ячейки
            if (roundGame) {
                if(roundGame.winner)
                    table += `<td>${roundGame.winner}</td>`;
                else
                    table +=
                        `<td>Playing:<br>${roundGame.firstPlayerLogin} vs ${roundGame.secondPlayerLogin}</td>`;
            } else {
                table += `<td class="empty"></td>`;
            }
        }
    }

    table += '</table>';

    tournamentGrid.innerHTML = table;
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

        if (!response.ok) {
            document.getElementById('errorMessage').innerText = `Ошибка ${response.status}: ${response.statusText}`;
        } else {
            document.getElementById('errorMessage').innerText = '';
            document.getElementById('successMessage').innerText = 'Турнир запущен';
        }
    } catch (error) {
        console.error('Ошибка при попытке старта:', error);
        document.getElementById('errorMessage').innerText = error.message;
    }
}


document.addEventListener('DOMContentLoaded', async () => {
    const params = new URLSearchParams(window.location.search);
    tournamentId = params.get('id');

    // Init
    fetchTournamentState()

    startPolling()

    document.getElementById('startTournamentButton').onclick = () => handleTournamentStartClick();
});