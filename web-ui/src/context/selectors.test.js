import {
  selectMemberProfiles,
  selectProfileMap,
  selectMemberRoles,
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
} from "./selectors";

describe("Selectors", () => {
  it("selectMemberProfiles should return an array of all member profiles", () => {
    const testMemberProfiles = [
      {
        id: 1,
        bioText: "foo",
        employeeId: 11,
        name: "A Person",
        firstName: "A",
        lastName: "PersonA",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        pdlId: 9,
        startDate: [2012, 9, 29],
      },
      {
        id: 2,
        bioText: "foo",
        employeeId: 12,
        name: "B Person",
        firstName: "B",
        lastName: "PersonB",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        pdlId: 9,
        startDate: [2012, 9, 29],
      },
      {
        id: 3,
        bioText: "foo",
        employeeId: 13,
        name: "C Person",
        firstName: "C",
        lastName: "PersonC",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        pdlId: 9,
        startDate: [2012, 9, 29],
      },
    ];
    const testState = {
      memberProfiles: {
        [testMemberProfiles[0].id]: testMemberProfiles[0],
        [testMemberProfiles[1].id]: testMemberProfiles[1],
        [testMemberProfiles[2].id]: testMemberProfiles[2],
      },
    };

    expect(selectMemberProfiles(testState)).toEqual(testState.memberProfiles);
  });

  it("selectProfileMap should return an array of all member profiles mapped by id", () => {
    const testMemberProfiles = [
      {
        id: 1,
        bioText: "foo",
        employeeId: 11,
        name: "A Person",
        firstName: "A",
        lastName: "PersonA",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        pdlId: 9,
        startDate: [2012, 9, 29],
      },
      {
        id: 2,
        bioText: "foo",
        employeeId: 12,
        name: "B Person",
        firstName: "B",
        lastName: "PersonB",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        pdlId: 9,
        startDate: [2012, 9, 29],
      },
      {
        id: 3,
        bioText: "foo",
        employeeId: 13,
        name: "C Person",
        firstName: "C",
        lastName: "PersonC",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        pdlId: 9,
        startDate: [2012, 9, 29],
      },
    ];

    const matchingProfiles = {
      [testMemberProfiles[0].id]: testMemberProfiles[0],
      [testMemberProfiles[1].id]: testMemberProfiles[1],
      [testMemberProfiles[2].id]: testMemberProfiles[2],
    };

    const testState = {
      memberProfiles: [
        testMemberProfiles[0],
        testMemberProfiles[1],
        testMemberProfiles[2],
      ],
    };

    expect(selectProfileMap(testState)).toEqual(matchingProfiles);
  });

  it("selectMemberRoles should return an array of all member roles", () => {
    const testMemberRoles = [
      {
        id: "1",
        memberid: "11",
        role: "MEMBER",
      },
      {
        id: "2",
        memberid: "12",
        role: "PDL",
      },
      {
        id: "3",
        memberid: "13",
        role: "PDL",
      },
    ];
    const testState = {
      roles: [
        {
          id: "11",
          memberid: "1",
          role: "MEMBER",
        },
        {
          id: "12",
          memberid: "2",
          role: "PDL",
        },
        {
          id: "13",
          memberid: "3",
          role: "PDL",
        },
      ],
    };

    expect(selectMemberRoles(testState)).toEqual(testState.roles);
  });

  it("selectPdlRoles should return an array of all member PDL roles", () => {
    const matchingRoles = [
      {
        id: "12",
        memberid: "2",
        role: "PDL",
      },
      {
        id: "13",
        memberid: "3",
        role: "PDL",
      },
    ];
    const testMemberRoles = [
      {
        id: "11",
        memberid: "1",
        role: "MEMBER",
      },
      matchingRoles[0],
      matchingRoles[1],
    ];
    const testState = {
      roles: [
        {
          id: "11",
          memberid: "1",
          role: "MEMBER",
        },
        {
          id: "12",
          memberid: "2",
          role: "PDL",
        },
        {
          id: "13",
          memberid: "3",
          role: "PDL",
        },
      ],
    };

    expect(selectPdlRoles(testState)).toEqual(matchingRoles);
  });

  it("selectMappedPdls should return an array of all member PDL profiles", () => {
    const testMemberProfiles = [
      {
        id: 1,
        bioText: "foo",
        employeeId: 11,
        name: "A Person",
        firstName: "A",
        lastName: "PersonA",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        pdlId: 9,
        startDate: [2012, 9, 29],
      },
      {
        id: 2,
        bioText: "foo",
        employeeId: 12,
        name: "B Person",
        firstName: "B",
        lastName: "PersonB",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        pdlId: 9,
        startDate: [2012, 9, 29],
      },
      {
        id: 3,
        bioText: "foo",
        employeeId: 13,
        name: "C Person",
        firstName: "C",
        lastName: "PersonC",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        pdlId: 9,
        startDate: [2012, 9, 29],
      },
    ];

    const matchingMembers = [
      {
        id: 2,
        bioText: "foo",
        employeeId: 12,
        name: "B Person",
        firstName: "B",
        lastName: "PersonB",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        pdlId: 9,
        startDate: [2012, 9, 29],
      },
      {
        id: 3,
        bioText: "foo",
        employeeId: 13,
        name: "C Person",
        firstName: "C",
        lastName: "PersonC",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        pdlId: 9,
        startDate: [2012, 9, 29],
      },
    ];

    const matchingRoles = [
      {
        id: "12",
        memberid: "2",
        role: "PDL",
      },
      {
        id: "13",
        memberid: "3",
        role: "PDL",
      },
    ];
    const testMemberRoles = [
      {
        id: "11",
        memberid: "1",
        role: "MEMBER",
      },
      matchingRoles[0],
      matchingRoles[1],
    ];
    const testState = {
      memberProfiles: [
        {
          id: 1,
          bioText: "foo",
          employeeId: 11,
          name: "A Person",
          firstName: "A",
          lastName: "PersonA",
          location: "St Louis",
          title: "engineer",
          workEmail: "employee@sample.com",
          pdlId: 9,
          startDate: [2012, 9, 29],
        },
        {
          id: 2,
          bioText: "foo",
          employeeId: 12,
          name: "B Person",
          firstName: "B",
          lastName: "PersonB",
          location: "St Louis",
          title: "engineer",
          workEmail: "employee@sample.com",
          pdlId: 9,
          startDate: [2012, 9, 29],
        },
        {
          id: 3,
          bioText: "foo",
          employeeId: 13,
          name: "C Person",
          firstName: "C",
          lastName: "PersonC",
          location: "St Louis",
          title: "engineer",
          workEmail: "employee@sample.com",
          pdlId: 9,
          startDate: [2012, 9, 29],
        },
      ],
      roles: [
        {
          id: "11",
          memberid: "1",
          role: "MEMBER",
        },
        {
          id: "12",
          memberid: "2",
          role: "PDL",
        },
        {
          id: "13",
          memberid: "3",
          role: "PDL",
        },
      ],
    };
    expect(selectMappedPdls(testState)).toEqual(matchingMembers);
  });

  it("selectOrderedPdls should return an array of all member PDL profiles ordered by last name", () => {
    const testMemberProfiles = [
      {
        id: 1,
        bioText: "foo",
        employeeId: 11,
        name: "A PersonA",
        firstName: "A",
        lastName: "PersonA",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        pdlId: 9,
        startDate: [2012, 9, 29],
      },
      {
        id: 2,
        bioText: "foo",
        employeeId: 12,
        name: "C PersonC",
        firstName: "C",
        lastName: "PersonC",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        pdlId: 9,
        startDate: [2012, 9, 29],
      },
      {
        id: 3,
        bioText: "foo",
        employeeId: 13,
        name: "B PersonB",
        firstName: "B",
        lastName: "PersonB",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        pdlId: 9,
        startDate: [2012, 9, 29],
      },
    ];

    const matchingMembers = [
      {
        id: 3,
        bioText: "foo",
        employeeId: 13,
        name: "B PersonB",
        firstName: "B",
        lastName: "PersonB",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        pdlId: 9,
        startDate: [2012, 9, 29],
      },
      {
        id: 2,
        bioText: "foo",
        employeeId: 12,
        name: "C PersonC",
        firstName: "C",
        lastName: "PersonC",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        pdlId: 9,
        startDate: [2012, 9, 29],
      },
    ];

    const testMemberRoles = [
      {
        id: "11",
        memberid: "1",
        role: "MEMBER",
      },
      {
        id: "12",
        memberid: "2",
        role: "PDL",
      },
      {
        id: "13",
        memberid: "3",
        role: "PDL",
      },
    ];
    const testState = {
      memberProfiles: [
        {
          id: 1,
          bioText: "foo",
          employeeId: 11,
          name: "A PersonA",
          firstName: "A",
          lastName: "PersonA",
          location: "St Louis",
          title: "engineer",
          workEmail: "employee@sample.com",
          pdlId: 9,
          startDate: [2012, 9, 29],
        },
        {
          id: 2,
          bioText: "foo",
          employeeId: 12,
          name: "C PersonC",
          firstName: "C",
          lastName: "PersonC",
          location: "St Louis",
          title: "engineer",
          workEmail: "employee@sample.com",
          pdlId: 9,
          startDate: [2012, 9, 29],
        },
        {
          id: 3,
          bioText: "foo",
          employeeId: 13,
          name: "B PersonB",
          firstName: "B",
          lastName: "PersonB",
          location: "St Louis",
          title: "engineer",
          workEmail: "employee@sample.com",
          pdlId: 9,
          startDate: [2012, 9, 29],
        },
      ],
      roles: [
        {
          id: "11",
          memberid: "1",
          role: "MEMBER",
        },
        {
          id: "12",
          memberid: "2",
          role: "PDL",
        },
        {
          id: "13",
          memberid: "3",
          role: "PDL",
        },
      ],
    };
    expect(selectOrderedPdls(testState)).toEqual(matchingMembers);
  });
});

