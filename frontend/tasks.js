document.addEventListener("DOMContentLoaded", async function () {
  const token = localStorage.getItem("token");
  let currentPage = 0; // Página atual
  let totalPages = 0; // Total de páginas
  let currentTaskId = null; // ID da tarefa sendo editada

  if (!token || token === "undefined") {
    alert("Usuário não autenticado. Faça login novamente.");
    localStorage.removeItem("token");
    window.location.href = "login.html";
    return;
  }

  // Função para buscar tarefas
  async function fetchTasks(page = 0) {
    try {
      const response = await fetch(
        `http://localhost:8080/tasks?page=${page}&size=3`,
        {
          method: "GET",
          headers: {
            Authorization: "Bearer " + token,
            "Content-Type": "application/json",
          },
        }
      );

      if (response.ok) {
        const data = await response.json();
        const tasks = data.content;
        totalPages = data.totalPages; // Atualiza o total de páginas
        const taskContainer = document.getElementById("taskContainer");
        taskContainer.innerHTML = "";

        tasks.forEach((task) => {
          const badgeClass =
            task.status === "PENDING"
              ? "bg-warning"
              : task.status === "IN_PROGRESS"
              ? "bg-primary"
              : "bg-success";

          const card = document.createElement("div");
          card.className = "col-md-4 mb-3";
          card.innerHTML = `
                      <div class="card shadow-sm border-0 h-100">
                          <div class="card-body">
                              <h6 class="card-title">${task.title}</h6>
                              <p class="card-text">${task.description}</p>
                              <span class="badge ${badgeClass} text-white">${
            task.status
          }</span>
                              <p class="mt-2"><strong>Data Limite:</strong> <span class="due-date">${new Date(
                                task.dueDate
                              ).toLocaleDateString()}</span></p>
                              <p class="text-muted small">Criado em: ${new Date(
                                task.createdAt
                              ).toLocaleDateString()}</p>
                              <p class="text-muted small">Última Atualização: ${
                                task.updatedAt
                                  ? new Date(
                                      task.updatedAt
                                    ).toLocaleDateString()
                                  : "-"
                              }</p>
                              <div class="d-flex justify-content-end">
                                  <button class="btn btn-success btn-sm me-2 mark-completed" data-id="${
                                    task.id
                                  }"><i class="fas fa-check"></i></button>
                                  <button class="btn btn-primary btn-sm me-2 edit-task" data-id="${
                                    task.id
                                  }"><i class="fas fa-edit"></i></button>
                                  <button class="btn btn-danger btn-sm mark-deleted" data-id="${
                                    task.id
                                  }"><i class="fas fa-trash"></i></button>
                              </div>
                          </div>
                      </div>
                  `;
          taskContainer.appendChild(card);
        });

        // Adiciona eventos ao botão de ação
        const completedButtons = document.querySelectorAll(".mark-completed");
        completedButtons.forEach((button) => {
          button.addEventListener("click", markAsCompleted);
        });

        const deletedButtons = document.querySelectorAll(".mark-deleted");
        deletedButtons.forEach((button) => {
          button.addEventListener("click", markAsDeleted);
        });

        const editButtons = document.querySelectorAll(".edit-task");
        editButtons.forEach((button) => {
          button.addEventListener("click", openEditModal);
        });

        // Atualiza o estado dos botões de paginação
        document.getElementById("prevPage").disabled = currentPage === 0;
        document.getElementById("nextPage").disabled =
          currentPage >= totalPages - 1;
      } else if (response.status === 401) {
        alert("Sessão expirada. Faça login novamente.");
        localStorage.removeItem("token");
        window.location.href = "login.html";
      } else {
        alert("Erro ao buscar tarefas. Verifique sua conexão.");
      }
    } catch (error) {
      console.error("Erro ao buscar tarefas:", error);
      alert("Erro inesperado. Tente novamente mais tarde.");
    }
  }

  async function markAsDeleted(event) {
    const taskId = event.target.closest("button").dataset.id; // Obtém o ID da tarefa

    try {
      const response = await fetch(`http://localhost:8080/tasks/${taskId}`, {
        method: "DELETE",
        headers: {
          Authorization: "Bearer " + token,
          "Content-Type": "application/json",
        },
      });

      if (response.ok) {
        alert("Tarefa deletada como concluída!");
        fetchTasks(currentPage); // Atualiza a lista de tarefas
      } else {
        alert("Erro ao deletar tarefa como concluída.");
      }
    } catch (error) {
      console.error("Erro ao deletar tarefa:", error);
      alert("Erro inesperado. Tente novamente mais tarde.");
    }
  }

  // Função para marcar tarefa como concluída
  async function markAsCompleted(event) {
    const taskId = event.target.closest("button").dataset.id; // Obtém o ID da tarefa

    const updatedTask = {
      status: "COMPLETED",
    }; // Apenas atualiza o status

    try {
      const response = await fetch(`http://localhost:8080/tasks/${taskId}`, {
        method: "PATCH", // Ou "PATCH", dependendo da sua API
        headers: {
          Authorization: "Bearer " + token,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(updatedTask), // O corpo deve ser passado aqui
      });

      if (response.ok) {
        alert("Tarefa marcada como concluída!");
        fetchTasks(currentPage); // Atualiza a lista de tarefas
      } else {
        alert("Erro ao marcar tarefa como concluída.");
      }
    } catch (error) {
      console.error("Erro ao marcar tarefa como concluída:", error);
      alert("Erro inesperado. Tente novamente mais tarde.");
    }
  }

  // Abrir o modal para edição
  function openEditModal(event) {
    const taskId = event.target.closest("button").dataset.id;
    currentTaskId = taskId;

    const taskCard = event.target.closest(".card-body");

    document.getElementById("editTaskTitle").value =
      taskCard.querySelector(".card-title").textContent;

    document.getElementById("editTaskDescription").value =
      taskCard.querySelector(".card-text").textContent;

    document.getElementById("editTaskDueDate").value =
      taskCard.querySelector(".due-date").textContent;

    document.getElementById("editTaskStatus").value =
      taskCard.querySelector(".badge").textContent;

    const modal = document.getElementById("editTaskModal");
    modal.classList.add("show", "fade");
    document.body.classList.add("modal-open");
  }

  // Fechar o modal
  document.getElementById("closeModal").addEventListener("click", () => {
    const modal = document.getElementById("editTaskModal");
    modal.classList.remove("show", "fade");
    document.body.classList.remove("modal-open");
  });

  document
    .getElementById("editTaskForm")
    .addEventListener("submit", async (event) => {
      event.preventDefault();

      const updatedTask = {
        title: document.getElementById("editTaskTitle").value,
        description: document.getElementById("editTaskDescription").value,
        dueDate: document.getElementById("editTaskDueDate").value,
        status: document.getElementById("editTaskStatus").value,
      };

      try {
        const response = await fetch(
          `http://localhost:8080/tasks/${currentTaskId}`,
          {
            method: "PUT",
            headers: {
              Authorization: "Bearer " + token,
              "Content-Type": "application/json",
            },
            body: JSON.stringify(updatedTask),
          }
        );

        if (response.ok) {
          alert("Tarefa atualizada com sucesso!");
          fetchTasks(currentPage);

          const modal = document.getElementById("editTaskModal");
          modal.classList.remove("show", "fade");
          document.body.classList.remove("modal-open");
        } else {
          alert("Erro ao atualizar tarefa.");
        }
      } catch (error) {
        console.error("Erro ao atualizar tarefa:", error);
        alert("Erro inesperado. Tente novamente mais tarde.");
      }
    });

  // Função para adicionar nova tarefa
  async function addTask(task) {
    try {
      const response = await fetch("http://localhost:8080/tasks", {
        method: "POST",
        headers: {
          Authorization: "Bearer " + token,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(task),
      });

      if (response.ok) {
        alert("Tarefa adicionada com sucesso!");
        fetchTasks(currentPage); // Atualiza a lista de tarefas

        // Contrai o componente collapse
        const collapseElement = document.getElementById("collapseForm");
        const collapse = new bootstrap.Collapse(collapseElement, {
          toggle: true, // Isso contrai o componente
        });
      } else {
        alert("Erro ao adicionar tarefa. Verifique os dados.");
      }
    } catch (error) {
      console.error("Erro ao adicionar tarefa:", error);
      alert("Erro inesperado. Tente novamente mais tarde.");
    }
  }

  // Evento para o envio do formulário
  const taskForm = document.getElementById("taskForm");
  taskForm.addEventListener("submit", function (event) {
    event.preventDefault(); // Impede o envio padrão do formulário

    const task = {
      title: document.getElementById("taskTitle").value,
      description: document.getElementById("taskDescription").value,
      dueDate: document.getElementById("taskDueDate").value,
      status: document.getElementById("taskStatus").value,
    };

    addTask(task); // Chama a função para adicionar a tarefa

    // Limpa o formulário após o envio
    taskForm.reset();
  });

  // Eventos para os botões de paginação
  document.getElementById("prevPage").addEventListener("click", function () {
    if (currentPage > 0) {
      currentPage--;
      fetchTasks(currentPage);
    }
  });

  document.getElementById("nextPage").addEventListener("click", function () {
    if (currentPage < totalPages - 1) {
      currentPage++;
      fetchTasks(currentPage);
    }
  });

  // Chama a função para buscar tarefas ao carregar a página
  fetchTasks(currentPage);
});
