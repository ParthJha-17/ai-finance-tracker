const statusBadge = document.getElementById("status");

const userForm = document.getElementById("user-form");
const transactionForm = document.getElementById("transaction-form");
const goalForm = document.getElementById("goal-form");
const insightForm = document.getElementById("insight-form");

const userResult = document.getElementById("user-result");
const transactionResult = document.getElementById("transaction-result");
const goalResult = document.getElementById("goal-result");
const insightResult = document.getElementById("insight-result");
const insightModal = document.getElementById("insight-modal");
const closeModalButton = document.getElementById("close-modal");
const modalBody = document.getElementById("modal-body");

const request = async (path, options = {}) => {
  const response = await fetch(path, {
    headers: {
      "Content-Type": "application/json",
    },
    ...options,
  });

  if (!response.ok) {
    throw new Error(`${response.status} ${response.statusText}`);
  }

  return response.json();
};

const setStatus = (message, isError = false) => {
  statusBadge.textContent = message;
  statusBadge.style.background = isError
    ? "rgba(239, 68, 68, 0.3)"
    : "rgba(255, 255, 255, 0.2)";
};

const formatInsight = (text) => {
  try {
    const parsed = JSON.parse(text);
    if (parsed?.choices?.[0]?.message?.content) {
      return parsed.choices[0].message.content;
    }
    if (parsed?.choices?.[0]?.text) {
      return parsed.choices[0].text;
    }
    if (parsed?.content) {
      return parsed.content;
    }
    return JSON.stringify(parsed, null, 2);
  } catch (error) {
    return text;
  }
};

const renderInsight = (content) => {
  modalBody.innerHTML = "";
  const lines = content
    .split(/\n+/)
    .map((line) => line.trim())
    .filter(Boolean);
  let list = null;

  const appendParagraph = (text) => {
    const paragraph = document.createElement("p");
    paragraph.textContent = text;
    modalBody.appendChild(paragraph);
  };

  lines.forEach((line) => {
    const bulletMatch = line.match(/^[-*•]\s+(.*)/);
    if (bulletMatch) {
      if (!list) {
        list = document.createElement("ul");
        modalBody.appendChild(list);
      }
      const item = document.createElement("li");
      item.textContent = bulletMatch[1];
      list.appendChild(item);
      return;
    }

    list = null;
    appendParagraph(line);
  });
};

const openModal = (content) => {
  renderInsight(content);
  insightModal.classList.add("open");
  insightModal.setAttribute("aria-hidden", "false");
};

const closeModal = () => {
  insightModal.classList.remove("open");
  insightModal.setAttribute("aria-hidden", "true");
};

closeModalButton.addEventListener("click", closeModal);
insightModal.addEventListener("click", (event) => {
  if (event.target === insightModal) {
    closeModal();
  }
});

userForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(userForm));
  try {
    const result = await request("/api/users", {
      method: "POST",
      body: JSON.stringify(payload),
    });
    userResult.textContent = `User created: ${result.name} (ID: ${result.id})`;
    setStatus("User saved");
  } catch (error) {
    userResult.textContent = error.message;
    setStatus("User save failed", true);
  }
});

transactionForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const data = Object.fromEntries(new FormData(transactionForm));
  const userId = data.userId;
  const payload = {
    category: data.category,
    description: data.description,
    amount: Number(data.amount),
    type: data.type,
    date: data.date,
  };

  try {
    const result = await request(`/api/transactions/${userId}`, {
      method: "POST",
      body: JSON.stringify(payload),
    });
    transactionResult.textContent = `Transaction added (ID: ${result.id})`;
    setStatus("Transaction saved");
  } catch (error) {
    transactionResult.textContent = error.message;
    setStatus("Transaction failed", true);
  }
});

goalForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const data = Object.fromEntries(new FormData(goalForm));
  const userId = data.userId;
  const payload = {
    title: data.title,
    description: data.description,
    targetAmount: Number(data.targetAmount),
    targetDate: data.targetDate,
  };

  try {
    const result = await request(`/api/goals/${userId}`, {
      method: "POST",
      body: JSON.stringify(payload),
    });
    goalResult.textContent = `Goal added (ID: ${result.id})`;
    setStatus("Goal saved");
  } catch (error) {
    goalResult.textContent = error.message;
    setStatus("Goal failed", true);
  }
});

insightForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const data = Object.fromEntries(new FormData(insightForm));
  const userId = data.userId;
  insightResult.textContent = "Generating insights...";

  try {
    const response = await fetch(`/api/insights/${userId}`);
    if (!response.ok) {
      throw new Error(`${response.status} ${response.statusText}`);
    }
    const text = await response.text();
    const formatted = formatInsight(text);
    insightResult.textContent = "Insights ready. Check the pop-up window.";
    openModal(formatted);
    setStatus("Insights ready");
  } catch (error) {
    insightResult.textContent = error.message;
    setStatus("Insights failed", true);
  }
});

const verifyApi = async () => {
  try {
    await fetch("/api/users");
    setStatus("Connected to API");
  } catch (error) {
    setStatus("API unavailable", true);
  }
};

verifyApi();
