import { useState } from 'react';
import Modal from 'components/UI/Modal';
import { useForm, Controller, set } from 'react-hook-form';
import Swal from 'sweetalert2';
import { useNavigate } from 'react-router-dom';
import { PropagateLoader } from 'react-spinners';
import Select from 'react-select';

import DatePicker from 'react-multi-date-picker';
import TimePicker from 'react-multi-date-picker/plugins/time_picker';
import DatePanel from 'react-multi-date-picker/plugins/date_panel';

import { useMutation, useQuery } from 'react-query';
import axios from 'api/axios';

const AddQuizModal = (props) => {
  const [studentsList, setStudentsList] = useState([]);
  const [groupsList, setGroupsList] = useState([]);
  const [studentsGroups, setStudentsGroups] = useState([]);
  const [selectedStudents, setSelectedStudents] = useState([]);
  const [quizzesList, setQuizzesList] = useState([]);

  const {
    register,
    formState: { errors, isDirty, isValid },
    handleSubmit,
    control
  } = useForm({
    defaultValues: {}
  });

  const getAllGroupsQuery = useQuery(
    'schedule_groups',
    () => {
      return axios.get('/teacher/groups/students').then((res) => res.data);
    },
    {
      onSuccess: (data) => {
        createOptionsFromResponse(data);
        mapGroupsToStudents(data);
      }
    }
  );

  const getAllQuizzesQuery = useQuery(
    'schedule_quizzes',
    () => {
      return axios.get('/teacher/quizzes').then((res) => res.data);
    },
    {
      onSuccess: (data) => {
        const items = data.map((quiz) => {
          return {
            value: quiz.id,
            label: quiz.title
          };
        });

        setQuizzesList(items);
      }
    }
  );

  const scheduleQuizQuery = useMutation({
    mutationFn: (values) =>
      axios({
        method: 'POST',
        url: '/teacher/schedules/add',
        data: values
      }).then((res) => res.data),
    onError: (error) => {
      // TODO: obsługa błędów
      console.log(error);
    },
    onSuccess: (data) => {
      Swal.fire({
        title: 'Sukces',
        text: `Poprawnie zaplanowano test.`,
        icon: 'success'
      });
      props.onClose();
    }
  });

  const createOptionsFromResponse = (data) => {
    const studentsList = [];
    const groupsList = [];

    data.forEach((group) => {
      groupsList.push({
        value: group.group.id,
        label: group.group.name
      });

      group.people.forEach((student) => {
        const existingStudent = studentsList.find(
          (s) => s.value === student.id
        );

        if (!existingStudent) {
          studentsList.push({
            value: student.id,
            label: `${student.firstName} ${student.lastName}`
          });
        }
      });
    });

    setStudentsList(studentsList);
    setGroupsList(groupsList);
  };

  const mapGroupsToStudents = (data) => {
    const arr = {};

    data.forEach((group) => {
      const groupId = Number(group.group.id);

      group.people.forEach((person) => {
        const personId = Number(person.id);

        if (!(groupId in arr)) {
          arr[groupId] = [];
        }

        if (!arr[groupId].includes(personId)) {
          arr[groupId].push(personId);
        }
      });
    });

    setStudentsGroups(arr);
  };

  const selectStudentsFromGroups = (data) => {
    const newSelectedStudents = [];

    data.forEach((group) => {
      const groupId = group.value;
      if (groupId in studentsGroups) {
        studentsGroups[groupId].forEach((student) => {
          if (!newSelectedStudents.includes(student)) {
            newSelectedStudents.push(student);
          }
        });
      }
    });

    const newItems = studentsList.filter((student) =>
      newSelectedStudents.includes(student.value)
    );

    setSelectedStudents(newItems);
  };

  const onSubmit = (data) => {
    const payload = {
      quizId: data.selectedQuiz.value,
      startsAt: new Date(data.startDate).toISOString(),
      endsAt: new Date(data.endDate).toISOString(),
      students: selectedStudents.map((student) => student.value)
    };

    if (selectedStudents.length === 0) {
      Swal.fire({
        title: 'Błąd',
        text: 'Nie wybrano żadnych uczniów.',
        icon: 'error'
      });
      return;
    }

    if (new Date(payload.startsAt) > new Date(payload.endsAt)) {
      Swal.fire({
        title: 'Błąd',
        text: 'Data zakończenia nie może być wcześniejsza niż data rozpoczęcia.',
        icon: 'error'
      });
      return;
    }

    if (new Date(payload.startsAt) < new Date()) {
      Swal.fire({
        title: 'Błąd',
        text: 'Data rozpoczęcia nie może być wcześniejsza niż obecna data.',
        icon: 'error'
      });
      return;
    }

    scheduleQuizQuery.mutate(payload);
  };

  return (
    <Modal onClose={props.onClose}>
      <div className="flex flex-col justify-center p-4">
        <div>
          <h3 className="text-4xl font-semibold">Planowanie nowego testu</h3>
        </div>
        <div>
          {getAllGroupsQuery.isFetching || getAllQuizzesQuery.isFetching ? (
            <div className="flex justify-center items-center h-64">
              <PropagateLoader color="#0ea5e9" className="block" />
            </div>
          ) : (
            <form
              className="flex flex-col pt-8"
              onSubmit={handleSubmit(onSubmit)}
            >
              <div className="pb-3">
                <label className="block">Wybierz datę rozpoczecia:</label>
                <Controller
                  control={control}
                  name="startDate"
                  rules={{
                    required: true
                  }}
                  render={({ field }) => (
                    <DatePicker
                      {...field}
                      className="block w-full"
                      format="MM/DD/YYYY HH:mm:ss"
                      plugins={[<TimePicker />]}
                      containerClassName="w-full"
                      disabled={scheduleQuizQuery.isLoading}
                      style={{
                        textAlign: 'center',
                        width: '100%',
                        display: 'block',
                        boxSizing: 'border-box',
                        height: '36px'
                      }}
                    />
                  )}
                />
                {errors.startDate && (
                  <p className="text-red-500 text-xs italic">
                    Niepoprawna data
                  </p>
                )}
              </div>
              <div className="pb-3">
                <label className="block">Wybierz datę zakończenia:</label>
                <Controller
                  control={control}
                  name="endDate"
                  rules={{
                    required: true
                  }}
                  render={({ field }) => (
                    <DatePicker
                      {...field}
                      className="block w-full"
                      format="MM/DD/YYYY HH:mm:ss"
                      plugins={[<TimePicker />]}
                      containerClassName="w-full"
                      disabled={scheduleQuizQuery.isLoading}
                      style={{
                        textAlign: 'center',
                        width: '100%',
                        display: 'block',
                        boxSizing: 'border-box',
                        height: '36px'
                      }}
                    />
                  )}
                />
                {errors.endDate && (
                  <p className="text-red-500 text-xs italic">
                    Niepoprawna data
                  </p>
                )}
              </div>
              <div className="pb-3">
                <label>Wybrany test:</label>
                <Controller
                  control={control}
                  name="selectedQuiz"
                  rules={{
                    required: true
                  }}
                  render={({ field }) => (
                    <Select
                      {...field}
                      disabled={scheduleQuizQuery.isLoading}
                      options={quizzesList}
                    />
                  )}
                />
                {errors.selectedQuiz && (
                  <p className="text-red-500 text-xs italic">
                    Niepoprawny test
                  </p>
                )}
              </div>
              <div className="pb-3">
                <label>Wybrane grupy:</label>
                <Select
                  options={groupsList}
                  isMulti
                  disabled={scheduleQuizQuery.isLoading}
                  onChange={selectStudentsFromGroups}
                />
              </div>
              <div className="pb-3">
                <label>Wybrani uczniowie:</label>
                <Select
                  value={selectedStudents}
                  options={studentsList}
                  isMulti
                  disabled={scheduleQuizQuery.isLoading}
                  onChange={setSelectedStudents}
                />
              </div>
              <button
                className={`w-full bg-blue-500 self-center mt-5 text-white font-bold py-4 px-4 rounded focus:outline-none text-2xl focus:shadow-outline  ${
                  scheduleQuizQuery.isLoading
                    ? 'bg-gray-500'
                    : 'hover:bg-blue-700'
                }`}
                type="submit"
                disabled={scheduleQuizQuery.isLoading}
              >
                Zaplanuj test
              </button>
            </form>
          )}
        </div>
      </div>
    </Modal>
  );
};

export default AddQuizModal;
