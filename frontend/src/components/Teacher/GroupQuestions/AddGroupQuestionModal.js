import React from 'react';
import { useForm } from 'react-hook-form';

import Swal from 'sweetalert2';
import { useQuery, useMutation } from 'react-query';
import axios from 'api/axios';
import Modal from 'components/UI/Modal';

const AddGroupQuestionModal = (props) => {
  const {
    register,
    formState: { errors },
    handleSubmit
  } = useForm({
    defaultValues: {
      name: ''
    }
  });

  const addQuestionGroupQuery = useMutation({
    mutationFn: (values) =>
      axios({
        method: 'post',
        url: '/teacher/questions/groups/add',
        data: values
      }).then((res) => res.data),
    onError: (error) => {
      // TODO: obsługa błędów
      console.log(error);
    },
    onSuccess: (data) => {
      Swal.fire({
        title: 'Sukces',
        text: `Poprawnie dodano grupę pytań`,
        icon: 'success'
      });
      props.onClose();
    }
  });

  const handleFormSubmit = (data) => {
    addQuestionGroupQuery.mutate(data);
  };

  return (
    <Modal onClose={props.onClose}>
      <div className="flex flex-col justify-center p-4">
        <div className="">
          <h3 className="text-4xl font-semibold">Dodaj nową grupę pytań</h3>
          {addQuestionGroupQuery.isLoading ? (
            <p className="text-2xl text-gray-400">Dodawanie grupy pytań ...</p>
          ) : null}
          {addQuestionGroupQuery.isError ? (
            <p className="text-2xl text-red-400">
              {addQuestionGroupQuery.error &&
                addQuestionGroupQuery.error.response.data.error}
            </p>
          ) : null}
        </div>
        <form
          className="flex flex-col pt-8 pb-8"
          onSubmit={handleSubmit(handleFormSubmit)}
        >
          <div className="mb-4">
            <label htmlFor="name" className="block font-bold mb-2">
              Nazwa grupy
            </label>
            <input
              id="name"
              type="text"
              disabled={addQuestionGroupQuery.isLoading}
              className={`block border border-gray-300 rounded w-full py-2 px-3 focus:outline-none focus:border-blue-500`}
              {...register('name', {
                required: true,
                maxLength: 64,
                minLength: 3
              })}
            />
            {errors.name && (
              <p className="text-red-500 mt-2">Niepoprawna nazwa grupy</p>
            )}
          </div>
          <div className="flex justify-end">
            <button
              className={`w-full bg-blue-500 self-center mt-5 text-white font-bold py-4 px-4 rounded focus:outline-none text-2xl focus:shadow-outline ${
                addQuestionGroupQuery.isLoading
                  ? 'bg-gray-500'
                  : 'hover:bg-blue-700'
              }`}
              type="submit"
              disabled={addQuestionGroupQuery.isLoading}
            >
              Dodaj
            </button>
          </div>
        </form>
      </div>
    </Modal>
  );
};

export default AddGroupQuestionModal;
