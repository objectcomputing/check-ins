export const BASE_API_URL = process.env.CHECKINS_API_URL;

export const resolve = async (promise) => {
  const resolved = {
    payload: null,
    error: null,
  };

  try {
    resolved.payload = await promise;
  } catch (e) {
    resolved.error = e;
  }

  return resolved;
};
