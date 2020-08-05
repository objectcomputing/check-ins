export const BASE_API_URL = process.env.CHECKINS_API_URL ? process.env.CHECKINS_API_URL : "http://localhost:8080"

export const resolve = async(promise) => {
    const resolved = {
      payload: null,
      data: null,
      error: null
    };
  
    try {
      resolved.payload = await promise;
      resolved.data = resolved.payload.data;
    } catch(e) {
      resolved.error = e;
    }
  
    return resolved;
  }