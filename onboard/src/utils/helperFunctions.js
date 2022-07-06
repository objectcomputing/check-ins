/**
 * Full check for whether an array actually exists or is empty, etc
 * @param array - an array
 * @returns a boolean
 */

export const isArrayPresent = (array) => Array.isArray(array) && array.length;
