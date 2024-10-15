document.getElementById('createGameForm').addEventListener('submit', async function(event) {
    event.preventDefault();

    const ownerSymbol = document.getElementById('ownerSymbol').value; // "X" или "O"
    const memberSymbol = document.getElementById('memberSymbol').value; // "X" или "O"
    const width = document.getElementById('width').value;
    const height = document.getElementById('height').value;
    const needToWin = document.getElementById('needToWin').value;

    const requestData = {
        ownerSymbol: ownerSymbol === "X" ? "CROSS" : "ZERO", // Конвертация в enum
        memberSymbol: memberSymbol === "X" ? "CROSS" : "ZERO", // Конвертация в enum
        width: parseInt(width),
        height: parseInt(height),
        needToWin: parseInt(needToWin)
    };

    const token = localStorage.getItem('accessToken');

    if (!token) {
        // Если токен отсутствует, перенаправьте на страницу логина
        window.location.href = '/auth/login';
    }

    try {
        const response = await fetch('/api/game/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(requestData)
        })

        if (!response.ok) {
            throw new Error('Ошибка при создании игры');
        }

        const createResponse = await response.json();
        window.location.href = `/game?id=${createResponse.id}`;

    } catch (error) {
        document.getElementById('responseMessage').innerText = error.message;
    }
});
