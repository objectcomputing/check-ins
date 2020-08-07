import axios from 'axios';
import { resolve, BASE_API_URL } from './api.js';


export const getMembersByPDL = async(id) => {
    return await resolve(axios({
            method: "get",
            url: `${BASE_API_URL}/member-profile/?pdlId=${id}`,
            responseType: "json",
          }
        )
    );
}