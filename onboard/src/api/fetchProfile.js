import axios from 'axios';
import { PROFILE_ACTIONS } from '../redux/reducers/profile';
import { getEnvSpecificAPIURI } from './../utils/helperFunctions';

const fetchProfile = (accessToken) => {
  return async (dispatch, getState) => {
    try {
      const baseURL = getEnvSpecificAPIURI();
      const url = `${baseURL}/api/account/profile`;

      console.log('Pulling account profile...');
      const response = await axios.get(url, {
        headers: {
          Accept: '*/*',
          'Content-Type': 'application/json',
          Authorization: `Bearer ${accessToken}`
        }
      });

      console.log(response.data);
      dispatch({
        type: PROFILE_ACTIONS.LOAD_PROFILE,
        payload: response.data
      });
    } catch (error) {
      console.error(error);
    }
    return getState();
  };
};
export default fetchProfile;
