import React from 'react';
import MemberIcon from './MemberIcon';

const profile = { image_url: 'nope' };

it('renders correctly', () => {
  snapshot(<MemberIcon profile={profile} />);
});
