import { ACTIONS } from './../redux/reducers/login';
import axios from 'axios';
import { bigintToHex, getEnvSpecificAPIURI } from './../utils/helperFunctions';

import { createVerifierAndSalt, SRPParameters, SRPRoutines } from 'tssrp6a';

const postCode = (
  email,
  code
) => {
  return async (dispatch, getState) => {
    try {
      const baseURL = getEnvSpecificAPIURI();
      const url = `${baseURL}/api/auth/activate`;

      const srp6aNimbusRoutines = new SRPRoutines(
        new SRPParameters(SRPParameters.PrimeGroup[512])
      );

      const { s, v } = await createVerifierAndSalt(
        srp6aNimbusRoutines,
        email,
        code
      );

      let convertedSaltBigIntToHex = bigintToHex(s);
      let convertedVerifierBigIntToHex = bigintToHex(v);

      const loginData = {
        emailAddress: email,
        salt: convertedSaltBigIntToHex,
        primaryVerifier: convertedVerifierBigIntToHex
      };

      const response = await axios.post(url, loginData, {
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      });

      console.log('Account Activation succeeded');
      dispatch({
        type: ACTIONS.LOAD_USER,
        payload: {
          email: email,
          accessToken: '',
          status: response?.status,
          expiration: ''
        }
      });

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
export default postCode;
