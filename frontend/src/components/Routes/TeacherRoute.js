import ProtectedRoute from './ProtectedRoute';

const TeacherRoute = ({ children }) => {
  return <ProtectedRoute role="ROLE_TEACHER">{children}</ProtectedRoute>;
};

export default TeacherRoute;
