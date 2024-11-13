import { resolve } from './api.js';

const teamUrl = `/services/teams`;
const teamMemberUrl = `/services/teams/members`;

export const getAllTeamMembers = async cookie => {
  return resolve({
    url: teamMemberUrl,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getMembersByTeam = async (id, cookie) => {
  return resolve({
    url: teamMemberUrl,
    params: {
      teamId: id
    },
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const updateTeam = async (team, cookie) => {
  return resolve({
    method: 'PUT',
    url: teamUrl,
    data: team,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const getTeamByMember = async (id, cookie) => {
  return resolve({
    url: teamUrl,
    params: {
      memberId: id
    },
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getTeamsByMember = async (id, cookie) => {
  return resolve({
    url: teamMemberUrl,
    params: {
      memberId: id
    },
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getAllTeams = async cookie => {
  return resolve({
    url: teamUrl,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const createTeam = async (team, cookie) => {
  return resolve({
    method: 'POST',
    url: teamUrl,
    data: team,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const addTeamMember = async (member, isLead, teamId, cookie) => {
  return resolve({
    method: 'POST',
    url: teamMemberUrl,
    data: { memberId: member.id, lead: isLead, teamId: teamId },
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const deleteTeam = async (id, cookie) => {
  return resolve({
    method: 'DELETE',
    url: `${teamUrl}/${id}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};
