import { createSelector } from 'reselect';

export const selectCsrfToken = state => state.csrf;
export const noPermission = 'You do not have permission to view this page.';
