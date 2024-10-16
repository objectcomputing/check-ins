import { format } from 'date-fns';
import { getQuarterBeginEnd, getQuarterDisplay } from './datetime';

describe('getQuarterBeginEnd', () => {
  it('returns the start and end dates of the current quarter', () => {
    const inputDate = new Date('2024-04-15');
    const expectedStart = '2024-04-01';
    const expectedEnd = '2024-06-30';

    const { startOfQuarter, endOfQuarter } = getQuarterBeginEnd(inputDate);

    expect(format(startOfQuarter, 'yyyy-MM-dd')).toBe(expectedStart);
    expect(format(endOfQuarter, 'yyyy-MM-dd')).toBe(expectedEnd);
  });

  it('returns the start and end dates of the first quarter', () => {
    const inputDate = new Date('2024-01-15');
    const expectedStart = '2024-01-01';
    const expectedEnd = '2024-03-31';

    const { startOfQuarter, endOfQuarter } = getQuarterBeginEnd(inputDate);

    expect(format(startOfQuarter, 'yyyy-MM-dd')).toBe(expectedStart);
    expect(format(endOfQuarter, 'yyyy-MM-dd')).toBe(expectedEnd);
  });

  it('returns the start and end dates of the fourth quarter', () => {
    const inputDate = new Date('2023-10-20');
    const expectedStart = '2023-10-01';
    const expectedEnd = '2023-12-31';

    const { startOfQuarter, endOfQuarter } = getQuarterBeginEnd(inputDate);

    expect(format(startOfQuarter, 'yyyy-MM-dd')).toBe(expectedStart);
    expect(format(endOfQuarter, 'yyyy-MM-dd')).toBe(expectedEnd);
  });

  it('returns the start and end dates of the first quarter in a leap year', () => {
    const inputDate = new Date('2020-02-15');
    const expectedStart = '2020-01-01';
    const expectedEnd = '2020-03-31';

    const { startOfQuarter, endOfQuarter } = getQuarterBeginEnd(inputDate);

    expect(format(startOfQuarter, 'yyyy-MM-dd')).toBe(expectedStart);
    expect(format(endOfQuarter, 'yyyy-MM-dd')).toBe(expectedEnd);
  });

  it('returns the start and end dates of the second quarter in a non-leap year', () => {
    const inputDate = new Date('2021-05-20'); // Non-leap year (2021)
    const expectedStart = '2021-04-01';
    const expectedEnd = '2021-06-30';

    const { startOfQuarter, endOfQuarter } = getQuarterBeginEnd(inputDate);

    expect(format(startOfQuarter, 'yyyy-MM-dd')).toBe(expectedStart);
    expect(format(endOfQuarter, 'yyyy-MM-dd')).toBe(expectedEnd);
  });

  it('returns the start and end dates of the first quarter in a different time zone', () => {
    const inputDate = new Date('2024-01-15T12:00:00-05:00'); // Eastern Standard Time (UTC-5)
    const expectedStart = '2024-01-01';
    const expectedEnd = '2024-03-31';

    const { startOfQuarter, endOfQuarter } = getQuarterBeginEnd(inputDate);

    expect(format(startOfQuarter, 'yyyy-MM-dd')).toBe(expectedStart);
    expect(format(endOfQuarter, 'yyyy-MM-dd')).toBe(expectedEnd);
  });
});

describe('getQuarterDisplay', () => {
  it('returns the current quarter number with year', () => {
    const date = new Date('2024-04-15');
    const expected = 'Q2 2024';

    const result = getQuarterDisplay(date);

    expect(result).toBe(expected);
  });

  it('returns the first quarter number with year', () => {
    const date = new Date('2024-01-15');
    const expected = 'Q1 2024';

    const result = getQuarterDisplay(date);

    expect(result).toBe(expected);
  });

  it('returns the fourth quarter number with year', () => {
    const date = new Date('2023-10-20');
    const expected = 'Q4 2023';

    const result = getQuarterDisplay(date);

    expect(result).toBe(expected);
  });

  it('returns the first quarter number with year in a leap year', () => {
    const date = new Date('2020-02-15');
    const expected = 'Q1 2020';

    const result = getQuarterDisplay(date);

    expect(result).toBe(expected);
  });

  it('returns the second quarter number with year in a non-leap year', () => {
    const date = new Date('2021-05-20'); // Non-leap year (2021)
    const expected = 'Q2 2021';

    const result = getQuarterDisplay(date);

    expect(result).toBe(expected);
  });

  it('returns the first quarter number with year in a different time zone', () => {
    const date = new Date('2024-01-15T12:00:00-05:00'); // Eastern Standard Time (UTC-5)
    const expected = 'Q1 2024';

    const result = getQuarterDisplay(date);

    expect(result).toBe(expected);
  });
});
