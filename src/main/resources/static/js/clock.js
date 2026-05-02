window.onload = function () {

    function updateClock() {

        const now = new Date();

        const sec = now.getSeconds();
        const min = now.getMinutes();
        const hour = now.getHours();

        const second = document.getElementById("second");
        const minute = document.getElementById("minute");
        const hourEl = document.getElementById("hour");
        const date = document.getElementById("date");

        if (!second || !minute || !hourEl || !date) return;

        second.style.transform = `rotate(${sec * 6}deg)`;
        minute.style.transform = `rotate(${min * 6}deg)`;
        hourEl.style.transform = `rotate(${(hour % 12) * 30 + min * 0.5}deg)`;

        date.innerText = now.toLocaleDateString('ru-RU');
    }

    updateClock();
    setInterval(updateClock, 1000);
};