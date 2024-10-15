document.addEventListener('DOMContentLoaded', () => {
    fetch('/api/user/history', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(gameRecords => {

        const tableBody = document.getElementById('gameHistory').getElementsByTagName('tbody')[0];
        gameRecords.forEach(record => {
            const row = tableBody.insertRow();
            row.insertCell(0).textContent = record.id;
            row.insertCell(1).textContent = record.player1Login;
            row.insertCell(2).textContent = record.player2Login;

            if (record.isDraw) {
                row.insertCell(3).textContent = "Ничья";
                row.insertCell(4).textContent = "Ничья";
                row.insertCell(5).textContent = "Да";
                row.cells[3].className = 'draw';
                row.cells[4].className = 'draw';
                row.cells[5].textContent = "Да";
            } else {
                row.insertCell(3).textContent = record.winnerLogin || "Нет";
                row.insertCell(4).textContent = record.looserLogin || "Нет";
                row.insertCell(5).textContent = "Нет";

                if (record.winnerLogin) {
                    row.cells[3].className = 'winner';
                    row.cells[4].className = 'looser';
                }
            }
        });
    })
    .catch(error => {
        console.error('Ошибка:', error);
    });
});
