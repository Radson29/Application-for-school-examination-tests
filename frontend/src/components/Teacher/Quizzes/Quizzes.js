import { useState } from 'react';
import { FaPlus, FaRegTrashAlt } from 'react-icons/fa';
import { Button } from 'react-daisyui';
import Swal from 'sweetalert2';
import { useQuery, useMutation } from 'react-query';

import axios from 'api/axios';
import { PropagateLoader } from 'react-spinners';

import PageWrapper from '../PageWrapper';

import AddQuizModal from './AddQuizModal';
// import EditQuizModal from './EditQuizModal';

const Quizzess = () => {
  const [showAddQuiz, setShowAddQuiz] = useState(false);
  // const [showEditQuiz, setShowEditQuiz] = useState(false);

  const showAddQuizModal = () => {
    setShowAddQuiz(true);
  };

  // const showEditQuizModal = () => {
  //   setShowEditQuiz(true);
  // };

  const closeAddQuizModal = () => {
    setShowAddQuiz(false);
    refetchQuizzes();
  };

  // const closeEditQuizModal = () => {
  //   setShowEditQuiz(false);
  //   refetchQuizzes();
  // };

  const getQuizzesQuery = useQuery('quizzes', () => {
    return axios.get('/teacher/quizzes').then((res) => res.data);
  });

  const deleteQuizQuery = useMutation({
    mutationFn: (testId) =>
      axios
        .delete(`/teacher/quizzes/delete?id=${testId}`)
        .then((res) => res.data),
    onSuccess: () => {
      Swal.fire({
        title: 'Sukces',
        text: 'Poprawnie usunięto test',
        icon: 'success'
      });
      refetchQuizzes();
    }
  });

  const refetchQuizzes = () => {
    getQuizzesQuery.refetch();
  };

  const deleteTest = (testId) => {
    Swal.fire({
      title: 'Jesteś pewien?',
      text: 'Nie będziesz mógł cofnąć tej operacji!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Tak, usuń!',
      cancelButtonText: 'Anuluj'
    }).then((result) => {
      if (result.isConfirmed) {
        deleteQuizQuery.mutate(testId);
      }
    });
  };

  return (
    <PageWrapper>
      {showAddQuiz && <AddQuizModal onClose={() => closeAddQuizModal()} />}
      {/* {showEditQuiz && <EditQuizModal onClose={() => closeEditQuizModal()} />} */}

      <header className="flex justify-between items-center pb-16">
        <div>
          <h1 className="text-4xl font-semibold">Testy</h1>
          <p className="text-gray-500">Zarządzanie testami w systemie</p>
        </div>
        <div>
          <Button
            onClick={() => showAddQuizModal()}
            className="bg-cyan-400 hover:bg-cyan-500 border-0 text-white border-cyan-900"
          >
            <FaPlus />
          </Button>
        </div>
      </header>

      {getQuizzesQuery.isFetching || deleteQuizQuery.isLoading ? (
        <div className="flex justify-center items-center h-64">
          <PropagateLoader color="#0ea5e9" className="block" />
        </div>
      ) : getQuizzesQuery.isError ? (
        <p className="text-center">{getQuizzesQuery.error.message}</p>
      ) : deleteQuizQuery.isError ? (
        <p className="text-center">{deleteQuizQuery.error.message}</p>
      ) : (
        <div className="flex flex-col">
          <div className="-my-2 overflow-x-auto sm:-mx-6 lg:-mx-8">
            <div className="py-2 align-middle inline-block sm:px-6 lg:px-8 w-full">
              <div className="shadow overflow-hidden border-b border-gray-200 sm:rounded-lg">
                <table className="min-w-full divide-y divide-gray-200 table-fixed">
                  <thead className="bg-gray-50">
                    <tr>
                      <th
                        scope="col"
                        className="px-6 w-10/12 py-3 text-left text-xl font-medium text-gray-500 uppercase tracking-wider"
                      >
                        Nazwa testu
                      </th>
                      <th
                        scope="col"
                        className="px-6 py-3 text-left text-xl font-medium text-gray-500 uppercase tracking-wider"
                      >
                        Akcje
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {getQuizzesQuery.data &&
                      getQuizzesQuery.data.map((quiz) => (
                        <tr key={quiz.id}>
                          <td className="px-6 py-4 whitespace-nowrap text-2xl text-gray-900">
                            <p className="capitalize">{quiz.title}</p>
                            <div className="flex gap-4 items-center">
                              <p className="text-gray-500 text-base">
                                Czas: {quiz.quizTime} min
                              </p>
                              <p className="text-gray-500 text-base">•</p>
                              <p className="text-gray-500 text-base">
                                Pytań:{' '}
                                {quiz.questionRecipes.reduce(
                                  (a, b) => a + b.questionsToGenerate,
                                  0
                                )}
                              </p>
                              <p className="text-gray-500 text-base">•</p>
                              <p className="text-gray-500 text-base">
                                Opis: {quiz.description}
                              </p>
                            </div>
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-2xl text-gray-900">
                            <div className="flex gap-x-3">
                              {/* <Button
                                onClick={() => showEditQuizModal()}
                                color="primary"
                              >
                                Edytuj
                              </Button> */}
                              <Button
                                color="error"
                                onClick={() => deleteTest(quiz.id)}
                              >
                                <FaRegTrashAlt />
                              </Button>
                            </div>
                          </td>
                        </tr>
                      ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      )}
    </PageWrapper>
  );
};

export default Quizzess;
