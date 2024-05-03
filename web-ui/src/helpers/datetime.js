import { startOfQuarter, endOfQuarter } from 'date-fns';

/**
 * Returns the start and end dates of the quarter that the given date falls in.
 * @param {Date} inputDate The date to get the quarter duration for.
 * @returns {{ startOfQuarter: Date, endOfQuarter: Date }} The start and end dates of the quarter.
 */
export const getQuarterDuration = inputDate => ({
  startOfQuarter: startOfQuarter(inputDate),
  endOfQuarter: endOfQuarter(inputDate)
});
