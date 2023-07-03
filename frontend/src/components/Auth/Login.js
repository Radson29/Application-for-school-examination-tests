import { useForm } from 'react-hook-form';
import Swal from 'sweetalert2';
import { useNavigate } from 'react-router-dom';

import { useAuth } from 'context/auth-context';
import { roleToPath } from 'utils/auth_utils';

function Login() {
  const {
    register,
    formState: { errors },
    handleSubmit,
    reset
  } = useForm({
    defaultValues: {
      userName: '',
      password: ''
    }
  });

  const navigate = useNavigate();
  const { login } = useAuth();

  const onSubmit = async (data) => {
    login(data.userName, data.password)
      .then((userData) => {
        reset();
        navigate(roleToPath(userData.role));
      })
      .catch((error) => {
        if (error.response.status === 400) {
          Swal.fire({
            title: 'Błąd!',
            text: 'Podaj poprawne dane logowania.',
            icon: 'error'
          });
        }
      });
  };

  const userNameInputClass =
    errors.userName?.type === 'required'
      ? 'border-red-500 text-2xl shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline'
      : 'shadow appearance-none text-2xl border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline';

  const passwodInputClass =
    errors.password?.type === 'required'
      ? 'border-red-500 text-2xl shadow appearance-none border  rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline'
      : 'shadow text-2xl appearance-none border  rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline';

  return (
    <div className="w-screen h-screen flex justify-center items-center bg-gradient-to-r from-cyan-500 to-blue-500 ">
      <div className=" w-1/3 ">
        <form
          className="flex flex-col  bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 "
          onSubmit={handleSubmit(onSubmit)}
        >
          <div className="mb-4">
            <label
              className="block text-gray-700 text-2xl font-bold mb-2"
              htmlFor="username"
            >
              Nazwa użytkownika
            </label>
            <input
              className={userNameInputClass}
              id="username"
              type="text"
              placeholder="Nazwa użytkownika"
              name="userName"
              {...register('userName', {
                required: true,
                maxLength: 64,
                minLength: 3
              })}
            />
          </div>
          {errors.userName?.type === 'required' && (
            <p className="text-red-500 text-xs italic">
              Prosze wprowadzić nazwe użytkownika!
            </p>
          )}

          <div className="mb-6">
            <label
              className="block text-gray-700 text-2xl font-bold mb-2"
              htmlFor="password"
            >
              Hasło
            </label>
            <input
              className={passwodInputClass}
              id="password"
              type="password"
              placeholder="******************"
              name="password"
              {...register('password', {
                required: true,
                maxLength: 64,
                minLength: 3
              })}
            />
            {errors.password?.type === 'required' && (
              <p className="text-red-500 text-xs italic">
                Prosze wprowadzić hasło!
              </p>
            )}
          </div>
          <button
            className="bg-blue-500 self-center w-1/3 hover:bg-blue-700 text-white font-bold py-4 px-4 rounded focus:outline-none text-2xl focus:shadow-outline "
            type="submit"
          >
            Zaloguj
          </button>
          <p className="text-center text-gray-500 text-xs p-4">
            &copy;2023 Projekt programowanie zespołowe. All rights reserved.
          </p>
        </form>
      </div>
    </div>
  );
}

export default Login;
