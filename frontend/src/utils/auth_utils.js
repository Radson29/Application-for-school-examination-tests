export const roleToPath = (role) => {
  switch (role) {
    case 'ROLE_ADMIN':
      return '/dyrektor';
    case 'ROLE_TEACHER':
      return '/nauczyciel';
    case 'ROLE_STUDENT':
      return '/uczen';
    default: // tu moze sie zapętlić XD
      return '/';
  }
};
