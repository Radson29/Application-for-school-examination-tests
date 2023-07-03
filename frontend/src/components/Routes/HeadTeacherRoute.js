import ProtectedRoute from './ProtectedRoute';

const HeadTeacherRoute = ({ children }) => {
  return <ProtectedRoute role="ROLE_ADMIN">{children}</ProtectedRoute>;
};

export default HeadTeacherRoute;
