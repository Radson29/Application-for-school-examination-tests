import React from 'react';
import { useState } from 'react';
import Modal from 'components/UI/Modal';
import { useForm } from 'react-hook-form';
import Swal from 'sweetalert2';

import { useQuery, useMutation } from 'react-query';
import axios from 'api/axios';

const EditQuestionModal = (props) => {
  const [answers, setAnswers] = useState([]);

  const {
    values,
    register,
    formState: { errors },
    handleSubmit,
    setValue,
    reset,
    unregister
  } = useForm();

  const getQuestionDetailsQuery = useQuery(
    'question_details',
    () => {
      return axios
        .get(`teacher/question/details?id=${props.questionId}`)
        .then((res) => res.data);
    },
    {
      onSuccess: (data) => {
        setValue('value', data.value);
        setAnswers(
          data.answers.map((answer) => ({
            value: answer.value,
            correct: answer.correct
          }))
        );
      }
    }
  );

  const updateQuestionQuery = useMutation({
    mutationFn: (data) =>
      axios({
        method: 'PUT',
        url: `teacher/questions/update?id=${props.questionId}`,
        data: data
      }).then((res) => res.data),
    onError: (error) => {
      // TODO: obsługa błędów
      console.log(error);
    },
    onSuccess: (data) => {
      Swal.fire({
        title: 'Sukces',
        text: `Poprawnie edytowano pytanie.`,
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

    // czitos bo mnie zaraz coś strzeli
    const payload = {
      value: data.value,
      answers: answers
    };

    updateQuestionQuery.mutate(payload);
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

    newAnswers.forEach((answer, index) => {
      unregister(`answers[${index}]`);
      setValue(`answers[${index}].value`, answer.value);
      setValue(`answers[${index}].correct`, answer.correct);
    });
  };

  const handleAddAnswer = () => {
    setAnswers([...answers, { value: '', correct: false }]);
  };

  return (
    <Modal onClose={props.onClose}>
      <div className="flex flex-col justify-center p-4">
        <h3 className="text-4xl font-semibold">Edytuj pytanie w grupie</h3>
      </div>
      <div>
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
            {getQuestionDetailsQuery.data &&
              answers.map((answer, index) => (
                <div key={index}>
                  <p className="mr-2 mb-2">{`Odpowiedź nr ${index + 1}`}</p>
                  <div className="flex items-center mb-2">
                    <div className="grow">
                      <input
                        id={`answer_${index}`}
                        type="text"
                        className="block border border-gray-300 rounded w-full py-2 px-3 focus:outline-none focus:border-blue-500"
                        value={answer.value || ''}
                        {...register(`answers[${index}].value`, {
                          required: true,
                          minLength: 4
                        })}
                        onChange={(event) => handleAnswerChange(index, event)}
                      />
                      {errors.answers && errors.answers[index] && (
                        <p className="text-red-500 mt-2">
                          Wypełnij to pole poprawnie
                        </p>
                      )}
                    </div>
                    <div>
                      <input
                        id={`correctAnswer_${index}`}
                        type="checkbox"
                        className="ml-2"
                        checked={answer.correct}
                        {...register(`answers[${index}].correct`)}
                        onChange={() => handleToggleCorrectAnswer(index)}
                      />
                      <label
                        htmlFor={`correctAnswer_${index}`}
                        className="ml-1"
                      >
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
              className={`w-full bg-blue-500 self-center mt-5 text-white font-bold py-4 px-4 rounded focus:outline-none text-2xl focus:shadow-outline`}
              type="submit"
            >
              Edytuj Pytanie
            </button>
          </div>
        </form>
      </div>
    </Modal>
  );
};

export default EditQuestionModal;
