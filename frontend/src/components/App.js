import { HashRouter, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from 'react-query';

import { AuthProvider } from 'context/auth-context';
import { AxiosInterceptor } from 'api/axios';

import ProtectedRoute from 'components/Routes/ProtectedRoute';
import PublicRoute from 'components/Routes/PublicRoute';
import TeacherRoute from 'components/Routes/TeacherRoute';
import StudentRoute from 'components/Routes/StudentRoute';
import HeadTeacherRoute from 'components/Routes/HeadTeacherRoute';

import Login from 'components/Auth/Login';
import Logout from 'components/Auth/Logout';
import TeacherDashboard from 'components/Teacher/Dashboard';
import TeacherHome from 'components/Teacher/Home';
import HeadTeacherHome from 'components/HeadTeacher/Home';
import TeacherStudents from 'components/Teacher/Students';
import GroupsQuestions from 'components/Teacher/GroupQuestions';
import HeadTeacherTeachers from 'components/HeadTeacher/Teachers/';
import Dashboard from 'components/Student/Dashboard';
import HeadTeacherDashboard from 'components/HeadTeacher/Dashboard';
import SolveTest from 'components/SolveTest/SolveTest';
import NotFound from 'components/NotFound';
import Groups from './Teacher/Groups/Groups';
import Quizzes from './Teacher/Quizzes';
import ScheduleQuizzes from './Teacher/ScheduleQuizzes';
import Results from './Teacher/Results';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false // default: true
    }
  }
});

function App() {
  return (
    <HashRouter>
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <AxiosInterceptor>
            <Routes>
              <Route
                path="/"
                element={
                  <PublicRoute>
                    <Login />
                  </PublicRoute>
                }
              />
              <Route
                path="/wyloguj"
                element={
                  <ProtectedRoute>
                    <Logout />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/nauczyciel"
                element={
                  <TeacherRoute>
                    <TeacherDashboard />
                  </TeacherRoute>
                }
              >
                <Route index element={<TeacherHome />} />
                <Route path="uczniowie" element={<TeacherStudents />} />
                <Route path="grupy" element={<Groups />} />
                <Route path="baza-pytan" element={<GroupsQuestions />} />
                <Route path="testy" element={<Quizzes />} />
                <Route path="harmonogram" element={<ScheduleQuizzes />} />
                <Route path="wyniki" element={<Results />} />
              </Route>
              <Route
                path="/uczen"
                element={
                  <StudentRoute>
                    <Dashboard />
                  </StudentRoute>
                }
              />
              <Route
                path="/dyrektor"
                element={
                  <HeadTeacherRoute>
                    <HeadTeacherDashboard />
                  </HeadTeacherRoute>
                }
              >
                <Route index element={<HeadTeacherHome />} />
                <Route path="nauczyciele" element={<HeadTeacherTeachers />} />
              </Route>
              <Route
                path="/test/:id"
                element={
                  <StudentRoute>
                    <SolveTest />
                  </StudentRoute>
                }
              />
              <Route path="*" element={<NotFound />} />
            </Routes>
          </AxiosInterceptor>
        </AuthProvider>
      </QueryClientProvider>
    </HashRouter>
  );
}

export default App;
