import { useEffect, useState } from 'react';
import WaitingTestsItem from './WaitingTestsItem';
import { useQuery, useMutation } from 'react-query';
import axios from 'api/axios';
import { PropagateLoader } from 'react-spinners';
import refresh from '../../assets/refresh.png';

const WaitingTests = () => {
  const [availableTests, setAvailableTests] = useState(null);

  const getAvailableTests = useQuery('availableTests', () => {
    axios.get(`/student/tests`).then((res) => {
      setAvailableTests(res.data);
    });
  });

  return (
    <div>
      <div
        className="mr-8 fixed left-[-100px] top-[-100px] w-[200px] h-[200px] bg-white rounded-full hover:scale-[1.1] ease-in-out duration-300 cursor-pointer select-none"
        onClick={() => {
          setAvailableTests(null);
          getAvailableTests.refetch();
        }}
      >
        <img
          src={refresh}
          className="w-[40px] h-auto block absolute bottom-10 right-10"
        />
      </div>
      <ul>
        {availableTests == null ? (
          <div className="flex justify-center items-center h-64">
            <PropagateLoader color="#0ea5e9" className="block" />
          </div>
        ) : getAvailableTests.isError ? (
          <p className="text-center">{getAvailableTests.error.message}</p>
        ) : availableTests.length == 0 || !availableTests ? (
          <div className="flex justify-center items-center h-64">
            <h1 className="text-[21px]">Brak aktualnie oczekujących testów</h1>
          </div>
        ) : (
          availableTests.map((test) => (
            <WaitingTestsItem
              key={test.id}
              id={test.id}
              name={test.quiz.title}
              subject={test.quiz.description}
              time={test.quiz.quizTime}
            />
          ))
        )}
      </ul>
    </div>
  );
};

export default WaitingTests;
