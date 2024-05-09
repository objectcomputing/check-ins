import React from 'react';
import image from '../../logo.svg';
import Kudos from './Kudos';

export default {
  component: Kudos,
  title: 'Check Ins/Kudos',
  decorators: [
    Kudos => (
      <div style={{ width: '375px', height: '400px' }}>
        <Kudos />
      </div>
    )
  ]
};

const Template = args => {
  return <Kudos {...args} />;
};

const kudosData = {
  kudosTo: {
    name: 'John Doe',
    imageUrl: image
  },
  content:
    'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
  kudosFrom: {
    name: 'Jane Doe',
    imageUrl: image,
    title: 'Senior Software Engineer'
  }
};

export const KudosNonConfidential = Template.bind({});
KudosNonConfidential.args = {
  ...kudosData
};

export const KudosConfidential = Template.bind({});
KudosConfidential.args = {
  ...kudosData,
  kudosFrom: undefined
};
