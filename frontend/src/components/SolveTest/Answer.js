const Answer = (props) => {
  const userAnswer = (answerId) => {
    props.userAnswer(answerId, props.question);
  };

  return (
    <div>
      <label className="cursor-pointer label mb-2 p-3">
        <input
          onChange={() => userAnswer(props.answerId, props.answer)}
          type="checkbox"
          className="checkbox checkbox-info mr-4"
        />
        <span className="label-text w-full text-[18px]">{props.answer}</span>
      </label>
    </div>
  );
};

export default Answer;
