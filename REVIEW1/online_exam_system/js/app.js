
// REGISTER
document.getElementById("registerForm").addEventListener("submit", function(e){
    e.preventDefault();

    const formData = new FormData();
    formData.append("name", document.getElementById("name").value);
    formData.append("email", document.getElementById("email").value);
    formData.append("password", document.getElementById("password").value);

    fetch("api/register.php", {
        method: "POST",
        body: formData
    })
    .then(res => res.json())
    .then(data => {
        console.log(data);
        if(data.status === "success"){
            alert("Registration Successful");
            window.location = "login.html";
        }else{
            alert("Error: " + data.message);
        }
    });
});
// LOGIN
document.getElementById("loginForm")?.addEventListener("submit", async function(e){
    e.preventDefault();

    const email = document.getElementById("loginEmail").value;
    const password = document.getElementById("loginPassword").value;

    try {
        const response = await fetch("api/login.php", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });

        const result = await response.json();

        console.log(result); // 👈 important for debugging

        if(result.status === "success"){
            localStorage.setItem("student_id", result.id);
            window.location.href = "exam.html";
        } else {
            alert("Invalid Email or Password");
        }

    } catch(error){
        console.error(error);
        alert("Login Failed");
    }
});

// LOAD QUESTIONS
if(document.getElementById("questions")){
fetch("api/fetch_questions.php")
.then(res=>res.json())
.then(data=>{
    let html="";
    data.forEach(q=>{
        html+=`<p>${q.question}</p>
        <input type="radio" name="q${q.id}" value="1">${q.option1}<br>
        <input type="radio" name="q${q.id}" value="2">${q.option2}<br>
        <input type="radio" name="q${q.id}" value="3">${q.option3}<br>
        <input type="radio" name="q${q.id}" value="4">${q.option4}<hr>`;
    });
    questions.innerHTML=html;
});
}

// SUBMIT EXAM
function submitExam(){
    let answers={};
    document.querySelectorAll("input[type=radio]:checked")
    .forEach(r=>{
        let qid=r.name.replace("q","");
        answers[qid]=r.value;
    });

    fetch("api/submit_exam.php",{
        method:"POST",
        headers:{"Content-Type":"application/json"},
        body:JSON.stringify({
            student_id:localStorage.getItem("student_id"),
            answers:answers
        })
    })
    .then(res=>res.json())
    .then(data=>{
        alert("Score: "+data.score+"/"+data.total);
        window.location="login.html";
    });
}