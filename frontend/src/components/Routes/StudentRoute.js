import ProtectedRoute from './ProtectedRoute';

const StudentRoute = ({ children }) => {
  return <ProtectedRoute role="ROLE_STUDENT">{children}</ProtectedRoute>;
};

export default StudentRoute;
