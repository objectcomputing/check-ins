import { resolve } from './api.js';

const reviewAssignmentsUrl = '/services/review-assignments';

export const getReviewAssignments = async (id, cookie) => {
  return resolve({
    url: `${reviewAssignmentsUrl}/period/${id}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const createReviewAssignments = async (id, assignments, cookie) => {
  return resolve({
    method: 'POST',
    url: `${reviewAssignmentsUrl}/${id}`,
    data: assignments,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const updateReviewAssignment = async (assignment, cookie) => {
  return resolve({
    method: assignment.id === null ? 'POST' : 'PUT',
    url: reviewAssignmentsUrl,
    data: assignment,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const removeReviewAssignment = async (id, cookie) => {
  return resolve({
    method: 'DELETE',
    url: `${reviewAssignmentsUrl}/${id}`,
    headers: { 'X-CSRF-Header': cookie }
  });
};
