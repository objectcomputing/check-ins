// Type definitions specified here will be available app-wide

/**
 * @typedef {Object} MemberProfile
 * @property {?string} bioText
 * @property {?Array} birthday
 * @property {string} employeeId
 * @property {?string} excluded
 * @property {string} firstName
 * @property {!string} id
 * @property {string} lastName
 * @property {string} location
 * @property {?string} middleName
 * @property {string} name
 * @property {?string} pdlId
 * @property {Array} startDate
 * @property {?string} suffix
 * @property {?string} supervisorid
 * @property {?Array} terminationDate
 * @property {string} title
 * @property {?string} voluntary
 * @property {string} workEmail
 */

/**
 * @typedef {MemberProfile} PDLProfile
 * @property {string} pdlId
 */

/**
 * Check-In related types
 *
 * @typedef {Object} Checkin
 * @property {string} id - The ID of the check-in.
 * @property {boolean} completed - Indicates whether the check-in is completed.
 * @property {Array} checkinDate - The date of the check-in.
 * @property {string} pdlId - The ID of the PDL.
 * @property {string} teamMemberId - The ID of the team member.
 *
 * @typedef {("Done" | "In Progress" | "Not Started")} CheckinStatus
 */
