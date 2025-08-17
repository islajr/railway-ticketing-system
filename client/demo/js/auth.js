const loginForm = document.getElementById('login-form');
const signupForm = document.getElementById('sign-up-form');
const signupContainer = document.getElementById('sign-up-container');
const loginContainer = document.getElementById('login-container');
const signUp = document.getElementById('signup-submit-button');
const globalURL = "https://rts-xdbm.onrender.com/";

/* functions */

// signup function
async function signUp(payload) {
    try {
        const response = await fetch(globalURL + "/api/v1/rts/auth/passenger/register", {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: payload
        })

        switch(response.status) {
            case Number (400):
                alert("please check your request.");
                break;
            case Number (401):
                alert("you are not authorized to access this resource");
                break;
            case Number (403):
                alert("you are forbidden from accessing this resource");
                break;    
            case Number (404):
                alert("not found!");
                break;
            case Number (500):
                alert("there was a problem on our end - please try again.");
                break;
            case Number (201):
                alert("successfully created new user. please login!");
                signupContainer.style.display = "none";
                loginContainer.style.display = "flex";
                break;
            default: // otherwise
                alert("something went wrong!");
                break;

        }

    } catch (error) {
        
    }
}

/* main code */

// signup form submission handler
document.getElementById('sign-up-form').addEventListener('submit', (e) => {
    e.preventDefault(); // prevent default form submission
})
signUp.addEventListener('submit', function(event) {
    event.preventDefault();
    const firstname = document.getElementById('signup-firstname').value;
    const lastname = document.getElementById('signup-lastname').value;
    const email = document.getElementById('signup-e-mail').value;
    const country = document.getElementById('signup-country').value;
    const phoneNumber = document.getElementById('phone-number').value;
    const password = document.getElementById('signup-password').value;

    const payload = JSON.stringify({
                "firstName": firstname,
                "lastName": lastname,
                "email": email,
                "country": country,
                "phoneNumber": phoneNumber,
                "password": password
            });
    
    signUp(payload);

    
});

// login form submission handler
document.getElementById('login-form').addEventListener('submit', (e) => {
    e.preventDefault(); // prevent default form submission
});
loginForm.addEventListener('submit', function(event) {
    event.preventDefault();
    const identifier = document.getElementById('login-identifier').value;
    const password = document.getElementById('login-password').value;

    async function getLogin() {
        try {
            const loginResponse = await fetch(globalURL + '/api/v1/rts/auth/passenger/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ 
                    "email": identifier, 
                    "password": password 
                })
            });

            // dealing with possible errors
            switch (loginResponse.status) {
                case Number (400):
                    alert("Please fill in all fields correctly.");
                    break;
                case Number (401):
                    alert("You are not authorized to use this resource")
                    break;
                case Number(404):
                    alert("User not found. Please check your credentials.");
                    break;
                case Number(500):
                    alert("An error occured, but don't worry, we're on it! Please try again later.");
                    break;
                default:
                    // if no errors...
                    const loginData = await loginResponse.json();
                    alert("Login successful!");
                    localStorage.setItem('accessToken', loginData.accessToken);
                    localStorage.setItem('refreshToken', loginData.refreshToken);
                    window.location.href = '../html/home.html';
                    break;
            }        
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while logging in. Please try again.');
        }
    }
    getLogin();
});