import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import SolvedTest from './SolvedTests';
import WaitingTests from './WaitingTests';
import refresh from '../../assets/refresh.png';
import Swal from 'sweetalert2';
import { Toast } from 'utils/toasts';
import axios from 'api/axios';

const Dashboard = () => {
  const [waitingTests, setWaitingTests] = useState(true);
  const [studentInfo, setStudentInfo] = useState(
    JSON.parse(localStorage.getItem('user'))
  );

  const navigate = useNavigate();

  const logout = () => {
    navigate('/wyloguj');
  };

  const shouldUserResetPassword = () => {
    const user = JSON.parse(localStorage.getItem('user'));
    return user['reset_password'];
  };

  const turnOffResetPassword = () => {
    const user = JSON.parse(localStorage.getItem('user'));
    user['reset_password'] = false;
    localStorage.setItem('user', JSON.stringify(user));
  };
  
//tu
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
    <div className="w-auto h-screen bg-gradient-to-r from-cyan-500 to-blue-500 pt-10">
      <div className="w-[100%] xl:w-[1280px] border-2 mx-auto p-7 bg-white">
        <div className="flex justify-between w-auto mb-5">
          <div className="">
            <h3 className="text-[20px]">Witaj:</h3>
            <div className="flex flex-col md:flex-row">
              <h1 className="text-[32px] font-semibold text-gray-700  mr-3">
                {studentInfo.firstName + ' ' + studentInfo.lastName}
              </h1>
              <div className="flex">
                <h1 className="text-[32px] font-semilight">
                  nr {studentInfo.id}
                </h1>
              </div>
            </div>
          </div>
          <div className="flex justify-center">
            <button
              onClick={logout}
              className="bg-gray-700 hover:bg-gray-600 self-center text-white text-[18px] font-bold py-4 px-7 rounded focus:outline-none text-2xl focus:shadow-outline align-middle"
            >
              Wyloguj się
            </button>
          </div>
        </div>

        <div className="mb-7 flex flex-col md:flex-row">
          <button
            onClick={() => {
              setWaitingTests(true);
            }}
            className="bg-gray-700 hover:bg-gray-600 cursor-pointer mb-5 md:mb-0 w-full md:w-1/2 md:mr-2 p-4 text-center tracking-tight"
          >
            <span className="text-white text-[25px] font-bold">Oczekujące</span>
            <span className="text-white text-[25px] font-light"> testy</span>
          </button>
          <button
            onClick={() => {
              setWaitingTests(false);
            }}
            className="bg-cyan-500 hover:bg-cyan-400 cursor-pointer w-full md:w-1/2 md:ml-2 p-4 text-center tracking-tight"
          >
            <span className="text-white text-[25px] font-bold">Rozwiązane</span>
            <span className="text-white text-[25px] font-light"> testy</span>
          </button>
        </div>
        <div>{waitingTests ? <WaitingTests /> : <SolvedTest />}</div>
      </div>
    </div>
  );
};

export default Dashboard;
