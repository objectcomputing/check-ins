import { JOBHISTORY_ACTIONS } from './../redux/reducers/jobhistory';
import { getEnvSpecificAPIURI } from './../utils/helperFunctions';
import axios from 'axios';
import fetchJobHistory from './fetchJobHistory';

const postJobHistory = (jobHistoryData, accessToken) => {
    return async (dispatch, getState) => {
        try {
            const baseURL = getEnvSpecificAPIURI();
            const url = `${baseURL}/api/jobhistory`;

            const postData = jobHistoryData;

            console.log("Create job history sections...");
            const response = await axios.post(url, postData, {
                withCredentials: true,
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${accessToken}`
                }
            });
            console.log(response);

            console.log('Job history sections creation succeeded');

            if (response.success) {
                dispatch(fetchJobHistory(accessToken));
            }

        } catch (error) {
            console.error(error);
            dispatch({
                type: JOBHISTORY_ACTIONS.INVALID_JOBHISTORY_LOAD,
                payload: { status: 'error' }
            });
        }
        return getState();
    };
};
export default postJobHistory; 