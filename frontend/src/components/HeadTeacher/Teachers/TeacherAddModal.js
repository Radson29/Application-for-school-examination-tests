import Modal from 'components/UI/Modal';
import { useForm } from 'react-hook-form';
import Swal from 'sweetalert2';
import { useNavigate } from 'react-router-dom';

import { useMutation } from 'react-query';
import axios from 'api/axios';

const TeacherAddModal = (props) => {
  const {
    register,
    formState: { errors, isDirty, isValid },
    handleSubmit
  } = useForm({
    defaultValues: {
      firstName: '',
      lastName: '',
      login: '',
      pesel: '12345678901'
    }
  });

  const addTeacherQuery = useMutation({
    mutationFn: (values) =>
      axios({
        method: 'post',
        url: '/admin/teacher/add',
        data: values
      }).then((res) => res.data),
    onError: (error) => {
      // TODO: obsługa błędów
      console.log(error);
    },
    onSuccess: (data) => {
      Swal.fire({
        title: 'Sukces',
        text: `Hasło do konta to: ${data.password}`,
        icon: 'success'
      });

      props.onClose();
    }
  });

  const onSubmit = (data) => {
    addTeacherQuery.mutate(data);
  };

  const userNameInputClass = errors.firstName
    ? 'block text-gray-600 text-2xl font-semibold border-b border-red-500 text-2xl shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline'
    : 'block text-gray-600 text-2xl font-semibold border-b border-black';

  const userLastNameInputClass = errors.lastName
    ? 'block text-gray-600 text-2xl font-semibold border-b border-red-500 text-2xl shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline'
    : 'block text-gray-600 text-2xl font-semibold border-b border-black';

  const userLoginInputClass = errors.login
    ? 'block text-gray-600 text-2xl font-semibold border-b border-red-500 text-2xl shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline'
    : 'block text-gray-600 text-2xl font-semibold border-b border-black';

  const userPeselnputClass = errors.pesel
    ? 'block text-gray-600 text-2xl font-semibold border-b border-red-500 text-2xl shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline'
    : 'block text-gray-600 text-2xl font-semibold border-b border-black';

  return (
    <Modal onClose={props.onClose}>
      <div className="flex flex-col justify-center p-4">
        <div className="">
          <h3 className="text-4xl font-semibold">Dodaj nowego nauczyciela</h3>
          {addTeacherQuery.isLoading ? (
            <p className="text-2xl text-gray-400">
              Dodawanie nauczyciela do systemu...
            </p>
          ) : null}
          {addTeacherQuery.isError ? (
            <p className="text-2xl text-red-400">
              {addTeacherQuery.error.response.data.error}
            </p>
          ) : null}
        </div>
        <div>
          <div className=" ">
            <form
              className="flex flex-col  pt-8 pb-8"
              onSubmit={handleSubmit(onSubmit)}
            >
              <div className="flex mb-4">
                <div className="m-2">
                  <label
                    className="text-lg text-gray-500 p-1"
                    htmlFor="firstName"
                  >
                    Imię
                  </label>
                  <input
                    id="firstName"
                    type="text"
                    placeholder="Jan"
                    className={userNameInputClass}
                    disabled={addTeacherQuery.isLoading}
                    name="firstName"
                    {...register('firstName', {
                      required: true,
                      maxLength: 64,
                      minLength: 3
                    })}
                  />
                  {errors.firstName && (
                    <p className="text-red-500 text-xs italic">
                      Niepoprawne Imię
                    </p>
                  )}
                </div>
                <div className="m-2">
                  <label
                    className="text-lg text-gray-500 p-1"
                    htmlFor="lastName"
                  >
                    Nazwisko
                  </label>
                  <input
                    id="lastName"
                    type="text"
                    placeholder="Kowalski"
                    className={userLastNameInputClass}
                    disabled={addTeacherQuery.isLoading}
                    name="lastName"
                    {...register('lastName', {
                      required: true,
                      maxLength: 64,
                      minLength: 3
                    })}
                  />
                  {errors.lastName && (
                    <p className="text-red-500 text-xs italic">
                      Niepoprawne Nazwisko
                    </p>
                  )}
                </div>
              </div>
              <div className="flex mb-4">
                <div className="m-2">
                  <label className="text-lg text-gray-500 p-1" htmlFor="login">
                    Login
                  </label>
                  <input
                    id="login"
                    type="text"
                    placeholder="jan.kowalski"
                    className={userLoginInputClass}
                    disabled={addTeacherQuery.isLoading}
                    name="login"
                    {...register('login', {
                      required: true,
                      maxLength: 64,
                      minLength: 3
                    })}
                  />
                  {errors.login && (
                    <p className="text-red-500 text-xs italic">
                      Niepoprawny Login
                    </p>
                  )}
                </div>

                <div className="m-2">
                  <label className="text-lg text-gray-500 p-1" htmlFor="pesel">
                    PESEL
                  </label>
                  <input
                    id="pesel"
                    type="text"
                    placeholder="99123119631"
                    className={userPeselnputClass}
                    disabled={addTeacherQuery.isLoading}
                    name="pesel"
                    {...register('pesel', {
                      required: true,
                      pattern: '/^d{11}$/',
                      maxLength: 11,
                      minLength: 11
                    })}
                  />
                  {errors.pesel && (
                    <p className="text-red-500 text-xs italic">
                      Niepoprawny PESEL
                    </p>
                  )}
                </div>
              </div>

              <button
                className={`w-full self-center mt-5 text-white font-bold py-4 px-4 rounded focus:outline-none text-2xl focus:shadow-outline ${
                  addTeacherQuery.isLoading || !isDirty
                    ? 'bg-gray-500'
                    : 'bg-green-500 hover:bg-green-700'
                }`}
                type="submit"
                disabled={addTeacherQuery.isLoading || !isDirty}
              >
                Dodaj
              </button>
            </form>
          </div>
        </div>
      </div>
    </Modal>
  );
};

export default TeacherAddModal;
