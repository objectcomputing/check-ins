import React from 'react';
import ProgressBar from './ProgressBar';
import renderer from 'react-test-renderer';

it('has billable hours', () => {
  snapshot(<ProgressBar billableHours={1200}
                        contributionHours={1500}
                        targetHours={1850} />);
});

it('has no billable hours', () => {
  snapshot(<ProgressBar billableHours={0}
                        contributionHours={1500}
                        targetHours={1850} />);
});