it("selectCheckinPdls should return an array of all member PDL profiles that have an associated checkin", () => {
  const matchingMembers = [
    {
      id: 12,
      bioText: "foo",
      name: "B PersonB",
      firstName: "B",
      lastName: "PersonB",
      location: "St Louis",
      title: "engineer",
      workEmail: "employee@sample.com",
      startDate: [2012, 9, 29],
    },
    {
      id: 13,
      bioText: "foo",
      name: "C PersonC",
      firstName: "C",
      lastName: "PersonC",
      location: "St Louis",
      title: "engineer",
      workEmail: "employee@sample.com",
      startDate: [2012, 9, 29],
    },
  ];

  const testState = {
    checkins: [
      { pdlId: 13, completed: false, checkInDate: [2020, 9, 13] },
      { pdlId: 12, completed: false, checkInDate: [2021, 9, 12] },
      { pdlId: 12, completed: true, checkInDate: [2020, 9, 11] },
    ],
    memberProfiles: [
      {
        id: 11,
        bioText: "foo",
        name: "A PersonA",
        firstName: "A",
        lastName: "PersonA",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        startDate: [2012, 9, 29],
      },
      {
        id: 12,
        bioText: "foo",
        name: "B PersonB",
        firstName: "B",
        lastName: "PersonB",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        startDate: [2012, 9, 29],
      },
      {
        id: 13,
        bioText: "foo",
        name: "C PersonC",
        firstName: "C",
        lastName: "PersonC",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        startDate: [2012, 9, 29],
      },
    ],
  };
  expect(selectCheckinPDLS(testState, true, true)).toEqual(matchingMembers);
});

