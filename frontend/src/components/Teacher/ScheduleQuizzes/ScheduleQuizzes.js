import { useState } from 'react';
import { FaPlus, FaRegTrashAlt } from 'react-icons/fa';
import { Button } from 'react-daisyui';
import { useQuery, useMutation } from 'react-query';
import Swal from 'sweetalert2';
import { formatToEuropeanDateTime } from 'utils/date_utils';

import axios from 'api/axios';
import { PropagateLoader } from 'react-spinners';

import PageWrapper from '../PageWrapper';

import AddScheduleQuizModal from './AddScheduleQuizModal';

const ScheduleQuizzes = () => {
  const [showScheduleQuiz, setShowScheduleQuiz] = useState(false);

  const showAddScheduleModal = () => {
    setShowScheduleQuiz(true);
  };

  const closeAddScheduleModal = () => {
    setShowScheduleQuiz(false);
    refetchSchedules();
  };

  const getSchedulesQuery = useQuery(
    'schedules',
    () => {
      return axios.get('/teacher/schedules').then((res) => res.data);
    },
    {
      onSuccess: (data) => {
        console.log(data);
      }
    }
  );

  const deleteScheduleQuery = useMutation({
    mutationFn: (scheduleId) =>
      axios
        .delete(`/teacher/schedules/delete?id=${scheduleId}`)
        .then((res) => res.data),
    onSuccess: () => {
      Swal.fire({
        title: 'Sukces',
        text: 'Poprawnie usunięto harmonogram',
        icon: 'success'
      });
      refetchSchedules();
    }
  });

  const refetchSchedules = () => {
    getSchedulesQuery.refetch();
  };

  const deleteSchedule = (scheduleId) => {
    Swal.fire({
      title: 'Jesteś pewien?',
      text: 'Nie będziesz mógł cofnąć tej operacji!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Tak, usuń!',
      cancelButtonText: 'Anuluj'
    }).then((result) => {
      if (result.isConfirmed) {
        deleteScheduleQuery.mutate(scheduleId);
      }
    });
  };

  return (
    <PageWrapper>
      {showScheduleQuiz && (
        <AddScheduleQuizModal onClose={() => closeAddScheduleModal()} />
      )}

      <header className="flex justify-between items-center pb-16">
        <div>
          <h1 className="text-4xl font-semibold">Harmonogram</h1>
          <p className="text-gray-500">Planowanie testów dla uczniów</p>
        </div>
        <div>
          <Button
            onClick={() => showAddScheduleModal()}
            className="bg-cyan-400 hover:bg-cyan-500 border-0 text-white border-cyan-900"
          >
            <FaPlus />
          </Button>
        </div>
      </header>

      {getSchedulesQuery.isFetching || deleteScheduleQuery.isLoading ? (
        <div className="flex justify-center items-center">
          <PropagateLoader color="#3B82F6" />
        </div>
      ) : getSchedulesQuery.isError ? (
        <p className="text-center">{getSchedulesQuery.error.message}</p>
      ) : deleteScheduleQuery.isLoading ? (
        <p className="text-center">{deleteScheduleQuery.error.message}</p>
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
                        Zaplanowany test
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
                    {getSchedulesQuery.data &&
                      getSchedulesQuery.data.map((schedule) => (
                        <tr key={schedule.id}>
                          <td className="px-6 py-4 whitespace-nowrap text-2xl text-gray-900">
                            <p className="capitalize">{schedule.quiz.title}</p>
                            <div className="flex gap-4 pb-2 items-center">
                              <p className="text-gray-500 text-base">
                                Czas trwania:{' '}
                                <b>{schedule.quiz.quizTime} min</b>
                              </p>
                              <p className="text-gray-500 text-base">•</p>
                              <p className="text-gray-500 text-base">
                                Ilość osób: <b>{schedule.available.length}</b>
                              </p>
                            </div>
                            <div className="flex gap-4 items-center">
                              <p className="text-gray-500 text-base">
                                Start:{' '}
                                {formatToEuropeanDateTime(
                                  new Date(schedule.startsAt)
                                )}
                              </p>
                              <p className="text-gray-500 text-base">•</p>
                              <p className="text-gray-500 text-base">
                                Koniec:{' '}
                                {formatToEuropeanDateTime(
                                  new Date(schedule.endsAt)
                                )}
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
                                onClick={() => deleteSchedule(schedule.id)}
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

export default ScheduleQuizzes;
