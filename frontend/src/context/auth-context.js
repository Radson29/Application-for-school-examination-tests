import * as React from 'react';
import axios from 'api/axios';

const AuthContext = React.createContext();

const storedToken = localStorage.getItem('token');
const storedUser = localStorage.getItem('user');

const initialState = {
  isAuthenticated: Boolean(storedToken),
  user: storedUser ? JSON.parse(storedUser) : null,
  token: storedToken
};

function authReducer(state, action) {
  switch (action.type) {
    case 'LOGIN': {
      return {
        ...state,
        isAuthenticated: true,
        user: action.payload.user,
        token: action.payload.token
      };
    }
    case 'LOGOUT': {
      return {
        ...state,
        isAuthenticated: false,
        user: null,
        token: null
      };
    }
    default: {
      throw new Error(`Unhandled action type: ${action.type}`);
    }
  }
}

function AuthProvider({ children }) {
  const [state, dispatch] = React.useReducer(authReducer, initialState);

  const login = async (username, password) => {
    try {
      const userToken = await axios({
        method: 'post',
        url: `oauth/token?grant_type=password&username=${username}&password=${password}`,
        auth: {
          username: 'ur-client',
          password: 'ur-secret'
        }
      });
      const { access_token: token } = userToken.data;

      const userInfoResponse = await axios.get('/user/info', {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      const userInfo = userInfoResponse.data;

      const userPermissionsResponse = await axios.get('/user/permissions', {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      const userPermissions = userPermissionsResponse.data;

      const userData = { ...userInfo, ...userPermissions };

      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(userData));

      dispatch({
        type: 'LOGIN',
        payload: {
          token,
          user: userData
        }
      });

      return userData;
    } catch (error) {
      throw error;
    }
  };

  const logout = async () => {
    try {
      await axios.get('user/logout');

      localStorage.removeItem('token');
      localStorage.removeItem('user');

      dispatch({ type: 'LOGOUT' });
    } catch (error) {
      throw error;
    }
  };

  const isAuthenticated = Boolean(state.token);

  const value = { state, dispatch, login, logout, isAuthenticated };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

function useAuth() {
  const context = React.useContext(AuthContext);

  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }

  return context;
}

export { AuthProvider, useAuth };