it("selectTeamMembersWithCheckinPDL should return an array of all members associated with a pdl that have a checkin", () => {
  const matchingMembers = [
    {
      id: 12,
      bioText: "foo",
      name: "B PersonB",
      firstName: "B",
      lastName: "PersonB",
      location: "St Louis",
      pdlId: 1,
      title: "engineer",
      workEmail: "employee@sample.com",
      startDate: [2012, 9, 29],
    },
    {
      id: 13,
      bioText: "foo",
      name: "C PersonC",
      firstName: "C",
      lastName: "PersonC",
      location: "St Louis",
      pdlId: 1,
      title: "engineer",
      workEmail: "employee@sample.com",
      startDate: [2012, 9, 29],
    },
  ];

  const testState = {
    checkins: [
      {
        teamMemberId: 12,
        pdlId: 1,
        completed: false,
        checkInDate: [2020, 9, 10],
      },
      {
        teamMemberId: 13,
        pdlId: 1,
        completed: false,
        checkInDate: [2020, 9, 10],
      },
      {
        teamMemberId: 11,
        pdlId: 2,
        completed: false,
        checkInDate: [2020, 9, 10],
      },
    ],
    memberProfiles: [
      {
        id: 11,
        bioText: "foo",
        name: "A PersonA",
        firstName: "A",
        lastName: "PersonA",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        pdlId: 2,
        startDate: [2012, 9, 29],
      },
      {
        id: 12,
        bioText: "foo",
        name: "B PersonB",
        firstName: "B",
        lastName: "PersonB",
        location: "St Louis",
        pdlId: 1,
        title: "engineer",
        workEmail: "employee@sample.com",
        startDate: [2012, 9, 29],
      },
      {
        id: 13,
        bioText: "foo",
        name: "C PersonC",
        firstName: "C",
        lastName: "PersonC",
        location: "St Louis",
        pdlId: 1,
        title: "engineer",
        workEmail: "employee@sample.com",
        startDate: [2012, 9, 29],
      },
    ],
  };
  expect(selectTeamMembersWithCheckinPDL(testState, 1)).toEqual(
    matchingMembers
  );
});

