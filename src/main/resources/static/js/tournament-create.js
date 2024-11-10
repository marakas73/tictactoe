document.getElementById('createTournamentForm').addEventListener('submit', async function(event) {
    event.preventDefault();

    const playersCount = document.getElementById('playersCount').value;

    const requestData = {
        playersCount: playersCount
    };

    const token = localStorage.getItem('accessToken');

    if (!token) {
        // Если токен отсутствует, перенаправьте на страницу логина
        window.location.href = '/auth/login';
    }

    try {
        const response = await fetch('/api/game/tournament/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(requestData)
        })

        if (!response.ok) {
            throw new Error('Ошибка при создании турнира');
        }

        const createResponse = await response.json();
        window.location.href = `/tournament?id=${createResponse.id}`;

    } catch (error) {
        document.getElementById('responseMessage').innerText = error.message;
    }
});
