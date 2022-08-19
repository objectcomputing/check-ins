import axios from 'axios';
import { ACTIONS } from './../redux/reducers/login';
import { loginHelper } from './../utils/loginHelper';
import postLogout from './postLogout';
import { getEnvSpecificAPIURI } from './../utils/helperFunctions';

const fetchToken = () => {
  return async (dispatch, getState) => {
    try {
      const baseURL = getEnvSpecificAPIURI();
      const url = `${baseURL}/api/token/interpreter`;

      console.log('Fetching token if session already exists...');
      const response = await axios.post(url, {}, {
        withCredentials: true,
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }});

      console.log(response);

      let decodedJWT = loginHelper(response?.data?.token);
      dispatch({
        type: ACTIONS.LOAD_USER,
        payload: {
          email: '',
          firstName: '',
          lastName: '',
          accessToken: response?.data?.token,
          status: decodedJWT.status,
          expiration: response?.data.expirationTime
        }
      });
    } catch (error) {
      console.error(error);
      console.log('Logging user out because session not present');
      dispatch(postLogout());
    }
    return getState();
  };
};
export default fetchToken;
