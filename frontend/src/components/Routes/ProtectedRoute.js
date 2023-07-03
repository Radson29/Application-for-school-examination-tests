import { Navigate } from 'react-router-dom';
import { useAuth } from 'context/auth-context';

const ProtectedRoute = ({ children, role }) => {
  const { isAuthenticated, state } = useAuth();
  const userRole = isAuthenticated ? state.user.role : null;

  // Jak user nie jest zalogowany, to przekieruj go na stronę logowania
  // Jak podaliśmy role, to sprawdź czy user ma tą rolę
  if (!isAuthenticated || (role && role !== userRole)) {
    return <Navigate to="/" replace />;
  }

  return children;
};

export default ProtectedRoute;
