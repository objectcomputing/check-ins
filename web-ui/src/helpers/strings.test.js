import { titleCase } from './strings';

describe('strings', () => {
  it('can title-case a string', () => {
    expect(titleCase('')).toBe('');
    expect(titleCase('a')).toEqual('A');
    expect(titleCase('test')).toBe('Test');
    expect(titleCase('one two three')).toBe('One Two Three');
    expect(titleCase('one_two_three')).toBe('One Two Three');
  });
});
