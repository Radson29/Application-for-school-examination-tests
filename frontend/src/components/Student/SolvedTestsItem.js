import { gradeToTailwindClass, gradeToText } from 'utils/tests_utils.js';

const SolvedTestItem = (props) => {
  let color = gradeToTailwindClass(props.grade);
  let grade = gradeToText(props.grade);

  return (
    <li className="p-5 flex flex-row border-2 rounded-md border-gray-400 mt-5">
      <div>
        <div className="flex items-center p-4 h-full">
          <div className={`rounded-full w-3 h-3 mr-4 ${color}`}></div>
        </div>
      </div>
      <div className="w-full">
        <h1 className="text-gray-700 text-[25px] mb-1s font-semibold">
          {props.name}
        </h1>
        <h1 className="text-gray-400 text-[20px] font-light">
          {props.teacher} -{' '}
          <span>
            {props.value1}/{props.value2}
          </span>
        </h1>
      </div>
      <div className="grid place-items-center">
        <button
          className={`${color}
        cursor-default py-[16px] text-center tracking-tight w-[140px] sm:w-[175px]`}
        >
          <span className="text-white text-[20px] sm:text-[23px] font-semibold">
            {grade}
          </span>
        </button>
      </div>
    </li>
  );
};

export default SolvedTestItem;
