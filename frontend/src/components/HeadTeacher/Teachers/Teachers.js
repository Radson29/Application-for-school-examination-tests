import { useMemo, useState } from 'react';
import { Button } from 'react-daisyui';
import Swal from 'sweetalert2';

import { useQuery, useMutation } from 'react-query';
import axios from 'api/axios';

import PageWrapper from 'components/Teacher/PageWrapper';
import Table from 'components/Teacher/Table';
import { FaEdit, FaRegTrashAlt, FaUserLock } from 'react-icons/fa';

import { PropagateLoader } from 'react-spinners';

import TeacherEditModal from './TeacherEditModal';
import TeacherAddModal from './TeacherAddModal';

function Teachers() {
  const [showTeacherEdit, setTeacherEdit] = useState(false);
  const [showTeacherAdd, setTeacherAdd] = useState(false);
  const [selectedTeacherId, setSelectedTeacherId] = useState(null);

  const getTeachersQuery = useQuery('teachers_all', () => {
    return axios.get('/admin/teachers').then((res) => res.data);
  });

  const deleteTeacherQuery = useMutation({
    mutationFn: (teacherId) =>
      axios
        .delete(`/admin/teacher/delete?id=${teacherId}`)
        .then((res) => res.data),
    onSuccess: () => {
      Swal.fire({
        title: 'Sukces',
        text: 'Poprawnie usunięto nauczyciela',
        icon: 'success'
      });
      refetchTeachers();
    }
  });

  const resetPasswordQuery = useMutation({
    mutationFn: (teacherId) =>
      axios.post(`admin/teacher/reset?id=${teacherId}`).then((res) => res.data),
    onSuccess: (data) => {
      Swal.fire({
        title: 'Sukces',
        text: `Tymczasowe hasło do konta to: ${data.password}`,
        icon: 'success'
      });
      refetchTeachers();
    }
  });

  const showTeacherAddModal = () => {
    setTeacherAdd(true);
  };

  const showTeacherEditModal = (teacherId) => {
    setSelectedTeacherId(teacherId);
    setTeacherEdit(true);
  };

  const closeTeacherAddModal = () => {
    setTeacherAdd(false);
    refetchTeachers();
  };

  const closeTeacherEditModal = () => {
    setSelectedTeacherId(null);
    setTeacherEdit(false);
    refetchTeachers();
  };

  const refetchTeachers = () => {
    getTeachersQuery.refetch();
  };

  const deleteTeacher = (teacherId) => {
    Swal.fire({
      title: 'Potwierdź usunięcie',
      text: 'Czy na pewno chcesz usunąć tego nauczyciela?',
      showCancelButton: true,
      cancelButtonText: 'Anuluj',
      icon: 'question'
    }).then((result) => {
      if (result.isConfirmed) {
        deleteTeacherQuery.mutate(teacherId);
      }
    });
  };

  const resetPassword = (teacherId) => {
    Swal.fire({
      title: 'Powtwierdź reset hasła',
      text: 'Czy na pewno chcesz zresetować hasło tego nauczyciela?',
      showCancelButton: true,
      cancelButtonText: 'Anuluj',
      icon: 'question'
    }).then((result) => {
      if (result.isConfirmed) {
        resetPasswordQuery.mutate(teacherId);
      }
    });
  };

  const columns = useMemo(
    () => [
      {
        Header: '#',
        accessor: 'id'
      },
      {
        Header: 'Imię',
        accessor: 'firstName'
      },
      {
        Header: 'Nazwisko',
        accessor: 'lastName'
      },
      {
        Header: 'Akcje',
        accessor: 'actions',
        maxWidth: 100 // opcjonalnie jak chcemy ograniczyć szerokość kolumny
      }
    ],
    []
  );

  const TeachersData = useMemo(() => {
    if (getTeachersQuery.isSuccess) {
      return getTeachersQuery.data.map((teacher) => {
        return {
          id: teacher.id,
          firstName: teacher.firstName,
          lastName: teacher.lastName,
          login: teacher.login,
          actions: (
            <div className="flex gap-x-3">
              <Button
                onClick={() => showTeacherEditModal(teacher.id)}
                color="primary"
              >
                <FaEdit />
              </Button>
              <Button color="error" onClick={() => deleteTeacher(teacher.id)}>
                <FaRegTrashAlt />
              </Button>
              <Button color="warning" onClick={() => resetPassword(teacher.id)}>
                <FaUserLock />
              </Button>
            </div>
          )
        };
      });
    }
    return [];
  }, [getTeachersQuery.data, getTeachersQuery.isSuccess]);

  return (
    <>
      {showTeacherEdit && (
        <TeacherEditModal
          teacherId={selectedTeacherId}
          onClose={closeTeacherEditModal}
        ></TeacherEditModal>
      )}
      {showTeacherAdd && (
        <TeacherAddModal onClose={closeTeacherAddModal}></TeacherAddModal>
      )}

      <PageWrapper>
        <header className="flex justify-between items-center pb-16">
          <div>
            <h1 className="text-4xl font-semibold">Nauczyciele</h1>
            <p className="text-gray-500">
              Zarządzanie nauczycielami w systemie
            </p>
          </div>
          <div>
            <Button onClick={() => showTeacherAddModal()} color="success">
              Dodaj nauczyciela
            </Button>
          </div>
        </header>
        <div>
          {getTeachersQuery.isFetching ||
          deleteTeacherQuery.isLoading ||
          resetPasswordQuery.isLoading ? (
            <div className="flex justify-center items-center h-64">
              <PropagateLoader color="#22c55e" className="block" />
            </div>
          ) : getTeachersQuery.isError ? (
            <p className="text-center">{getTeachersQuery.error.message}</p>
          ) : deleteTeacherQuery.isError ? (
            <p className="text-center">{deleteTeacherQuery.error.message}</p>
          ) : resetPasswordQuery.isError ? (
            <p className="text-center">{resetPasswordQuery.error.message}</p>
          ) : (
            <Table columns={columns} data={TeachersData} />
          )}
        </div>
      </PageWrapper>
    </>
  );
}

export default Teachers;
