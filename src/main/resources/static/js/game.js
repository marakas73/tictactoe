let intervalId = null;

document.addEventListener('DOMContentLoaded', () => {
    fetchGameState();
    // Start polling
    startPolling();
});

document.getElementById('start-game').onclick = async () => {
    try {
        const response = await fetch('api/game/state', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
            }
        });

        if (!response.ok) {
            throw new Error('Ошибка получения состояния игры');
        }

        const gameState = await response.json();
        if(gameState.gameStatus == "NOT_STARTED") {

            // Start game
            try {
                const response = await fetch('api/game/start', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
                    }
                });

                if (!response.ok) {
                    throw new Error('Ошибка начала игры');
                }

            } catch (error) {
                console.error('Ошибка при начале игры:', error);
                document.getElementById('status').innerText = error.message;
            }
        }
    } catch (error) {
        console.error('Ошибка при проверке состояния игры:', error);
        document.getElementById('status').innerText = error.message;
    }
};

function handleGameStatus(gameStatus) {
    if(!gameStatus || gameStatus == "IN_PROGRESS"
    || gameStatus == "NOT_STARTED") {
        return;
    }

    stopPolling();

    let message;
    switch(gameStatus) {
    case "CROSS_WON":
        message = "Крестики победили";
        break;
    case "ZERO_WON":
        message = "Нолики победили";
        break;
    case "DRAW":
        message = "Ничья";
        break;
    case "ABORTED":
        message = "Игра была прервана";
        break;
    default:
        message = "Ошибка";
        break;
    }

    document.getElementById('status').innerText = message;
}

function startPolling() {
    intervalId = setInterval(fetchGameState, 500);
}

function stopPolling() {
    clearInterval(intervalId);
}

async function fetchGameState() {
    try {
        const response = await fetch('api/game/state', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
            }
        });

        if (!response.ok) {
            throw new Error('Ошибка получения состояния игры');
        }

        const gameState = await response.json();
        if(gameState) {
            updateGameBoard(gameState);
            handleGameStatus(gameState.gameStatus);
        }
    } catch (error) {
        console.error('Ошибка при опросе состояния игры:', error);
        document.getElementById('status').innerText = error.message;
    }
}

function getSymbolByText(gameSymbol) {
    switch(gameSymbol) {
    case "CROSS":
        return "X";
    case "ZERO":
        return "O";
    case null:
        return "";
    default:
        return "";
    }
}

function updateGameBoard(gameState) {
    const board = document.getElementById('game-board');
    board.innerHTML = ''; // очищаем текущее состояние

    // Стили для контейнера
    board.style.display = 'grid';

    board.style.gridTemplateColumns = `repeat(${gameState.field[0].length}, 15px)`;
    board.style.gridGap = '1px';

    gameState.field.forEach((row, rowIndex) => {
        row.forEach((cell, colIndex) => {
            const cellDiv = document.createElement('div');
            cellDiv.className = 'cell';
            cellDiv.innerText = getSymbolByText(cell);
            cellDiv.style.width = '15px';
            cellDiv.style.height = '15px';
            cellDiv.style.backgroundColor = 'gray';
            cellDiv.style.border = '1px solid black';
            cellDiv.onclick = () => handleCellClick(rowIndex, colIndex, gameState);
            board.appendChild(cellDiv);
        });
    });

    document.getElementById('current-turn').innerText = getSymbolByText(gameState.currentMove);
    document.getElementById('player1').innerText = gameState.ownerId;
    document.getElementById('player2').innerText = gameState.memberId;
    document.getElementById('symbol1').innerText = getSymbolByText(gameState.ownerSymbol);
    document.getElementById('symbol2').innerText = getSymbolByText(gameState.memberSymbol);
}

async function handleCellClick(rowIndex, colIndex, gameState) {
    if(gameState.gameStatus == "IN_PROGRESS")
    {
        try {
            const moveRequest = {
                x: colIndex,
                y: rowIndex
            };

            const response = await fetch('api/game/move', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
                },
                body: JSON.stringify(moveRequest)
            });

            if (!response.ok) {
                throw new Error('Ошибка во время хода');
            }


            // Update cell instantly
            const cell = document.querySelector(`.cell[data-row="${colIndex}"][data-col="${rowIndex}"]`);
            if (cell) {
                cell.innerText = getSymbolByText(gameState.currentMove);
            }

        } catch (error) {
            console.error('Ошибка во время хода:', error);
        }
    }
}

async function handleExitGameClick() {
    // leave game
    try {
        const response = await fetch('api/game/leave', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
            }
        });

        window.location.href = '/home';

    } catch (error) {
        console.error('Ошибка при попытке выхода:', error);
        document.getElementById('status').innerText = error.message;
    }
}

document.getElementById('exit-game').onclick = () => handleExitGameClick();
