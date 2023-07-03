import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  FaHome,
  FaUser,
  FaClock,
  FaBook,
  FaAward,
  FaDatabase,
  FaUsers,
  FaWindowClose,
  FaChalkboardTeacher
} from 'react-icons/fa';

const Sidebar = () => {
  const menu = [
    {
      name: 'Home',
      path: '/dyrektor',
      icon: FaHome
    },
    // {
    //   name: 'Wyniki',
    //   path: 'wyniki',
    //   icon: FaAward
    // },
    // {
    //   name: 'Harmonogram',
    //   path: 'harmonogram',
    //   icon: FaClock
    // },
    // {
    //   name: 'Testy',
    //   path: 'testy',
    //   icon: FaBook
    // },
    // {
    //   name: 'Baza Pytań',
    //   path: 'baza-pytan',
    //   icon: FaDatabase
    // },
    // {
    //   name: 'Grupy',
    //   path: 'grupy',
    //   icon: FaUsers
    // },
    // {
    //   name: 'Uczniowie',
    //   path: 'uczniowie',
    //   icon: FaUser
    // },
    {
      name: 'Nauczyciele',
      path: 'nauczyciele',
      icon: FaChalkboardTeacher
    },
    {
      name: 'Wyloguj',
      path: '/wyloguj',
      icon: FaWindowClose
    }
  ];

  const getHeadTeacherFullName = () => {
    const headTeacher = JSON.parse(localStorage.getItem('user'));
    return `${headTeacher.firstName} ${headTeacher.lastName}`;
  };

  return (
    <div className="px-10 py-16 flex flex-col w-[25%] min-h-screen text-gray-100 text-[25px] bg-gray-100">
      <div className="flex items-center place-items-start h-20">
        <div className="rounded-full bg-green-500 w-20 h-20 flex items-center justify-center">
          <FaUser className="text-white" size={38} />
        </div>
        <div className="ml-4">
          <p className="text-[30px] font-bold text-green-500">
            {getHeadTeacherFullName()}
          </p>
          <p className="text-[20px] text-gray-400 ">Dyrektor</p>
        </div>
      </div>
      <nav className="flex-grow mt-5">
        <ul className="flex flex-col">
          {menu.map((item, index) => (
            <li key={index}>
              <NavLink
                className={({ isActive }) =>
                  `p-4 mt-3 flex items-center rounded-md duration-300 cursor-pointer duration-150 hover:bg-green-500 hover:text-white text-gray-400 ${
                    isActive
                      ? 'bg-green-500 text-white'
                      : 'hover:text-white hover:bg-green-600'
                  }`
                }
                to={item.path}
                end
              >
                <item.icon size={16} className="mr-5" />
                {item.name}
              </NavLink>
            </li>
          ))}
        </ul>
      </nav>
    </div>
  );
};

export default Sidebar;
