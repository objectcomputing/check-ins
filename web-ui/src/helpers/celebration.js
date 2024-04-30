/**
 * Converts a string birthday from "4/23" to "April 23rd"
 * @param {string} dateStr
 * @returns string representing a Month ##{st|nd|rd|th}
 */
export const formatBirthday = dateStr => {
  const months = [
    'January',
    'February',
    'March',
    'April',
    'May',
    'June',
    'July',
    'August',
    'September',
    'October',
    'November',
    'December'
  ];
  const [month, day] = dateStr.split('/').map(Number);
  const monthName = months[month - 1];

  let suffix = 'th';
  if (day % 10 === 1 && day !== 11) {
    suffix = 'st';
  } else if (day % 10 === 2 && day !== 12) {
    suffix = 'nd';
  } else if (day % 10 === 3 && day !== 13) {
    suffix = 'rd';
  }

  return `${monthName} ${day}${suffix}`;
};
