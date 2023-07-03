import axios from 'axios';
import Swal from 'sweetalert2';
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080'
});

const AxiosInterceptor = ({ children }) => {
  const navigate = useNavigate();

  const logoutUser = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate(0);
  };

  const requestInterceptor = axiosInstance.interceptors.request.use(
    function (config) {
      const accessToken = localStorage.getItem('token');
      if (accessToken) {
        config.headers.Authorization = `Bearer ${accessToken}`;
      }

      return config;
    },
    function (error) {
      return Promise.reject(error);
    }
  );

  const responseInterceptor = axiosInstance.interceptors.response.use(
    function (response) {
      return response;
    },
    function (error) {
      // Obsługa gdy API jest wyłączone
      if (
        !error.response ||
        (error.response && error.response.status === 401)
      ) {
        logoutUser();
      }

      return Promise.reject(error);
    }
  );

  useEffect(() => {
    return () => {
      axiosInstance.interceptors.request.eject(requestInterceptor);
      axiosInstance.interceptors.response.eject(responseInterceptor);
    };
  }, [navigate]);

  return children;
};

export { AxiosInterceptor };
export default axiosInstance;
