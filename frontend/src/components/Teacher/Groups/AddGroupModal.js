import Modal from 'components/UI/Modal';
import { useForm } from 'react-hook-form';
import Swal from 'sweetalert2';
import { useNavigate } from 'react-router-dom';

import { useMutation } from 'react-query';
import axios from 'api/axios';

const AddGroup = (props) => {
  const {
    register,
    formState: { errors, isDirty, isValid },
    handleSubmit
  } = useForm({
    defaultValues: {
      name: ''
    }
  });

  const addGroupQuery = useMutation({
    mutationFn: (values) =>
      axios({
        method: 'post',
        url: '/group/add',
        data: values
      }).then((res) => res.data),
    onError: (error) => {
      // TODO: obsługa błędów
      console.log(error);
    },
    onSuccess: (data) => {
      Swal.fire({
        title: 'Sukces',
        text: `Poprawnie stworzono nową grupę`,
        icon: 'success'
      });
      props.onClose();
    }
  });

  const onSubmit = (data) => {
    addGroupQuery.mutate(data);
  };

  const userStudentInputClass = errors.student
    ? 'block text-gray-600 text-2xl font-semibold border-b border-red-500 text-2xl shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline'
    : 'block text-gray-600 text-2xl font-semibold border-b border-black w-full';

  return (
    <Modal onClose={props.onClose}>
      <div className="flex flex-col justify-center p-4">
        <div className="">
          <h3 className="text-4xl font-semibold">Dodawanie nowej grupy</h3>
          {addGroupQuery.isLoading ? (
            <p className="text-2xl text-gray-400">
              Dodawanie grupy do systemu...
            </p>
          ) : null}
          {addGroupQuery.isError ? (
            <p className="text-2xl text-red-400">
              {addGroupQuery.error.response.data.error}
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
                <div className="w-full">
                  <label className="text-lg text-gray-500 p-1" htmlFor="group">
                    Wpisz nazwę grupy
                  </label>
                  <input
                    id="group"
                    type="text"
                    placeholder=""
                    className={userStudentInputClass}
                    disabled={addGroupQuery.isLoading}
                    name="name"
                    {...register('name', {
                      required: true,
                      maxLength: 64,
                      minLength: 2
                    })}
                  />
                  {errors.name && (
                    <p className="text-red-500 text-xs italic">
                      Niepoprawna nazwa grupy
                    </p>
                  )}
                </div>
              </div>

              <button
                className={`w-full bg-blue-500 self-center mt-5 text-white font-bold py-4 px-4 rounded focus:outline-none text-2xl focus:shadow-outline ${
                  addGroupQuery.isLoading || !isDirty
                    ? 'bg-gray-500'
                    : 'hover:bg-blue-700'
                }`}
                type="submit"
                disabled={addGroupQuery.isLoading || !isDirty}
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

export default AddGroup;
