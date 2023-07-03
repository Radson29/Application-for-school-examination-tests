import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
const Dashboard = () => {
  return (
    <div className="h-screen w-screen flex overflow-y-scroll">
      <Sidebar />
      <main className="flex-grow flex justify-center overflow-y-scroll">
        <Outlet />
      </main>
    </div>
  );
};
export default Dashboard;
