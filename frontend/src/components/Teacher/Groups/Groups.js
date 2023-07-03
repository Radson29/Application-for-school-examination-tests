import { FaDownload, FaPlus, FaRegTrashAlt } from 'react-icons/fa';
import { Button, Modal } from 'react-daisyui';
import { useState, useRef } from 'react';
import Swal from 'sweetalert2';
import { useQuery, useMutation } from 'react-query';
import axios from 'api/axios';
import { PropagateLoader } from 'react-spinners';

import PageWrapper from '../PageWrapper';

import AddStudentToGroupModal from './AddStudentToGroupModal';
import AddGroupModal from './AddGroupModal';

const Groups = () => {
  const [showAddGroup, setShowAddGroup] = useState(false);
  const [showAddStudentToGroup, setShowAddStudentToGroup] = useState(false);
  const [selectedGroupId, setSelectedGroupId] = useState(null);
  const [showPDFModal, setShowPDFModal] = useState(false);
  const [pdfUrl, setPdfUrl] = useState('');
  const [isPdfLoading, setIsPdfLoading] = useState(false);

  const fetchPDF = async () => {
    try {
      setIsPdfLoading(true);

      const response = await axios.get('/group/pdf', {
        responseType: 'arraybuffer'
      });

      const pdfBlob = new Blob([response.data], { type: 'application/pdf' });
      const url = URL.createObjectURL(pdfBlob);

      openPdfModal(url);
    } catch (error) {
      Swal.fire({
        title: 'Błąd',
        text: 'Nie udało się pobrać listy grup w formacie PDF.',
        icon: 'error'
      });
    } finally {
      setIsPdfLoading(false);
    }
  };

  const getAllGroupsWithStudents = useQuery('students_groups', () => {
    return axios.get('/teacher/groups/students').then((res) => res.data);
  });

  const deleteGroupQuery = useMutation({
    mutationFn: (groupId) =>
      axios.delete(`/group/delete?id=${groupId}`).then((res) => res.data),
    onSuccess: () => {
      Swal.fire({
        title: 'Sukces',
        text: 'Poprawnie usunięto grupę.',
        icon: 'success'
      });
      refetchGroups();
    }
  });

  const deleteStudentFromGroupQuery = useMutation({
    mutationFn: (obj) =>
      axios
        .delete(
          `group/deletePerson?groupId=${obj.groupId}&personId=${obj.studentId}`
        )
        .then((res) => res.data),
    onSuccess: () => {
      Swal.fire({
        title: 'Sukces',
        text: 'Poprawnie usunięto studenta z grupy.',
        icon: 'success'
      });
      refetchGroups();
    }
  });

  const refetchGroups = () => {
    getAllGroupsWithStudents.refetch();
  };

  const showAddGroupModal = () => {
    setShowAddGroup(true);
  };

  const showAddStudentToGroupModal = (groupId) => {
    setSelectedGroupId(groupId);
    setShowAddStudentToGroup(true);
  };

  const closeAddGroupModal = () => {
    refetchGroups();
    setShowAddGroup(false);
  };

  const closeAddStudentToGroupModal = () => {
    refetchGroups();
    setShowAddStudentToGroup(false);
    setSelectedGroupId(null);
  };

  const openPdfModal = (url) => {
    setPdfUrl(url);
    setShowPDFModal(true);
  };

  const closePdfModal = () => {
    setShowPDFModal(false);
    URL.revokeObjectURL(pdfUrl);
  };

  const deleteGroup = (groupId) => {
    Swal.fire({
      title: 'Czy chcesz usunąć wybraną grupę?',
      showDenyButton: true,
      showConfirmButton: false,
      denyButtonText: `Usuń`
    }).then((result) => {
      if (result.isDenied) {
        deleteGroupQuery.mutate(groupId);
      }
    });
  };

  const deleteStudentFromGroup = (groupId, studentId) => {
    Swal.fire({
      title: 'Czy chcesz usunąć wybranego ucznia z tej grupy?',
      showDenyButton: true,
      showConfirmButton: false,
      denyButtonText: `Usuń`
    }).then((result) => {
      if (result.isDenied) {
        deleteStudentFromGroupQuery.mutate({
          groupId,
          studentId
        });
      }
    });
  };

  return (
    <PageWrapper>
      {showPDFModal && (
        <Modal
          className="p-8 w-11/12 max-w-5xl h-full flex flex-col"
          open={showPDFModal}
          onClickBackdrop={closePdfModal}
        >
          <Modal.Header className="font-bold">
            <p className="text-2xl text-center">Lista grup do pobrania w PDF</p>
          </Modal.Header>

          <Modal.Body className="grow">
            <embed
              src={pdfUrl}
              type="application/pdf"
              width="100%"
              height="100%"
            />
          </Modal.Body>

          <Modal.Actions>
            <Button className="w-full bg-sky-600" onClick={closePdfModal}>
              Ok
            </Button>
          </Modal.Actions>
        </Modal>
      )}

      {showAddStudentToGroup && (
        <AddStudentToGroupModal
          groupId={selectedGroupId}
          onClose={() => closeAddStudentToGroupModal()}
        />
      )}
      {showAddGroup && <AddGroupModal onClose={() => closeAddGroupModal()} />}

      <header className="flex justify-between items-center pb-16">
        <div>
          <h1 className="text-4xl font-semibold">Grupy</h1>
          <p className="text-gray-500">
            Zarządzanie grupami uczniów w systemie
          </p>
        </div>
        <div className="flex gap-3">
          <Button
            onClick={() => fetchPDF()}
            disabled={isPdfLoading}
            className={`bg-teal-500 hover:bg-teal-600 border-0 text-white border-teal-900 ${
              isPdfLoading && 'bg-gray-400 cursor-not-allowed'
            }`}
          >
            <FaDownload />
          </Button>
          <Button
            onClick={showAddGroupModal}
            className="bg-cyan-400 hover:bg-cyan-500 border-0 text-white border-cyan-900"
          >
            <FaPlus />
          </Button>
        </div>
      </header>

      {getAllGroupsWithStudents.isFetching ||
      deleteGroupQuery.isLoading ||
      deleteStudentFromGroupQuery.isLoading ||
      isPdfLoading ? (
        <div className="flex justify-center items-center h-64">
          <PropagateLoader color="#0ea5e9" className="block" />
        </div>
      ) : getAllGroupsWithStudents.isError ? (
        <p className="text-center">{getAllGroupsWithStudents.error.message}</p>
      ) : (
        getAllGroupsWithStudents.data.map((group) => (
          <div className="w-full mb-10" key={group.group.id}>
            <div className="h-[80px] flex justify-between text-white">
              <div className="grid place-items-center text-[22px] bg-cyan-400 w-[250px] font-bold text-center text-white ">
                {group.group.name}
              </div>
              <div className="grid grid-cols-1 justify-items-start items-center pl-8 text-[20px] tracking-wide bg-gray-600 w-full">
                <span>
                  Liczba uczniów:{' '}
                  <span className="pl-2 font-bold">{group.people.length}</span>
                </span>
              </div>
              <div className=" flex justify-around bg-gray-600 w-[200px] items-center pr-5">
                <Button
                  className="bg-cyan-400 hover:bg-cyan-500 border-0 text-white"
                  onClick={() => showAddStudentToGroupModal(group.group.id)}
                >
                  <FaPlus />
                </Button>
                <Button
                  color="error"
                  onClick={() => {
                    deleteGroup(group.group.id);
                  }}
                >
                  <FaRegTrashAlt />
                </Button>
              </div>
            </div>
            <ul className="w-full text-black">
              {group.people.map((student, index) => {
                const fullName = `${student.firstName} ${student.lastName}`;
                return (
                  <li
                    className="bg-white p-5 flex justify-between items-center"
                    key={student.id}
                  >
                    <div>
                      <span className="pr-3 text-gray-400">{index + 1}</span>
                      {fullName}
                    </div>
                    <div
                      className="text-[17px] cursor-pointer"
                      onClick={() => {
                        deleteStudentFromGroup(group.group.id, student.id);
                      }}
                    >
                      <FaRegTrashAlt color="red" />
                    </div>
                  </li>
                );
              })}
            </ul>
          </div>
        ))
      )}
    </PageWrapper>
  );
};

export default Groups;
