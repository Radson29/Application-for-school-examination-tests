import { Navigate } from 'react-router-dom';
import { useAuth } from 'context/auth-context';
import { roleToPath } from 'utils/auth_utils';

const PublicRoute = ({ children }) => {
  const { isAuthenticated, state } = useAuth();

  if (isAuthenticated && state.user.role) {
    return <Navigate to={roleToPath(state.user.role)} replace />;
  }

  return children;
};

export default PublicRoute;
