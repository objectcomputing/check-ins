import { JOBHISTORY_ACTIONS } from './../redux/reducers/jobhistory';
import { getEnvSpecificAPIURI } from './../utils/helperFunctions';
import axios from 'axios';

const fetchJobHistory = (accessToken) => {
    return async (dispatch, getState) => {
        try {
            const baseURL = getEnvSpecificAPIURI();
            const url = `${baseURL}/api/jobhistory`;

            console.log("Pulling all job history sections...");
            const response = await axios.get(url, {
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${accessToken}`
                }
            });

            console.log(response);

            console.log('Sending all job history sections to store...');
            dispatch({
                type: JOBHISTORY_ACTIONS.LOAD_JOBHISTORY,
                payload: response.data
            });
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
export default fetchJobHistory; 