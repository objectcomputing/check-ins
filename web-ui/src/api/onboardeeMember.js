import { resolve } from "./api.js";

const onboardeeInitUrl = "/services/create-onboardee";
const onboardeeProfileUrl = "/services/onboardee-profiles";

export const initializeOnboardee = async (email, cookie) => {
    let initOnboardee = {
        emailAddress: email
    };
    return resolve({
        method: "post",
        url: onboardeeInitUrl,
        responseType: "json",
        data: initOnboardee,
        headers: { "X-CSRF-Header": cookie },
    });
};

export const updateOnboardee = async (onboardee, cookie) => {
    return resolve({
        method: "put",
        url: onboardeeProfileUrl,
        responseType: "json",
        data: onboardee,
        headers: { "X-CSRF-Header": cookie },
    });
};

export const createOnboardee = async (newOnboardee, cookie) => {
    console.log(newOnboardee);
    let initProfile = {
        firstName: newOnboardee.firstName,
        lastName: newOnboardee.lastName,
        personalEmail: newOnboardee?.email,
        currentAddress: "",
        middleName: "",
        phoneNumber: "",
        socialSecurityNumber: ""
    };
    return resolve({
        method: "post",
        url: onboardeeProfileUrl,
        responseType: "json",
        data: initProfile,
        headers: { "X-CSRF-Header": cookie },
    });
};

