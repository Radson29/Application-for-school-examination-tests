export const scoreToPercentage = (score, max) => {
  let scoreNum = Number(score);
  let maxNum = Number(max);

  return Math.round((scoreNum / maxNum) * 100);
};

export const percentageToGrade = (percentages) => {
  if (percentages >= 90 && percentages <= 100) {
    return '5';
  }
  if (percentages >= 70) {
    return '4';
  }
  if (percentages >= 50) {
    return '3';
  }
  if (percentages >= 30) {
    return '2';
  }
  if (percentages < 30 && percentages >= 0) {
    return '1';
  }
};

export const scoreToText = (percentages) => {
  if (percentages >= 90 && percentages <= 100) {
    return 'Bardzo Dobry';
  }
  if (percentages >= 70) {
    return 'Dobry';
  }
  if (percentages >= 50) {
    return 'Dostateczny';
  }
  if (percentages >= 30) {
    return 'Dopuszczający';
  }
  if (percentages < 30 && percentages >= 0) {
    return 'Niedostateczny';
  }
};

export const scoreToTailwindClass = (percentages) => {
  if (percentages >= 90 && percentages <= 100) {
    return 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-300';
  } else if (percentages >= 70) {
    return 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-300';
  } else if (percentages >= 50) {
    return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-300';
  } else if (percentages >= 30) {
    return 'bg-violet-100 text-violet-800 dark:bg-violet-900 dark:text-violet-300';
  } else if (percentages < 30 && percentages >= 0) {
    return 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300';
  } else {
    return 'bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-300';
  }
};

export const gradeToText = (grade) => {
  let gradeNumber = Number(grade);

  if (gradeNumber === 5) {
    return 'Bardzo Dobry';
  } else if (gradeNumber === 4) {
    return 'Dobry';
  } else if (gradeNumber === 3) {
    return 'Dostateczny';
  } else if (gradeNumber === 2) {
    return 'Dopuszczający';
  } else if (gradeNumber === 1) {
    return 'Niedostateczny';
  } else {
    return 'Brak oceny';
  }
};

export const gradeToTailwindClass = (grade) => {
  let gradeNumber = Number(grade);

  if (gradeNumber === 5) {
    return 'bg-green-500';
  } else if (gradeNumber === 4) {
    return 'bg-blue-500';
  } else if (gradeNumber === 3) {
    return 'bg-yellow-500';
  } else if (gradeNumber === 2) {
    return 'bg-violet-500';
  } else if (gradeNumber === 1) {
    return 'bg-red-500';
  } else {
    return 'bg-gray-500';
  }
};
