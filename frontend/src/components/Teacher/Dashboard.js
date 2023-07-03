import { Outlet } from 'react-router-dom';

import Sidebar from 'components/Teacher/Sidebar';
import { useEffect } from 'react';

import Swal from 'sweetalert2';
import { Toast } from 'utils/toasts';
import axios from 'api/axios';

function Dashboard() {
  const shouldUserResetPassword = () => {
    const user = JSON.parse(localStorage.getItem('user'));
    return user['reset_password'];
  };

  const turnOffResetPassword = () => {
    const user = JSON.parse(localStorage.getItem('user'));
    user['reset_password'] = false;
    localStorage.setItem('user', JSON.stringify(user));
  };

  useEffect(() => {
    if (shouldUserResetPassword()) {
      Swal.fire({
        icon: 'info',
        title: 'Wymagana zmiana hasła',
        input: 'password',
        inputLabel: 'Podaj nowe hasło:',
        inputPlaceholder: 'Nowe hasło',
        inputAttributes: {
          maxlength: 32,
          autocapitalize: 'off',
          autocorrect: 'off'
        },
        showCancelButton: false,
        confirmButtonText: 'Potwierdź',
        confirmButtonColor: '#3085d6',
        allowOutsideClick: false,
        showLoaderOnConfirm: true,
        preConfirm: (login) => {
          return axios({
            method: 'post',
            url: 'user/changepassword',
            data: {
              password: login,
              confirmPassword: login
            }
          })
            .then((response) => response.data)
            .catch((error) => {
              Swal.showValidationMessage(`Request failed: ${error}`);
            });
        },
        inputValidator: (value) => {
          return new Promise((resolve) => {
            if (value.length >= 3 && value.length <= 32) {
              resolve();
            } else {
              resolve('Hasło powinno mieć między 3 a 32 znaki.');
            }
          });
        }
      }).then((result) => {
        if (result.isConfirmed) {
          Toast.fire({
            icon: 'success',
            text: 'Hasło zostało zmienione.'
          });

          turnOffResetPassword();
        }
      });
    }
  }, []);

  return (
    <div className="h-screen w-screen flex">
      <Sidebar />
      <main className="bg-gray-100 flex-grow flex justify-center overflow-y-scroll">
        <Outlet />
      </main>
    </div>
  );
}

export default Dashboard;
