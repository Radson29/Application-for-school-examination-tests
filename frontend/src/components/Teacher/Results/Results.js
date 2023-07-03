import { useEffect, useState } from 'react';
import { Button, Collapse } from 'react-daisyui';
import { useQuery } from 'react-query';
import { formatToEuropeanDateTime } from 'utils/date_utils';
import { FaChevronUp, FaChevronDown } from 'react-icons/fa';

import ShowAnswersModal from './ShowAnswersModal';

import axios from 'api/axios';
import { PropagateLoader } from 'react-spinners';

import {
  scoreToPercentage,
  scoreToTailwindClass,
  scoreToText
} from 'utils/tests_utils';
import PageWrapper from '../PageWrapper';

const Results = () => {
  const [isOpen, setIsOpen] = useState({});
  const [result, setResult] = useState({});
  const [showQuestions, setShowQuestions] = useState(false);
  const [selectedResult, setSelectedResult] = useState(null);

  function groupAndSortResults(results) {
    const groupedResults = {};

    results.sort((a, b) => {
      return new Date(b.submittedAt) - new Date(a.submittedAt);
    });

    results.forEach((result) => {
      const quizTitle = result.quizTitle;
      const studentName = `${result.person.firstName} ${result.person.lastName}`;

      if (!groupedResults[quizTitle]) {
        groupedResults[quizTitle] = {};
      }

      if (!groupedResults[quizTitle][studentName]) {
        groupedResults[quizTitle][studentName] = [];
      }

      groupedResults[quizTitle][studentName].push(result);
    });

    return groupedResults;
  }

  const getResultsQuery = useQuery('results', () => {
    return axios.get('/teacher/test/results').then((res) => {
      setResult(groupAndSortResults(res.data));
    });
  });

  useEffect(() => {
    if (!result) return;

    setIsOpen(
      Object.keys(result).reduce((acc, curr) => {
        acc[curr] = false;
        return acc;
      }, {})
    );
  }, [result]);

  const toggleCollapse = (quizTitle) => {
    setIsOpen((prev) => ({
      ...prev,
      [quizTitle]: !prev[quizTitle]
    }));
  };

  const showAnswersModal = (resultId) => {
    setSelectedResult(resultId);
    setShowQuestions(true);
  };

  const closeAnswersModal = () => {
    setSelectedResult(null);
    setShowQuestions(false);
  };

  return (
    <PageWrapper>
      {showQuestions && (
        <ShowAnswersModal
          resultId={selectedResult}
          onClose={() => closeAnswersModal()}
        />
      )}

      <header className="flex justify-between items-center pb-16">
        <div>
          <h1 className="text-4xl font-semibold">Wyniki</h1>
          <p className="text-gray-500">
            Przeglądaj wyniki rozwiązanych testów.
          </p>
        </div>
      </header>

      {getResultsQuery.isFetching ? (
        <div className="flex justify-center items-center h-64">
          <PropagateLoader color="#0ea5e9" className="block" />
        </div>
      ) : getResultsQuery.isError ? (
        <p className="text-center">{getResultsQuery.error.message}</p>
      ) : Object.keys(result).length ? (
        Object.keys(result).map((quizTitle) => (
          <Collapse key={quizTitle} open={isOpen[quizTitle]}>
            <Collapse.Title className="flex items-center justify-between border-b text-sky-100 border-gray-200 bg-sky-600 mb-5 w-full text-left text-xl font-regular">
              <div>
                Nazwa testu:{' '}
                <span className="font-medium text-white uppercase">
                  {quizTitle}
                </span>
              </div>
              <div>
                <Button
                  className="bg-sky-600 hover:bg-sky-700 border-0 text-white border-sky-900"
                  onClick={() => toggleCollapse(quizTitle)}
                >
                  {isOpen[quizTitle] ? (
                    <FaChevronUp className="text-xl" />
                  ) : (
                    <FaChevronDown className="text-xl" />
                  )}
                </Button>
              </div>
            </Collapse.Title>
            <Collapse.Content>
              <div className="flex flex-col mb-8" key={quizTitle}>
                {Object.keys(result[quizTitle]).map((studentName) => (
                  <div className="flex flex-col" key={studentName}>
                    <div className="-my-2 overflow-x-auto sm:-mx-6 lg:-mx-8">
                      <div className="py-2 align-middle inline-block sm:px-6 lg:px-8 w-full">
                        <div className="shadow overflow-hidden border-b border-gray-200">
                          <table className="min-w-full divide-y divide-gray-200 table-fixed">
                            <thead className="bg-gray-50">
                              <tr>
                                <th
                                  scope="col"
                                  className="px-6 w-10/12 py-3 text-left text-xl font-medium text-gray-600 tracking-wider"
                                >
                                  {studentName}{' '}
                                  <span className="text-gray-400">
                                    ({result[quizTitle][studentName].length})
                                  </span>
                                </th>
                              </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                              {result[quizTitle][studentName].map((result) => {
                                const score = scoreToPercentage(
                                  result.score,
                                  result.questionsCount
                                );
                                const scoreText = scoreToText(score);
                                const scoreClassName =
                                  scoreToTailwindClass(score);

                                return (
                                  <tr key={result.id}>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                      <div className="flex items-center">
                                        <div className="ml-4">
                                          <div className="flex gap-4 items-center">
                                            <p className="text-gray-500 text-base">
                                              Ocena:{' '}
                                              <span
                                                className={`ml-1 text-xs font-medium mr-2 px-2.5 py-0.5 rounded-full ${scoreClassName}`}
                                              >
                                                {scoreText}
                                              </span>
                                            </p>
                                            <p className="text-gray-500 text-base">
                                              •
                                            </p>
                                            <p className="text-gray-500 text-base">
                                              Wynik: {result.score}/
                                              {result.questionsCount}
                                            </p>
                                            <p className="text-gray-500 text-base">
                                              •
                                            </p>

                                            <p className="text-gray-500 text-base">
                                              Przesłano:{' '}
                                              {formatToEuropeanDateTime(
                                                new Date(result.submittedAt)
                                              )}
                                            </p>
                                            <p className="text-gray-500 text-base">
                                              •
                                            </p>
                                            <p
                                              onClick={() =>
                                                showAnswersModal(result.id)
                                              }
                                              className="text-sky-500 text-underline text-base cursor-pointer"
                                            >
                                              Zobacz odpowiedzi
                                            </p>
                                          </div>
                                        </div>
                                      </div>
                                    </td>
                                  </tr>
                                );
                              })}
                            </tbody>
                          </table>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </Collapse.Content>
          </Collapse>
        ))
      ) : (
        <p className="text-center">Brak wyników.</p>
      )}
    </PageWrapper>
  );
};

export default Results;
