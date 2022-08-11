import { EDUCATION_ACTIONS } from './../redux/reducers/education';
import { getEnvSpecificAPIURI } from './../utils/helperFunctions';
import axios from 'axios';

const fetchEducation = (accessToken) => {
    return async (dispatch, getState) => {
        try {
            const baseURL = getEnvSpecificAPIURI();
            const url = `${baseURL}/api/education`;

            console.log("Pulling all education sections...");
            const response = await axios.get(url, {
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${accessToken}`
                }
            });

            console.log(response);

            console.log('Sending all education sections to store...');
            dispatch({
                type: EDUCATION_ACTIONS.LOAD_EDUCATION,
                payload: response.data
            });
        } catch (error) {
            console.error(error);
            dispatch({
                type: EDUCATION_ACTIONS.INVALID_EDUCATION_LOAD,
                payload: { status: 'error' }
            });
        }
        return getState();
    };
};
export default fetchEducation;