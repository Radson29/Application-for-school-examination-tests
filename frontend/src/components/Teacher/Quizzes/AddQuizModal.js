import { useState } from 'react';
import Modal from 'components/UI/Modal';
import { useForm, Controller } from 'react-hook-form';
import Swal from 'sweetalert2';
import { useNavigate } from 'react-router-dom';

import { useMutation, useQuery } from 'react-query';
import axios from 'api/axios';

const AddQuizModal = (props) => {
  const [questions, setQuestions] = useState([]);
  const [selectedQuestions, setSelectedQuestions] = useState([]);

  const {
    register,
    formState: { errors, isDirty, isValid },
    handleSubmit,
    control
  } = useForm({
    defaultValues: {
      title: '',
      description: '',
      quizTime: 20,
      recipes: []
    }
  });

  const getAllQestionsQuery = useQuery(
    'quiz_questions_groups',
    () => {
      return axios.get('/teacher/questions').then((res) => res.data);
    },
    {
      onSuccess: (data) => {
        setQuestions(data);
      }
    }
  );

  const addQuizQuery = useMutation({
    mutationFn: (values) =>
      axios({
        method: 'post',
        url: '/teacher/quizzes/add',
        data: values
      }).then((res) => res.data),
    onError: (error) => {
      // TODO: obsługa błędów
      console.log(error);
    },
    onSuccess: (data) => {
      Swal.fire({
        title: 'Sukces',
        text: `Poprawnie stworzono nowy test.`,
        icon: 'success'
      });
      props.onClose();
    }
  });

  const onSubmit = (data) => {
    console.log(data);

    const isAtLeastOneQuestionSelected = data.recipes.some(
      (recipe) => recipe.count > 0
    );

    if (!isAtLeastOneQuestionSelected) {
      Swal.fire({
        title: 'Błąd',
        text: `Musisz wybrać przynajmniej jedno pytanie.`,
        icon: 'error'
      });
      return;
    }

    addQuizQuery.mutate(data);
  };

  return (
    <Modal onClose={props.onClose}>
      <div className="flex flex-col justify-center p-4">
        <div className="">
          <h3 className="text-4xl font-semibold">Dodawanie nowego testu</h3>
          {addQuizQuery.isLoading ? (
            <p className="text-2xl text-gray-400">
              Dodawanie grupy do systemu...
            </p>
          ) : null}
          {addQuizQuery.isError ? (
            <p className="text-2xl text-red-400">
              {addQuizQuery.error && addQuizQuery.error.response.data.error}
            </p>
          ) : null}
        </div>
        <div>
          <div className=" ">
            <form
              className="flex flex-col pt-8 pb-8"
              onSubmit={handleSubmit(onSubmit)}
            >
              <div className="mb-4 w-full">
                <label
                  className="text-lg text-gray-500 p-1 block"
                  htmlFor="title"
                >
                  Nazwa
                </label>
                <input
                  id="title"
                  type="text"
                  placeholder=""
                  className="w-full block border border-gray-300 rounded py-2 px-3 focus:outline-none focus:border-blue-500"
                  disabled={addQuizQuery.isLoading}
                  name="title"
                  {...register('title', {
                    required: true,
                    maxLength: 64,
                    minLength: 2
                  })}
                />
                {errors.title && (
                  <p className="text-red-500 text-xs italic">
                    Niepoprawny tytuł quizu
                  </p>
                )}
              </div>

              <div className="mb-4 w-full">
                <label
                  className="text-lg text-gray-500 p-1 block"
                  htmlFor="description"
                >
                  Opis
                </label>
                <input
                  id="description"
                  type="text"
                  placeholder=""
                  className="w-full block border border-gray-300 rounded py-2 px-3 focus:outline-none focus:border-blue-500"
                  disabled={addQuizQuery.isLoading}
                  name="description"
                  {...register('description', {
                    required: true,
                    maxLength: 128,
                    minLength: 2
                  })}
                />
                {errors.description && (
                  <p className="text-red-500 text-xs italic">
                    Niepoprawny opis quizu
                  </p>
                )}
              </div>

              <div className="mb-4 w-full">
                <label
                  className="text-lg text-gray-500 p-1 block"
                  htmlFor="quizTime"
                >
                  Czas trwania
                </label>
                <input
                  id="quizTime"
                  type="number"
                  placeholder=""
                  min="1"
                  max="180"
                  className="w-full block border border-gray-300 rounded py-2 px-3 focus:outline-none focus:border-blue-500"
                  disabled={addQuizQuery.isLoading}
                  name="name"
                  {...register('quizTime', {
                    required: true,
                    maxLength: 180,
                    minLength: 1
                  })}
                />
                {errors.quizTime && (
                  <p className="text-red-500 text-xs italic">
                    Niepoprawny czas trwania quizu
                  </p>
                )}
              </div>

              <div className="max-h-80 overflow-y-scroll">
                {getAllQestionsQuery.data &&
                  getAllQestionsQuery.data
                    .filter((group) => group.questions.length)
                    .map((group, index) => {
                      return (
                        <div key={group.id}>
                          <label
                            className="text-lg text-gray-500 p-1 block"
                            htmlFor={`recipe_${index}`}
                          >
                            {group.name} ({group.questions.length})
                          </label>
                          <input
                            type="hidden"
                            value={group.id}
                            {...register(`recipes[${index}].groupId`, {})}
                          />
                          <Controller
                            name={`recipes[${index}].count`}
                            control={control}
                            defaultValue={0}
                            rules={{
                              required: true,
                              min: 0,
                              max: group.questions.length
                            }}
                            render={({ field: { onChange, value } }) => (
                              <div className="flex items-center gap-5">
                                <input
                                  className="grow"
                                  type="range"
                                  min={0}
                                  disabled={addQuizQuery.isLoading}
                                  max={group.questions.length}
                                  onChange={(e) => onChange(e.target.value)}
                                  value={value}
                                />
                                <input
                                  id={`recipe_${index}`}
                                  className="block border border-gray-300 rounded py-2 px-3 focus:outline-none focus:border-blue-500"
                                  type="number"
                                  min={0}
                                  disabled={addQuizQuery.isLoading}
                                  max={group.questions.length}
                                  onChange={(e) =>
                                    onChange(Number(e.target.value))
                                  }
                                  value={value}
                                />
                              </div>
                            )}
                          />
                          {errors.groups?.[index] && (
                            <p className="text-red-500">
                              Niepoprawna ilość pytań
                            </p>
                          )}
                        </div>
                      );
                    })}
              </div>

              <button
                className={`w-full bg-blue-500 self-center mt-5 text-white font-bold py-4 px-4 rounded focus:outline-none text-2xl focus:shadow-outline  ${
                  addQuizQuery.isLoading ? 'bg-gray-500' : 'hover:bg-blue-700'
                }`}
                type="submit"
                disabled={addQuizQuery.isLoading}
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

export default AddQuizModal;