it("selectCheckinsForTeamMemberAndPDL should return an array of all members associated with a pdl that have a checkin", () => {
  const matchingCheckins = [
    {
      id: 1,
      teamMemberId: 12,
      pdlId: 1,
      completed: false,
      checkInDate: [2020, 9, 10],
    },
    {
      id: 2,
      teamMemberId: 12,
      pdlId: 1,
      completed: true,
      checkInDate: [2020, 10, 10],
    },
  ];

  const testState = {
    checkins: [
      {
        id: 1,
        teamMemberId: 12,
        pdlId: 1,
        completed: false,
        checkInDate: [2020, 9, 10],
      },
      {
        id: 2,
        teamMemberId: 12,
        pdlId: 1,
        completed: true,
        checkInDate: [2020, 10, 10],
      },
      {
        id: 3,
        teamMemberId: 11,
        pdlId: 2,
        completed: false,
        checkInDate: [2020, 9, 10],
      },
      {
        id: 4,
        teamMemberId: 19,
        pdlId: 4,
        completed: false,
        checkInDate: [2020, 9, 10],
      },
    ],
    memberProfiles: [
      {
        id: 11,
        bioText: "foo",
        name: "A PersonA",
        firstName: "A",
        lastName: "PersonA",
        location: "St Louis",
        title: "engineer",
        workEmail: "employee@sample.com",
        pdlId: 2,
        startDate: [2012, 9, 29],
      },
      {
        id: 12,
        bioText: "foo",
        name: "B PersonB",
        firstName: "B",
        lastName: "PersonB",
        location: "St Louis",
        pdlId: 1,
        title: "engineer",
        workEmail: "employee@sample.com",
        startDate: [2012, 9, 29],
      },
      {
        id: 13,
        bioText: "foo",
        name: "C PersonC",
        firstName: "C",
        lastName: "PersonC",
        location: "St Louis",
        pdlId: 1,
        title: "engineer",
        workEmail: "employee@sample.com",
        startDate: [2012, 9, 29],
      },
    ],
  };
  expect(selectCheckinsForTeamMemberAndPDL(testState, 12, 1)).toEqual(
    matchingCheckins
  );
});

