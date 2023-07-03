import Answer from './Answer';

const Question = (props) => {
  const userAnswer = (answerId, answer) => {
    props.userAnswer(answerId, answer);
  };

  return (
    <div className="mb-8">
      <div className="flex justify-between w-auto mb-3">
        <div className="grid place-content-center grid-cols-1">
          <div className="text-[27px] font-semibold text-cyan-500">
            {props.question}
          </div>
        </div>
        <div className="grid grid-cols-1 place-content-center align-center min-w-[165px]">
          <div>
            <span className="text-[28px] font-semibold">Pytanie </span>
            <span className="text-[28px] font-bold text-gray-700">
              {props.questionNumber}
            </span>
            {/* <span className="text-[28px] font-semibold"> z </span>
            <span className="text-[28px] font-bold text-gray-700">
              {props.questionCount}
            </span> */}
          </div>
        </div>
      </div>
      <div className="form-control">
        {props.answers.map((item) => {
          return (
            <Answer
              answer={item.value}
              key={item.id}
              question={props.question}
              answerId={item.id}
              userAnswer={userAnswer}
            />
          );
        })}
      </div>
    </div>
  );
};

export default Question;
