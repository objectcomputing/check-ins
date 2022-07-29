import { ACTIONS } from 'redux/reducers/login';
import axios from 'axios';
import { SRPClientSession, SRPParameters, SRPRoutines } from 'tssrp6a';
import { bigintToHex, getEnvSpecificAPIURI } from './../utils/helperFunctions';

const postUser = (email, workspace, secret) => {
  return async (dispatch, getState) => {

    try {
      const baseURL = getEnvSpecificAPIURI();
      const url = `${baseURL}/api/challenge`;
      const loginData = { scope: workspace, identity: email };

      const response = await axios
        .post(url, loginData, {
          withCredentials: true,
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
          }
        });

      const url2 = `${baseURL}/api/authenticate`;

      const srp6aNimbusRoutines = new SRPRoutines(
        new SRPParameters(SRPParameters.PrimeGroup[512])
      );

      const srp6aClient = await new SRPClientSession(
        srp6aNimbusRoutines
      ).step1(email, secret);
      secret = '';

      let salt = response.data.salt;
      let b = response.data.b;

      const srp6aClient_step2 = await srp6aClient.step2(
        BigInt('0x' + salt),
        BigInt('0x' + b)
      );

      let hexedM1 = bigintToHex(srp6aClient_step2.M1);
      let hexedA = bigintToHex(srp6aClient_step2.A);
      let encodedSecret = hexedM1 + ':' + hexedA;

      const loginData2 = {
        identity: email,
        secret: encodedSecret
      };

      const response2 = await axios
        .post(url2, loginData2, {
          withCredentials: true,
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
          }
        });
      console.log('Login succeeded');
      dispatch({
        type: ACTIONS.LOAD_USER,
        payload: {
          email: email,
          firstName: '',
          lastName: '',
          accessToken: response2?.data?.token,
          status: response2?.status,
          expiration: response2?.data?.expirationTime
        }
      });
      document.body.style.cursor = 'default';
    } catch (error) {
      dispatch({
        type: ACTIONS.INVALID_LOAD,
        payload: { status: 'error' }
      });
    }
    return getState();
  };
};
export default postUser;
