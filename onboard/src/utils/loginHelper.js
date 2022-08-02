import jwt_decode from 'jwt-decode';

// Decode the JWT access token
export const loginHelper = (jwt) => {
  var decoded = jwt_decode(jwt);

  return decoded;
};
