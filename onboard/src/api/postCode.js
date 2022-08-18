import { ACTIONS } from './../redux/reducers/login';
import axios from 'axios';
import { SRPClientSession, SRPParameters, SRPRoutines } from 'tssrp6a';
import { bigintToHex, getEnvSpecificAPIURI } from './../utils/helperFunctions';

const postCode = (email, code) => {
  return async (dispatch, getState) => {

    try {
      const baseURL = getEnvSpecificAPIURI();
      const url = `${baseURL}/api/auth/activate/challenge`;
      const loginData = { emailAddress: email };

      const response = await axios
        .post(url, loginData, {
          withCredentials: true,
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
          }
        });

      const url2 = `${baseURL}/api/auth/activate`;

      const srp6aNimbusRoutines = new SRPRoutines(
        new SRPParameters(SRPParameters.PrimeGroup[512])
      );

      const srp6aClient = await new SRPClientSession(
        srp6aNimbusRoutines
      ).step1(email, code);
      code = '';

      let salt = response.data.salt;
      let b = response.data.b;

      const srp6aClient_step2 = await srp6aClient.step2(
        BigInt('0x' + salt),
        BigInt('0x' + b)
      );

      let hexedM1 = bigintToHex(srp6aClient_step2.M1);
      let hexedA = bigintToHex(srp6aClient_step2.A);
      let encodedCode = hexedM1 + ':' + hexedA;

      const loginData2 = {
        emailAddress: email,
        secret: encodedCode
      };

      const response2 = await axios
        .post(url2, loginData2, {
          withCredentials: true,
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
          }
        });
      console.log('Code activation succeeded');
      console.log(response2);
      dispatch({
        type: ACTIONS.LOAD_USER,
        payload: {
          email: '',
          accessToken: '',
          status: "success",
          expiration: ''
        }
      });
      document.body.style.cursor = 'default';

      // TODO: add an option for expired auth code and set status to 'expired'.
    } catch (error) {
      dispatch({
        type: ACTIONS.INVALID_LOAD,
        payload: { status: 'error' }
      });
    }
    return getState();
  };
};
export default postCode;
