import Modal from 'components/UI/Modal';
import { FaCheck, FaExclamation } from 'react-icons/fa';
import { PropagateLoader } from 'react-spinners';
import { useQuery } from 'react-query';
import axios from 'api/axios';

const ShowAnswersModal = (props) => {
  const getHistoryAnswersQuery = useQuery('results_answers', () => {
    return axios
      .get(`/teacher/test/results/history?id=${props.resultId}`)
      .then((res) => res.data);
  });

  return (
    <Modal onClose={props.onClose}>
      <div className="p-2 overflow-y-scroll">
        <div className="mb-8">
          <h2 className="text-2xl font-semibold">Odpowiedzi z testu:</h2>
        </div>

        {getHistoryAnswersQuery.isFetching ? (
          <div className="flex justify-center items-center h-64">
            <PropagateLoader color="#0ea5e9" className="block" />
          </div>
        ) : getHistoryAnswersQuery.isError ? (
          <p className="text-center">{getHistoryAnswersQuery.error.message}</p>
        ) : (
          <div className="grid grid-cols-2 gap-4 max-h-[40rem]">
            {getHistoryAnswersQuery.data ? (
              getHistoryAnswersQuery.data.questions.map((question, index) => (
                <div key={index} className="mb-4 p-2">
                  <h3 className="text-lg font-semibold">
                    {index + 1}. {question.question}
                  </h3>
                  <div className="mt-2">
                    {question.answers.map((answer, index) => (
                      <div
                        key={index}
                        className={`flex items-center ${
                          answer.correct
                            ? 'text-green-600'
                            : answer.selected
                            ? 'text-red-500'
                            : 'text-gray-500'
                        }`}
                      >
                        <span className="mr-2">
                          {answer.selected ? (
                            answer.correct ? (
                              <FaCheck className="w-4 h-4" />
                            ) : (
                              <FaExclamation className="w-4 h-4" />
                            )
                          ) : (
                            <span className="block w-4 h-4" />
                          )}
                        </span>
                        <span className="pr-2">{index + 1}. </span>
                        <p>{answer.value}</p>
                      </div>
                    ))}
                  </div>
                </div>
              ))
            ) : (
              <p className="text-center">Brak odpowiedzi</p>
            )}
          </div>
        )}
      </div>
    </Modal>
  );
};

export default ShowAnswersModal;
