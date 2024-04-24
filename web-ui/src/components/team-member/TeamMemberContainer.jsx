import React, { useEffect, useContext, useState } from 'react';
import MemberIcon from './MemberIcon';
import { AppContext } from '../../context/AppContext';
import { getMembersByTeam, getTeamsByMember } from '../../api/team';
import { getMember } from '../../api/member';

import './TeamMember.css';

const TeamMemberContainer = () => {
  const { state } = useContext(AppContext);
  const { csrf, userProfile } = state;
  const id =
    userProfile && userProfile.memberProfile
      ? userProfile.memberProfile.id
      : undefined;
  const [selectedProfile, setSelectedProfile] = useState({
    name: null,
    imageUrl: null
  });
  const [teamMembers, setTeamMembers] = useState({});
  const [teams, setTeams] = useState([]);
  const [currentTeam, setCurrentTeam] = useState([]);
  const {
    bioText,
    imageUrl,
    location,
    name,
    pdlId,
    role,
    startDate,
    workEmail
  } = selectedProfile;

  const [pdl, setPDL] = useState();

  // Get PDL's name
  useEffect(() => {
    async function getPDLName() {
      if (pdlId) {
        let res = await getMember(pdlId, csrf);
        let pdlProfile =
          res.payload.data && !res.error ? res.payload.data : undefined;
        setPDL(pdlProfile ? pdlProfile.name : '');
      }
    }
    if (csrf) {
      getPDLName();
    }
  }, [csrf, pdlId]);

  // Get member teams
  useEffect(() => {
    async function updateTeams() {
      if (id) {
        let res = await getTeamsByMember(id, csrf);
        let data =
          res.payload && res.payload.status === 200 ? res.payload.data : null;
        let memberTeams = data && !res.error ? data : [];
        setTeams(memberTeams);
      }
    }
    if (csrf) {
      updateTeams();
    }
  }, [csrf, id]);

  useEffect(() => {
    async function updateTeamMembers() {
      if (teams) {
        const teamMemberMap = Object.assign(
          {},
          ...(await Promise.all(
            teams.map(async team => {
              let res = await getMembersByTeam(team.uuid, csrf);
              let data =
                res && res.payload && res.payload.status === 200
                  ? res.payload.data
                  : null;
              if (data && !res.error) {
                return {
                  [team.uuid]: await Promise.all(
                    data.map(async member => {
                      let res = await getMember(member.memberid, csrf);
                      let data =
                        res &&
                        res.payload &&
                        res.payload.status === 200 &&
                        !res.error
                          ? res.payload.data
                          : null;
                      return data;
                    })
                  )
                };
              } else {
                return { [team.uuid]: [] };
              }
            })
          ))
        );
        setTeamMembers(teamMemberMap);
      }
    }
    if (csrf) {
      updateTeamMembers();
    }
  }, [csrf, teams]);

  let teamProfile = profiles => {
    let team = profiles.map(profile => {
      return (
        <MemberIcon
          key={`profile-${profile.workEmail}`}
          profile={profile}
          onSelect={setSelectedProfile}
        ></MemberIcon>
      );
    });

    return team;
  };
  let team = teamProfile(currentTeam);

  const mapTeams = teams.map(team => {
    return (
      <div
        key={`team-${team.uuid}`}
        onClick={async () => setCurrentTeam(teamMembers[team.uuid])}
      >
        {team.name.toUpperCase()}
      </div>
    );
  });

  return (
    <div>
      <div className="team-names">{mapTeams}</div>
      {name && (
        <div className="flex-row" style={{ minWidth: '800px' }}>
          <div className="image-div">
            <img
              alt="Profile"
              src={imageUrl ? imageUrl : '/default_profile.jpg'}
            />
          </div>
          <div className="team-member-info">
            <div style={{ textAlign: 'left' }}>
              <h2 style={{ margin: 0 }}>{name}</h2>
              <div style={{ display: 'flex' }}>
                <div style={{ marginRight: '50px', textAlign: 'left' }}>
                  <p>Role: {role}</p>
                  <p>PDL: {pdl}</p>
                  <p>Location: {location}</p>
                </div>
                <div>
                  <p>
                    Start Date:{' '}
                    {startDate && startDate.length === 3
                      ? new Date(
                          startDate[0],
                          startDate[1] - 1,
                          startDate[2]
                        ).toLocaleDateString()
                      : ''}
                  </p>
                  <p>Email: {workEmail}</p>
                  <p>Bio: {bioText}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
      <div className="flex-row" style={{ flexWrap: 'wrap' }}>
        {team}
      </div>
    </div>
  );
};

export default TeamMemberContainer;
