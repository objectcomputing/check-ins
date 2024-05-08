import { getQuarterBeginEnd } from '../../../helpers';

/**
 * @typedef {("Not Scheduled" | "Scheduled" | "Completed")} SchedulingStatus
 */

/**
 * Get the date of a check-in.
 * @param {Checkin} checkin - A check-in.
 * @returns {Date} The date of the check-in.
 */
export const getCheckinDate = checkin => {
  if (!checkin || !checkin.checkInDate) return;
  const [year, month, day, hour, minute] = checkin.checkInDate;
  return new Date(year, month - 1, day, hour, minute, 0);
};

/**
 * Get the date of the last check-in.
 * @param {Checkin[]} checkins - Check-ins for a member.
 * @returns {Date} The date of the last check-in or epoch if no check-ins.
 */
export const getLastCheckinDate = checkins => {
  if (checkins.length === 0) return new Date(0);
  return checkins.reduce((acc, checkin) => {
    const checkinDate = getCheckinDate(checkin);
    return checkinDate > acc ? checkinDate : acc;
  }, new Date(0));
};

/**
 * Determine check-in status for a member during the reporting period.
 * @param {Checkin[]} checkins - Check-ins for a member.
 * @param {Date} reportDate - The date of the report.
 * @returns {SchedulingStatus} The status of the check-ins.
 */
export const statusForPeriodByMemberScheduling = (
  checkins = [],
  reportDate
) => {
  if (checkins.length === 0) return 'Not Scheduled';
  const { startOfQuarter, endOfQuarter } = getQuarterBeginEnd(reportDate);
  const scheduled = checkins.filter(checkin => {
    const checkinDate = getCheckinDate(checkin);
    return checkinDate >= startOfQuarter && checkinDate <= endOfQuarter;
  });
  if (scheduled.length === 0) return 'Not Scheduled';
  const completed = scheduled.filter(checkin => checkin.completed);
  if (completed.length === scheduled.length) return 'Completed';
  return 'Scheduled';
};
