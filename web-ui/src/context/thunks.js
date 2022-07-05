import {
  getCheckinByMemberId,
  getAllCheckins,
  createCheckin,
} from "../api/checkins";
import { UPDATE_CHECKINS, ADD_CHECKIN } from "./actions";

export const getCheckins = async (id, pdlId, dispatch, csrf) => {
  const res = await getCheckinByMemberId(id, csrf);
  let data =
    res.payload && res.payload.data && res.payload.status === 200 && !res.error
      ? res.payload.data
      : null;

  //without this check you get infinite checkin calls
  if (data && data.length > 0) {
    dispatch({ type: UPDATE_CHECKINS, payload: data });
  }
};

export const getAllCheckinsForAdmin = async (dispatch, csrf) => {
  const res = await getAllCheckins(csrf);
  let data =
    res.payload && res.payload.data && res.payload.status === 200 && !res.error
      ? res.payload.data
      : null;

  //without this check you get infinite checkin calls
  if (data && data.length > 0) {
    dispatch({ type: UPDATE_CHECKINS, payload: data });
  }
};

export const createNewCheckin = async (memberProfile, dispatch, csrf) => {
  if (memberProfile) {
    const today = new Date();
    const dateTimeArray = [
      today.getFullYear(),
      today.getMonth() + 1,
      today.getDate(),
      today.getHours(),
      today.getMinutes(),
      today.getSeconds(),
    ];
    const res = await createCheckin(
      {
        teamMemberId: memberProfile.id,
        pdlId: memberProfile.pdlId,
        checkInDate: dateTimeArray,
        completed: false,
      },
      csrf
    );
    const checkin =
      res.payload && res.payload.data && !res.error ? res.payload.data : null;

    dispatch({ type: ADD_CHECKIN, payload: checkin });
    return checkin?.id;
  }
};

export const createNewCheckinWithDate = async (memberProfile, date, dispatch, csrf) => {
  if (memberProfile) {
    const dateTimeArray = [
      date.getFullYear(),
      date.getMonth() + 1,
      date.getDate(),
      date.getHours(),
      date.getMinutes(),
      date.getSeconds(),
    ];
    const res = await createCheckin(
      {
        teamMemberId: memberProfile.id,
        pdlId: memberProfile.pdlId,
        checkInDate: dateTimeArray,
        completed: false,
      },
      csrf
    );
    const checkin =
      res.payload && res.payload.data && !res.error ? res.payload.data : null;

    dispatch({ type: ADD_CHECKIN, payload: checkin });
    return checkin?.id;
  }
}
