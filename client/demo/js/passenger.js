const welcomeGreeting = document.querySelector(".welcome-greeting");
const reservationsList = document.getElementById("aside-reservations-list");
const asideSchedulesList = document.getElementById("aside-schedules-list")
const logoutFeature = document.querySelector(".logout-feature")
const heroSectionContainer = document.querySelector(".hero-section-container");
const homeBookTicket = document.getElementById("home-book-a-ticket");
const homeViewTickets = document.getElementById("home-view-tickets");
const homeCancelTickets = document.getElementById("home-cancel-ticket");
const ticketBookingSection = document.querySelector("#ticket-booking-section");
const scheduleSearchFilterForm = document.getElementById("schedule-search-filter-form");
const originScheduleFilter = document.getElementById("schedule-search-filter-origin");
const destinationScheduleFilter = document.getElementById("schedule-search-filter-destination");
const allScheduleFilter = document.getElementById("schedule-search-filter-all");
const scheduleSearchFields = document.getElementById("schedule-search-fields");
const scheduleSearchOrigin = document.getElementById("ticket-booking-schedule-search-origin");
const scheduleSearchDestination = document.getElementById("ticket-booking-schedule-search-destination");
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
        console.log(data);
        welcomeGreeting.textContent = `Welcome, ${data.userDetails.firstName}`;
        
        // reservations display
        if (data.reservations.length != 0) {
            data.reservations.forEach(reservation => {
                reservationsList.innerHTML = reservationsList.innerHTML + `<li>${reservation.reservationId}</li>`
            });
        }

        // train schedules display
        if (data.upcomingSchedules.length != 0) {
            data.upcomingSchedules.forEach(schedule => {
                asideSchedulesList.innerHTML = asideSchedulesList.innerHTML + `<li>${schedule.scheduleId}</li>`
            })
        }

    } catch(error) {
        console.log("error: ", error)
        alert("failed to get homepage")
    }
}

async function logout() {
    accessToken = localStorage.getItem("accessToken");

    try {
        const response = await fetch(globalURL + "/api/v1/rts/auth/logout", {
            method: 'GET',
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${accessToken}`
            }
        })

        switch(response.status) {
            case Number (401):
                alert("you are not authorized to access this resource");
                break;
            case Number (403):
                alert("you are forbidden from accessing this resource");
                break;    
            case Number (500):
                alert("there was a problem on our end - please try again.");
                break;
            case Number (200):
                console.log("logout successful!");
                alert("successfully logged out!");
                localStorage.clear();
                window.location.href = "../html/index.html";
                break;
            default: // otherwise
                alert("failed to logout!");
                break;

        }

        
    } catch (error) {
        console.log("error: ", error);
        alert("failed to complete logout!")
    }
}

/* main logic */

// load homepage
loadHomePage();

// logout event listener
logoutFeature.addEventListener('click', (e) => {
    
    // maybe a confirmatory prompt?
    e.preventDefault();
    logout();
});

originScheduleFilter.addEventListener('change', (e) => {
    if (e.target.checked) {
        scheduleSearchFields.hidden = false;
        scheduleSearchOrigin.hidden = false;
    } else {
        scheduleSearchOrigin.hidden = true;
    }

})

destinationScheduleFilter.addEventListener('click', (e) => {
    if (e.target.checked) {
        scheduleSearchFields.hidden = false;
        scheduleSearchDestination.hidden = false;
    } else {
        scheduleSearchDestination.hidden = true;
    }
})

allScheduleFilter.addEventListener('click', (e) => {
    if (e.target.checked) {
        scheduleSearchFields.hidden = false;
        scheduleSearchOrigin.hidden = false;
        scheduleSearchDestination.hidden = false;
    } else {
        scheduleSearchFields.hidden = true;
        scheduleSearchOrigin.hidden =true;
        scheduleSearchDestination.hidden = true;
    }
})
