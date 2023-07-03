import { useNavigate } from 'react-router-dom';

const WaitingTestsItem = (props) => {
  const navigate = useNavigate();

  const startTest = (id) => {
    //TO DO Start test
    navigate('/test/' + id);
  };

  return (
    <li className="p-5 flex flex-row border-2 rounded-md border-gray-400 mt-5">
      <div>
        <div className="flex items-center p-4 h-full">
          <div className="bg-cyan-500 rounded-full w-3 h-3 mr-4"></div>
        </div>
      </div>
      <div className="w-full">
        <h1 className="text-gray-700 text-[25px] mb-1s font-semibold">
          {props.name}
        </h1>
        <h1 className="text-gray-400 text-[20px] font-light">
          czas trwania: {props.time} minut - {props.subject}
        </h1>
      </div>
      <div className="grid place-items-center">
        <button
          onClick={() => {
            startTest(props.id);
          }}
          className="bg-cyan-500 hover:bg-cyan-400 cursor-pointer py-[16px] w-[140px] sm:w-[175px] text-center tracking-tight"
        >
          <span className="text-white text-[20px] sm:text-[23px] font-semibold">
            Rozpocznij
          </span>
        </button>
      </div>
    </li>
  );
};

export default WaitingTestsItem;
