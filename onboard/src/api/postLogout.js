import axios from 'axios';
import { getEnvSpecificAPIURI } from './../utils/helperFunctions';

const postLogout = () => {
  return async (dispatch, getState) => {
    try {
      const baseURL = getEnvSpecificAPIURI();
      const url = `${baseURL}/api/logout`;
      console.log('Logging out');
      // const response = await axios.post(
      //   url,
      //   {},
      //   {
      //     withCredentials: true,
      //     headers: {
      //       Accept: 'application/json',
      //       'Content-Type': 'application/json'
      //     }
      //   }
      // );

      // if (response.data.success) {
        console.log('Successfully logged out... wiping REDUX store');
        dispatch({
          type: 'LOGOUT'
        });
      // }
    } catch (error) {
      throw Error(error);
    }
    return getState();
  };
};
export default postLogout;
