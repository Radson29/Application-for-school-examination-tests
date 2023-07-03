import { useQuery } from 'react-query';
import { useNavigate } from 'react-router-dom';

import { useAuth } from 'context/auth-context';

function Logout() {
  const { logout } = useAuth();
  const navigate = useNavigate();

  useQuery({
    queryKey: ['userLogout'],
    queryFn: () => logout().then(() => navigate('/'))
  });

  return null;
}

export default Logout;
