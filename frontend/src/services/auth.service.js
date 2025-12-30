import api from "./api";

const sendOtp = (email) => api.post("/auth/otp", { email });

const register = (username, email, password, otp) =>
    api.post("/auth/signup", { username, email, password, otp });

const login = (email, password) =>
    api.post("/auth/login", { email, password })
        .then((response) => {
            if (response.data.token) localStorage.setItem("user", JSON.stringify(response.data));
            return response.data;
        });

const resetPassword = (email, otp, newPassword) =>
    api.post("/auth/reset-password", { email, otp, newPassword });

const logout = () => localStorage.removeItem("user");

const getCurrentUser = () => JSON.parse(localStorage.getItem("user"));

const AuthService = { sendOtp, register, login, resetPassword, logout, getCurrentUser };
export default AuthService;