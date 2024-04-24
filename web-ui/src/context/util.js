import confetti from 'canvas-confetti';

export const levelList = [
  'NONE',
  'INTERESTED',
  'NOVICE',
  'INTERMEDIATE',
  'ADVANCED',
  'EXPERT'
];

export const levelMap = levelList.reduce((acc, level, index) => {
  acc[level] = index;
  return acc;
}, {});

export const sortAnniversaries = anniversaryData => {
  return anniversaryData.sort((a, b) => b.yearsOfService - a.yearsOfService);
};

export const sortBirthdays = birthdayData => {
  return birthdayData.sort(
    // This will change the date string to a int.
    // The first 1-2 numbers will be the month and the last 2 numbers will be the day.
    // For example "11/25" will be 1125 or "5/9" will be 509
    // This will ensure proper sorting.
    (a, b) =>
      Number(b.birthDay.substring(0, b.birthDay.indexOf('/')) * 100) +
      Number(b.birthDay.substring(b.birthDay.indexOf('/'), b.birthDay.length)) -
      (Number(a.birthDay.substring(0, a.birthDay.indexOf('/')) * 100) +
        Number(
          a.birthDay.substring(a.birthDay.indexOf('/'), a.birthDay.length)
        ))
  );
};

export const randomConfetti = (y, x) => {
  confetti({
    angle: Math.floor(Math.random() * (125 - 55) + 55),
    spread: Math.floor(Math.random() * (75, 50) + 50),
    particleCount: Math.floor(Math.random() * (100, 50) + 50),
    origin: { y: y, x: x }
  });
};
