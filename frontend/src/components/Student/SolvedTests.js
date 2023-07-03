import { PropagateLoader } from 'react-spinners';
import SolvedTestsItem from './SolvedTestsItem';
import { percentageToGrade, scoreToPercentage } from 'utils/tests_utils';
import { useState } from 'react';
import { useQuery } from 'react-query';
import refresh from 'assets/refresh.png';
import axiosInstance from 'api/axios';

const SolvedTests = (props) => {
  const [solvedTests, setSolvedTests] = useState(null);

  const getSolvedTests = useQuery('solvedTests', () => {
    axiosInstance.get(`/student/test/results`).then((res) => {
      setSolvedTests(res.data);
    });
  });

  return (
    <div>
      <div
        className="mr-8 fixed left-[-100px] top-[-100px] w-[200px] h-[200px] bg-white rounded-full hover:scale-[1.1] ease-in-out duration-300 cursor-pointer select-none"
        onClick={() => {
          setSolvedTests(null);
          getSolvedTests.refetch();
        }}
      >
        <img
          alt=""
          src={refresh}
          className="w-[40px] h-auto block absolute bottom-10 right-10"
        />
      </div>

      <ul>
        {solvedTests == null ? (
          <div className="flex justify-center items-center h-64">
            <PropagateLoader color="#0ea5e9" className="block" />
          </div>
        ) : getSolvedTests.isError ? (
          <p className="text-center">{getSolvedTests.error.message}</p>
        ) : solvedTests.length === 0 || !solvedTests ? (
          <div className="flex justify-center items-center h-64">
            <h1 className="text-[21px]">Brak aktualnie rozwiązanych testów</h1>
          </div>
        ) : (
          solvedTests.map((test) => (
            <SolvedTestsItem
              key={test.id}
              name={test.quizTitle}
              teacher={test.quizCreator}
              value1={test.score}
              value2={test.questionsCount}
              grade={percentageToGrade(
                scoreToPercentage(test.score, test.questionsCount)
              )}
            />
          ))
        )}
      </ul>
    </div>
  );
};

export default SolvedTests;
