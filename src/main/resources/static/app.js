const statusBadge = document.getElementById("status");

const userForm = document.getElementById("user-form");
const transactionForm = document.getElementById("transaction-form");
const goalForm = document.getElementById("goal-form");
const insightForm = document.getElementById("insight-form");

const userResult = document.getElementById("user-result");
const transactionResult = document.getElementById("transaction-result");
const goalResult = document.getElementById("goal-result");
const insightResult = document.getElementById("insight-result");

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
    insightResult.textContent = text;
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
