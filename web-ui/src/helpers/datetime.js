import { endOfQuarter, format, getQuarter, startOfQuarter } from 'date-fns';

/**
 * Formats a date that can be a JavaScriopt Date object or
 * a date from the DatePicker in @mui/x-date-pickers/DatePicker.
 */
export const formatDate = date => {
  if (!date) return '';
  if (date instanceof Date) return format(date, 'yyyy-MM-dd');
  const paddedMonth = (date.$M + 1).toString().padStart(2, '0');
  const paddedYear = date.$D.toString().padStart(2, '0');
  return `${date.$y}-${paddedMonth}-${paddedYear}`;
};

/**
 * Returns the start and end dates of the quarter that the given date falls in.
 * @param {Date} inputDate The date to get the quarter duration for.
 * @returns {{ startOfQuarter: Date, endOfQuarter: Date }} The start and end dates of the quarter.
 */
export const getQuarterBeginEnd = inputDate => ({
  startOfQuarter: startOfQuarter(inputDate),
  endOfQuarter: endOfQuarter(inputDate)
});

/**
 * Return the current quarter number with year.
 * @param {Date} date - The date to get the quarter number for.
 * @returns {string} The quarter number with year.
 */
export const getQuarterDisplay = date =>
  `Q${getQuarter(date)} ${date.getFullYear()}`;
