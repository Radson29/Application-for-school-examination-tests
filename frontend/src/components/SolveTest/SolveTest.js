import Modal from 'components/UI/Modal';
import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Question from './Question';
import Timer from './Timer';
import axios from 'api/axios';
import { useQuery, useMutation } from 'react-query';
import { PropagateLoader } from 'react-spinners';
import Swal from 'sweetalert2';

const SolveTest = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [test, setTest] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [stopTimer, setStopTimer] = useState(false);
  const [questions, setQuestions] = useState([]);
  const [questionsID, setQuestionsID] = useState([]);
  const [answers, setAnswers] = useState([]);

  var ques = [];
  var quesId = [];
  var ans = [];

  const getTest = useQuery('getTest', () => {
    axios.get(`/student/test/start?id=${id}`).then((res) => {
      setTest(res.data);

      for (var i = 0; i < res.data.generatedQuestions.length; i++) {
        ques.push(res.data.generatedQuestions[i].question.value);
        ans.push([]);
        quesId.push(res.data.generatedQuestions[i].id);
      }

      setQuestions(ques);
      setAnswers(ans);
      setQuestionsID(quesId);
    });
  });

  const endTest = () => {
    var allData = [];

    for (var i = 0; i < questionsID.length; i++) {
      allData.push({
        questionId: questionsID[i],
        answersIds: answers[i]
      });
    }

    var json = {
      quizInstanceId: test.id,
      data: allData
    };

    sendTest.mutate(json);
  };

  const sendTest = useMutation({
    mutationFn: (values) =>
      axios({
        method: 'post',
        url: '/student/test/finish',
        data: values
      }).then((res) => res.data),
    onError: (error) => {
      // TODO: obsługa błędów
      console.log(error);
    },
    onSuccess: (data) => {
      setStopTimer(true);
      setShowModal(false);
      Swal.fire({
        title: 'Sukces',
        text: `Twoje rozwiązanie zostało przesłane`,
        icon: 'success',
        allowOutsideClick: false
      }).then((result) => {
        if (result.isConfirmed) {
          navigate('/uczen');
        }
      });
    }
  });

  const userAnswer = (answerId, question) => {
    let index = questions.indexOf(question);

    var isMarked = answers[index].indexOf(answerId);

    var currentAnswers = answers;

    if (isMarked !== -1) {
      currentAnswers[index].splice(isMarked, 1);
    } else {
      currentAnswers[index].push(answerId);
    }

    setAnswers(currentAnswers);
  };

  return (
    <div>
      <div className="w-auto h-auto bg-gradient-to-r from-cyan-500 to-blue-500 pt-10">
        <div className="w-[100%] xl:w-[1280px] border-2 mx-auto p-7 bg-white">
          {test == null ? (
            <div className="flex justify-center items-center h-64">
              <PropagateLoader color="#0ea5e9" className="block" />
            </div>
          ) : test.isError ? (
            <p className="text-center">{test.error.message}</p>
          ) : (
            <div>
              {stopTimer ? (
                <div></div>
              ) : (
                <Timer
                  time={60 * parseInt(test.quiz.quizTime) - 5}
                  endTest={endTest}
                  stopTimer={stopTimer}
                />
              )}

              <div className="w-auto mb-6 text-[31px] font-bold text-gray-700 text-center">
                {test.quiz.title}
              </div>
              {test.generatedQuestions.map((question, index) => (
                <Question
                  key={index + 1}
                  questionNumber={index + 1}
                  question={question.question.value}
                  questionId={question.question.id}
                  answers={question.question.answers}
                  testID={id}
                  userAnswer={userAnswer}
                />
              ))}
              <div className="w-full">
                <button
                  onClick={() => {
                    showModal === false
                      ? setShowModal(true)
                      : setShowModal(false);
                  }}
                  className="bg-cyan-500 block mx-auto hover:bg-cyan-400 cursor-pointer py-[14px] w-[175px] text-center tracking-tight"
                >
                  <span className="text-white text-[20px] sm:text-[23px] font-semibold">
                    Zakończ Test
                  </span>
                </button>
              </div>
            </div>
          )}
        </div>
      </div>

      {showModal && (
        <Modal
          onClose={() => {
            showModal === false ? setShowModal(true) : setShowModal(false);
          }}
        >
          <div className="text-center text-bold text-[28px] mb-10">
            Czy na pewno chcesz zakończyć test?
          </div>
          <div className="flex space-around gap-2 mb-2">
            <button
              onClick={() => {
                showModal === false ? setShowModal(true) : setShowModal(false);
              }}
              className="bg-gray-700 hover:bg-gray-600 block mx-auto cursor-pointer py-[14px] w-[175px] text-center tracking-tight"
            >
              <span className="text-white text-[20px] sm:text-[23px] font-semibold">
                Anuluj
              </span>
            </button>
            <button
              onClick={endTest}
              className="bg-cyan-500 block mx-auto hover:bg-cyan-400 cursor-pointer py-[14px] w-[175px] text-center tracking-tight"
            >
              <span className="text-white text-[20px] sm:text-[23px] font-semibold">
                Zakończ
              </span>
            </button>
          </div>
        </Modal>
      )}
    </div>
  );
};

export default SolveTest;
