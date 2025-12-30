import api from "./api";

const analyzeText = (text, title) => api.post("/contracts/analyze/text", { text, title });

const analyzePdf = (file) => {
    const formData = new FormData();
    formData.append("file", file);
    return api.post("/contracts/analyze/pdf", formData, {
        headers: { "Content-Type": "multipart/form-data" },
    });
};

const getHistory = () => api.get("/contracts/history");
const getDashboardStats = () => api.get("/user/dashboard");

const ContractService = { analyzeText, analyzePdf, getHistory, getDashboardStats };
export default ContractService;