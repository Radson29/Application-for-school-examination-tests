import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useQuery, useMutation } from 'react-query';
import axios from 'api/axios';

import Swal from 'sweetalert2';
import Modal from 'components/UI/Modal';

const AddQuestionToGroupModal = (props) => {
  const [answers, setAnswers] = useState([
    { text: '', correct: false },
    { text: '', correct: false },
    { text: '', correct: false }
  ]);

  const {
    register,
    formState: { errors },
    handleSubmit,
    reset
  } = useForm();

  const addQuestionToGroupQuery = useMutation({
    mutationFn: (values) => {
      return axios({
        method: 'POST',
        url: `teacher/questions/add?id=${props.groupId}`,
        data: values
      }).then((res) => res.data);
    },
    onError: (error) => {
      // TODO: obsługa błędów
      console.log(error);
    },
    onSuccess: (data) => {
      Swal.fire({
        title: 'Sukces',
        text: `Poprawnie dodano pytanie do grupy`,
        icon: 'success'
      });
      props.onClose();
    }
  });

  const handleFormSubmit = (data) => {
    const isAtLeastOneAnswerCorrect = answers.some((answer) => answer.correct);

    if (!isAtLeastOneAnswerCorrect) {
      Swal.fire({
        title: 'Błąd',
        text: `Musisz zaznaczyć przynajmniej jedną poprawną odpowiedź.`,
        icon: 'error'
      });
      return;
    }

    addQuestionToGroupQuery.mutate(data);
  };

  const handleAnswerChange = (index, event) => {
    const newAnswers = [...answers];
    newAnswers[index].value = event.target.value;
    setAnswers(newAnswers);
  };

  const handleToggleCorrectAnswer = (index) => {
    const newAnswers = [...answers];
    newAnswers[index].correct = !newAnswers[index].correct;
    setAnswers(newAnswers);
  };

  const handleRemoveAnswer = (index) => {
    const newAnswers = [...answers];
    newAnswers.splice(index, 1);
    setAnswers(newAnswers);
  };

  const handleAddAnswer = () => {
    setAnswers([...answers, { text: '', correct: false }]);
  };

  return (
    <Modal onClose={props.onClose}>
      <div className="flex flex-col justify-center p-4">
        <h3 className="text-4xl font-semibold mb-4 text-center">
          Dodaj nowe pytanie do grupy
        </h3>
        {addQuestionToGroupQuery.isLoading ? (
          <p className="text-2xl text-gray-400">
            Dodawanie pytania do grupy ...
          </p>
        ) : null}
        {addQuestionToGroupQuery.isError ? (
          <p className="text-2xl text-red-400">
            {addQuestionToGroupQuery.error &&
              addQuestionToGroupQuery.error.response.data.error}
          </p>
        ) : null}
        <form
          className="flex flex-col pt-8 pb-8"
          onSubmit={handleSubmit(handleFormSubmit)}
        >
          <div className="mb-4">
            <label htmlFor="value" className="block font-bold mb-2">
              Treść pytania
            </label>
            <input
              id="value"
              type="text"
              className="block border border-gray-300 rounded w-full py-2 px-3 focus:outline-none focus:border-blue-500"
              {...register('value', { required: true, minLength: 4 })}
            />
            {errors.value && (
              <p className="text-red-500 mt-2">To pole jest wymagane</p>
            )}
          </div>
          <div className="mb-4">
            <label className="block font-bold mb-2">Odpowiedzi</label>
            {answers.map((answer, index) => (
              <div key={index}>
                <p className="mr-2 mb-2">{`Odpowiedź nr ${index + 1}`}</p>
                <div className="flex items-center mb-2">
                  <input
                    id={`answer_${index}`}
                    type="text"
                    className="block border border-gray-300 rounded w-full py-2 px-3 focus:outline-none focus:border-blue-500"
                    value={answer.value || ''}
                    {...register(`answers[${index}].value`, {
                      required: true,
                      minLength: 3
                    })}
                    onChange={(event) => handleAnswerChange(index, event)}
                  />
                  <input
                    id={`correctAnswer_${index}`}
                    type="checkbox"
                    className="ml-2"
                    checked={answer.correct}
                    {...register(`answers[${index}].correct`)}
                    onChange={() => handleToggleCorrectAnswer(index)}
                  />
                  <label htmlFor={`correctAnswer_${index}`} className="ml-1">
                    Poprawna
                  </label>
                  {index > 2 && (
                    <button
                      type="button"
                      className="ml-2 text-red-500"
                      onClick={() => handleRemoveAnswer(index)}
                    >
                      &#10005;
                    </button>
                  )}
                </div>
              </div>
            ))}
            {answers.length < 6 && (
              <button
                type="button"
                className="text-blue-500 "
                onClick={handleAddAnswer}
              >
                Dodaj odpowiedź
              </button>
            )}
          </div>
          <div className="flex justify-end">
            <button
              className={`w-full bg-blue-500 self-center mt-5 text-white font-bold py-4 px-4 rounded focus:outline-none text-2xl focus:shadow-outline ${
                addQuestionToGroupQuery.isLoading
                  ? 'bg-gray-500'
                  : 'hover:bg-blue-700'
              }`}
              type="submit"
              disabled={addQuestionToGroupQuery.isLoading}
            >
              Dodaj Pytanie
            </button>
          </div>
        </form>
      </div>
    </Modal>
  );
};

export default AddQuestionToGroupModal;
