const allPermissions = [
  'View Action Items',
  'Edit Action Items',
  'Create Action Items',
  'Add Team Members',
  'Delete Team Members'
];

const admin = {
  id: 1,
  name: 'Admin',
  permissions: [
    'View Action Items',
    'Edit Action Items',
    'Create Action Items',
    'Add Team Members',
    'Delete Team Members'
  ]
};

const PDL = {
  id: 2,
  name: 'PDL',
  permissions: [
    'View Action Items',
    'Edit Action Items',
    'Create Action Items',
    'Add Team Members'
  ]
};

const teamLead = {
  id: 3,
  name: 'Team Lead',
  permissions: ['View Action Items', 'Add Team Members']
};

const teamMembers = {
  id: 4,
  name: 'Team Member',
  permissions: ['View Action Items']
};

const roles = [admin, PDL, teamLead, teamMembers];

const handleChange = (role, permission) => {
  alert(`toggle the permission titled "${permission}" for role: ${role.name}`);
  console.log(role);
  console.log(permission);
};

export { allPermissions, roles, handleChange };