it("selectCurrentMembers should return an array of non-terminated profiles", () => {
  const testMemberProfiles = [
    {
      id: 1,
      bioText: "foo",
      employeeId: 11,
      name: "Iván López Martín",
      firstName: "Iván",
      lastName: "Martín",
      location: "St Louis",
      title: "engineer",
      workEmail: "employee@sample.com",
      pdlId: 9,
      startDate: [2012, 9, 29],
    },
    {
      id: 2,
      bioText: "foo",
      employeeId: 12,
      name: "B Person",
      firstName: "B",
      lastName: "PersonB",
      location: "St Louis",
      title: "engineer",
      workEmail: "employee@sample.com",
      pdlId: 9,
      startDate: [2012, 9, 29],
    },
    {
      id: 3,
      bioText: "foo",
      employeeId: 13,
      name: "C Person",
      firstName: "C",
      lastName: "PersonC",
      location: "St Louis",
      title: "engineer",
      workEmail: "employee@sample.com",
      pdlId: 9,
      startDate: [2012, 9, 29],
      terminationDate: [2020, 12, 31],
    },
  ];
  const testState = {
    memberProfiles: [
      testMemberProfiles[0],
      testMemberProfiles[1],
      testMemberProfiles[2],
    ],
  };

  const result = {
    memberProfiles: [testMemberProfiles[0], testMemberProfiles[1]],
  };

  expect(selectCurrentMembers(testState)).toEqual(result.memberProfiles);
});

it("selectNormalizedMembers should return an array of appropriate member profiles despite accents", () => {
  const testMemberProfiles = [
    {
      id: 1,
      bioText: "foo",
      employeeId: 11,
      name: "Iván López Martín",
      firstName: "Iván",
      lastName: "Martín",
      location: "St Louis",
      title: "engineer",
      workEmail: "employee@sample.com",
      pdlId: 9,
      startDate: [2012, 9, 29],
    },
    {
      id: 2,
      bioText: "foo",
      employeeId: 12,
      name: "B Person",
      firstName: "B",
      lastName: "PersonB",
      location: "St Louis",
      title: "engineer",
      workEmail: "employee@sample.com",
      pdlId: 9,
      startDate: [2012, 9, 29],
    },
    {
      id: 3,
      bioText: "foo",
      employeeId: 13,
      name: "C Person",
      firstName: "C",
      lastName: "PersonC",
      location: "St Louis",
      title: "engineer",
      workEmail: "employee@sample.com",
      pdlId: 9,
      startDate: [2012, 9, 29],
    },
  ];
  const testState = {
    memberProfiles: [
      testMemberProfiles[0],
      testMemberProfiles[1],
      testMemberProfiles[2],
    ],
  };

  const result = {
    memberProfiles: [testMemberProfiles[0]],
  };

  const searchText = "ivan";

  expect(selectNormalizedMembers(testState, searchText)).toEqual(
    result.memberProfiles
  );
});

it("selectNormalizedTeams should return an array of appropriate teams despite accents", () => {
  const testTeams = [
    {
      id: "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      name: "Iváns Team",
      description: "string",
    },
    {
      id: "3fa4-5717-4562-b3fc-2c963f66afa6",
      name: "stuff",
      description: "",
    },
  ];
  const testState = {
    teams: [testTeams[0], testTeams[1]],
  };

  const searchText = "ivan";

  const result = {
    teams: [testTeams[0]],
  };

  expect(selectNormalizedTeams(testState, searchText)).toEqual(result.teams);
});

it("selectMostRecentCheckin should return the most recent and or open checkin", () => {
  const memberId = "1";
  const checkins = [
    {
      checkInDate: [2020, 9, 29, 10, 32, 29, 40000000],
      completed: false,
      id: "2",
      pdlId: "1",
      teamMemberId: "1",
    },
    {
      checkInDate: [2020, 9, 30, 10, 32, 29, 40000000],
      completed: false,
      id: "1",
      pdlId: "1",
      teamMemberId: "1",
    },
  ];

  const state = { checkins: [checkins[0], checkins[1]] };

  const expectedResult = {
    checkInDate: [2020, 9, 30, 10, 32, 29, 40000000],
    completed: false,
    id: "1",
    pdlId: "1",
    teamMemberId: "1",
  };

  console.log(selectMostRecentCheckin(state, memberId));
  expect(selectMostRecentCheckin(state, memberId)).toEqual(expectedResult);
});
