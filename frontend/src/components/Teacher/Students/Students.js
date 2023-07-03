import { useMemo, useState } from 'react';
import { Button } from 'react-daisyui';
import Swal from 'sweetalert2';

import { useQuery, useMutation } from 'react-query';
import axios from 'api/axios';

import { PropagateLoader } from 'react-spinners';

import PageWrapper from 'components/Teacher/PageWrapper';
import Table from 'components/Teacher/Table';
import { FaEdit, FaRegTrashAlt, FaUserLock } from 'react-icons/fa';

import Modal from 'components/UI/Modal';
import StudentEditModal from './StudentEditModal';
import StudentAddModal from './StudentAddModal';

function Students() {
  const [showStudentEdit, setStudentEdit] = useState(false);
  const [showStudentAdd, setStudentAdd] = useState(false);
  const [selectedStudentId, setSelectedStudentId] = useState(null);

  const getStudentsQuery = useQuery('students', () => {
    return axios.get('/teacher/students').then((res) => res.data);
  });

  const deleteStudentQuery = useMutation({
    mutationFn: (studentId) =>
      axios
        .delete(`/teacher/student/delete?id=${studentId}`)
        .then((res) => res.data),
    onSuccess: () => {
      Swal.fire({
        title: 'Sukces',
        text: 'Poprawnie usunięto studenta',
        icon: 'success'
      });
      refetchStudents();
    }
  });

  const resetPasswordQuery = useMutation({
    mutationFn: (studentId) =>
      axios
        .post(`teacher/student/reset?id=${studentId}`)
        .then((res) => res.data),
    onSuccess: (data) => {
      Swal.fire({
        title: 'Sukces',
        text: `Tymczasowe hasło do konta to: ${data.password}`,
        icon: 'success'
      });
      refetchStudents();
    }
  });

  const showStudentAddModal = () => {
    setStudentAdd(true);
  };

  const showStudentEditModal = (studentId) => {
    setSelectedStudentId(studentId);
    setStudentEdit(true);
  };

  const closeStudentAddModal = () => {
    setStudentAdd(false);
  };

  const closeStudentEditModal = () => {
    setSelectedStudentId(null);
    setStudentEdit(false);
  };

  const refetchStudents = () => {
    getStudentsQuery.refetch();
  };

  const deleteStudent = (studentId) => {
    Swal.fire({
      title: 'Potwierdź usunięcie',
      text: 'Czy na pewno chcesz usunąć tego studenta?',
      showCancelButton: true,
      cancelButtonText: 'Anuluj',
      icon: 'question'
    }).then((result) => {
      if (result.isConfirmed) {
        deleteStudentQuery.mutate(studentId);
      }
    });
  };

  const resetPassword = (studentId) => {
    Swal.fire({
      title: 'Powtwierdź reset hasła',
      text: 'Czy na pewno chcesz zresetować hasło tego studenta?',
      showCancelButton: true,
      cancelButtonText: 'Anuluj',
      icon: 'question'
    }).then((result) => {
      if (result.isConfirmed) {
        resetPasswordQuery.mutate(studentId);
      }
    });
  };

  const columns = useMemo(
    () => [
      {
        Header: '#',
        accessor: 'id' // accessor is the "key" in the data
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
        Header: 'Login',
        accessor: 'login'
      },
      {
        Header: 'Akcje',
        accessor: 'actions',
        maxWidth: 100 // opcjonalnie jak chcemy ograniczyć szerokość kolumny
      }
    ],
    []
  );

  const studentsData = useMemo(() => {
    if (getStudentsQuery.isSuccess) {
      return getStudentsQuery.data.map((student) => {
        return {
          id: student.id,
          firstName: student.firstName,
          lastName: student.lastName,
          login: student.login,
          actions: (
            <div className="flex gap-x-3">
              <Button
                onClick={() => showStudentEditModal(student.id)}
                color="primary"
              >
                <FaEdit />
              </Button>
              <Button color="error" onClick={() => deleteStudent(student.id)}>
                <FaRegTrashAlt />
              </Button>
              <Button color="warning" onClick={() => resetPassword(student.id)}>
                <FaUserLock />
              </Button>
            </div>
          )
        };
      });
    }
    return [];
  }, [getStudentsQuery.data, getStudentsQuery.isSuccess]);

  return (
    <>
      {showStudentEdit && (
        <Modal onClose={closeStudentEditModal}>
          <StudentEditModal
            studentId={selectedStudentId}
            handleClose={closeStudentEditModal}
            refetchData={refetchStudents}
          ></StudentEditModal>
        </Modal>
      )}
      {showStudentAdd && (
        <Modal onClose={closeStudentAddModal}>
          <StudentAddModal
            handleClose={closeStudentAddModal}
            refetchData={refetchStudents}
          ></StudentAddModal>
        </Modal>
      )}
      <PageWrapper>
        <header className="flex justify-between items-center pb-16">
          <div>
            <h1 className="text-4xl font-semibold">Uczniowie</h1>
            <p className="text-gray-500">Zarządzanie uczniami w systemie</p>
          </div>
          <div>
            <Button onClick={() => showStudentAddModal()} color="success">
              Dodaj ucznia
            </Button>
          </div>
        </header>
        <div>
          {getStudentsQuery.isFetching ||
          deleteStudentQuery.isLoading ||
          resetPasswordQuery.isLoading ? (
            <div className="flex justify-center items-center h-64">
              <PropagateLoader color="#0ea5e9" className="block" />
            </div>
          ) : getStudentsQuery.isError ? (
            <p className="text-center">{getStudentsQuery.error.message}</p>
          ) : deleteStudentQuery.isError ? (
            <p className="text-center">{deleteStudentQuery.error.message}</p>
          ) : resetPasswordQuery.isError ? (
            <p className="text-center">{resetPasswordQuery.error.message}</p>
          ) : (
            <Table columns={columns} data={studentsData} />
          )}
        </div>
      </PageWrapper>
    </>
  );
}

export default Students;
