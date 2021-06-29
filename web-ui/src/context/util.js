export const levelList = [
  "NONE",
  "INTERESTED",
  "NOVICE",
  "INTERMEDIATE",
  "ADVANCED",
  "EXPERT",
];

export const levelMap = levelList.reduce((acc, level, index) => {
  acc[level] = index;
  return acc;
}, {});
