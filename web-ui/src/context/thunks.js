import { getCheckinByMemberId, createCheckin } from "../api/checkins";
import {
  UPDATE_CHECKINS,
} from "./actions";

const date = (months, prevCheckinDate) => {
  let currentMonth = new Date().getMonth() + 1;
  let newDate = prevCheckinDate ? new Date(...prevCheckinDate) : new Date();
  if (prevCheckinDate) {
    let prevCheckinMonth = new Date (prevCheckinDate).getMonth() + 1;
    newDate.setMonth(
      newDate.getMonth() + currentMonth - prevCheckinMonth >= 3 ? 1 : months
    );
  } else {
    newDate.setMonth(newDate.getMonth() + months);
  }
  const year = newDate.getFullYear();
  const month = newDate.getMonth() + 1;
  const day = newDate.getDate();
  const hours = newDate.getHours();
  const minutes = newDate.getMinutes();
  const dateTimeArray = [year, month, day, hours, minutes, 0];
  return dateTimeArray;
};

export const getCheckins = async (id, pdlId, dispatch, csrf) => {
  const res = await getCheckinByMemberId(id, csrf);
  let data =
    res.payload && res.payload.data && res.payload.status === 200 && !res.error
      ? res.payload.data
      : null;
  if (data && data.length > 0) {
    const allComplete = data.every((checkin) => checkin.completed === true);
    if (allComplete) {
      const prevCheckinDate = data[data.length - 1].checkInDate;
      if (pdlId) {
        const res = await createCheckin({
          teamMemberId: id,
          pdlId: pdlId,
          checkInDate: date(3, prevCheckinDate),
          completed: false,
        }, csrf);
        const checkin =
          res.payload && res.payload.data && !res.error
            ? res.payload.data
            : null;
        data.push(checkin);
      }
    }
  } else if (data && data.length === 0) {
    if (pdlId) {
      const res = await createCheckin({
        teamMemberId: id,
        pdlId: pdlId,
        checkInDate: date(1),
        completed: false,
      }, csrf);
      const checkin =
        res.payload && res.payload.data && !res.error ? res.payload.data : null;
      data = [checkin];
    }
  }
  //without this check you get infinite checkin calls
  if (data && data.length > 0) {
    dispatch({ type: UPDATE_CHECKINS, payload: data });
  }
};
