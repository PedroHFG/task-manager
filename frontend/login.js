document
  .getElementById("loginForm")
  .addEventListener("submit", async function (event) {
    event.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const clientId = "myclientid";
    const clientSecret = "myclientsecret";
    localStorage.removeItem("token"); // Remove qualquer token inválido

    const headers = new Headers();
    headers.append(
      "Authorization",
      "Basic " + btoa(clientId + ":" + clientSecret)
    );
    headers.append("Content-Type", "application/x-www-form-urlencoded");

    const body = new URLSearchParams();
    body.append("grant_type", "password");
    body.append("username", email);
    body.append("password", password);

    try {
      const response = await fetch("http://localhost:8080/oauth2/token", {
        method: "POST",
        headers: headers,
        body: body,
      });

      if (response.ok) {
        const data = await response.json();
        if (data.access_token) {
          localStorage.setItem("token", data.access_token);
          alert("Login realizado com sucesso!");
          window.location.href = "index.html"; // Redirecionar para a página de tarefas
        } else {
          alert("Erro ao obter token de autenticação.");
        }
      } else {
        alert("Falha no login. Verifique suas credenciais.");
      }
    } catch (error) {
      console.error("Erro ao fazer login:", error);
      alert("Erro inesperado. Tente novamente mais tarde.");
    }
  });
