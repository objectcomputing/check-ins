import React from 'react';
import OpportunityCard from './OpportunityCard';

export default {
  component: OpportunityCard,
  title: 'Check Ins/Opportunity',
  decorators: [
    OpportunityCard => (
      <div style={{ width: '375px', height: '400px' }}>
        <OpportunityCard />
      </div>
    )
  ]
};

const Template = args => {
  return <OpportunityCard {...args} />;
};

const opportunityCardData = {
  opportunity: {
    description: 'Looking for someone to spit some hot fire',
    expiresOn: '01/02/2030',
    name: 'Hip hop artist',
    pending: false,
    url: 'www.google.com'
  }
};

export const OpportunityCards = Template.bind({});
OpportunityCards.args = {
  ...opportunityCardData
};
