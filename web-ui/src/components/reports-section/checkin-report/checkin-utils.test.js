import {
  getCheckinDate,
  getLastCheckinDate,
  statusForPeriodByMemberScheduling
} from './checkin-utils';

describe('getCheckinDate', () => {
  test('returns correct date when check-in is provided', () => {
    const checkin = {
      checkInDate: [2024, 4, 9, 10, 30] // [year, month, day, hour, minute]
    };

    const result = getCheckinDate(checkin);

    expect(result).toBeInstanceOf(Date);
    expect(result.getFullYear()).toBe(2024);
    expect(result.getMonth()).toBe(3); // Months are zero-indexed (April is 3)
    expect(result.getDate()).toBe(9);
    expect(result.getHours()).toBe(10);
    expect(result.getMinutes()).toBe(30);
  });

  test('returns undefined when check-in is not provided', () => {
    const result = getCheckinDate(undefined);
    expect(result).toBeUndefined();
  });

  test('returns undefined when check-in date is not available', () => {
    const checkin = {};
    const result = getCheckinDate(checkin);
    expect(result).toBeUndefined();
  });
});

describe('getLastCheckinDate', () => {
  test('returns correct last check-in date from multiple check-ins', () => {
    const checkins = [
      { checkInDate: [2024, 4, 9, 10, 30] },
      { checkInDate: [2024, 4, 10, 11, 0] },
      { checkInDate: [2024, 4, 8, 9, 45] }
    ];

    const result = getLastCheckinDate(checkins);

    expect(result).toBeInstanceOf(Date);
    expect(result.getFullYear()).toBe(2024);
    expect(result.getMonth()).toBe(3); // April is 3 (zero-indexed)
    expect(result.getDate()).toBe(10); // Latest date in the array
    expect(result.getHours()).toBe(11);
    expect(result.getMinutes()).toBe(0);
  });

  test('returns default date (epoch) when no check-ins provided', () => {
    const checkins = [];
    const result = getLastCheckinDate(checkins);
    expect(result).toEqual(new Date(0)); // Date at epoch (Jan 1, 1970)
  });

  test('returns default date (epoch) when check-ins array is empty', () => {
    const checkins = [];
    const result = getLastCheckinDate(checkins);
    expect(result).toEqual(new Date(0)); // Date at epoch (Jan 1, 1970)
  });
});

describe('statusForPeriodByMemberScheduling', () => {
  const mockReportDate = new Date(2024, 3, 15); // April 15, 2024 (example report date)

  test('returns "Not Scheduled" when no check-ins are provided', () => {
    const result = statusForPeriodByMemberScheduling([], mockReportDate);
    expect(result).toBe('Not Scheduled');
  });

  test('returns "Not Scheduled" when all check-ins are outside the reporting grace period', () => {
    const checkins = [
      { checkInDate: [2024, 2, 1, 10, 30], completed: false }, // Feb 1, 2024
      { checkInDate: [2024, 3, 31, 9, 0], completed: false }, // March 31, 2024
      { checkInDate: [2024, 8, 2, 11, 15], completed: false } // Aug 2, 2024
    ];
    const result = statusForPeriodByMemberScheduling(checkins, mockReportDate);
    expect(result).toBe('Not Scheduled');
  });

  // There is a grace period of one month after the quarter in which we consider a check-in "In Progress"
  test('returns "Scheduled" when at least one check-in falls within the reporting grace period', () => {
    const checkins = [
      { checkInDate: [2024, 2, 1, 10, 30], completed: false }, // Feb 1, 2024
      { checkInDate: [2024, 3, 31, 9, 0], completed: false }, // March 31, 2024
      { checkInDate: [2024, 7, 10, 14, 0], completed: false } // Jul 10, 2024
    ];
    const result = statusForPeriodByMemberScheduling(checkins, mockReportDate);
    expect(result).toBe('Scheduled');
  });

  test('returns "Scheduled" when some check-ins within the reporting period are completed', () => {
    const checkins = [
      { checkInDate: [2024, 3, 1, 10, 0], completed: true }, // March 1, 2024 (within reporting period, completed)
      { checkInDate: [2024, 4, 1, 9, 0], completed: false }, // April 1, 2024 (within reporting period, not completed)
      { checkInDate: [2024, 4, 15, 14, 30], completed: false } // April 15, 2024 (within reporting period, not completed)
    ];
    const result = statusForPeriodByMemberScheduling(checkins, mockReportDate);
    expect(result).toBe('Scheduled');
  });

  test('returns "Scheduled" when all check-ins within the reporting period are not completed', () => {
    const checkins = [
      { checkInDate: [2024, 3, 1, 10, 0], completed: false }, // March 1, 2024 (within reporting period, not completed)
      { checkInDate: [2024, 4, 1, 9, 0], completed: false }, // April 1, 2024 (within reporting period, not completed)
      { checkInDate: [2024, 4, 15, 14, 30], completed: false } // April 15, 2024 (within reporting period, not completed)
    ];
    const result = statusForPeriodByMemberScheduling(checkins, mockReportDate);
    expect(result).toBe('Scheduled');
  });
});
