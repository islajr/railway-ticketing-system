const loginForm = document.getElementById('login-form');
const signupForm = document.getElementById('sign-up-form');
const signupContainer = document.getElementById('sign-up-container');
const loginContainer = document.getElementById('login-container');
const adminSignupContainer = document.getElementById('admin-signup-container');
const adminLoginContainer = document.getElementById('admin-login-container');
const signUp = document.getElementById('signup-submit-button');
const adminSignUpForm = document.getElementById('admin-signup-form');
const adminLoginForm = document.getElementById('admin-login-form');
const passengerBtn  = document.getElementById('passenger-role-btn');
const adminBtn = document.getElementById('admin-role-btn');
const globalURL = "https://rts-xdbm.onrender.com";

/* functions */

// signup function
async function register(payload) {
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
                console.log("successfully created new passenger with e-mail:", JSON.parse(payload).email);
                alert("successfully created new passenger. please login!");
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

// admin signup function
async function registerAdmin(payload) {
    try {
        const response = await fetch(globalURL + "/api/v1/rts/auth/admin/register", {
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
                console.log("successfully created new admin with e-mail:", JSON.parse(payload).email);
                alert("successfully created new admin. please login!");
                adminSignupContainer.style.display = "none";
                adminLoginContainer.style.display = "flex";
                break;
            default: // otherwise
                alert("something went wrong!");
                break;

        }

    } catch (error) {
        console.log('Error:', error);
        alert('An error occurred while registering. Please try again.');
    }
}

// login function
async function getLogin(identifier, password) {
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
                    window.location.href = '../html/passenger-home.html';
                    break;
            }        
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while logging in. Please try again.');
        }
}

// admin login function
async function adminLogin(identifier, password) {
        try {
            const loginResponse = await fetch(globalURL + '/api/v1/rts/auth/admin/login', {
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
                    alert("Admin not found. Please check your credentials.");
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
                    window.location.href = '../html/admin-home.html';
                    break;
            }        
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while logging in. Please try again.');
        }
    }

/* main code */

// signup form submission handler
signupForm.addEventListener('submit', function(event) {
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
                "phone": phoneNumber,
                "password": password
            });
    
    register(payload);

    
});

// login form submission handler
document.getElementById('login-form').addEventListener('submit', (e) => {
    e.preventDefault(); // prevent default form submission
});
loginForm.addEventListener('submit', function(event) {
    event.preventDefault();
    const identifier = document.getElementById('login-identifier').value;
    const password = document.getElementById('login-password').value;

    
    getLogin(identifier, password);
});

// admin signup form submission handler
adminSignUpForm.addEventListener('submit', function(event) {
    event.preventDefault();
    const firstname = document.getElementById('admin-signup-firstname').value;
    const lastname = document.getElementById('admin-signup-lastname').value;
    const email = document.getElementById('admin-signup-e-mail').value;
    const password = document.getElementById('admin-signup-password').value;

    const payload = JSON.stringify({
                "firstName": firstname,
                "lastName": lastname,
                "email": email,
                "password": password
            });

    registerAdmin(payload);
});

// admin login form submission handler
adminLoginForm.addEventListener('submit', function(event) {
    event.preventDefault();
    const identifier = document.getElementById('admin-login-identifier').value;
    const password = document.getElementById('admin-login-password').value;

    adminLogin(identifier, password);
});

// role selection logic
passengerBtn.addEventListener('click', (e) => {
    e.preventDefault();
    console.log("passenger role selected");
    document.getElementById('role-choice').style.display = "none";
    signupContainer.style.display = "none";
    loginContainer.style.display = "flex";

})

adminBtn.addEventListener('click', (e) => {
    e.preventDefault();
    console.log("admin role selected");
    document.getElementById('role-choice').style.display = "none";
    adminSignupContainer.style.display = "none";
    adminLoginContainer.style.display = "flex";
})
