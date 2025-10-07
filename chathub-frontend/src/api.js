import axios from "axios";

const API_URL = "http://localhost:8080";


const axiosInstance = axios.create({
    baseURL: API_URL,
    withCredentials: true
});

export const registerUser = (userData) => {
    return axiosInstance.post("/registerUser", userData);
};

export const loginUser = (userData) => {
    return axiosInstance.post("/loginUser", userData);
};
