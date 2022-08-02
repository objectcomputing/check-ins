import { resolve } from "./api.js";

const onboardeeProfileUrl = "/services/onboardee-profiles";

export const updateOnboardee = async (onboardee, cookie) => {
    return resolve({
        method: "put",
        url: onboardeeProfileUrl,
        responseType: "json",
        headers: { "X-CSRF-Header": cookie },
    });
};

export const createOnboardee = async (newOnboardee, cookie) => {
    return resolve({
        method: "post",
        url: onboardeeProfileUrl,
        responseType: "json",
        data: newOnboardee,
        headers: { "X-CSRF-Header": cookie },
    });
};

