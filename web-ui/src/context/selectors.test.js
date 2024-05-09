import {
  selectMemberProfiles,
  selectProfileMap,
  selectPdlRoles,
  selectMappedPdls,
  selectOrderedPdls,
  selectCheckinPDLS,
  selectTeamMembersWithCheckinPDL,
  selectCheckinsForTeamMemberAndPDL,
  selectCurrentMembers,
  selectNormalizedMembers,
  selectNormalizedTeams,
  selectMostRecentCheckin,
  selectSupervisors,
  selectSupervisorHierarchyIds,
  selectSubordinates,
  selectIsSubordinateOfCurrentUser,
  selectHasReportPermission
} from './selectors';

describe('Selectors', () => {
  it('selectMemberProfiles should return an array of all member profiles', () => {
    const testMemberProfiles = [
      {
        id: 1,
        bioText: 'foo',
        employeeId: 11,
        name: 'A Person',
        firstName: 'A',
        lastName: 'PersonA',
        location: 'St Louis',
        title: 'engineer',
        workEmail: 'employee@sample.com',
        pdlId: 9,
        startDate: [2012, 9, 29]
      },
      {
        id: 2,
        bioText: 'foo',
        employeeId: 12,
        name: 'B Person',
        firstName: 'B',
        lastName: 'PersonB',
        location: 'St Louis',
        title: 'engineer',
        workEmail: 'employee@sample.com',
        pdlId: 9,
        startDate: [2012, 9, 29]
      },
      {
        id: 3,
        bioText: 'foo',
        employeeId: 13,
        name: 'C Person',
        firstName: 'C',
        lastName: 'PersonC',
        location: 'St Louis',
        title: 'engineer',
        workEmail: 'employee@sample.com',
        pdlId: 9,
        startDate: [2012, 9, 29]
      }
    ];
    const testState = {
      memberProfiles: testMemberProfiles
    };

    expect(selectMemberProfiles(testState)).toEqual(testState.memberProfiles);
  });

  it('selectProfileMap should return an array of all member profiles mapped by id', () => {
    const testMemberProfiles = [
      {
        id: 1,
        bioText: 'foo',
        employeeId: 11,
        name: 'A Person',
        firstName: 'A',
        lastName: 'PersonA',
        location: 'St Louis',
        title: 'engineer',
        workEmail: 'employee@sample.com',
        pdlId: 9,
        startDate: [2012, 9, 29]
      },
      {
        id: 2,
        bioText: 'foo',
        employeeId: 12,
        name: 'B Person',
        firstName: 'B',
        lastName: 'PersonB',
        location: 'St Louis',
        title: 'engineer',
        workEmail: 'employee@sample.com',
        pdlId: 9,
        startDate: [2012, 9, 29]
      },
      {
        id: 3,
        bioText: 'foo',
        employeeId: 13,
        name: 'C Person',
        firstName: 'C',
        lastName: 'PersonC',
        location: 'St Louis',
        title: 'engineer',
        workEmail: 'employee@sample.com',
        pdlId: 9,
        startDate: [2012, 9, 29]
      }
    ];

    const matchingProfiles = {
      [testMemberProfiles[0].id]: testMemberProfiles[0],
      [testMemberProfiles[1].id]: testMemberProfiles[1],
      [testMemberProfiles[2].id]: testMemberProfiles[2]
    };

    const testState = {
      memberProfiles: [
        testMemberProfiles[0],
        testMemberProfiles[1],
        testMemberProfiles[2]
      ]
    };

    expect(selectProfileMap(testState)).toEqual(matchingProfiles);
  });

  it('selectPdlRoles should return an array of all member PDL roles', () => {
    const matchingRoles = [
      {
        id: '12',
        memberid: '2',
        role: 'PDL'
      },
      {
        id: '13',
        memberid: '3',
        role: 'PDL'
      }
    ];
    const testState = {
      roles: [
        {
          id: '11',
          memberid: '1',
          role: 'MEMBER'
        },
        {
          id: '12',
          memberid: '2',
          role: 'PDL'
        },
        {
          id: '13',
          memberid: '3',
          role: 'PDL'
        }
      ]
    };

    expect(selectPdlRoles(testState)).toEqual(matchingRoles);
  });

  it('selectMappedPdls should return an array of all member PDL profiles', () => {
    const matchingMembers = [
      {
        id: '2',
        bioText: 'foo',
        employeeId: 12,
        name: 'B Person',
        firstName: 'B',
        lastName: 'PersonB',
        location: 'St Louis',
        title: 'engineer',
        workEmail: 'employee@sample.com',
        pdlId: 9,
        startDate: [2012, 9, 29]
      },
      {
        id: '3',
        bioText: 'foo',
        employeeId: 13,
        name: 'C Person',
        firstName: 'C',
        lastName: 'PersonC',
        location: 'St Louis',
        title: 'engineer',
        workEmail: 'employee@sample.com',
        pdlId: 9,
        startDate: [2012, 9, 29]
      }
    ];

    const testRoles = [
      {
        id: '11',
        role: 'MEMBER'
      },
      {
        id: '12',
        role: 'PDL'
      }
    ];

    const matchingRoles = [
      {
        memberRoleId: {
          roleId: '12',
          memberId: '2'
        }
      },
      {
        memberRoleId: {
          roleId: '12',
          memberId: '3'
        }
      }
    ];

    const testMemberRoles = [
      {
        memberRoleId: {
          roleId: '11',
          memberId: '1'
        }
      },
      matchingRoles[0],
      matchingRoles[1]
    ];

    const testState = {
      memberProfiles: [
        {
          id: '1',
          bioText: 'foo',
          employeeId: 11,
          name: 'A Person',
          firstName: 'A',
          lastName: 'PersonA',
          location: 'St Louis',
          title: 'engineer',
          workEmail: 'employee@sample.com',
          pdlId: 9,
          startDate: [2012, 9, 29]
        },
        {
          id: '2',
          bioText: 'foo',
          employeeId: 12,
          name: 'B Person',
          firstName: 'B',
          lastName: 'PersonB',
          location: 'St Louis',
          title: 'engineer',
          workEmail: 'employee@sample.com',
          pdlId: 9,
          startDate: [2012, 9, 29]
        },
        {
          id: '3',
          bioText: 'foo',
          employeeId: 13,
          name: 'C Person',
          firstName: 'C',
          lastName: 'PersonC',
          location: 'St Louis',
          title: 'engineer',
          workEmail: 'employee@sample.com',
          pdlId: 9,
          startDate: [2012, 9, 29]
        }
      ],
      roles: testRoles,
      userRoles: testMemberRoles
    };
    expect(selectMappedPdls(testState)).toEqual(matchingMembers);
  });

  it('selectOrderedPdls should return an array of all member PDL profiles ordered by last name', () => {
    const matchingMembers = [
      {
        id: '3',
        bioText: 'foo',
        employeeId: 13,
        name: 'B PersonB',
        firstName: 'B',
        lastName: 'PersonB',
        location: 'St Louis',
        title: 'engineer',
        workEmail: 'employee@sample.com',
        pdlId: 9,
        startDate: [2012, 9, 29]
      },
      {
        id: '2',
        bioText: 'foo',
        employeeId: 12,
        name: 'C PersonC',
        firstName: 'C',
        lastName: 'PersonC',
        location: 'St Louis',
        title: 'engineer',
        workEmail: 'employee@sample.com',
        pdlId: 9,
        startDate: [2012, 9, 29]
      }
    ];

    const testRoles = [
      {
        id: '11',
        role: 'MEMBER'
      },
      {
        id: '12',
        role: 'PDL'
      }
    ];

    const matchingRoles = [
      {
        memberRoleId: {
          roleId: '12',
          memberId: '2'
        }
      },
      {
        memberRoleId: {
          roleId: '12',
          memberId: '3'
        }
      }
    ];

    const testMemberRoles = [
      {
        memberRoleId: {
          roleId: '11',
          memberId: '1'
        }
      },
      matchingRoles[0],
      matchingRoles[1]
    ];

    const testState = {
      memberProfiles: [
        {
          id: '1',
          bioText: 'foo',
          employeeId: 11,
          name: 'A PersonA',
          firstName: 'A',
          lastName: 'PersonA',
          location: 'St Louis',
          title: 'engineer',
          workEmail: 'employee@sample.com',
          pdlId: 9,
          startDate: [2012, 9, 29]
        },
        {
          id: '2',
          bioText: 'foo',
          employeeId: 12,
          name: 'C PersonC',
          firstName: 'C',
          lastName: 'PersonC',
          location: 'St Louis',
          title: 'engineer',
          workEmail: 'employee@sample.com',
          pdlId: 9,
          startDate: [2012, 9, 29]
        },
        {
          id: '3',
          bioText: 'foo',
          employeeId: 13,
          name: 'B PersonB',
          firstName: 'B',
          lastName: 'PersonB',
          location: 'St Louis',
          title: 'engineer',
          workEmail: 'employee@sample.com',
          pdlId: 9,
          startDate: [2012, 9, 29]
        }
      ],
      roles: testRoles,
      userRoles: testMemberRoles
    };
    expect(selectOrderedPdls(testState)).toEqual(matchingMembers);
  });

  it('selectCheckinPdls should return an array of all member PDL profiles that have an associated checkin', () => {
    const matchingMembers = [
      {
        id: 12,
        bioText: 'foo',
        name: 'B PersonB',
        firstName: 'B',
        lastName: 'PersonB',
        location: 'St Louis',
        title: 'engineer',
        workEmail: 'employee@sample.com',
        startDate: [2012, 9, 29]
      },
      {
        id: 13,
        bioText: 'foo',
        name: 'C PersonC',
        firstName: 'C',
        lastName: 'PersonC',
        location: 'St Louis',
        title: 'engineer',
        workEmail: 'employee@sample.com',
        startDate: [2012, 9, 29]
      }
    ];

    const testState = {
      checkins: [
        { pdlId: 13, completed: false, checkInDate: [2020, 9, 13] },
        { pdlId: 12, completed: false, checkInDate: [2021, 9, 12] },
        { pdlId: 12, completed: true, checkInDate: [2020, 9, 11] }
      ],
      memberProfiles: [
        {
          id: 11,
          bioText: 'foo',
          name: 'A PersonA',
          firstName: 'A',
          lastName: 'PersonA',
          location: 'St Louis',
          title: 'engineer',
          workEmail: 'employee@sample.com',
          startDate: [2012, 9, 29]
        },
        {
          id: 12,
          bioText: 'foo',
          name: 'B PersonB',
          firstName: 'B',
          lastName: 'PersonB',
          location: 'St Louis',
          title: 'engineer',
          workEmail: 'employee@sample.com',
          startDate: [2012, 9, 29]
        },
        {
          id: 13,
          bioText: 'foo',
          name: 'C PersonC',
          firstName: 'C',
          lastName: 'PersonC',
          location: 'St Louis',
          title: 'engineer',
          workEmail: 'employee@sample.com',
          startDate: [2012, 9, 29]
        }
      ]
    };
    expect(selectCheckinPDLS(testState, true, true)).toEqual(matchingMembers);
  });

  it('selectTeamMembersWithCheckinPDL should return an array of all members associated with a pdl that have a checkin', () => {
    const matchingMembers = [
      {
        id: 12,
        bioText: 'foo',
        name: 'B PersonB',
        firstName: 'B',
        lastName: 'PersonB',
        location: 'St Louis',
        pdlId: 1,
        title: 'engineer',
        workEmail: 'employee@sample.com',
        startDate: [2012, 9, 29]
      },
      {
        id: 13,
        bioText: 'foo',
        name: 'C PersonC',
        firstName: 'C',
        lastName: 'PersonC',
        location: 'St Louis',
        pdlId: 1,
        title: 'engineer',
        workEmail: 'employee@sample.com',
        startDate: [2012, 9, 29]
      }
    ];

    const testState = {
      checkins: [
        {
          teamMemberId: 12,
          pdlId: 1,
          completed: false,
          checkInDate: [2020, 9, 10]
        },
        {
          teamMemberId: 13,
          pdlId: 1,
          completed: false,
          checkInDate: [2020, 9, 10]
        },
        {
          teamMemberId: 11,
          pdlId: 2,
          completed: false,
          checkInDate: [2020, 9, 10]
        }
      ],
      memberProfiles: [
        {
          id: 11,
          bioText: 'foo',
          name: 'A PersonA',
          firstName: 'A',
          lastName: 'PersonA',
          location: 'St Louis',
          title: 'engineer',
          workEmail: 'employee@sample.com',
          pdlId: 2,
          startDate: [2012, 9, 29]
        },
        {
          id: 12,
          bioText: 'foo',
          name: 'B PersonB',
          firstName: 'B',
          lastName: 'PersonB',
          location: 'St Louis',
          pdlId: 1,
          title: 'engineer',
          workEmail: 'employee@sample.com',
          startDate: [2012, 9, 29]
        },
        {
          id: 13,
          bioText: 'foo',
          name: 'C PersonC',
          firstName: 'C',
          lastName: 'PersonC',
          location: 'St Louis',
          pdlId: 1,
          title: 'engineer',
          workEmail: 'employee@sample.com',
          startDate: [2012, 9, 29]
        }
      ]
    };
    expect(selectTeamMembersWithCheckinPDL(testState, 1)).toEqual(
      matchingMembers
    );
  });

  it('selectCheckinsForTeamMemberAndPDL should return an array of all members associated with a pdl that have a checkin', () => {
    const matchingCheckins = [
      {
        id: 1,
        teamMemberId: 12,
        pdlId: 1,
        completed: false,
        checkInDate: [2020, 9, 10]
      },
      {
        id: 2,
        teamMemberId: 12,
        pdlId: 1,
        completed: true,
        checkInDate: [2020, 10, 10]
      }
    ];

    const testState = {
      checkins: [
        {
          id: 1,
          teamMemberId: 12,
          pdlId: 1,
          completed: false,
          checkInDate: [2020, 9, 10]
        },
        {
          id: 2,
          teamMemberId: 12,
          pdlId: 1,
          completed: true,
          checkInDate: [2020, 10, 10]
        },
        {
          id: 3,
          teamMemberId: 11,
          pdlId: 2,
          completed: false,
          checkInDate: [2020, 9, 10]
        },
        {
          id: 4,
          teamMemberId: 19,
          pdlId: 4,
          completed: false,
          checkInDate: [2020, 9, 10]
        }
      ],
      memberProfiles: [
        {
          id: 11,
          bioText: 'foo',
          name: 'A PersonA',
          firstName: 'A',
          lastName: 'PersonA',
          location: 'St Louis',
          title: 'engineer',
          workEmail: 'employee@sample.com',
          pdlId: 2,
          startDate: [2012, 9, 29]
        },
        {
          id: 12,
          bioText: 'foo',
          name: 'B PersonB',
          firstName: 'B',
          lastName: 'PersonB',
          location: 'St Louis',
          pdlId: 1,
          title: 'engineer',
          workEmail: 'employee@sample.com',
          startDate: [2012, 9, 29]
        },
        {
          id: 13,
          bioText: 'foo',
          name: 'C PersonC',
          firstName: 'C',
          lastName: 'PersonC',
          location: 'St Louis',
          pdlId: 1,
          title: 'engineer',
          workEmail: 'employee@sample.com',
          startDate: [2012, 9, 29]
        }
      ]
    };
    expect(selectCheckinsForTeamMemberAndPDL(testState, 12, 1)).toEqual(
      matchingCheckins
    );
  });

  it('selectCurrentMembers should return an array of non-terminated profiles', () => {
    const testMemberProfiles = [
      {
        id: 1,
        bioText: 'foo',
        employeeId: 11,
        name: 'Iván López Martín',
        firstName: 'Iván',
        lastName: 'Martín',
        location: 'St Louis',
        title: 'engineer',
        workEmail: 'employee@sample.com',
        pdlId: 9,
        startDate: [2012, 9, 29]
      },
      {
        id: 2,
        bioText: 'foo',
        employeeId: 12,
        name: 'B Person',
        firstName: 'B',
        lastName: 'PersonB',
        location: 'St Louis',
        title: 'engineer',
        workEmail: 'employee@sample.com',
        pdlId: 9,
        startDate: [2012, 9, 29]
      },
      {
        id: 3,
        bioText: 'foo',
        employeeId: 13,
        name: 'C Person',
        firstName: 'C',
        lastName: 'PersonC',
        location: 'St Louis',
        title: 'engineer',
        workEmail: 'employee@sample.com',
        pdlId: 9,
        startDate: [2012, 9, 29],
        terminationDate: [2020, 12, 31]
      }
    ];
    const testState = {
      memberProfiles: [
        testMemberProfiles[0],
        testMemberProfiles[1],
        testMemberProfiles[2]
      ]
    };

    const result = {
      memberProfiles: [testMemberProfiles[0], testMemberProfiles[1]]
    };

    expect(selectCurrentMembers(testState)).toEqual(result.memberProfiles);
  });

  it('selectNormalizedMembers should return an array of appropriate member profiles despite accents', () => {
    const testMemberProfiles = [
      {
        id: 1,
        bioText: 'foo',
        employeeId: 11,
        name: 'Iván López Martín',
        firstName: 'Iván',
        lastName: 'Martín',
        location: 'St Louis',
        title: 'engineer',
        workEmail: 'employee@sample.com',
        pdlId: 9,
        startDate: [2012, 9, 29]
      },
      {
        id: 2,
        bioText: 'foo',
        employeeId: 12,
        name: 'B Person',
        firstName: 'B',
        lastName: 'PersonB',
        location: 'St Louis',
        title: 'engineer',
        workEmail: 'employee@sample.com',
        pdlId: 9,
        startDate: [2012, 9, 29]
      },
      {
        id: 3,
        bioText: 'foo',
        employeeId: 13,
        name: 'C Person',
        firstName: 'C',
        lastName: 'PersonC',
        location: 'St Louis',
        title: 'engineer',
        workEmail: 'employee@sample.com',
        pdlId: 9,
        startDate: [2012, 9, 29]
      }
    ];
    const testState = {
      memberProfiles: [
        testMemberProfiles[0],
        testMemberProfiles[1],
        testMemberProfiles[2]
      ]
    };

    const result = {
      memberProfiles: [testMemberProfiles[0]]
    };

    const searchText = 'ivan';

    expect(selectNormalizedMembers(testState, searchText)).toEqual(
      result.memberProfiles
    );
  });

  it('selectNormalizedTeams should return an array of appropriate teams despite accents', () => {
    const testTeams = [
      {
        id: '3fa85f64-5717-4562-b3fc-2c963f66afa6',
        name: 'Iváns Team',
        description: 'string'
      },
      {
        id: '3fa4-5717-4562-b3fc-2c963f66afa6',
        name: 'stuff',
        description: ''
      }
    ];
    const testState = {
      teams: [testTeams[0], testTeams[1]]
    };

    const searchText = 'ivan';

    const result = {
      teams: [testTeams[0]]
    };

    expect(selectNormalizedTeams(testState, searchText)).toEqual(result.teams);
  });

  it('selectMostRecentCheckin should return the most recent and or open checkin', () => {
    const memberId = '1';
    const checkins = [
      {
        checkInDate: [2020, 9, 29, 10, 32, 29, 40000000],
        completed: false,
        id: '2',
        pdlId: '1',
        teamMemberId: '1'
      },
      {
        checkInDate: [2020, 9, 30, 10, 32, 29, 40000000],
        completed: false,
        id: '1',
        pdlId: '1',
        teamMemberId: '1'
      }
    ];

    const state = { checkins: [checkins[0], checkins[1]] };

    const expectedResult = {
      checkInDate: [2020, 9, 30, 10, 32, 29, 40000000],
      completed: false,
      id: '1',
      pdlId: '1',
      teamMemberId: '1'
    };

    console.log(selectMostRecentCheckin(state, memberId));
    expect(selectMostRecentCheckin(state, memberId)).toEqual(expectedResult);
  });

  it('selectSupervisors should return only members who are supervisors', () => {
    const testMemberProfiles = [
      {
        id: 1,
        employeeId: 11,
        name: 'Big Boss',
        firstName: 'Big',
        lastName: 'Boss',
        supervisorid: 5
      },
      {
        id: 2,
        employeeId: 12,
        name: 'Huey Emmerich',
        firstName: 'Huey',
        lastName: 'Emmerich',
        supervisorid: 1
      },
      {
        id: 3,
        employeeId: 13,
        name: 'Kazuhira Miller',
        firstName: 'Kazuhira',
        lastName: 'Miller',
        supervisorid: 1
      },
      {
        id: 4,
        employeeId: 14,
        name: 'Revolver Ocelot',
        firstName: 'Revolver',
        lastName: 'Ocelot',
        supervisorid: 3
      },
      {
        id: 5,
        employeeId: 15,
        name: 'The Boss',
        firstName: 'The',
        lastName: 'Boss'
      }
    ];

    const testState = {
      memberProfiles: testMemberProfiles
    };

    const expectedResult = [
      testMemberProfiles[4],
      testMemberProfiles[0],
      testMemberProfiles[2]
    ];

    expect(selectSupervisors(testState)).toEqual(expectedResult);
  });

  it('selectSupervisorHierarchyIds should return a list of ids of everyone who is above the selected member', () => {
    const testMemberProfiles = [
      {
        id: 1,
        employeeId: 11,
        name: 'Big Boss',
        firstName: 'Big',
        lastName: 'Boss',
        supervisorid: 2
      },
      {
        id: 2,
        employeeId: 12,
        name: 'Huey Emmerich',
        firstName: 'Huey',
        lastName: 'Emmerich',
        supervisorid: 4
      },
      {
        id: 3,
        employeeId: 13,
        name: 'Kazuhira Miller',
        firstName: 'Kazuhira',
        lastName: 'Miller',
        supervisorid: 5
      },
      {
        id: 4,
        employeeId: 14,
        name: 'Revolver Ocelot',
        firstName: 'Revolver',
        lastName: 'Ocelot',
        supervisorid: 6
      },
      {
        id: 5,
        employeeId: 15,
        name: 'The Boss',
        firstName: 'The',
        lastName: 'Boss',
        supervisorid: 6
      },
      {
        id: 6,
        employeeId: 15,
        name: 'No Supervisor',
        firstName: 'No',
        lastName: 'Supervisor'
      }
    ];

    const testState = {
      memberProfiles: testMemberProfiles
    };

    const expectedResult = [
      testMemberProfiles[1].id,
      testMemberProfiles[3].id,
      testMemberProfiles[5].id
    ];

    expect(
      selectSupervisorHierarchyIds(testMemberProfiles[0])(testState)
    ).toEqual(expectedResult);
  });

  it('selectSubordinates should return entire subordinate heirarchy', () => {
    const testMemberProfiles = [
      {
        id: 1,
        employeeId: 11,
        name: 'Big Boss',
        firstName: 'Big',
        lastName: 'Boss',
        supervisorid: 5
      },
      {
        id: 2,
        employeeId: 12,
        name: 'Huey Emmerich',
        firstName: 'Huey',
        lastName: 'Emmerich',
        supervisorid: 1
      },
      {
        id: 3,
        employeeId: 13,
        name: 'Kazuhira Miller',
        firstName: 'Kazuhira',
        lastName: 'Miller',
        supervisorid: 1
      },
      {
        id: 4,
        employeeId: 14,
        name: 'Revolver Ocelot',
        firstName: 'Revolver',
        lastName: 'Ocelot',
        supervisorid: 3
      },
      {
        id: 5,
        employeeId: 15,
        name: 'THE Boss',
        firstName: 'THE',
        lastName: 'Boss'
      },
      {
        id: 6,
        employeeId: 16,
        name: 'Entry Level',
        firstName: 'Entry',
        lastName: 'Level',
        supervisorid: 4
      },
      {
        id: 7,
        employeeId: 15,
        name: 'Other Boss',
        firstName: 'Other',
        lastName: 'Boss',
        supervisorid: 5
      },
      {
        id: 8,
        employeeId: 15,
        name: 'Other Person',
        firstName: 'Other',
        lastName: 'Person',
        supervisorid: 7
      }
    ];

    const testState = {
      memberProfiles: testMemberProfiles
    };

    const expectedResult = [
      testMemberProfiles[1],
      testMemberProfiles[2],
      testMemberProfiles[3],
      testMemberProfiles[5]
    ];

    expect(selectSubordinates(testState, testMemberProfiles[0].id)).toEqual(
      expectedResult
    );
  });

  it("selectIsSubordinateOfCurrentUser should return true when user is in the current users' heirarchy", () => {
    const testMemberProfiles = [
      {
        id: 1,
        employeeId: 11,
        name: 'Big Boss',
        firstName: 'Big',
        lastName: 'Boss',
        supervisorid: 5
      },
      {
        id: 2,
        employeeId: 12,
        name: 'Huey Emmerich',
        firstName: 'Huey',
        lastName: 'Emmerich',
        supervisorid: 1
      },
      {
        id: 3,
        employeeId: 13,
        name: 'Kazuhira Miller',
        firstName: 'Kazuhira',
        lastName: 'Miller',
        supervisorid: 1
      },
      {
        id: 4,
        employeeId: 14,
        name: 'Revolver Ocelot',
        firstName: 'Revolver',
        lastName: 'Ocelot',
        supervisorid: 3
      },
      {
        id: 5,
        employeeId: 15,
        name: 'THE Boss',
        firstName: 'THE',
        lastName: 'Boss'
      },
      {
        id: 6,
        employeeId: 16,
        name: 'Entry Level',
        firstName: 'Entry',
        lastName: 'Level',
        supervisorid: 4
      },
      {
        id: 7,
        employeeId: 15,
        name: 'Other Boss',
        firstName: 'Other',
        lastName: 'Boss',
        supervisorid: 5
      },
      {
        id: 8,
        employeeId: 15,
        name: 'Other Person',
        firstName: 'Other',
        lastName: 'Person',
        supervisorid: 7
      }
    ];

    const testState = {
      userProfile: {
        memberProfile: testMemberProfiles[0]
      },
      memberProfiles: testMemberProfiles
    };

    expect(
      selectIsSubordinateOfCurrentUser(testState, testMemberProfiles[5].id)
    ).toBe(true);
  });

  it("selectIsSubordinateOfCurrentUser should return false when user is not in the current users' heirarchy", () => {
    const testMemberProfiles = [
      {
        id: 1,
        employeeId: 11,
        name: 'Big Boss',
        firstName: 'Big',
        lastName: 'Boss',
        supervisorid: 5
      },
      {
        id: 2,
        employeeId: 12,
        name: 'Huey Emmerich',
        firstName: 'Huey',
        lastName: 'Emmerich',
        supervisorid: 1
      },
      {
        id: 3,
        employeeId: 13,
        name: 'Kazuhira Miller',
        firstName: 'Kazuhira',
        lastName: 'Miller',
        supervisorid: 1
      },
      {
        id: 4,
        employeeId: 14,
        name: 'Revolver Ocelot',
        firstName: 'Revolver',
        lastName: 'Ocelot',
        supervisorid: 3
      },
      {
        id: 5,
        employeeId: 15,
        name: 'THE Boss',
        firstName: 'THE',
        lastName: 'Boss'
      },
      {
        id: 6,
        employeeId: 16,
        name: 'Entry Level',
        firstName: 'Entry',
        lastName: 'Level',
        supervisorid: 4
      },
      {
        id: 7,
        employeeId: 15,
        name: 'Other Boss',
        firstName: 'Other',
        lastName: 'Boss',
        supervisorid: 5
      },
      {
        id: 8,
        employeeId: 15,
        name: 'Other Person',
        firstName: 'Other',
        lastName: 'Person',
        supervisorid: 7
      }
    ];

    const testState = {
      userProfile: {
        memberProfile: testMemberProfiles[0]
      },
      memberProfiles: testMemberProfiles
    };

    expect(
      selectIsSubordinateOfCurrentUser(testState, testMemberProfiles[7].id)
    ).toBe(false);
  });

  it("selectHasReportPermission should return false when user does not have a 'REPORT' permission", () => {
    const testState = {
      userProfile: {
        firstName: 'Huey',
        lastName: 'Emmerich',
        role: 'MEMBER',
        permissions: [
          { permission: 'CAN_VIEW_FEEDBACK_REQUEST' },
          { permission: 'CAN_VIEW_FEEDBACK_ANSWER' }
        ]
      }
    };

    expect(selectHasReportPermission(testState)).toBe(false);
  });

  it("selectHasAnniversaryReportPermission should return false when user does not have 'CAN_VIEW_ANNIVERSARY_REPORT' permission", () => {
    const testState1 = {
      userProfile: {
        firstName: 'Big',
        lastName: 'Boss',
        role: 'ADMIN',
        permissions: [{ permission: 'CAN_VIEW_ANNIVERSARY_REPORT' }]
      }
    };
    const testState2 = {
      userProfile: {
        firstName: 'Huey',
        lastName: 'Emmerich',
        role: 'MEMBER',
        permissions: [
          { permission: 'CAN_VIEW_FEEDBACK_REQUEST' },
          { permission: 'CAN_VIEW_FEEDBACK_ANSWER' }
        ]
      }
    };

    expect(selectHasReportPermission(testState1)).toBe(true);
    expect(selectHasReportPermission(testState2)).toBe(false);
  });

  it("selectHasBirthdayReportPermission should return false when user does not have 'CAN_VIEW_BIRTHDAY_REPORT' permission", () => {
    const testState1 = {
      userProfile: {
        firstName: 'Big',
        lastName: 'Boss',
        role: 'ADMIN',
        permissions: [{ permission: 'CAN_VIEW_BIRTHDAY_REPORT' }]
      }
    };
    const testState2 = {
      userProfile: {
        firstName: 'Huey',
        lastName: 'Emmerich',
        role: 'MEMBER',
        permissions: [
          { permission: 'CAN_VIEW_FEEDBACK_REQUEST' },
          { permission: 'CAN_VIEW_FEEDBACK_ANSWER' }
        ]
      }
    };

    expect(selectHasReportPermission(testState1)).toBe(true);
    expect(selectHasReportPermission(testState2)).toBe(false);
  });

  it("selectHasCheckinsReportPermission should return false when user does not have 'CAN_VIEW_CHECKINS' permission", () => {
    const testState = {
      userProfile: {
        firstName: 'Huey',
        lastName: 'Emmerich',
        role: 'MEMBER',
        permissions: [
          { permission: 'CAN_VIEW_FEEDBACK_REQUEST' },
          { permission: 'CAN_VIEW_FEEDBACK_ANSWER' }
        ]
      }
    };

    expect(selectHasReportPermission(testState)).toBe(false);
  });

  it("selectHasSkillsReportPermission should return false when user does not have 'CAN_VIEW_SKILLS_REPORT' permission", () => {
    const testState = {
      userProfile: {
        firstName: 'Huey',
        lastName: 'Emmerich',
        role: 'MEMBER',
        permissions: [
          { permission: 'CAN_VIEW_FEEDBACK_REQUEST' },
          { permission: 'CAN_VIEW_FEEDBACK_ANSWER' }
        ]
      }
    };

    expect(selectHasReportPermission(testState)).toBe(false);
  });

  it("selectHasTeamSkillsReportPermission should return false when user does not have 'CAN_VIEW_SKILLS_REPORT' permission", () => {
    const testState = {
      userProfile: {
        firstName: 'Huey',
        lastName: 'Emmerich',
        role: 'MEMBER',
        permissions: [
          { permission: 'CAN_VIEW_FEEDBACK_REQUEST' },
          { permission: 'CAN_VIEW_FEEDBACK_ANSWER' }
        ]
      }
    };

    expect(selectHasReportPermission(testState)).toBe(false);
  });
});
