const STORAGE_KEY = "fitai_user_session";

export const getStoredUser = () => {
  try {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) return JSON.parse(saved);
  } catch (e) {
    console.error("Failed to load user session", e);
  }
  return {
    email: "piyush@fitness.com",
    name: "Piyush Tekriwal",
    roles: ["USER", "ADMIN"]
  };
};

export const setStoredUser = (user) => {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
  } catch (e) {
    console.error("Failed to save user session", e);
  }
};

export const currentUser = getStoredUser();

export const hasRole = (role) => {
  const user = getStoredUser();
  return user.roles && user.roles.includes(role);
};
