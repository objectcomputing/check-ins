export const getEnvSpecificAPIURI = () => {
  // Check whether the app is in local / develop mode:
  let isDevelopment = process.env.NODE_ENV === "development";

  // This references the localhost port or the public url as the endpoint for the Micronaut API
  const url = `${
    isDevelopment
      ? process.env.REACT_APP_DEV_PLATFORM_URI
      : process.env.PUBLIC_URL
  }`;

  return url;
};

/**
 * Full check for whether an array actually exists or is empty, etc
 * @param array - an array
 * @returns a boolean
 */

export const isArrayPresent = (array) => (Array.isArray(array) && array.length) ? true : false;

/**
 * Converts a non-negative bigint to a hexadecimal string
 * @param a - a non negative bigint
 * @returns hexadecimal representation of the input bigint
 *
 * @throws {RangeError}
 * Thrown if a < 0
 */
export const bigintToHex = (a) => {
  if (a < 0) {
    throw RangeError(
      "Value should be a non-negative integer. Negative values are not supported."
    );
  }
  let hex = BigInt(a).toString(16);
  if (hex.length % 2) {
    hex = "0" + hex;
  }
  return hex;
};
