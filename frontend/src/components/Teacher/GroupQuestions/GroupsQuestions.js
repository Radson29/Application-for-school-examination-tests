import React, { useState } from 'react';
import { Button } from 'react-daisyui';
import { FaPlus, FaRegTrashAlt, FaInfoCircle } from 'react-icons/fa';
import Swal from 'sweetalert2';
import { useQuery, useMutation } from 'react-query';
import axios from 'api/axios';
import { PropagateLoader } from 'react-spinners';

import PageWrapper from 'components/Teacher/PageWrapper';

import AddGroupQuestionModal from './AddGroupQuestionModal';
import AddQuestionToGroupModal from './AddQuestionToGroupModal';
import EditQuestionModal from './EditQuestionModal';

const GroupsQuestions = () => {
  const [showAddQuestion, setShowAddQuestion] = useState(false);
  const [showAddGroup, setShowAddGroup] = useState(false);
  const [showQuestionInfo, setShowQuestionInfo] = useState(false);
  const [selectedQuestionGroupId, setSelectedQuestionGroupId] = useState(null);
  const [selectedQuestionId, setSelectedQuestionId] = useState(null);

  const getAllQuestionGroupsQuery = useQuery('question_groups', () => {
    return axios.get('/teacher/questions').then((res) => res.data);
  });

  const deleteQuestionGroupQuery = useMutation({
    mutationFn: (groupId) =>
      axios
        .delete(`/teacher/questions/groups/delete?id=${groupId}`)
        .then((res) => res.data),
    onSuccess: () => {
      Swal.fire({
        title: 'Sukces',
        text: 'Poprawnie usunięto grupę.',
        icon: 'success'
      });
      refetchQuestionGroups();
    }
  });

  const deleteQuestionFromGroupQuery = useMutation({
    mutationFn: (questionId) =>
      axios
        .delete(`teacher/questions/delete?&id=${questionId}`)
        .then((res) => res.data),
    onSuccess: () => {
      Swal.fire({
        title: 'Sukces',
        text: 'Poprawnie usunięto pytanie z grupy.',
        icon: 'success'
      });
      refetchQuestionGroups();
    }
  });

  const refetchQuestionGroups = () => {
    getAllQuestionGroupsQuery.refetch();
  };

  const showQuestionInfoModal = (question) => {
    setSelectedQuestionId(question);
    setShowQuestionInfo(true);
  };

  const showAddQuestionModal = (groupId) => {
    setSelectedQuestionGroupId(groupId);
    setShowAddQuestion(true);
  };

  const showAddGroupModal = () => {
    setShowAddGroup(true);
  };

  const closeAddQuestionModal = () => {
    setSelectedQuestionGroupId(null);
    setShowAddQuestion(false);
    refetchQuestionGroups();
  };

  const closeAddGroupModal = () => {
    setShowAddGroup(false);
    refetchQuestionGroups();
  };

  const closeQuestionInfoModal = () => {
    setShowQuestionInfo(false);
    refetchQuestionGroups();
  };

  const deleteGroup = (groupId) => {
    Swal.fire({
      title: 'Czy chcesz usunąć wybraną grupę?',
      showDenyButton: true,
      showConfirmButton: false,
      denyButtonText: `Usuń`
    }).then((result) => {
      if (result.isDenied) {
        deleteQuestionGroupQuery.mutate(groupId);
      }
    });
  };

  const deleteQuestionFromGroup = (questionId) => {
    Swal.fire({
      title: 'Czy chcesz usunąć wybrane pytanie z tej grupy?',
      showDenyButton: true,
      showConfirmButton: false,
      denyButtonText: `Usuń`
    }).then((result) => {
      if (result.isDenied) {
        deleteQuestionFromGroupQuery.mutate(questionId);
      }
    });
  };

  return (
    <PageWrapper>
      {showAddGroup && (
        <AddGroupQuestionModal onClose={() => closeAddGroupModal()} />
      )}
      {showAddQuestion && (
        <AddQuestionToGroupModal
          onClose={() => closeAddQuestionModal()}
          groupId={selectedQuestionGroupId}
        />
      )}
      {showQuestionInfo && (
        <EditQuestionModal
          questionId={selectedQuestionId}
          onClose={() => closeQuestionInfoModal()}
        />
      )}
      <header className="flex justify-between items-center pb-16">
        <div>
          <h1 className="text-4xl font-semibold">Baza Pytań</h1>
          <p className="text-gray-500">Twórz zbiory pytań do testów</p>
        </div>
        <div>
          <Button
            onClick={() => showAddGroupModal()}
            className="bg-cyan-400 hover:bg-cyan-500 border-0 text-white border-cyan-900"
          >
            <FaPlus />
          </Button>
        </div>
      </header>
      {getAllQuestionGroupsQuery.isFetching ||
      deleteQuestionGroupQuery.isLoading ||
      deleteQuestionFromGroupQuery.isLoading ? (
        <div className="flex justify-center items-center h-64">
          <PropagateLoader color="#0ea5e9" className="block" />
        </div>
      ) : getAllQuestionGroupsQuery.isError ? (
        <p className="text-center">{getAllQuestionGroupsQuery.error.message}</p>
      ) : deleteQuestionGroupQuery.isError ? (
        <p className="text-center">{deleteQuestionGroupQuery.error.message}</p>
      ) : deleteQuestionFromGroupQuery.isError ? (
        <p className="text-center">
          {deleteQuestionFromGroupQuery.error.message}
        </p>
      ) : (
        getAllQuestionGroupsQuery.data &&
        getAllQuestionGroupsQuery.data.map((group) => (
          <div key={group.id} className="w-full mb-10">
            <div className="h-[80px] flex justify-between text-white">
              <div className="grid place-items-center text-[22px] bg-cyan-400 w-[250px] font-bold text-center text-white ">
                {group.name}
              </div>
              <div className="grid grid-cols-1 justify-items-start items-center pl-8 text-[20px] tracking-wide bg-gray-600 w-full">
                <span>
                  Liczba pytań:{' '}
                  <span className="pl-2 font-bold">
                    {group.questions.length}
                  </span>
                </span>
              </div>
              <div className=" flex justify-around bg-gray-600 w-[200px] items-center pr-5">
                <Button
                  className="bg-cyan-400 hover:bg-cyan-500 border-0 text-white"
                  onClick={() => showAddQuestionModal(group.id)}
                >
                  <FaPlus />
                </Button>
                <Button
                  color="error"
                  onClick={() => {
                    deleteGroup(group.id);
                  }}
                >
                  <FaRegTrashAlt />
                </Button>
              </div>
            </div>
            <ul className="w-full text-black">
              {group.questions.map((question, index) => (
                <li
                  key={question.id}
                  className="bg-white p-5 flex justify-between items-center"
                >
                  <div>
                    <span className="pr-3 text-gray-400">{index + 1}</span>
                    {question.value}
                  </div>
                  <div className="flex items-center">
                    <div className="pr-3 text-[17px] cursor-pointer">
                      <FaInfoCircle
                        color="deepskyblue"
                        onClick={() => {
                          showQuestionInfoModal(question.id);
                        }}
                      />
                    </div>
                    <div
                      className="text-[17px] cursor-pointer"
                      onClick={() => {
                        deleteQuestionFromGroup(question.id);
                      }}
                    >
                      <FaRegTrashAlt color="red" />
                    </div>
                  </div>
                </li>
              ))}
            </ul>
          </div>
        ))
      )}
    </PageWrapper>
  );
};

export default GroupsQuestions;
