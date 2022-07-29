import { ACTIONS } from 'redux/reducers/login';
import axios from 'axios';
import postUser from './postUser';
import { bigintToHex, getEnvSpecificAPIURI } from './../utils/helperFunctions';

import { createVerifierAndSalt, SRPParameters, SRPRoutines } from 'tssrp6a';

const registerUser = (
  email,
  password,
  firstName,
  lastName,
) => {
  return async (dispatch, getState) => {
    try {
      const baseURL = getEnvSpecificAPIURI();
      const url = `${baseURL}/api/administration/organization/${workspace}/addMember`;

      const srp6aNimbusRoutines = new SRPRoutines(
        new SRPParameters(SRPParameters.PrimeGroup[512])
      );

      const { s, v } = await createVerifierAndSalt(
        srp6aNimbusRoutines,
        email,
        password
      );

      let convertedSaltBigIntToHex = bigintToHex(s);
      let convertedVerifierBigIntToHex = bigintToHex(v);

      const loginData = {
        emailAddress: email,
        firstName: firstName,
        lastName: lastName,
        salt: convertedSaltBigIntToHex,
        primaryVerifier: convertedVerifierBigIntToHex
      };

      await axios.post(url, loginData, {
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      });

      console.log('Registration succeeded');
      dispatch(postUser(email, workspace, password));
    } catch (error) {
      console.error(error);
      dispatch({
        type: ACTIONS.INVALID_LOAD,
        payload: { status: 'error' }
      });
    }
    return getState();
  };
};
export default registerUser;
