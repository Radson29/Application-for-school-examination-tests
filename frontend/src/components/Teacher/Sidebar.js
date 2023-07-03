import { NavLink } from 'react-router-dom';

import {
  FaHome,
  FaUserGraduate,
  FaPowerOff,
  FaUser,
  FaUsers,
  FaQuestion,
  FaFile,
  FaRegCalendarAlt,
  FaRegChartBar
} from 'react-icons/fa';

function Sidebar() {
  const teacherName = () => {
    const teacher = JSON.parse(localStorage.getItem('user'));
    return `${teacher.firstName} ${teacher.lastName}`;
  };

  const menu = [
    {
      name: 'Home',
      path: '/nauczyciel',
      icon: FaHome
    },
    {
      name: 'Wyniki',
      path: 'wyniki',
      icon: FaRegChartBar
    },
    {
      name: 'Harmonogram',
      path: 'harmonogram',
      icon: FaRegCalendarAlt
    },
    {
      name: 'Testy',
      path: 'testy',
      icon: FaFile
    },
    {
      name: 'Baza Pytań',
      path: 'baza-pytan',
      icon: FaQuestion
    },
    {
      name: 'Grupy',
      path: 'grupy',
      icon: FaUsers
    },
    {
      name: 'Uczniowie',
      path: 'uczniowie',
      icon: FaUser
    },
    {
      name: 'Wyloguj',
      path: '/wyloguj',
      icon: FaPowerOff
    }
  ];

  return (
    <aside className="h-full w-80 bg-sky-300 p-5 bg-slate-50 border-r border-gray-200">
      <div className="p-3 pb-0 flex items-center">
        <div className="w-12 h-12 rounded mr-5 bg-gradient-to-br from-sky-400 to-sky-600 flex">
          <FaUserGraduate size={16} className="text-white m-auto" />
        </div>
        <div>
          <h1 className="font-bold text-black text-lg">{teacherName()}</h1>
          <p className="text-sky-600">Nauczyciel</p>
        </div>
      </div>
      <hr className="h-px my-8 bg-gray-200 border-0 dark:bg-gray-700" />
      <ul>
        {menu.map((item, index) => (
          <li key={index}>
            <NavLink
              className={({ isActive }) =>
                `p-4 mt-3 font-semibold flex items-center rounded-md px-4
              text-gray-400 duration-300 cursor-pointer duration-150
              ${isActive
                  ? 'bg-sky-500 text-white'
                  : 'hover:text-sky-600 hover:bg-sky-100'
                }`
              }
              to={item.path}
              end
            >
              <item.icon size={16} className="mr-3" />
              {item.name}
            </NavLink>
          </li>
        ))}
      </ul>
    </aside>
  );
}

export default Sidebar;
