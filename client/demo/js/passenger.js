const welcomeGreeting = document.querySelector(".welcome-greeting");
const reservationsList = document.getElementById("#aside-reservations-list");
const globalURL = "https://rts-xdbm.onrender.com";


/* functions and methods */
async function loadHomePage() {
    accessToken = localStorage.getItem("accessToken");
    
    try {
        const response = await fetch(globalURL + "/api/v1/rts/app/home", {
            method: 'GET',
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${accessToken}`
            }
        })

        data = await response.json();
        welcomeGreeting = `Welcome, ${data.userDetails.firstName}`;
        
        if (data.reservations.length() != 0) {
            data.reservations.forEach(reservation => {
                reservationsList.innerHTML = reservationsList.innerHTML + `<li>reservation.reservationId</li>`
            });
        }

    } catch(error) {
        console.log("error: ", error)
        alert("failed to get homepage")
    }
}
/* main logic */

// load homepage
loadHomePage();
