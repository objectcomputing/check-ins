import { startOfQuarter, endOfQuarter } from 'date-fns';

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
export const getQuarterDisplay = date => {
  const quarter = Math.floor((date.getMonth() + 3) / 3);
  return `Q${quarter} ${date.getFullYear()}`;
};
