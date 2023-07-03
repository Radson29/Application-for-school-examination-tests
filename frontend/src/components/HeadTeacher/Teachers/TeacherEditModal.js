import Modal from 'components/UI/Modal';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import Swal from 'sweetalert2';
import { useNavigate } from 'react-router-dom';

import { useQuery, useMutation } from 'react-query';
import axios from 'api/axios';

const TeacherEditModal = (props) => {
  const { teacherId } = props;
  const [teacherFullName, setTeacherFullName] = useState('');

  const {
    register,
    formState: { errors, isDirty },
    setValue,
    handleSubmit
  } = useForm({
    defaultValues: {
      firstName: '',
      lastName: '',
      login: '',
      pesel: ''
    }
  });

  const getTeacherDetailsQuery = useQuery(
    'teacher_details',
    () => {
      return axios
        .get(`/admin/teacher?id=${teacherId}`)
        .then((res) => res.data);
    },
    {
      onSuccess: (data) => {
        setValue('firstName', data.person.firstName);
        setValue('lastName', data.person.lastName);
        setValue('login', data.login);
        setValue('pesel', data.person.pesel);

        setTeacherFullName(`${data.person.firstName} ${data.person.lastName}`);
      }
    }
  );

  const updateTeacherQuery = useMutation({
    mutationFn: (values) =>
      axios({
        method: 'put',
        url: `/admin/teacher/update?id=${teacherId}`,
        data: values
      }).then((res) => res.data),
    onError: (error) => {
      // TODO: obsługa błędów
      console.log(error);
    },
    onSuccess: () => {
      Swal.fire({
        title: 'Sukces',
        text: 'Poprawnie edytowano nauczyciela',
        icon: 'success'
      });

      props.onClose();
    }
  });

  const onSubmit = (data) => {
    updateTeacherQuery.mutate(data);
  };

  const userNameInputClass = errors.imie
    ? 'block text-gray-600 text-2xl font-semibold border-b border-red-500 text-2xl shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline'
    : 'block text-gray-600 text-2xl font-semibold border-b border-black';

  const userLastNameInputClass = errors.nazwisko
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
          <h3 className="text-4xl font-semibold">Edytuj infromacje</h3>
          {getTeacherDetailsQuery.isFetching ? (
            <p className="text-2xl text-gray-400">
              Pobieranie informacji o nauczycielu ...
            </p>
          ) : updateTeacherQuery.isLoading ? (
            <p className="text-2xl text-gray-400">
              Aktualizowanie informacji o nauczycielu ...
            </p>
          ) : getTeacherDetailsQuery.isError ? (
            <p className="text-2xl text-red-400">
              {getTeacherDetailsQuery.error.message}
            </p>
          ) : updateTeacherQuery.isError ? (
            <p className="text-2xl text-red-400">
              {updateTeacherQuery.error.message}
            </p>
          ) : (
            <p className="text-2xl text-sky-400">{teacherFullName}</p>
          )}
        </div>
        <div>
          <div className=" ">
            <form
              className="flex flex-col pt-8 pb-8"
              onSubmit={handleSubmit(onSubmit)}
            >
              <div className="flex mb-4">
                <div className="m-2">
                  <label className="text-lg text-gray-500 p-1" htmlFor="imie">
                    Imię
                  </label>
                  <input
                    id="firstName"
                    type="text"
                    placeholder="Tomasz"
                    className={userNameInputClass}
                    disabled={getTeacherDetailsQuery.isFetching}
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
                    htmlFor="nazwisko"
                  >
                    Nazwisko
                  </label>
                  <input
                    id="lastName"
                    type="text"
                    placeholder="Działo"
                    className={userLastNameInputClass}
                    disabled={getTeacherDetailsQuery.isFetching}
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
                    placeholder="xXTomekXx"
                    className={userLoginInputClass}
                    disabled={getTeacherDetailsQuery.isFetching}
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
                    disabled={getTeacherDetailsQuery.isFetching}
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
                  updateTeacherQuery.isLoading || !isDirty
                    ? 'bg-gray-500'
                    : 'bg-green-500 hover:bg-green-700'
                }`}
                type="submit"
                disabled={updateTeacherQuery.isLoading || !isDirty}
              >
                Aktualizuj
              </button>
            </form>
          </div>
        </div>
      </div>
    </Modal>
  );
};

export default TeacherEditModal;
