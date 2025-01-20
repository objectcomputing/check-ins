import {
  getCheckinDate,
  getCheckinDateForPeriod,
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

  test('returns epoch when check-in is not provided', () => {
    const result = getCheckinDate(undefined);
    expect(result).toEqual(new Date(0)); // Date at epoch (Jan 1, 1970)
  });

  test('returns epoch when check-in date is not available', () => {
    const checkin = {};
    const result = getCheckinDate(checkin);
    expect(result).toEqual(new Date(0)); // Date at epoch (Jan 1, 1970)
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

describe('getCheckinDateForPeriod', () => {
  const mockReportDateQ1 = new Date(2024, 2, 31); // March 31, 2024 (end of Q1)
  const mockReportDateQ2 = new Date(2024, 5, 30); // June 30, 2024 (end of Q2)

  test('returns correct date when check-in is within Q1 reporting period', () => {
    const checkins = [
      { checkInDate: [2024, 1, 10, 10, 0] }, // January 10, 2024
      { checkInDate: [2024, 2, 20, 9, 0] }, // February 20, 2024
      { checkInDate: [2024, 3, 31, 14, 30] } // March 31, 2024
    ];

    const result = getCheckinDateForPeriod(checkins, mockReportDateQ1);

    expect(result).toBeInstanceOf(Date);
    expect(result.getFullYear()).toBe(2024);
    expect(result.getMonth()).toBe(2); // March is 2 (zero-indexed)
    expect(result.getDate()).toBe(31); // Latest date in the array
    expect(result.getHours()).toBe(14);
    expect(result.getMinutes()).toBe(30);
  });

  test('returns correct date when check-in is within Q2 reporting period and grace period', () => {
    const checkins = [
      { checkInDate: [2024, 4, 10, 10, 0] }, // April 10, 2024
      { checkInDate: [2024, 5, 20, 9, 0] }, // May 20, 2024
      { checkInDate: [2024, 6, 30, 14, 30] } // June 30, 2024
    ];

    const result = getCheckinDateForPeriod(checkins, mockReportDateQ2);

    expect(result).toBeInstanceOf(Date);
    expect(result.getFullYear()).toBe(2024);
    expect(result.getMonth()).toBe(5); // June is 5 (zero-indexed)
    expect(result.getDate()).toBe(30); // Latest date in the array
    expect(result.getHours()).toBe(14);
    expect(result.getMinutes()).toBe(30);
  });

  test('returns correct date regardless of checkin status (completed or not) for Q1', () => {
    const checkins = [
      { checkInDate: [2024, 1, 10, 10, 0], completed: true }, // January 10, 2024 (completed)
      { checkInDate: [2024, 2, 20, 9, 0], completed: false }, // February 20, 2024 (not completed)
      { checkInDate: [2024, 3, 31, 14, 30], completed: false } // March 31, 2024 (not completed)
    ];

    const result = getCheckinDateForPeriod(checkins, mockReportDateQ1);

    expect(result).toBeInstanceOf(Date);
    expect(result.getFullYear()).toBe(2024);
    expect(result.getMonth()).toBe(2); // March is 2 (zero-indexed)
    expect(result.getDate()).toBe(31); // Latest date in the array
    expect(result.getHours()).toBe(14);
    expect(result.getMinutes()).toBe(30);
  });

  test('returns correct date regardless of checkin status (completed or not) for Q2', () => {
    const checkins = [
      { checkInDate: [2024, 4, 10, 10, 0], completed: true }, // April 10, 2024 (completed)
      { checkInDate: [2024, 5, 20, 9, 0], completed: false }, // May 20, 2024 (not completed)
      { checkInDate: [2024, 6, 30, 14, 30], completed: false } // June 30, 2024 (not completed)
    ];

    const result = getCheckinDateForPeriod(checkins, mockReportDateQ2);

    expect(result).toBeInstanceOf(Date);
    expect(result.getFullYear()).toBe(2024);
    expect(result.getMonth()).toBe(5); // June is 5 (zero-indexed)
    expect(result.getDate()).toBe(30); // Latest date in the array
    expect(result.getHours()).toBe(14);
    expect(result.getMinutes()).toBe(30);
  });

  test('returns correct date when check-ins are on Feb 12 and May 1 with a report date in Q1', () => {
    const checkins = [
      { checkInDate: [2024, 2, 12, 10, 0] }, // February 12, 2024
      { checkInDate: [2024, 5, 1, 9, 0] } // May 1, 2024
    ];

    const mockReportDate = new Date(2024, 2, 15); // March 15, 2024 (within Q1)
    const result = getCheckinDateForPeriod(checkins, mockReportDate);

    expect(result).toBeInstanceOf(Date);
    expect(result.getFullYear()).toBe(2024);
    expect(result.getMonth()).toBe(1); // February is 2 (zero-indexed)
    expect(result.getDate()).toBe(12); // Latest date in the array within Q1
    expect(result.getHours()).toBe(10);
    expect(result.getMinutes()).toBe(0);
  });
});

describe('statusForPeriodByMemberScheduling', () => {
  const mockReportDate = new Date(2024, 3, 15); // April 15, 2024 (example report date)

  test('returns "Not Scheduled" when no check-ins are provided', () => {
    const result = statusForPeriodByMemberScheduling(null, mockReportDate);
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

  test('returns "Scheduled" when the check-in is within the reporting period are not completed', () => {
    const checkin =
      { checkInDate: [2024, 4, 10, 10, 0], completed: false }; // April 1, 2024 (within reporting period, not completed)
    const result = statusForPeriodByMemberScheduling(checkin, mockReportDate);
    expect(result).toBe('Scheduled');
  });
});
