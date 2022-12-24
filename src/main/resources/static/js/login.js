let button = document.getElementById("button");
let emailInput = document.getElementById("email-input");
let passwordInput = document.getElementById("password-input");

button.addEventListener("click", (event) => {
    const email = emailInput.value;
    const password = passwordInput.value
    loginUser(email, password)
})


function loginUser(username, password) {
    const body = JSON.stringify({"username": username, "password": password})
    console.log(body)
    fetch('/oauth2/login', {
        credentials: "include",
        redirect: "follow",
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: body
    })
        .then(response => response.json())
        .then(response => console.log(JSON.stringify(response)))
        .catch(err => console.log(err))
}
