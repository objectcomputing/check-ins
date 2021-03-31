import {
    selectMemberProfiles, selectProfileMap, selectMemberRoles, selectPdlRoles, selectMappedPdls,
    selectOrderedPdls
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
            memberProfiles: {
                [testMemberProfiles[0].id]: testMemberProfiles[0],
                [testMemberProfiles[1].id]: testMemberProfiles[1],
                [testMemberProfiles[2].id]: testMemberProfiles[2]
            }
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
        const testState = {
            memberProfiles: {
                [testMemberProfiles[0].id]: testMemberProfiles[0],
                [testMemberProfiles[1].id]: testMemberProfiles[1],
                [testMemberProfiles[2].id]: testMemberProfiles[2]
            }
        };

        expect(selectProfileMap(testState)).toEqual(testState.memberProfiles);
    });

    it('selectMemberRoles should return an array of all member roles', () => {
        const testMemberRoles = [
            {
                id: '1',
                memberid: '11',
                role: 'MEMBER'
            },
            {
                id: '2',
                memberid: '12',
                role: 'PDL'
            },
            {
                id: '3',
                memberid: '13',
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

        expect(selectMemberRoles(testState)).toEqual(testState.roles);
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
        ]
        const testMemberRoles = [
            {
                id: '11',
                memberid: '1',
                role: 'MEMBER'
            },
            matchingRoles[0],
            matchingRoles[1]
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

        const matchingMembers = [
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
        ]

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
        ]
        const testMemberRoles = [
            {
                id: '11',
                memberid: '1',
                role: 'MEMBER'
            },
            matchingRoles[0],
            matchingRoles[1]
        ];
        const testState = {
            memberProfiles: {
                [testMemberProfiles[0].id]: testMemberProfiles[0],
                [testMemberProfiles[1].id]: testMemberProfiles[1],
                [testMemberProfiles[2].id]: testMemberProfiles[2]
            },
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
        expect(selectMappedPdls(testState)).toEqual(matchingMembers);
    });

    it('selectOrderedPdls should return an array of all member PDL profiles ordered by last name', () => {
        const testMemberProfiles = [
            {
                id: 1,
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
                id: 2,
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
                id: 3,
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
        ];

        const matchingMembers = [
            {
                id: 3,
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
                id: 2,
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
        ]

        const testMemberRoles = [
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
        ];
        const testState = {
            memberProfiles: {
                [testMemberProfiles[0].id]: testMemberProfiles[0],
                [testMemberProfiles[1].id]: testMemberProfiles[1],
                [testMemberProfiles[2].id]: testMemberProfiles[2]
            },
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
        expect(selectOrderedPdls(testState)).toEqual(matchingMembers);
    });
});
