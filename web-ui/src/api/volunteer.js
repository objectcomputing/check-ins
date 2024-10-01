import { resolve } from './api';

const organizationBaseUrl = '/services/volunteer/organization';

// Create New Organization
export const createNewOrganization = async (csrf, newOrganization) => {
    const res = await resolve({
      method: 'POST',
      url: organizationBaseUrl,
      headers: {
        'X-CSRF-Header': csrf,
        'Content-Type': 'application/json',
      },
      data: newOrganization,
    });
  
    return res;
  };

// Save New Organization
export const saveNewOrganization = async (csrf, newOrganization) => {
  const res = await resolve({
    method: 'POST',
    url: organizationBaseUrl,
    headers: {
      'X-CSRF-Header': csrf,
      'Content-Type': 'application/json'
    },
    data: newOrganization
  });

  return res;
};

// Save New Event
export const saveNewEvent = async (csrf, newEvent) => {
  const res = await resolve({
    method: 'POST',
    url: '/services/volunteer/event',
    headers: {
      'X-CSRF-Header': csrf,
      'Content-Type': 'application/json'
    },
    data: newEvent
  });

  return res;
};

