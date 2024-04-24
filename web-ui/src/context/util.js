import confetti from 'canvas-confetti';
import { getDayOfYear } from 'date-fns';

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

function monthDayToDayOfYear(monthDayString) {
  const [month, day] = monthDayString.split('/');
  const now = new Date();
  const date = new Date(now.getFullYear(), Number(month) - 1, Number(day));
  return getDayOfYear(date);
}

export const sortBirthdays = birthdayData => {
  return birthdayData.sort((a, b) => {
    const aDayOfYear = monthDayToDayOfYear(a.birthDay);
    const bDayOfYear = monthDayToDayOfYear(b.birthDay);
    return aDayOfYear - bDayOfYear;
  });
};

export const randomConfetti = (y, x) => {
  confetti({
    angle: Math.floor(Math.random() * (125 - 55) + 55),
    spread: Math.floor(Math.random() * (75, 50) + 50),
    particleCount: Math.floor(Math.random() * (100, 50) + 50),
    origin: { y: y, x: x }
  });
};
