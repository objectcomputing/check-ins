import { EDUCATION_ACTIONS } from './../redux/reducers/education';
import { getEnvSpecificAPIURI } from './../utils/helperFunctions';
import axios from 'axios';
import fetchEducation from './fetchEducation';

const postEducation = (educationData, accessToken) => {
    return async (dispatch, getState) => {
        try {
            const baseURL = getEnvSpecificAPIURI();
            const url = `${baseURL}/api/education`;

            const postData = educationData;

            console.log("Create education sections...");
            const response = await axios.post(url, postData, {
                withCredentials: true,
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${accessToken}`
                }
            });
            console.log(response);

            console.log('Education sections creation succeeded');

            if (response.success) {
                dispatch(fetchEducation(accessToken));
            }

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
export default postEducation;