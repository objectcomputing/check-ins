/**
 * Full check for whether an array actually exists or is empty, etc
 * @param arr - an array
 * @returns a boolean
 */

export const isArrayPresent = (arr) => Array.isArray(arr) && arr.length;

/**
 * Check for whether unique object is already in an array and return a boolean.
 * @param arr - an array
 * @returns a boolean
 */
export const isObjectInArray = (arr, obj) => {
  return arr.includes(obj);
};

/**
 * If a parameter is found in an object within an array, return the array with just that object.
 * @param arr - an array
 * @param value - a value
 * @param key - an optional key with which to search
 * @returns an array
 */

export function filterObjectByValOrKey(arr, value, key) {
  return arr.filter(
    key
      ? (a) => a[key].indexOf(value) > -1
      : (a) => Object.keys(a).some((k) => a[k] === value)
  );
}
