import Modal from 'components/UI/Modal';
import { useForm, Controller } from 'react-hook-form';
import Swal from 'sweetalert2';
import Select from 'react-select';

import { useQuery, useMutation } from 'react-query';
import axios from 'api/axios';

const AddStudentToGroup = (props) => {
  const {
    register,
    control,
    formState: { errors, isValid },
    handleSubmit
  } = useForm({
    defaultValues: {
      student: ''
    }
  });

  const getStudentsQuery = useQuery('groups_students_list', () => {
    return axios.get('/teacher/students').then((res) => res.data);
  });

  const addStudentToGroupQuery = useMutation({
    mutationFn: (values) =>
      axios({
        method: 'PUT',
        url: `group/addPerson?groupId=${values.groupId}&personId=${values.personId}`
      }).then((res) => res.data),
    onError: (error) => {
      // TODO: obsługa błędów
      console.log(error);
    },
    onSuccess: (data) => {
      Swal.fire({
        title: 'Sukces',
        text: `Poprawnie dodano studenta do grupy`,
        icon: 'success'
      });
      props.onClose();
    }
  });

  const onSubmit = (data) => {
    addStudentToGroupQuery.mutate({
      groupId: props.groupId,
      personId: data.student.value
    });
  };

  const userStudentInputClass = errors.student
    ? 'block text-gray-600 text-2xl font-semibold border-b border-red-500 text-2xl shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline'
    : 'block text-gray-600 text-2xl font-semibold border-b border-black';

  return (
    <Modal onClose={props.onClose}>
      <div className="flex flex-col justify-center p-4">
        <div className="">
          <h3 className="text-4xl font-semibold">
            Dodawanie studenta do grupy
          </h3>
          {getStudentsQuery.isFetching ? (
            <p className="text-2xl text-gray-400">
              Pobieranie uczniów z systemu ...
            </p>
          ) : null}
          {addStudentToGroupQuery.isLoading ? (
            <p className="text-2xl text-gray-400">
              Dodawanie ucznia do grupy ...
            </p>
          ) : null}
          {getStudentsQuery.isError || addStudentToGroupQuery.isError ? (
            <p className="text-2xl text-red-400">
              {(getStudentsQuery.error &&
                getStudentsQuery.error.response.data.error) ||
                (addStudentToGroupQuery.error &&
                  addStudentToGroupQuery.error.response.data.error)}
            </p>
          ) : null}
        </div>
        <div>
          <div className=" ">
            <form
              className="flex flex-col  pt-8 pb-8"
              onSubmit={handleSubmit(onSubmit)}
            >
              <div className="pb-3">
                <label>Wybierz studenta:</label>
                <Controller
                  control={control}
                  name="student"
                  rules={{
                    required: true
                  }}
                  render={({ field }) => (
                    <Select
                      {...field}
                      disabled={
                        getStudentsQuery.isFetching ||
                        addStudentToGroupQuery.isLoading
                      }
                      options={
                        getStudentsQuery.data &&
                        getStudentsQuery.data.map((student) => ({
                          value: student.id,
                          label: `${student.firstName} ${student.lastName}`
                        }))
                      }
                    />
                  )}
                />
                {errors.student && (
                  <p className="text-red-500 text-xs italic">Podaj studenta</p>
                )}
              </div>
              <button
                className={`w-full bg-blue-500 self-center mt-5 text-white font-bold py-4 px-4 rounded focus:outline-none text-2xl focus:shadow-outline 'hover:bg-blue-700' ${
                  getStudentsQuery.isFetching ||
                  addStudentToGroupQuery.isLoading
                    ? 'bg-gray-500'
                    : 'hover:bg-blue-700'
                }`}
                type="submit"
                disabled={
                  getStudentsQuery.isFetching ||
                  addStudentToGroupQuery.isLoading
                }
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

export default AddStudentToGroup;
